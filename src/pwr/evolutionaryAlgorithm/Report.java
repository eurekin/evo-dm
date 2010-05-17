package pwr.evolutionaryAlgorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.utils.Clock;

public class Report {

    private int testNumber;               // number of tests
    private String testFileReport;        // test file report
    private String testExFileReport;      // extended report filename
    private int testAllDone;              // number of all tests
    private long testSumGen;              // total number of generations
    private long testSumTime;             // total time
    private double testSumFitnessTrain;   // total fitness on train data
    private double testSumFitnessTest;    // total fitness on test data
    private double testSumAccuracyTest;
    private double testSumAccuracyTrain;
    private double[] testFitnessTest = null;
    private double[] testFitnessTrain = null;
    private double[] trainAccuracy = null;
    private double[] testAccuracy = null;
    private double[] testGen = null;
    private double[] testTime = null;

    Report(int testNumber, int crossvalidation, String testFileReport,
            String testExFileReport) {

        this.testNumber = testNumber;
        this.testFileReport = testFileReport;
        this.testExFileReport = testExFileReport;

        testAllDone = 0;              //number of all tests
        testSumGen = 0;               //total number of generations
        testSumTime = 0;              //total number of generations
        testSumFitnessTrain = 0;      //total number of fitness
        testSumFitnessTest = 0;       //total number of fitness
        testSumAccuracyTrain = 0;     //total number of fitness
        testSumAccuracyTest = 0;      //total number of fitness

        testFitnessTest = new double[testNumber * crossvalidation];
        testFitnessTrain = new double[testNumber * crossvalidation];
        testAccuracy = new double[testNumber * crossvalidation];
        trainAccuracy = new double[testNumber * crossvalidation];
        testGen = new double[testNumber * crossvalidation];
        testTime = new double[testNumber * crossvalidation];
    }

    public void addStatistics(long generations,
            float fitnessTrain,
            float fitnessTest,
            float train_acc,
            float test_acc,
            long Time) {

        testFitnessTest[testAllDone] = fitnessTest;
        testFitnessTrain[testAllDone] = fitnessTrain;
        testAccuracy[testAllDone] = test_acc;
        trainAccuracy[testAllDone] = train_acc;
        testGen[testAllDone] = generations;
        testTime[testAllDone] = Time;

        testAllDone++;
        testSumFitnessTrain += fitnessTrain;
        testSumFitnessTest += fitnessTest;
        testSumAccuracyTrain += train_acc;
        testSumAccuracyTest += test_acc;
        testSumTime += Time;
        testSumGen += generations;
// UNUSED
//        StringBuilder sb = new StringBuilder();
//        sb.append(ReportContent);
//        sb.append(num3(fitnessTrain));
//        sb.append(";").append(num3(fitnessTest));
//        sb.append(";").append(num3(train_acc));
//        sb.append(";").append(num3(test_acc));
//        sb.append(";").append(generations);
//        sb.append(";").append(Time).append(";;;;\n");
//        ReportContent = sb.toString();
    }

