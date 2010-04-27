// #[regen=yes,id=DCE.CDAF4B1A-454B-95E9-E711-63B839E7F67E]
//import Individual;
package EvolutionaryAlgorithm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import EvolutionaryAlgorithm.Individual.*;
import EvolutionaryAlgorithm.Population.diversity;

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

            ///this.mDataLoader = new DataLoader()
            /**
             * todo: only ECCV_2002 -- insert universal code here
             */
            this.mDataLoader = new DataLoader(config.getImageWordsFilename(),
                    config.getImageDocWordFilename(),
                    config.getImageBlobCountsFilename(),
                    config.getImageBlobsFilename(),
                    config.getImageTESTDocWordsFilename(),
                    config.getImageTESTBlobCountsFilename(),
                    config.getImageTESTBlobsFilename());
        } else {
            this.mDataLoader = new DataLoader(Configuration.getConfiguration().getTrainFileName(), Configuration.getConfiguration().getTestFileName());
        }

        this.myClock = new Clock();
        this.theBestIndividual = new RuleSet();
        System.out.print("done!");
        System.out.print(Configuration.getConfiguration().getPrompt() + DataLoader.FileSummary() + "\n");
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
                this.myClock.Reset();
                this.myClock.Start();
                totalTimeClock.Start();
                this.mRulePopulation = new Population(new RuleSet());
                this.mRulePopulation.Initialise();
                this.theBestIndividual = null;
                boolean StopEval = false;
                this.generationNo = 0;

                this.mRulePopulation.Evaluate(DataLoader.getTrainData());
                this.updateTheBestIndividual(this.mRulePopulation.getBestIndividual());

                //EA works....
                while (StopEval == false && Config.getStopGeneration() != this.generationNo) {
                    /*new generation*/
                    Population nRulePop = this.mRulePopulation.generate(DataLoader.getTrainData());

                    this.mRulePopulation = nRulePop;
                    this.generationNo++;
                    this.mRulePopulation.Evaluate(DataLoader.getTrainData());

                    //the best individual?
                    float f = this.mRulePopulation.getBestFitness();
                    if (this.isTheBestIndividual(f)) {
                        this.updateTheBestIndividual(this.mRulePopulation.getBestIndividual());
                    }

                    if (Configuration.getConfiguration().getStopEval() <= this.mRulePopulation.getBestFitness()) {
                        StopEval = true;
                        break;
                    }

                    if (Config.isEcho()) {

                        StringBuilder SB = new StringBuilder("\n" + generationNo + ";" + String.format("%.3f", this.theBestIndividual.getEvaluation().getFitness())
                                + ";" + String.format("%.3f", this.theBestIndividual.getEvaluation().getAccuracy())
                                + ";" + String.format("%.3f", this.mRulePopulation.getAvgFitness())
                                + ";" + String.format("%.3f", this.mRulePopulation.getWorstFitness()));

                        //diversity d = this.mRulePopulation.getDiversity();
                        //SB.append(";"+Integer.toString(d.diff)+";"+Integer.toString(d.clones));
                        Config.getReport().ConsoleReport(SB.toString().replace(".", ","));
                    }
                }//END: EA works

                //REPORTS.... XXX to powinno się znaleźć w odpowiedniej metodzie
                // odpowiedniego obiektu
                this.myClock.Pause();
                totalTimeClock.Pause();

                Eval.Evaluate(DataLoader.getTrainData(), this.theBestIndividual);

                float train = this.theBestIndividual.getEvaluation().getFsc();
                float train_acc = this.theBestIndividual.getEvaluation().getAccuracy();

                Eval.Evaluate(DataLoader.getTestData(), this.theBestIndividual);
                float test = this.theBestIndividual.getEvaluation().getFsc();
                float test_acc = this.theBestIndividual.getEvaluation().getAccuracy();

                Config.getReport().ConsoleReport("\n ["
                        + Config.getReport().getAllDone()
                        + "]. train (Fsc=" + String.format("%.3f", train)
                        + " acc=" + String.format("%.3f", train_acc)
                        + ")  test (Fsc=" + String.format("%.3f", test)
                        + " acc=" + String.format("%.3f", test_acc)
                        + ") time=" + String.format("%.3f", (this.myClock.GetTotalTime() / 1000.0)) + "s");

                Config.getReport().addCSVLine(generationNo, train, train_acc, test, test_acc, this.myClock.GetTotalTime() );
                Config.getReport().AddStatistics(generationNo, train, test, train_acc, test_acc, this.myClock.GetTotalTime());

                if (false) // XXX turned off bigfile generation
                try {

                    Config.getReport().ReportExText(Config.toString()
                            + Eval.FullClassificationReport(DataLoader.getTrainData(), (RuleSet) this.theBestIndividual, "TRAIN") /*TODO REMOVE IT!*/
                            + Eval.FullClassificationReport(DataLoader.getTestData(), (RuleSet) this.theBestIndividual, "TEST"));
                } catch (IOException ex) {
                    Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                }

                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (TheBestOfTheBest == null) {
                    TheBestOfTheBest = new RuleSet((RuleSet) (this.theBestIndividual));
                    Eval.Evaluate(DataLoader.getTestData(), TheBestOfTheBest);
                }
                if (TheBestOfTheBest.getEvaluation().getAccuracy() < this.theBestIndividual.getEvaluation().getAccuracy()) {
                    TheBestOfTheBest = new RuleSet((RuleSet) (this.theBestIndividual));
                    Eval.Evaluate(DataLoader.getTestData(), TheBestOfTheBest);
                    Config.getReport().ConsoleReport(" <THE_BEST " + String.format("%.3f", TheBestOfTheBest.getEvaluation().getAccuracy()));
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            } //END: EA - run N times

        }//END: CROSSVALIDATION CV TIMES

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

        ////// THE BEST of THE BEST LOG
        if (false) // XXX turned off bigfile generation
        try {
            //Eval.Evaluate(DataLoader.getTestData(), TheBestOfTheBest);
            Config.getReport().ReportExText(Config.toString()
                    + Eval.FullClassificationReport(DataLoader.getTrainData(), (RuleSet) TheBestOfTheBest, "TRAIN")
                    + Eval.FullClassificationReport(DataLoader.getTestData(), (RuleSet) TheBestOfTheBest, "TEST"));
        } catch (IOException ex) {
            Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
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

