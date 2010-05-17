package pwr.evolutionaryAlgorithm;

import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.utils.Clock;

public class EvoAlgorithm {

    private Clock myClock;
    private long generation;
    private Clock totalTimeClock;
    private Individual best;
    private DataLoader dataLoader;
    private Population<RuleSet> rulePopulation;

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
        // MAIN Evolutionary Algorithm
        while (!stop  && config.getStopGeneration() != generation) {
            /*new generation*/
            rulePopulation = rulePopulation.recombinate();
            generation++;
            rulePopulation.evaluate(DataLoader.getTrainData());
            //the best individual
            if (rulePopulation.getBestFitness() > best.getFitness()) {
                best = new RuleSet(rulePopulation.getBestIndividual());
            }
            if (config.getStopEval() <= rulePopulation.getBestFitness()) {
                stop = true;
                break;
            }
            if (config.isEcho()) {
                report.reportAfterOneGeneration(best, rulePopulation, generation);
            }
        }
        //END: EA works
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    /**
     * testing constructor
     */
    public EvoAlgorithm() {
        generation = 0;
        myClock.Reset();
        myClock = new Clock();
        best = new RuleSet();
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
        Report report = config.getReport();
        String prompt = config.getPrompt();
        report.evoAlgInitStart(prompt);
        dataLoader = DataLoader.getDataLoader(config);
        myClock = new Clock();
        best = new RuleSet();
        report.evoAlgInitStop(prompt, DataLoader.FileSummary());
    }

        Individual bestOfAllFolds = null;
    public void start() {
        Evaluator eval = Evaluator.getEvaluator();
        bestOfAllFolds = null;
        totalTimeClock = new Clock();
        totalTimeClock.Reset();
        final Configuration config = Configuration.getConfiguration();
        final Report report = config.getReport();
        final int testNo = report.getTestNumber();
        int crossvalidationNo = config.getCrossvalidationValue();

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
                best = null;

                rulePopulation.evaluate(DataLoader.getTrainData());
                updateTheBestIndividual(rulePopulation.getBestIndividual());

                // EA START
                evolve(config, report);
                // EA DONE

                myClock.Pause();
                totalTimeClock.Pause();
                evaluateAndReport(eval, report, config);
                bestOfAllFolds = getNewBestOfTheBestIndividual(bestOfAllFolds, report);
            }
            DataLoader.doCrossvalidationNext();
        }
        //END: CROSSVALIDATION CV TIMES
        report.reportAllToFile(config, eval, bestOfAllFolds, totalTimeClock);
    }

    public void evaluateAndReport(Evaluator eval, Report report, Configuration config) {
        final float trainFsc, trainAcc, testFsc, testAcc;
        best.evaluate(DataLoader.getTrainData());
        trainFsc = best.getFsc();
        trainAcc = best.getAccuracy();

        best.evaluate(DataLoader.getTestData());
        testFsc = best.getFsc();
        testAcc = best.getAccuracy();

        report.report(generation, trainFsc, trainAcc, testFsc, testAcc, myClock.GetTotalTime());
        report.extendedReport(config, eval, (RuleSet) best);
    }

    private void updateTheBestIndividual(Individual I) {
        best = new RuleSet((RuleSet) (I));
    }

    public Individual getNewBestOfTheBestIndividual(
            Individual bestInd, Report report) {
        if (bestInd == null) {
            bestInd = new RuleSet((RuleSet) best);
            bestInd.evaluate(DataLoader.getTestData());
        }
        // Tu nie było Buga? (porównywano zawsze ACC)
        if (bestInd.getFitness() < best.getFitness()) {
            bestInd = new RuleSet((RuleSet) best);
            bestInd.evaluate(DataLoader.getTestData());
            report.reportBestInd(bestInd);
        }
        return bestInd;
    }

    public long getGeneration() {
        return generation;
    }
}