    /**
     * returns string of test (as String) 
     * @param Header 
     * @return 
     * @throws IOException
     */
    public String getReportStatistic(String Header)
            throws IOException {

        //////// SUMMARY REPORT
        long AvgTime = testSumTime / testAllDone;
        long AvgGen = testSumGen / testAllDone;
        double AvgTest = testSumFitnessTest / testAllDone;
        double AvgTrain = testSumFitnessTrain / testAllDone;
        double AvgTrainAcc = testSumAccuracyTrain / testAllDone;
        double AvgTestAcc = testSumAccuracyTest / testAllDone;

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
        for (int i = 0; i < testAllDone; i++) {
            if (i == 0) {
                TestMin = testFitnessTest[i];
                TestMax = testFitnessTest[i];
                TrainMax = testFitnessTrain[i];
                TrainMin = testFitnessTrain[i];
                TrainAccMax = trainAccuracy[i];
                TrainAccMin = trainAccuracy[i];
                TestAccMax = testAccuracy[i];
                TestAccMin = testAccuracy[i];
            }
            if (TestMin > testFitnessTest[i]) {
                TestMin = testFitnessTest[i];
            }
            if (TestMax < testFitnessTest[i]) {
                TestMax = testFitnessTest[i];
            }
            if (TrainMin > testFitnessTrain[i]) {
                TrainMin = testFitnessTrain[i];
            }
            if (TrainMax < testFitnessTrain[i]) {
                TrainMax = testFitnessTrain[i];
            }
            if (TrainAccMax < trainAccuracy[i]) {
                TrainAccMax = trainAccuracy[i];
            }
            if (TrainAccMin > trainAccuracy[i]) {
                TrainAccMin = trainAccuracy[i];
            }
            if (TestAccMax < testAccuracy[i]) {
                TestAccMax = testAccuracy[i];
            }
            if (TestAccMin > testAccuracy[i]) {
                TestAccMin = testAccuracy[i];
            }


            StdDevTest += (testFitnessTest[i] - AvgTest) * (testFitnessTest[i] - AvgTest);
            StdDevTrain += (testFitnessTrain[i] - AvgTrain) * (testFitnessTrain[i] - AvgTrain);
            StdDevTime += (testTime[i] - AvgTime) * (testTime[i] - AvgTime);
            StdDevGen += (testGen[i] - AvgGen) * (testGen[i] - AvgGen);
            StdDevTestAcc += (testAccuracy[i] - AvgTestAcc) * (testAccuracy[i] - AvgTestAcc);
            StdDevTrainAcc += (trainAccuracy[i] - AvgTrainAcc) * (trainAccuracy[i] - AvgTrainAcc);
        }
        StdDevTest = Math.sqrt(StdDevTest / testAllDone);
        StdDevTrain = Math.sqrt(StdDevTrain / testAllDone);
        StdDevTime = Math.sqrt(StdDevTime / testAllDone);
        StdDevGen = Math.sqrt(StdDevGen / testAllDone);
        StdDevTestAcc = Math.sqrt(StdDevTestAcc / testAllDone);
        StdDevTrainAcc = Math.sqrt(StdDevTrainAcc / testAllDone);
        ///////////////////////
        StringBuilder sb = new StringBuilder();

        sb.append(Configuration.getConfiguration().toString());
        sb.append("\n time avg ").append(AvgTime / 1000);
        sb.append("s std.dev").append(num4(StdDevTime / 1000)).append("s");
        sb.append(" gen ").append(AvgGen);
        sb.append(" (std.dev").append(num4(StdDevGen)).append(")");

        sb.append("\n [TRAIN] FSC <avg=").append(num4(AvgTrain));
        sb.append(" dev=").append(num4(StdDevTrain));
        sb.append(" min=").append(num4(TrainMin));
        sb.append(" max=").append(num4(TrainMax));
        sb.append(">");

        sb.append(" ACC <avg=").append(num4(AvgTrainAcc));
        sb.append(" dev=").append(num4(StdDevTrainAcc));
        sb.append(" min=").append(num4(TrainAccMin));
        sb.append(" max=").append(num4(TrainAccMax));
        sb.append(">");

        sb.append("\n [TEST] FSC <avg=").append(num4(AvgTest));
        sb.append(" dev=").append(num4(StdDevTest));
        sb.append(" min=").append(num4(TestMin));
        sb.append(" max=").append(num4(TestMax));
        sb.append(">");

        sb.append(" ACC <avg=").append(num4(AvgTestAcc));
        sb.append(" dev=").append(num4(StdDevTestAcc));
        sb.append(" min=").append(num4(TestAccMin));
        sb.append(" max=").append(num4(TestAccMax));
        sb.append(">");

        sb.append("\n===============================================================\n");

        return sb.toString();
    }

    private String num3(float train) {
        return String.format("%.3f", train);
    }

    private String num4(double numberToFormat) {
        return String.format("%.4f", numberToFormat);
    }

