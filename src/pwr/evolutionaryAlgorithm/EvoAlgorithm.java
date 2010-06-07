package pwr.evolutionaryAlgorithm;

import pl.eurekin.util.BestIndividualSelector;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.utils.Clock;

/**
 *
 * @author pawelm
 */
public class EvoAlgorithm {

    private long generation;
    private final Configuration config;
    private final Report report;
    private Population<RuleSet> rulePopulation;
    private BestIndividualSelector<RuleSet> bestOfRun;

    public void initPopulation() {
        rulePopulation = new Population<RuleSet>(new RuleSet());
        rulePopulation.evaluate(DataLoader.getTrainData());
    }

    public void start() {
        final int testNo = report.getTestNumber();
        final int crossvalidationNo = config.getCrossvalidationValue();
        final Clock totalTimeClock = new Clock();
        final Clock myClock = new Clock();
        bestOfFold = new BestIndividualSelector<RuleSet>();
        //CROSSVALIDATION CV TIMES
        DataLoader.doCrossvalidation();
        for (int cv = 0; cv < crossvalidationNo; cv++) {
            report.indicateCrossvalidationFold(cv);
            //RUN N-times EA....
            for (int run = 0; run < testNo; run++) {
                // czas jednego powtórzenia kroswalidacji
                myClock.Reset();
                myClock.Start();
                totalTimeClock.Start();
                initPopulation();
                // EA START
                evolve();
                // EA DONE
                myClock.Pause();
                totalTimeClock.Pause();
                evaluateAndReport(myClock);
            }
            DataLoader.doCrossvalidationNext();
        }
        //END: CROSSVALIDATION CV TIMES
        report.reportAllToFile(config, bestOfFold.getBest(), totalTimeClock);
    }

    /**
     * Evolutionary Algorithm - core implementation
     * 
     * @param config supplying parameters
     * @param report object to send messages to
     */
    private void evolve() {
        // warunek stopu
        boolean stop = false;
        generation = 0;
        bestOfRun = new BestIndividualSelector<RuleSet>();
        // MAIN Evolutionary Algorithm
        while (!stop) {
            /*new generation*/
            rulePopulation = rulePopulation.recombinate();
            generation++;
            rulePopulation.evaluate(DataLoader.getTrainData());
            //the best individual
            bestOfRun.rememberBestFrom(rulePopulation);
            reportGenerationEnd();
            stop = config.getStopEval() <= rulePopulation.getBestFitness();
            stop |= config.getStopGeneration() == generation;
        }
        //END: EA works
    }

    private void reportGenerationEnd() {
        if (config.isEcho()) {
            report.reportAfterOneGeneration(bestOfRun.getBest(),
                    rulePopulation, generation);
        }
    }

    /**
     * testing constructor
     */
    public EvoAlgorithm() {
        generation = 0;
        config = null;
        report = null;
        rulePopulation = new Population<RuleSet>(new RuleSet());
        rulePopulation.init();
    }

    public EvoAlgorithm(String ConfigFileName, String ResearchComment) {
        this(setConfiguration(ConfigFileName, ResearchComment));
    }

    private static Configuration setConfiguration(String configFileName,
            String researchComment) {
        Configuration.newConfiguration(configFileName, researchComment);
        return Configuration.getConfiguration();
    }

    public EvoAlgorithm(Configuration config) {
        this.config = config;
        report = config.getReport();
        String prompt = config.getPrompt();
        
        report.evoAlgInitStart(prompt);
        DataLoader.getDataLoader(config);
        report.evoAlgInitStop(prompt, DataLoader.FileSummary());
    }
    private BestIndividualSelector<RuleSet> bestOfFold;

    public void evaluateAndReport(Clock clock) {
        final float trainFsc, trainAcc, testFsc, testAcc;
        final RuleSet best = bestOfRun.getBest();

        best.evaluate(DataLoader.getTrainData());
        trainFsc = best.getFsc();
        trainAcc = best.getAccuracy();

        best.evaluate(DataLoader.getTestData());
        testFsc = best.getFsc();
        testAcc = best.getAccuracy();

        report.report(generation, trainFsc, trainAcc, testFsc, testAcc, clock.GetTotalTime());
        report.extendedReport(config, best);

        bestOfFold.rememberIfBest(best);
        if (bestOfFold.isBetterThanLastOne()) {
            report.reportBestInd(best);
        }
    }
}
