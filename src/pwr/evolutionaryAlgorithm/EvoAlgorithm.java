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
    private Individual theBestInd;
    private DataLoader dataLoader;
    private Population rulePopulation;

    /**
     * Evolutionary Algorithm - core implementation
     * 
     * @param config supplying parameters
     * @param report object to send messages to
     */
    private void evolve(final Configuration config, final Report report) {
        // warunek stopu
        boolean stopEval = false;
        generation = 0;
        // MAIN Evolutionary Algorithm
        while (stopEval == false && config.getStopGeneration() != generation) {
            /*new generation*/
            rulePopulation = rulePopulation.recombinate();
            generation++;
            rulePopulation.evaluate(DataLoader.getTrainData());
            //the best individual
            if (rulePopulation.getBestFitness() > theBestInd.getEvaluation().getFitness()) {
                theBestInd = new RuleSet((RuleSet) (rulePopulation.getBestIndividual()));
            }
            if (config.getStopEval() <= rulePopulation.getBestFitness()) {
                stopEval = true;
                break;
            }
            if (config.isEcho()) {
                report.reportAfterOneGeneration(theBestInd, rulePopulation, generation);
            }
        }
        //END: EA works
    }

     private void updateTheBestIndividual(Individual I) {
        theBestInd = new RuleSet((RuleSet) (I));
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
        theBestInd = new RuleSet();
        totalTimeClock = new Clock();
        dataLoader = new DataLoader(null, null);
        rulePopulation = new Population(new RuleSet());
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
        theBestInd = new RuleSet();
        report.evoAlgInitStop(prompt, DataLoader.FileSummary());
    }

    public void start() {
        Evaluator eval = Evaluator.getEvaluator();
        Individual theBestOfTheBest = null;
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
                rulePopulation = new Population(new RuleSet());
                rulePopulation.init();
                theBestInd = null;

                rulePopulation.evaluate(DataLoader.getTrainData());
                updateTheBestIndividual(rulePopulation.getBestIndividual());

                // EA START
                evolve(config, report);
                // EA DONE

                myClock.Pause();
                totalTimeClock.Pause();
                evaluateAndReport(eval, report, config);
                theBestOfTheBest = getNewBestOfTheBestIndividual(theBestOfTheBest, eval, config);
            }
            DataLoader.doCrossvalidationNext();
        }
        //END: CROSSVALIDATION CV TIMES
        report.reportAllToFile(config, eval, theBestOfTheBest, totalTimeClock);
    }

    public void evaluateAndReport(Evaluator eval, Report report, Configuration config) {
        eval.evaluate(DataLoader.getTrainData(), theBestInd);
        float train_fsc = theBestInd.getEvaluation().getFsc();
        float train_acc = theBestInd.getEvaluation().getAccuracy();

        eval.evaluate(DataLoader.getTestData(), theBestInd);
        float test_fsc = theBestInd.getEvaluation().getFsc();
        float test_acc = theBestInd.getEvaluation().getAccuracy();

        report.report(generation, train_fsc, train_acc, test_fsc, test_acc, myClock.GetTotalTime());
        report.extendedReport(config, eval, (RuleSet) theBestInd);
    }

    public Individual getNewBestOfTheBestIndividual(Individual bestInd,
            Evaluator eval, Configuration config) {
        if (bestInd == null) {
            bestInd = new RuleSet((RuleSet) (theBestInd));
            eval.evaluate(DataLoader.getTestData(), bestInd);
        }
        if (bestInd.getEvaluation().getAccuracy() < theBestInd.getEvaluation().getAccuracy()) {
            bestInd = new RuleSet((RuleSet) (theBestInd));
            eval.evaluate(DataLoader.getTestData(), bestInd);
            config.getReport().reportBestInd(bestInd);
        }
        return bestInd;
    }

    public long getGeneration() {
        return this.generation;
    }
}