    public String getCSVReportStatistic(String Header) {

        //////// SUMMARY REPORT
        long avgTime = testSumTime / testAllDone;
        long avgGen = testSumGen / testAllDone;
        double avgTest = testSumFitnessTest / testAllDone;
        double avgTrain = testSumFitnessTrain / testAllDone;
        double avgTrainAcc = testSumAccuracyTrain / testAllDone;
        double avgTestAcc = testSumAccuracyTest / testAllDone;

        double testMin = 0;
        double testMax = 0;
        double trainMax = 0;
        double trainMin = 0;
        double trainAccMin = 0;
        double trainAccMax = 0;
        double testAccMin = 0;
        double testAccMax = 0;

        double stdDevTime = 0;
        double stdDevGen = 0;
        double stdDevTest = 0;
        double stdDevTrain = 0;
        double stdDevTrainAcc = 0;
        double stdDevTestAcc = 0;

        //Standard deviation
        for (int i = 0; i < testAllDone; i++) {
            if (i == 0) {
                testMin = testFitnessTest[i];
                testMax = testFitnessTest[i];
                trainMax = testFitnessTrain[i];
                trainMin = testFitnessTrain[i];
                trainAccMax = trainAccuracy[i];
                trainAccMin = trainAccuracy[i];
                testAccMax = testAccuracy[i];
                testAccMin = testAccuracy[i];
            }
            if (testMin > testFitnessTest[i]) {
                testMin = testFitnessTest[i];
            }
            if (testMax < testFitnessTest[i]) {
                testMax = testFitnessTest[i];
            }
            if (trainMin > testFitnessTrain[i]) {
                trainMin = testFitnessTrain[i];
            }
            if (trainMax < testFitnessTrain[i]) {
                trainMax = testFitnessTrain[i];
            }
            if (trainAccMax < trainAccuracy[i]) {
                trainAccMax = trainAccuracy[i];
            }
            if (trainAccMin > trainAccuracy[i]) {
                trainAccMin = trainAccuracy[i];
            }
            if (testAccMax < testAccuracy[i]) {
                testAccMax = testAccuracy[i];
            }
            if (testAccMin > testAccuracy[i]) {
                testAccMin = testAccuracy[i];
            }


            stdDevTest += (testFitnessTest[i] - avgTest) * (testFitnessTest[i] - avgTest);
            stdDevTrain += (testFitnessTrain[i] - avgTrain) * (testFitnessTrain[i] - avgTrain);
            stdDevTime += (testTime[i] - avgTime) * (testTime[i] - avgTime);
            stdDevGen += (testGen[i] - avgGen) * (testGen[i] - avgGen);
            stdDevTestAcc += (testAccuracy[i] - avgTestAcc) * (testAccuracy[i] - avgTestAcc);
            stdDevTrainAcc += (trainAccuracy[i] - avgTrainAcc) * (trainAccuracy[i] - avgTrainAcc);
        }
        stdDevTest = Math.sqrt(stdDevTest / testAllDone);
        stdDevTrain = Math.sqrt(stdDevTrain / testAllDone);
        stdDevTime = Math.sqrt(stdDevTime / testAllDone);
        stdDevGen = Math.sqrt(stdDevGen / testAllDone);
        stdDevTestAcc = Math.sqrt(stdDevTestAcc / testAllDone);
        stdDevTrainAcc = Math.sqrt(stdDevTrainAcc / testAllDone);
        ///////////////////////
        StringBuilder csv = new StringBuilder();

        // Template:
        // CSV.append("configuration;time_avg;time_dev;gen_avg;gen_dev;");
        // CSV.append("tr_fsc_avg;tr_fsc_dev;tr_fsc_min;tr_fsc_max;");
        // CSV.append("tr_acc_avg;tr_acc_dev;tr_acc_min;tr_acc_max;");
        // CSV.append("tst_fsc_avg;tst_fsc_dev;tst_fsc_min;tst_fsc_max;");
        // CSV.append("tst_acc_avg;tst_acc_dev;tst_acc_min;tst_acc_max;\n");

        csv.append(Configuration.getConfiguration().toCSVString());
        csv.append(';').append(avgTime / 1000);
        csv.append(";").append(num4(stdDevTime / 1000));
        csv.append(";").append(avgGen);
        csv.append(";").append(num4(stdDevGen));
        csv.append(";").append(avgTrain);
        csv.append(";").append(num4(stdDevTrain));
        csv.append(";").append(num4(trainMin));
        csv.append(";").append(num4(trainMax));
        csv.append(";").append(num4(avgTrainAcc));
        csv.append(";").append(num4(stdDevTrainAcc));
        csv.append(";").append(num4(trainAccMin));
        csv.append(";").append(num4(trainAccMax));
        csv.append(";").append(num4(avgTest));
        csv.append(";").append(num4(stdDevTest));
        csv.append(";").append(num4(testMin));
        csv.append(";").append(num4(testMax));
        csv.append(";").append(num4(avgTestAcc));
        csv.append(";").append(num4(stdDevTestAcc));
        csv.append(";").append(num4(testAccMin));
        csv.append(";").append(num4(testAccMax));
        csv.append(";");

        return csv.toString().replace('.', ',');
    }

    public void appendCSVReportLineToFile(String csvLine) {
        String filename = testFileReport;
        filename = filename.replace(".txt", ".csv");
        if (!filename.endsWith(".csv")) {
            filename += ".csv";
        }
        appendTextToFile(csvLine, filename);
    }

