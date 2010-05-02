package pl.eurekin.test.client;

import evolutionaryAlgorithm.Configuration;
import evolutionaryAlgorithm.EvoAlgorithm;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eurekin
 */
public class Job {

    public static final String BEGIN_RESULT_TRANSER = "[SAIIIIIII-give-rezalt!]";

    public static void main(String... args) {
        {
            try {
                // Łapiemy konfigurację
                Configuration conf = receiveConfigurationThroughInputStream();
                File resultFile = prepareConfigurationForReporting(conf);
                Configuration.setConfiguration(conf);
                EvoAlgorithm e = new EvoAlgorithm(conf);
                e.start();

                System.out.println("\n" + BEGIN_RESULT_TRANSER);
                String result = retrieveResultsFromTempFile(resultFile);
                System.out.println(result);

                resultFile.delete();
            } catch (IOException ex) {
                System.err.println("JOB error");
                ex.printStackTrace();
                Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static String retrieveResultsFromTempFile(File resultFile)
            throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader in = new BufferedReader(new FileReader(resultFile));
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }

    private static Configuration receiveConfigurationThroughInputStream() {
        ObjectInputStream ois = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(System.in);
            ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            return (Configuration) o;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("JOB ERROR - couldn't receive configuration");
        return null;
    }

    static File prepareConfigurationForReporting(Configuration conf)
            throws IOException {
        File tmp = File.createTempFile("~CAREX_", "job.tmp.csv");
        conf.setReportFileName(tmp.toString());
        return tmp;
    }
}
