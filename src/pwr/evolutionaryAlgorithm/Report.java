package pwr.evolutionaryAlgorithm;

import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import pwr.evolutionaryAlgorithm.utils.Clock;

public class Report implements Serializable {

    private String ReportContent;
    private int TestNumber;             //number of tests
    private String TestFileReport;   //test file report
    private String TestExFileReport;
    private int TestAllDone;              //number of all tests
    private long TestSumGen;          //total number of generations
    private long TestSumTime;          //total number of generations
    private double TestSumFitnessTrain; //total number of fitness
    private double TestSumFitnessTest; //total number of fitness
    private double TestSumAccuracyTest;
    private double TestSumAccuracyTrain;
    private double[] TestFitnessTest = null;
    private double[] TestFitnessTrain = null;
    private double[] TrainAccuracy = null;
    private double[] TestAccuracy = null;
    private double[] TestGen = null;
    private double[] TestTime = null;

    Report(int _TestNumber, int _crossvalidation, String _TestFileReport, String _TestExFileReport) {
        ReportContent = new String();

        this.TestNumber = _TestNumber;
        this.TestFileReport = new String(_TestFileReport);
        this.TestExFileReport = new String(_TestExFileReport);

        /////////////////////////////////////////////////////
        TestAllDone = 0;              //number of all tests
        TestSumGen = 0;          //total number of generations
        TestSumTime = 0;          //total number of generations
        TestSumFitnessTrain = 0; //total number of fitness
        TestSumFitnessTest = 0; //total number of fitness
        TestSumAccuracyTrain = 0; //total number of fitness
        TestSumAccuracyTest = 0; //total number of fitness


        /**/
        TestFitnessTest = new double[TestNumber * _crossvalidation];
        TestFitnessTrain = new double[TestNumber * _crossvalidation];
        TestAccuracy = new double[TestNumber * _crossvalidation];
        TrainAccuracy = new double[TestNumber * _crossvalidation];
        TestGen = new double[TestNumber * _crossvalidation];
        TestTime = new double[TestNumber * _crossvalidation];
    }

//------------------------------------------------------------------------------
    public void addStatistics(long generations, float fitnessTrain, float fitnessTest, float train_acc, float test_acc, long Time) {
        TestFitnessTest[TestAllDone] = fitnessTest;
        TestFitnessTrain[TestAllDone] = fitnessTrain;
        TestAccuracy[TestAllDone] = test_acc;
        TrainAccuracy[TestAllDone] = train_acc;
        TestGen[TestAllDone] = generations;
        TestTime[TestAllDone] = Time;

        TestAllDone++;
        TestSumFitnessTrain = TestSumFitnessTrain + fitnessTrain;
        TestSumFitnessTest = TestSumFitnessTest + fitnessTest;
        TestSumAccuracyTrain = TestSumAccuracyTrain + train_acc;
        TestSumAccuracyTest = TestSumAccuracyTest + test_acc;
        TestSumTime = TestSumTime + Time;
        TestSumGen = TestSumGen + generations;
        StringBuilder SB = new StringBuilder();
        SB.append(ReportContent);
        SB.append("" + String.format("%.3f", fitnessTrain) + ";"
                + String.format("%.3f", fitnessTest) + ";"
                + String.format("%.3f", train_acc) + ";"
                + String.format("%.3f", test_acc) + ";"
                + generations + ";" + Time + ";;;;\n");
        ReportContent = SB.toString();
    }

//	------------------------------------------------------------------------------
    /**
     * returns string of test (as String) and writes it info file (if second is true)
     */
    public String getReportStatistic(String Header, boolean writeIntoFile) throws IOException {

        //////// SUMMARY REPORT
        long AvgTime = TestSumTime / TestAllDone;
        long AvgGen = TestSumGen / TestAllDone;
        double AvgTest = TestSumFitnessTest / TestAllDone;
        double AvgTrain = TestSumFitnessTrain / TestAllDone;
        double AvgTrainAcc = TestSumAccuracyTrain / TestAllDone;
        double AvgTestAcc = TestSumAccuracyTest / TestAllDone;

        double TestMin = 0;
        double TestMax = 0;
        double TrainMax = 0;
        double TrainMin = 0;
        double TrainAccMin = 0;
        double TrainAccMax = 0;
        double TestAccMin = 0;
        double TestAccMax = 0;

        double StdDevTime = 0;
        double StdDevGen = 0;
        double StdDevTest = 0;
        double StdDevTrain = 0;
        double StdDevTrainAcc = 0;
        double StdDevTestAcc = 0;

        //Standard deviation
        for (int i = 0; i < TestAllDone; i++) {
            if (i == 0) {
                TestMin = TestFitnessTest[i];
                TestMax = TestFitnessTest[i];
                TrainMax = TestFitnessTrain[i];
                TrainMin = TestFitnessTrain[i];
                TrainAccMax = TrainAccuracy[i];
                TrainAccMin = TrainAccuracy[i];
                TestAccMax = TestAccuracy[i];
                TestAccMin = TestAccuracy[i];
            }
            if (TestMin > TestFitnessTest[i]) {
                TestMin = TestFitnessTest[i];
            }
            if (TestMax < TestFitnessTest[i]) {
                TestMax = TestFitnessTest[i];
            }
            if (TrainMin > TestFitnessTrain[i]) {
                TrainMin = TestFitnessTrain[i];
            }
            if (TrainMax < TestFitnessTrain[i]) {
                TrainMax = TestFitnessTrain[i];
            }
            if (TrainAccMax < TrainAccuracy[i]) {
                TrainAccMax = TrainAccuracy[i];
            }
            if (TrainAccMin > TrainAccuracy[i]) {
                TrainAccMin = TrainAccuracy[i];
            }
            if (TestAccMax < TestAccuracy[i]) {
                TestAccMax = TestAccuracy[i];
            }
            if (TestAccMin > TestAccuracy[i]) {
                TestAccMin = TestAccuracy[i];
            }


            StdDevTest = StdDevTest + (TestFitnessTest[i] - AvgTest) * (TestFitnessTest[i] - AvgTest);
            StdDevTrain = StdDevTrain + (TestFitnessTrain[i] - AvgTrain) * (TestFitnessTrain[i] - AvgTrain);
            StdDevTime = StdDevTime + (TestTime[i] - AvgTime) * (TestTime[i] - AvgTime);
            StdDevGen = StdDevGen + (TestGen[i] - AvgGen) * (TestGen[i] - AvgGen);
            StdDevTestAcc = StdDevTestAcc + (TestAccuracy[i] - AvgTestAcc) * (TestAccuracy[i] - AvgTestAcc);
            StdDevTrainAcc = StdDevTrainAcc + (TrainAccuracy[i] - AvgTrainAcc) * (TrainAccuracy[i] - AvgTrainAcc);
        }
        StdDevTest = Math.sqrt(StdDevTest / TestAllDone);
        StdDevTrain = Math.sqrt(StdDevTrain / TestAllDone);
        StdDevTime = Math.sqrt(StdDevTime / TestAllDone);
        StdDevGen = Math.sqrt(StdDevGen / TestAllDone);
        StdDevTestAcc = Math.sqrt(StdDevTestAcc / TestAllDone);
        StdDevTrainAcc = Math.sqrt(StdDevTrainAcc / TestAllDone);
        ///////////////////////
        StringBuilder SB = new StringBuilder();

        SB.append(Configuration.getConfiguration().toString());

        SB.append("\n time avg " + AvgTime / 1000 + "s std.dev" + String.format("%.4f", StdDevTime / 1000) + "s");
        SB.append(" gen " + AvgGen + " (std.dev" + String.format("%.4f", StdDevGen) + ")");
        SB.append("\n [TRAIN] FSC <avg=" + String.format("%.4f", AvgTrain)
                + " dev=" + String.format("%.4f", StdDevTrain)
                + " min=" + String.format("%.4f", TrainMin)
                + " max=" + String.format("%.4f", TrainMax) + ">");
        SB.append(" ACC <avg=" + String.format("%.4f", AvgTrainAcc)
                + " dev=" + String.format("%.4f", StdDevTrainAcc)
                + " min=" + String.format("%.4f", TrainAccMin)
                + " max=" + String.format("%.4f", TrainAccMax) + ">");
        SB.append("\n [TEST] FSC <avg=" + String.format("%.4f", AvgTest)
                + " dev=" + String.format("%.4f", StdDevTest)
                + " min=" + String.format("%.4f", TestMin)
                + " max=" + String.format("%.4f", TestMax) + ">");
        SB.append(" ACC <avg=" + String.format("%.4f", AvgTestAcc)
                + " dev=" + String.format("%.4f", StdDevTestAcc)
                + " min=" + String.format("%.4f", TestAccMin)
                + " max=" + String.format("%.4f", TestAccMax) + ">");
        SB.append("\n===============================================================\n");

        return SB.toString();
    }

//---------------------------------------------------------------------------------------------------------
    public String getCSVReportStatistic(String Header, boolean writeIntoFile) throws IOException {

        //////// SUMMARY REPORT
        long AvgTime = TestSumTime / TestAllDone;
        long AvgGen = TestSumGen / TestAllDone;
        double AvgTest = TestSumFitnessTest / TestAllDone;
        double AvgTrain = TestSumFitnessTrain / TestAllDone;
        double AvgTrainAcc = TestSumAccuracyTrain / TestAllDone;
        double AvgTestAcc = TestSumAccuracyTest / TestAllDone;

        double TestMin = 0;
        double TestMax = 0;
        double TrainMax = 0;
        double TrainMin = 0;
        double TrainAccMin = 0;
        double TrainAccMax = 0;
        double TestAccMin = 0;
        double TestAccMax = 0;

        double StdDevTime = 0;
        double StdDevGen = 0;
        double StdDevTest = 0;
        double StdDevTrain = 0;
        double StdDevTrainAcc = 0;
        double StdDevTestAcc = 0;

        //Standard deviation
        for (int i = 0; i < TestAllDone; i++) {
            if (i == 0) {
                TestMin = TestFitnessTest[i];
                TestMax = TestFitnessTest[i];
                TrainMax = TestFitnessTrain[i];
                TrainMin = TestFitnessTrain[i];
                TrainAccMax = TrainAccuracy[i];
                TrainAccMin = TrainAccuracy[i];
                TestAccMax = TestAccuracy[i];
                TestAccMin = TestAccuracy[i];
            }
            if (TestMin > TestFitnessTest[i]) {
                TestMin = TestFitnessTest[i];
            }
            if (TestMax < TestFitnessTest[i]) {
                TestMax = TestFitnessTest[i];
            }
            if (TrainMin > TestFitnessTrain[i]) {
                TrainMin = TestFitnessTrain[i];
            }
            if (TrainMax < TestFitnessTrain[i]) {
                TrainMax = TestFitnessTrain[i];
            }
            if (TrainAccMax < TrainAccuracy[i]) {
                TrainAccMax = TrainAccuracy[i];
            }
            if (TrainAccMin > TrainAccuracy[i]) {
                TrainAccMin = TrainAccuracy[i];
            }
            if (TestAccMax < TestAccuracy[i]) {
                TestAccMax = TestAccuracy[i];
            }
            if (TestAccMin > TestAccuracy[i]) {
                TestAccMin = TestAccuracy[i];
            }


            StdDevTest = StdDevTest + (TestFitnessTest[i] - AvgTest) * (TestFitnessTest[i] - AvgTest);
            StdDevTrain = StdDevTrain + (TestFitnessTrain[i] - AvgTrain) * (TestFitnessTrain[i] - AvgTrain);
            StdDevTime = StdDevTime + (TestTime[i] - AvgTime) * (TestTime[i] - AvgTime);
            StdDevGen = StdDevGen + (TestGen[i] - AvgGen) * (TestGen[i] - AvgGen);
            StdDevTestAcc = StdDevTestAcc + (TestAccuracy[i] - AvgTestAcc) * (TestAccuracy[i] - AvgTestAcc);
            StdDevTrainAcc = StdDevTrainAcc + (TrainAccuracy[i] - AvgTrainAcc) * (TrainAccuracy[i] - AvgTrainAcc);
        }
        StdDevTest = Math.sqrt(StdDevTest / TestAllDone);
        StdDevTrain = Math.sqrt(StdDevTrain / TestAllDone);
        StdDevTime = Math.sqrt(StdDevTime / TestAllDone);
        StdDevGen = Math.sqrt(StdDevGen / TestAllDone);
        StdDevTestAcc = Math.sqrt(StdDevTestAcc / TestAllDone);
        StdDevTrainAcc = Math.sqrt(StdDevTrainAcc / TestAllDone);
        ///////////////////////
        StringBuilder CSV = new StringBuilder();

//        CSV.append("configuration;time_avg;time_dev;gen_avg;gen_dev;");
//        CSV.append("tr_fsc_avg;tr_fsc_dev;tr_fsc_min;tr_fsc_max;");
//        CSV.append("tr_acc_avg;tr_acc_dev;tr_acc_min;tr_acc_max;");
//        CSV.append("tst_fsc_avg;tst_fsc_dev;tst_fsc_min;tst_fsc_max;");
//        CSV.append("tst_acc_avg;tst_acc_dev;tst_acc_min;tst_acc_max;\n");

        CSV.append(Configuration.getConfiguration().toCSVString() + ';');
        CSV.append((AvgTime / 1000) + ";" + String.format("%.4f", StdDevTime / 1000) + ";" + AvgGen + ";" + String.format("%.4f", StdDevGen) + ";");
        CSV.append(AvgTrain + ";" + String.format("%.4f", StdDevTrain) + ";" + String.format("%.4f", TrainMin) + ";" + String.format("%.4f", TrainMax) + ";");
        CSV.append(String.format("%.4f", AvgTrainAcc) + ";" + String.format("%.4f", StdDevTrainAcc) + ";" + String.format("%.4f", TrainAccMin) + ";" + String.format("%.4f", TrainAccMax) + ";");
        CSV.append(String.format("%.4f", AvgTest) + ";" + String.format("%.4f", StdDevTest) + ";" + String.format("%.4f", TestMin) + ";" + String.format("%.4f", TestMax) + ";");
        CSV.append(String.format("%.4f", AvgTestAcc) + ";" + String.format("%.4f", StdDevTestAcc) + ";" + String.format("%.4f", TestAccMin) + ";" + String.format("%.4f", TestAccMax) + ";");

        return CSV.toString().replace('.', ',');
    }

