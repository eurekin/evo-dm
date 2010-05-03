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

    private void updateTheBestIndividual(Individual I) {
        this.theBestInd = new RuleSet((RuleSet) (I));
    }

    public DataLoader getDataLoader() {
        return this.dataLoader;
    }

    /**
     * testing constructor
     */
    public EvoAlgorithm() {
        this.generation = 0;
        this.myClock.Reset();
        this.myClock = new Clock();
        this.theBestInd = new RuleSet();
        this.rulePopulation.Initialise();
        this.totalTimeClock = new Clock();
        this.dataLoader = new DataLoader(null, null);
        this.rulePopulation = new Population(new RuleSet());
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
        final boolean echo = config.isEcho();
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
                rulePopulation.Initialise();
                theBestInd = null;

                // warunek stopu
                boolean stopEval = false;
                generation = 0;

                rulePopulation.evaluate(DataLoader.getTrainData());
                updateTheBestIndividual(rulePopulation.getBestIndividual());

                //EA works....
                while (stopEval == false && config.getStopGeneration() != generation) {
                    /*new generation*/
                    rulePopulation = rulePopulation.recombinate();
                    generation++;
                    rulePopulation.evaluate(DataLoader.getTrainData());

                    //the best individual
                    // W0000t ?! evaluation only for one class?! TODO XXX
                    if (rulePopulation.getBestFitness() > theBestInd.getEvaluation().getFitness()) {
                        theBestInd = new RuleSet(
                                (RuleSet) (rulePopulation.getBestIndividual()));
                    }

                    // stop condition
                    if (config.getStopEval() <= rulePopulation.getBestFitness()) {
                        stopEval = true;
                        break;
                    }

                    if (echo) {
                        report.reportAfterOneGeneration(theBestInd, rulePopulation, generation);
                    }
                }//END: EA works

                myClock.Pause();
                totalTimeClock.Pause();

                evaluateAndReport(eval, report, config);
                theBestOfTheBest = getNewBestOfTheBestIndividual(theBestOfTheBest, eval, config);
            }
            DataLoader.doCrossvalidationNext();
        }//END: CROSSVALIDATION CV TIMES
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

