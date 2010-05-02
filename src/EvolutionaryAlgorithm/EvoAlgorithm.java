package EvolutionaryAlgorithm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import EvolutionaryAlgorithm.Individual.*;

import data.DataLoader;
import data.Evaluator;
import utils.Clock;

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

    protected void reportAllToFile(Configuration Config, Evaluator Eval, Individual TheBestOfTheBest) {
        try {
            // String R = Config.getReport().getReportStatistic(Configuration.getConfiguration().toString(), true);
            String CSV = Config.getReport().getCSVReportStatistic(Configuration.getConfiguration().toString(), true);
            CSV = CSV + String.format("%.3f", totalTimeClock.GetTotalTime() / 1000.0d);
            //            Config.getReport().ReportText(R);
            //            Config.getReport().ConsoleReport(R);
            Config.getReport().AppendCSVReportLineToFile(CSV);
        } catch (IOException ex) {
            Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (false) {
            try {
                //Eval.Evaluate(DataLoader.getTestData(), TheBestOfTheBest);
                Config.getReport().ReportExText(Config.toString()
                        + Eval.FullClassificationReport(DataLoader.getTrainData(),
                        (RuleSet) TheBestOfTheBest, "TRAIN")
                        + Eval.FullClassificationReport(DataLoader.getTestData(),
                        (RuleSet) TheBestOfTheBest, "TEST"));
            } catch (IOException ex) {
                Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    private Population mRulePopulation;
    private DataLoader mDataLoader;

    public DataLoader getDataLoader() {
        return this.mDataLoader;
    }

    /**
     * testing constructor
     */
    public EvoAlgorithm() {
        this.generation = 0;
        this.mDataLoader = new DataLoader(null, null);
        this.myClock = new Clock();
        this.myClock.Reset();
        this.mRulePopulation = new Population(new RuleSet());
        this.mRulePopulation.Initialise();
        this.theBestInd = new RuleSet();

    }

    public EvoAlgorithm(String ConfigFileName, String ResearchComment) {
        this(setConfiguration(ConfigFileName, ResearchComment));
    }

    private static Configuration setConfiguration(String ConfigFileName, String ResearchComment) {
        Configuration.NewConfiguration(ConfigFileName, ResearchComment);
        return Configuration.getConfiguration();
    }

    public EvoAlgorithm(Configuration config) {

        System.out.print(config.getPrompt() + "initalising...");

        if (config.isImageDataConfiguration() == true) {

            /**
             * todo: only ECCV_2002 -- insert universal code here
             */
            mDataLoader = new DataLoader(config.getImageWordsFilename(),
                    config.getImageDocWordFilename(),
                    config.getImageBlobCountsFilename(),
                    config.getImageBlobsFilename(),
                    config.getImageTESTDocWordsFilename(),
                    config.getImageTESTBlobCountsFilename(),
                    config.getImageTESTBlobsFilename());
        } else {
            mDataLoader = new DataLoader(
                    Configuration.getConfiguration().getTrainFileName(),
                    Configuration.getConfiguration().getTestFileName());
        }

        myClock = new Clock();
        theBestInd = new RuleSet();
        System.out.print("done!");
        System.out.print(Configuration.getConfiguration().getPrompt()
                + DataLoader.FileSummary() + "\n");
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
        for (int cv = 0; cv < crossvalidationNo; cv++) {
            report.indicateCrossvalidationFold(cv);

            if (cv == 0) {
                DataLoader.DoCrossvalidation();
            } else {
                DataLoader.DoCrossvalidationNext();
            }

            //RUN N-times EA....
            for (int run = 0; run < testNo; run++) {
                // czas jednego powtórzenia kroswalidacji
                myClock.Reset();
                myClock.Start();
                totalTimeClock.Start();

                // inicjalizacja pojedynczego przebiegu algorytmu ewolucyjnego
                // tworzenie nowej populacji
                mRulePopulation = new Population(new RuleSet());
                mRulePopulation.Initialise();
                theBestInd = null;

                // warunek stopu
                boolean stopEval = false;
                generation = 0;

                mRulePopulation.Evaluate(DataLoader.getTrainData());
                updateTheBestIndividual(mRulePopulation.getBestIndividual());

                //EA works....
                while (stopEval == false && config.getStopGeneration() != generation) {
                    /*new generation*/
                    Population temporaryPopulation = mRulePopulation.recombinate();
                    mRulePopulation = temporaryPopulation;
                    // whats the use of temporary population?

                    // we just advanced
                    generation++;

                    // evaluation
                    mRulePopulation.Evaluate(DataLoader.getTrainData());

                    //the best individual?
                    // What's the use of it?!
                    float f = mRulePopulation.getBestFitness();
                    if (isTheBestIndividual(f)) {
                        updateTheBestIndividual(mRulePopulation.getBestIndividual());
                    }

                    // stop condition
                    if (Configuration.getConfiguration().getStopEval()
                            <= mRulePopulation.getBestFitness()) {
                        stopEval = true;
                        break;
                    }

                    // reporting
                    if (echo) {
                        report.reportAfterOneGeneration(theBestInd, mRulePopulation, generation);
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
        }//END: CROSSVALIDATION CV TIMES
        reportAllToFile(config, eval, theBestOfTheBest);
    }

    public String toSting() {
        /**
         * @todo insert code here
         */
        return "";
    }

    public long getGeneration() {
        return this.generation;
    }
}