    public void AppendCSVReportLineToFile(String S) {
        String filename = TestFileReport;
        filename = filename.replace(".txt", ".csv");
        if (!filename.endsWith(".csv")) {
            filename = filename + ".csv";
        }
        AppendCSVReportLineToFile(S, filename);
    }

    public void AppendCSVReportLineToFile(String S, String filename) {
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(filename, true);
            f.write(S.getBytes());
            f.write("\n".getBytes());
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ReportText(String S) throws IOException {
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(TestFileReport, true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        f.write(S.getBytes());
        f.write("\n".getBytes());
        f.close();
    }

//---------------------------------------------------------------------------------------------------------
    public void reportExText(String S) throws IOException {
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(TestExFileReport, true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        f.write(S.getBytes());
        f.write("\n".getBytes());
        f.close();
    }

    void extendedReport(Configuration config, Evaluator eval, RuleSet theBestInd) {
        if (false) {
            // XXX turned off bigfile generation
            try {
                reportExText(config.toString()
                        + eval.FullClassificationReport(DataLoader.getTrainData(), (RuleSet) theBestInd, "TRAIN")
                        + eval.FullClassificationReport(DataLoader.getTestData(), (RuleSet) theBestInd, "TEST"));
            } catch (IOException ex) {
                Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
//---------------------------------------------------------------------------------------------------------
    public int getTestNumber() {
        return TestNumber;
    }

//------------------------------------------------------------------------------
    public int getAllDone() {
        return TestAllDone;
    }

//------------------------------------------------------------------------------
    public void consoleReport(String S) {
        System.out.print(S);
    }

    public void consoleString(float train, float train_acc, float test, float test_acc, long time) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n [");
        sb.append(TestAllDone);
        sb.append("]. train (Fsc=");
        sb.append(String.format("%.3f", train));
        sb.append(" acc=");
        sb.append(String.format("%.3f", train_acc));
        sb.append(")  test (Fsc=");
        sb.append(String.format("%.3f", test));
        sb.append(" acc=");
        sb.append(String.format("%.3f", test_acc));
        sb.append(") time=");
        sb.append(String.format("%.3f", time / 1000.0));
        sb.append("s");
        consoleReport(sb.toString());
    }

    void addCSVLine(long generationNo, float train, float train_acc, float test, float test_acc, long total_time) {
        StringBuilder sb = new StringBuilder();
        sb.append(Configuration.getConfiguration().toCSVString());
        sb.append(';');
        sb.append(generationNo);
        sb.append(";");
        sb.append(train);
        sb.append(";");
        sb.append(train_acc);
        sb.append(";");
        sb.append(test);
        sb.append(";");
        sb.append(test_acc);
        sb.append(";");
        sb.append((((double) total_time) / 1000.0d));
        AppendCSVReportLineToFile(sb.toString().replace('.', ','), "detailed.csv");
    }

    public void report(long generationNo, float train, float train_acc, float test, float test_acc, long getTotalTime) {
        consoleString(train, train_acc, test, test_acc, getTotalTime);
        addCSVLine(generationNo, train, train_acc, test, test_acc, getTotalTime);
        addStatistics(generationNo, train, test, train_acc, test_acc, getTotalTime);
    }

    void reportAfterOneGeneration(Individual theBestIndividual,
            Population mRulePopulation, long generationNo) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(generationNo);
        sb.append(";");
        sb.append(String.format("%.3f", theBestIndividual.getEvaluation().getFitness()));
        sb.append(";");
        sb.append(String.format("%.3f", theBestIndividual.getEvaluation().getAccuracy()));
        sb.append(";");
        sb.append(String.format("%.3f", mRulePopulation.getAvgFitness()));
        sb.append(";");
        sb.append(String.format("%.3f", mRulePopulation.getWorstFitness()));
        consoleReport(sb.toString().replace(".", ","));
    }

    void reportBestInd(Individual theBestOfTheBest) {
        consoleReport(" <THE_BEST " + String.format("%.3f", theBestOfTheBest.getEvaluation().getAccuracy()));
    }

    void indicateCrossvalidationFold(int no) {
        consoleReport("\n\n CROSSVALIDATION " + no + "\n");
    }

    void reportAllToFile(Configuration config, Evaluator eval, Individual theBestOfTheBest, Clock totalTimeClock) {
        final Report report = config.getReport();
        try {
            // String R = Config.getReport().getReportStatistic(Configuration.getConfiguration().toString(), true);
            String CSV = report.getCSVReportStatistic(Configuration.getConfiguration().toString(), true);
            CSV = CSV + String.format("%.3f", totalTimeClock.GetTotalTime() / 1000.0d);
            //            Config.getReport().ReportText(R);
            //            Config.getReport().ConsoleReport(R);
            report.AppendCSVReportLineToFile(CSV);
        } catch (IOException ex) {
            Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (false && theBestOfTheBest instanceof RuleSet) {
            try {
                //Eval.Evaluate(DataLoader.getTestData(), TheBestOfTheBest);
                report.reportExText(config.toString()
                        + eval.FullClassificationReport(DataLoader.getTrainData(),
                        (RuleSet) theBestOfTheBest, "TRAIN")
                        + eval.FullClassificationReport(DataLoader.getTestData(),
                        (RuleSet) theBestOfTheBest, "TEST"));
            } catch (IOException ex) {
                Logger.getLogger(EvoAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void evoAlgInitStart(String prompt) {
        System.out.print(prompt + "initalising...");
    }

    void evoAlgInitStop(String prompt, String fileSummary) {
        System.out.println("done!" + prompt + fileSummary);
    }
}
