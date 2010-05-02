
import EvolutionaryAlgorithm.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import utils.Clock;
//import java.awt.Toolkit;

/**
 *
 * @author pawelm
 */
public class Main {

    /**
     * main function of
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // XXX CSV report HEADER: comment;file_comment;system;pop_size;stop_gen;rule_no;cross_value;measure;mutation_val;mutation_type;crossover_value;crossover_type;selection;configuration;time_avg;time_dev;gen_avg;gen_dev;tr_fsc_avg;tr_fsc_dev;tr_fsc_min;tr_fsc_max;tr_acc_avg;tr_acc_dev;tr_acc_min;tr_acc_max;tst_fsc_avg;tst_fsc_dev;tst_fsc_min;tst_fsc_max;tst_acc_avg;tst_acc_dev;tst_acc_min;tst_acc_max;time
        // XXX CSV detail HEADER:comment;file_comment;system;pop_size;stop_gen;rule_no;cross_value;measure;mutation_val;mutation_type;crossover_value;crossover_type;selection;configuration;generations;train_fsc;train_acc;test_fsc;test_acc;time


        // Struktury
        EvoAlgorithm e;
        Configuration.newConfiguration("_iris", "Finaru tesuto");
        Configuration config = Configuration.getConfiguration();

        // Dane
        float testedParameterMinRange = 0.0f;
        float testedParameterMaxRange = 1.0f;
        String startTimeStr = (new Date()).toString();

        // test range
        double MAX = 1.0d;
        List<Double> testValues = new ArrayList<Double>();
//        List<Double> testValuesA = new ArrayList();
//        List<Double> testValuesB = new ArrayList();
//        testValues.addAll(Arrays.asList(new Integer[]{0, 1, 2, 3, 4, 5}));
//        testValues.addAll(range(10.0d, 5000d, 100));
//        testValuesB.addAll(range(0.0d, 1.0d, 20));
        for (int i = 0; i < 45; i++) {
            double v = interpolate(i, 0, 45, 3, 6);
            double l = Math.pow(10, -v);
            testValues.add(l);
        }


        // Algorytm
//        while (true) {
//            for (int vala : testValues) {

        int count = 1;
        Clock clock = new Clock();
        Clock subClock = new Clock();
        System.out.println("Testowane parametry: " + Arrays.toString(testValues.toArray(new Double[]{})));
        for (double v = 0; v < 0.5d; v += 0.05d) {
            System.out.println("\n[MAIN] przebieg główny: " + count + " z " + testValues.size() + " (" + fraction(testValues.size(), count) + "%)");
            System.out.println("[MAIN] krzyzowanie           : " + v);
            clock.Reset();
            clock.Start();
            for (int i = 0; i < 5; i++) {
                subClock.Reset();
                subClock.Start();
                Configuration.newConfiguration("_iris", "IRIS with CX" + startTimeStr);
                config = Configuration.getConfiguration();
//                config.setSelection(vala);
                config.setPcrossover(
                        interpolate(
                        (float) v,
                        0.0f,
                        (float) MAX,
                        testedParameterMinRange,
                        testedParameterMaxRange));
                e = new EvoAlgorithm(config);
                e.start();
                subClock.Pause();
                System.out.println("\n[MAIN][SUB] przebieg podrzędny: " + i + ", czas : " + subClock);
            }
            clock.Pause();
            System.out.println("\n[MAIN] czas przebiegu głównego: " + clock);
            count++;
        }
//        }
//        Toolkit.getDefaultToolkit().beep();



    }

    private static void testCrossvalidations() {
        for (String configFilename : new String[]{"_glass", "_wine", "_diabetes", "_iris"})
            for (int[] cv : new int[][]{{5, 2, 10}, {1, 5, 10}, {1, 3, 10}, {1, 10, 10}})
                testCrossvalidation(configFilename, cv[0], cv[1], cv[2]);
    }

    private static void testCrossvalidation(String configFilename, int times, int Cv, int tests) {
        singleTestPointStart();
        Configuration.newConfiguration("_iris", "IRIS with CX" + startTimeStr);
        Configuration configuration = Configuration.getConfiguration();
        singleTestPointStop();
    }

    private static final String startTimeStr = (new Date()).toString();
    private static final Clock singleTestPointClock = new Clock();
    private static int singleTestPointNo = 0;

    private static void singleTestPointStart() {
        singleTestPointNo++;
        singleTestPointClock.Reset();
        singleTestPointClock.Start();
    }

    private static void singleTestPointStop() {
        singleTestPointClock.Pause();
        System.out.println("\n[MAIN][SUB] przebieg podrzędny: "
                + singleTestPointNo + ", czas : " + singleTestPointClock);
    }

    /////// Helpers  ///////////  ////////   /////    ///     //    //     /   
    private static double fraction(int total, int actual) {
        return 100.0d * ((double) actual) / ((double) total);
    }

    /**
     * Linear interpolation
     * @param i can be in or outside defined range
     * @param from_min
     * @param from_max
     * @param to_min
     * @param to_max
     * @return
     */
    private static double interpolate(
            double i,
            double from_min,
            double from_max,
            double to_min,
            double to_max) {
        double from_range = from_max - from_min;
        double to_range = to_max - to_min;
        double norm = (i - from_min) / from_range;
        return to_min + norm * to_range;
    }

    /**
     * Linear interpolation
     * @param i can be in or outside defined range
     * @param from_min
     * @param from_max
     * @param to_min
     * @param to_max
     * @return
     */
    private static float interpolate(
            float i,
            float from_min,
            float from_max,
            float to_min,
            float to_max) {
        float from_range = from_max - from_min;
        float to_range = to_max - to_min;
        float norm = (i - from_min) / from_range;
        return to_min + norm * to_range;
    }

    private static List<Double> range(
            double from,
            double to,
            int steps) {
        ArrayList<Double> r = new ArrayList<Double>();
        for (int i = 1; i <= steps; i++) {
            r.add(interpolate(i, 1, steps, from, to));
        }
        return r;
    }
}
