package pl.eurekin.test.server;

import evolutionaryAlgorithm.Configuration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 *
 * @author eurekin
 */
public class JobScheduler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        testCrossvalidations();
    }

    public static void assureDirExists(String dir) {
        File c = new File(dir);
        if (!c.exists())
            c.mkdir();
    }

    private static void serialize(File f, Configuration configuration) {
        try {
            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(configuration);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testCrossvalidations() {
        for (String configFilename : new String[]{"_glass", "_wine", "_diabetes", "_iris"})
            for (int[] cv : new int[][]{{5, 2, 10}, {1, 5, 10}, {1, 3, 10}, {1, 10, 10}})
                testCrossvalidation(configFilename, cv[0], cv[1], cv[2]);

            //new int[][]{{450, 2, 1}})
    }

    private static void testCrossvalidation(
            String configFilename, int times, int Cv, int tests) {
        Configuration.newConfiguration(
                configFilename, "testCrossvalidation" + startTimeStr);
        Configuration configuration = Configuration.getConfiguration();
        configuration.setCv(tests, Cv);
        for (int i = 0; i < times; i++)
            addToJobList(configuration);
    }
    private static final String startTimeStr = (new Date()).toString();

    private static void addToJobList(Configuration configuration) {
        assureDirExists(jobDir);
        File f = new File(getNextJobFileName());
        serialize(f, configuration);
    }
    static final String jobDir = "jobs";
    static final String jobPrefix = "CAREX_";
    static final String jobSuffix = ".java.ser.job";

    static String getJobNameWith(int jobNo) {
        return jobDir + "/"
                + jobPrefix + String.format("%04d", jobNo) + jobSuffix;
    }

    private static String getNextJobFileName() {

        int jobNo = 1;
        String result = "";
        boolean exist = true;
        while (exist) {
            File f = new File(getJobNameWith(jobNo));
            exist = f.exists();

            if (!exist)
                result = f.toString();

            jobNo++;
        }
        return result;
    }
}
