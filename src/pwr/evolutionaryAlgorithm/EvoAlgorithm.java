package pwr.evolutionaryAlgorithm;

import pl.eurekin.util.BestIndividualSelector;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.utils.Clock;

public class EvoAlgorithm {

    private Clock myClock;
    private long generation;
    private Clock totalTimeClock;
    private DataLoader dataLoader;
    private Population<RuleSet> rulePopulation;
    private BestIndividualSelector<RuleSet> bestOfRun;
    private final Configuration config;
    private final Report report;

    /**
     * Evolutionary Algorithm - core implementation
     * 
     * @param config supplying parameters
     * @param report object to send messages to
     */
    private void evolve(final Configuration config, final Report report) {
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
            reportGenerationEnd(config, report);
            stop = config.getStopEval() <= rulePopulation.getBestFitness();
            stop |= config.getStopGeneration() == generation;
        }
        //END: EA works
    }

    private void reportGenerationEnd(final Configuration config, final Report report) {
        if (config.isEcho()) {
            report.reportAfterOneGeneration(bestOfRun.getBest(), rulePopulation, generation);
        }
    }

    /**
     * testing constructor
     */
    public EvoAlgorithm() {
        generation = 0;
        config = null;
        report = null;
        myClock.Reset();
        myClock = new Clock();
        totalTimeClock = new Clock();
        dataLoader = new DataLoader(null, null);
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
        dataLoader = DataLoader.getDataLoader(config);
        myClock = new Clock();
        report.evoAlgInitStop(prompt, DataLoader.FileSummary());
    }

    public void start() {
        Evaluator eval = Evaluator.getEvaluator();
        totalTimeClock = new Clock();
        totalTimeClock.Reset();
        final int testNo = report.getTestNumber();
        int crossvalidationNo = config.getCrossvalidationValue();
        BestIndividualSelector<RuleSet> bestOfFold =
                new BestIndividualSelector<RuleSet>();

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

                // tworzenie nowej populacji
                rulePopulation = new Population<RuleSet>(new RuleSet());
                rulePopulation.init();
                rulePopulation.evaluate(DataLoader.getTrainData());

                // EA START
                evolve(config, report);
                // EA DONE
                evaluateAndReport();
                RuleSet best = bestOfRun.getBest();
                bestOfFold.rememberIfBest(best);
                if (bestOfFold.isBetterThanLastOne()) {
                    report.reportBestInd(best);
                }

                myClock.Pause();
                totalTimeClock.Pause();
            }
            DataLoader.doCrossvalidationNext();
        }
        //END: CROSSVALIDATION CV TIMES
        report.reportAllToFile(config, eval, bestOfFold.getBest(), totalTimeClock);
    }

    public void evaluateAndReport() {
        final float trainFsc, trainAcc, testFsc, testAcc;
        final RuleSet best = bestOfRun.getBest();

        best.evaluate(DataLoader.getTrainData());
        trainFsc = best.getFsc();
        trainAcc = best.getAccuracy();

        best.evaluate(DataLoader.getTestData());
        testFsc = best.getFsc();
        testAcc = best.getAccuracy();

        report.report(generation, trainFsc, trainAcc, testFsc, testAcc, myClock.GetTotalTime());
        report.extendedReport(config, best);
    }
}