    public void appendTextToFile(String str, String filename) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename, true);
            fw.write(str);
            fw.write("\n");
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Saves {@code report} string to report file.
     * Adds new line character after the string.
     *
     * @param report to write
     */
    public void reportlnText(String report) {
        appendTextToFile(report, testFileReport);
    }

    /**
     * Saves {@code report} string to extended report file.
     * Adds new line character after the string.
     * 
     * @param report to write
     */
    public void reportlnExText(String report) {
        appendTextToFile(report, testExFileReport);
    }

    public void extendedReport(Configuration config, Evaluator eval, RuleSet best) {
        if (false) {
            // XXX turned off bigfile generation
            final DataSource trainData = DataLoader.getTrainData();
            final DataSource testData = DataLoader.getTestData();
            reportlnExText(config.toString()
                    + fullClassificationReport(trainData, best, "TRAIN")
                    + fullClassificationReport(testData, best, "TEST"));

        }
    }

    public int getTestNumber() {
        return testNumber;
    }

    public int getAllDone() {
        return testAllDone;
    }

    public void consoleReport(String S) {
        System.out.print(S);
    }

    public void consoleReport(
            float train,
            float train_acc,
            float test,
            float test_acc,
            long time) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n [");
        sb.append(testAllDone);
        sb.append("]. train (Fsc=").append(num3(train));
        sb.append(" acc=").append(num3(train_acc));
        sb.append(")  test (Fsc=").append(num3(test));
        sb.append(" acc=").append(num3(test_acc));
        sb.append(") time=").append(num3((float) (time / 1000.0)));
        sb.append("s");
        consoleReport(sb.toString());
    }

    public void appendCSVLine(
            long generationNo,
            float train,
            float trainAcc,
            float test,
            float testAcc,
            long totalTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(Configuration.getConfiguration().toCSVString());
        sb.append(';').append(generationNo);
        sb.append(";").append(train);
        sb.append(";").append(trainAcc);
        sb.append(";").append(test);
        sb.append(";").append(testAcc);
        sb.append(";").append((((double) totalTime) / 1000.0d));
        String result = sb.toString().replace('.', ',');
        appendTextToFile(result, "detailed.csv");
    }

    public void report(
            long generationNo,
            float train,
            float trainAcc,
            float test,
            float testAcc,
            long totalTime) {
        consoleReport(train, trainAcc, test, testAcc, totalTime);
        appendCSVLine(generationNo, train, trainAcc, test, testAcc, totalTime);
        addStatistics(generationNo, train, test, trainAcc, testAcc, totalTime);
    }

    /**
     *
     * @param best
     * @param pop
     * @param generations
     */
    public void reportAfterOneGeneration(Individual best,
            Population<? extends Individual> pop, long generations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(generations).append(";");
        sb.append(String.format("%.3f;", best.getFitness()));
        sb.append(String.format("%.3f;", best.getAccuracy()));
        sb.append(String.format("%.3f;", pop.getAvgFitness()));
        sb.append(num3(pop.getWorstFitness()));
        consoleReport(sb.toString().replace(".", ","));
    }

    public void reportBestInd(Individual theBestOfTheBest) {
        consoleReport(" <THE_BEST " + num3(theBestOfTheBest.getAccuracy()));
    }

    public void indicateCrossvalidationFold(int no) {
        consoleReport("\n\n CROSSVALIDATION " + no + "\n");
    }

    public void reportAllToFile(Configuration config, Evaluator eval,
            Individual theBestOfTheBest, Clock totalTimeClock) {
        final Report report = config.getReport();

        // String R = Config.getReport().getReportStatistic(
        // Configuration.getConfiguration().toString(), true);
        String CSV = report.getCSVReportStatistic(config.toString());
        CSV += String.format("%.3f", totalTimeClock.GetTotalTime() / 1000.0d);
        //            Config.getReport().ReportText(R);
        //            Config.getReport().ConsoleReport(R);
        report.appendCSVReportLineToFile(CSV);

        if (false && theBestOfTheBest instanceof RuleSet) {

            final DataSource testData = DataLoader.getTestData();
            final DataSource trainData = DataLoader.getTrainData();
            StringBuilder sb = new StringBuilder();

            theBestOfTheBest.evaluate(testData);
            sb.append(config.toString());
            sb.append(fullClassificationReport(trainData, theBestOfTheBest, "TRAIN"));
            sb.append(fullClassificationReport(testData, theBestOfTheBest, "TEST"));

            report.reportlnExText(sb.toString());
        }
    }

    /**
     * Return as string Report of classification of selected RuleSet
     * @param dSrc dataSource (Train or Test) as dataScurce
     * @param ind
     * @param text
     * @return string for report
     */
    public static String fullClassificationReport(DataSource dSrc,
            Individual ind, String text) {

        StringBuilder sb = new StringBuilder();
        ind.evaluate(dSrc);

        sb.append("\n############################ ");
        sb.append(text).append("############################\n");
        sb.append("\n").append(ind.toString());
        sb.append("\n\n").append(text).append("_DATASOURCE  ");
        sb.append(dSrc.toString());
        sb.append("\n##############################");
        sb.append("####################################\n");

        return sb.toString();
    }

    public void evoAlgInitStart(String prompt) {
        System.out.print(prompt + "initalizing...");
    }

    public void evoAlgInitStop(String prompt, String fileSummary) {
        System.out.println("done!" + prompt + fileSummary);
    }
}
