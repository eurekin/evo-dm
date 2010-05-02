// #[regen=yes,id=DCE.CDAF4B1A-454B-95E9-E711-63B839E7F67E]
//import Individual;
package EvolutionaryAlgorithm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import EvolutionaryAlgorithm.Individual.*;

import data.DataLoader;
import data.Evaluator;
import utils.Clock;

// </editor-fold> 
public class EvoAlgorithm {

    private Clock myClock;
    private Clock totalTimeClock = new Clock();
    private Individual theBestIndividual;

    public boolean isTheBestIndividual(float f) {
        if (f > this.theBestIndividual.getEvaluation().getFitness()) {
            return true;
        } else {
            return false;
        }
    }

    protected void reportAllToFile(Configuration Config, Evaluator Eval, Individual TheBestOfTheBest) {
        //REPORTING into files-------------------------------------------------------------------------
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
                        + Eval.FullClassificationReport(DataLoader.getTrainData(), (RuleSet) TheBestOfTheBest, "TRAIN")
                        + Eval.FullClassificationReport(DataLoader.getTestData(), (RuleSet) TheBestOfTheBest, "TEST"));
            } catch (IOException ex) {
                Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //END: REPORTING into files-------------------------------------------------------------------------
    }

//------------------------------------------------------------------------------
    private void updateTheBestIndividual(Individual I) {
        this.theBestIndividual = new RuleSet((RuleSet) (I));
    }
//------------------------------------------------------------------------------
    private long generationNo;
    private Population mRulePopulation;
    private DataLoader mDataLoader;

    public DataLoader getDataLoader() {
        return this.mDataLoader;
    }

//------------------------------------------------------------------------------
    /**
     * testing constructor
     */
    public EvoAlgorithm() {
        this.generationNo = 0;
        this.mDataLoader = new DataLoader(null, null);
        this.myClock = new Clock();
        this.myClock.Reset();
        this.mRulePopulation = new Population(new RuleSet());
        this.mRulePopulation.Initialise();
        this.theBestIndividual = new RuleSet();

    }

    public EvoAlgorithm(String ConfigFileName, String ResearchComment) {
        this(setConfiguration(ConfigFileName, ResearchComment));
    }

    private static Configuration setConfiguration(String ConfigFileName, String ResearchComment) {
        Configuration.NewConfiguration(ConfigFileName, ResearchComment);
        return Configuration.getConfiguration();
    }

//  ------------------------------------------------------------------------------
    public EvoAlgorithm(Configuration config) {

        System.out.print(config.getPrompt() + "initalising...");

        if (config.isImageDataConfiguration() == true) {

            ///mDataLoader = new DataLoader()
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
        theBestIndividual = new RuleSet();
        System.out.print("done!");
        System.out.print(Configuration.getConfiguration().getPrompt()
                + DataLoader.FileSummary() + "\n");
    }

//------------------------------------------------------------------------------
    public void start() {

        Configuration Config = Configuration.getConfiguration();
        Evaluator Eval = Evaluator.getEvaluator();
        Individual TheBestOfTheBest = null;
        totalTimeClock = new Clock();
        totalTimeClock.Reset();


        int testNo = Config.getReport().getTestNumber();
        int CROSSVALIDATION = Config.getCrossvalidationValue();

        //CROSSVALIDATION CV TIMES
        for (int cv = 0; cv < CROSSVALIDATION; cv++) {

            Config.getReport().ConsoleReport("\n\n CROSSVALIDATION " + cv + "\n");

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
                theBestIndividual = null;

                // warunek stopu
                boolean StopEval = false;
                generationNo = 0;

                mRulePopulation.Evaluate(DataLoader.getTrainData());
                updateTheBestIndividual(mRulePopulation.getBestIndividual());

                //EA works....
                while (StopEval == false && Config.getStopGeneration() != generationNo) {
                    /*new generation*/
                    Population temporaryPopulation = mRulePopulation.recombinate();
                    mRulePopulation = temporaryPopulation;
                    // whats the use of temporary population?

                    // we just advanced
                    generationNo++;

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
                        StopEval = true;
                        break;
                    }

                    // reporting
                    if (Config.isEcho()) {
                        Config.getReport().reportAfterOneGeneration(theBestIndividual, mRulePopulation, generationNo);
                    }
                }//END: EA works

                //REPORTS.... XXX to powinno się znaleźć w odpowiedniej metodzie
                // odpowiedniego obiektu
                myClock.Pause();
                totalTimeClock.Pause();

                Eval.evaluate(DataLoader.getTrainData(), theBestIndividual);

                float train = theBestIndividual.getEvaluation().getFsc();
                float train_acc = theBestIndividual.getEvaluation().getAccuracy();

                Eval.evaluate(DataLoader.getTestData(), theBestIndividual);
                float test = theBestIndividual.getEvaluation().getFsc();
                float test_acc = theBestIndividual.getEvaluation().getAccuracy();

                Config.getReport().report(generationNo, train, train_acc, test, test_acc, myClock.GetTotalTime());

                if (false) // XXX turned off bigfile generation
                {
                    try {

                        Config.getReport().ReportExText(Config.toString()
                                + Eval.FullClassificationReport(
                                DataLoader.getTrainData(), (RuleSet) theBestIndividual, "TRAIN")
                                + Eval.FullClassificationReport(
                                DataLoader.getTestData(), (RuleSet) theBestIndividual, "TEST"));
                    } catch (IOException ex) {
                        Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                /////////////////////////////////////////////////////////////////
                if (TheBestOfTheBest == null) {
                    TheBestOfTheBest = new RuleSet((RuleSet) (theBestIndividual));
                    Eval.evaluate(DataLoader.getTestData(), TheBestOfTheBest);
                }
                if (TheBestOfTheBest.getEvaluation().getAccuracy() < theBestIndividual.getEvaluation().getAccuracy()) {
                    TheBestOfTheBest = new RuleSet((RuleSet) (theBestIndividual));
                    Eval.evaluate(DataLoader.getTestData(), TheBestOfTheBest);
                    Config.getReport().ConsoleReport(" <THE_BEST "
                            + String.format("%.3f", TheBestOfTheBest.getEvaluation().getAccuracy()));
                }
                ////////////////////////////////////////////////////////////////
            }
        }//END: CROSSVALIDATION CV TIMES
        reportAllToFile(Config, Eval, TheBestOfTheBest);
        //END: REPORTING into files-------------------------------------------------------------------------

    }

//------------------------------------------------------------------------------
    public String toSting() {
        /**
         * @todo insert code here
         */
        return "";
    }

//------------------------------------------------------------------------------
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.9B1B5C69-B3F8-1952-EE62-3C789905BD45]
    // </editor-fold> 
    public long getGeneration() {
        return this.generationNo;
    }
//------------------------------------------------------------------------------
}

