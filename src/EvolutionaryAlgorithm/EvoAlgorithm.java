package EvolutionaryAlgorithm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import data.DataLoader;
import data.Evaluator;
import utils.Clock;
import EvolutionaryAlgorithm.Individual.*;

public class EvoAlgorithm {

    private Clock myClock;
    private Clock totalTimeClock = new Clock();
    private Individual theBestInd;

    public boolean isTheBestIndividual(float f) {
        if (f > this.theBestInd.getEvaluation().getFitness()) {
            return true;
        } else {
            return false;
        }
    }

    private Individual getNewBestOfTheBestIndividual(Individual bestInd,
            Evaluator Eval, Configuration Config) {
        if (bestInd == null) {
            bestInd = new RuleSet((RuleSet) (theBestInd));
            Eval.evaluate(DataLoader.getTestData(), bestInd);
        }
        if (bestInd.getEvaluation().getAccuracy() < theBestInd.getEvaluation().getAccuracy()) {
            bestInd = new RuleSet((RuleSet) (theBestInd));
            Eval.evaluate(DataLoader.getTestData(), bestInd);
            Config.getReport().reportBestInd(bestInd);
        }
        return bestInd;
    }

    private void updateTheBestIndividual(Individual I) {
        this.theBestInd = new RuleSet((RuleSet) (I));
    }
    private long generation;
    private Population rulePopulation;
    private DataLoader dataLoader;

    public DataLoader getDataLoader() {
        return this.dataLoader;
    }

    /**
     * testing constructor
     */
    public EvoAlgorithm() {
        this.generation = 0;
        this.dataLoader = new DataLoader(null, null);
        this.myClock = new Clock();
        this.myClock.Reset();
        this.rulePopulation = new Population(new RuleSet());
        this.rulePopulation.Initialise();
        this.theBestInd = new RuleSet();

    }

    public EvoAlgorithm(String ConfigFileName, String ResearchComment) {
        this(setConfiguration(ConfigFileName, ResearchComment));
    }

    private static Configuration setConfiguration(String configFileName, String researchComment) {
        Configuration.newConfiguration(configFileName, researchComment);
        return Configuration.getConfiguration();
    }

    public EvoAlgorithm(Configuration config) {
        System.out.print(config.getPrompt() + "initalising...");
        dataLoader = DataLoader.getDataLoader(config);
        myClock = new Clock();
        theBestInd = new RuleSet();
        System.out.print("done!");
        System.out.print(config.getPrompt() + DataLoader.FileSummary() + "\n");
    }

    public void start() {

        Evaluator eval = Evaluator.getEvaluator();
        Individual theBestOfTheBest = null;
        totalTimeClock = new Clock();
        totalTimeClock.Reset();
        final Configuration config = Configuration.getConfiguration();
        final Report report = config.getReport();
        final boolean echo = config.isEcho();


        int testNo = report.getTestNumber();
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

                // inicjalizacja pojedynczego przebiegu algorytmu ewolucyjnego
                // tworzenie nowej populacji
                rulePopulation = new Population(new RuleSet());
                rulePopulation.Initialise();
                theBestInd = null;

                // warunek stopu
                boolean stopEval = false;
                generation = 0;

                rulePopulation.Evaluate(DataLoader.getTrainData());
                updateTheBestIndividual(rulePopulation.getBestIndividual());

                //EA works....
                while (stopEval == false && config.getStopGeneration() != generation) {
                    /*new generation*/
                    Population temporaryPopulation = rulePopulation.recombinate();
                    rulePopulation = temporaryPopulation;
                    // whats the use of temporary population?

                    // we just advanced
                    generation++;

                    // evaluation
                    rulePopulation.Evaluate(DataLoader.getTrainData());

                    //the best individual?
                    // What's the use of it?!
                    float f = rulePopulation.getBestFitness();
                    if (isTheBestIndividual(f)) {
                        updateTheBestIndividual(rulePopulation.getBestIndividual());
                    }

                    // stop condition
                    if (Configuration.getConfiguration().getStopEval()
                            <= rulePopulation.getBestFitness()) {
                        stopEval = true;
                        break;
                    }

                    // reporting
                    if (echo) {
                        report.reportAfterOneGeneration(theBestInd, rulePopulation, generation);
                    }
                }//END: EA works

                //REPORTS.... 
                myClock.Pause();
                totalTimeClock.Pause();

                eval.evaluate(DataLoader.getTrainData(), theBestInd);

                float train = theBestInd.getEvaluation().getFsc();
                float train_acc = theBestInd.getEvaluation().getAccuracy();

                eval.evaluate(DataLoader.getTestData(), theBestInd);
                float test = theBestInd.getEvaluation().getFsc();
                float test_acc = theBestInd.getEvaluation().getAccuracy();

                report.report(generation, train, train_acc, test, test_acc, myClock.GetTotalTime());

                if (false) { // XXX turned off bigfile generation
                    try {
                        report.ReportExText(config.toString()
                                + eval.FullClassificationReport(
                                DataLoader.getTrainData(), (RuleSet) theBestInd, "TRAIN")
                                + eval.FullClassificationReport(
                                DataLoader.getTestData(), (RuleSet) theBestInd, "TEST"));
                    } catch (IOException ex) {
                        Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                theBestOfTheBest = getNewBestOfTheBestIndividual(theBestOfTheBest, eval, config);
                ////////////////////////////////////////////////////////////////
            }
            DataLoader.doCrossvalidationNext();
        }//END: CROSSVALIDATION CV TIMES
        report.reportAllToFile(config, eval, theBestOfTheBest, totalTimeClock);
    }

    public long getGeneration() {
        return this.generation;
    }
}

