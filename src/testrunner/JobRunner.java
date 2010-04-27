package testrunner;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Clock;

/**
 * Wykonuje zadania pomijając architekturę rozproszoną.
 * 
 * @author eurekin
 */
public class JobRunner {

    public static void main(String[] args) {
        runExecutors();
    }

    private static void runExecutors() {
        final int threadNo = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadNo);
        for (int i = 0; i < threadNo; i++) {
            executor.submit(new jobExecutorThread());
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
    }
    public static final String globalResultFilename = "result.csv";
    public static final String globalResultFileHeader =
            "comment;file_comment;system;pop_size;stop_gen;"
            + "rule_no;cross_value;measure;mutation_val;mutation_type;"
            + "crossover_value;crossover_type;selection;configuration;"
            + "time_avg;time_dev;gen_avg;gen_dev;tr_fsc_avg;tr_fsc_dev;"
            + "tr_fsc_min;tr_fsc_max;tr_acc_avg;tr_acc_dev;tr_acc_min;"
            + "tr_acc_max;tst_fsc_avg;tst_fsc_dev;tst_fsc_min;tst_fsc_max;"
            + "tst_acc_avg;tst_acc_dev;tst_acc_min;tst_acc_max;time"
            + System.getProperty("line.separator");

    synchronized static void collectResult(String result) {
        try {
            File resultFile = new File(globalResultFilename);
            boolean writeHeader = false;
            if (!resultFile.exists()) {
                resultFile.createNewFile();
                writeHeader = true;
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile, true));
            if (writeHeader)
                bw.write(globalResultFileHeader);

            bw.write(result);
            if (!result.endsWith("\n"))
                bw.write("\n");
            bw.flush();
            bw.close();
            System.out.println("[COLLECTOR] Result saved");
        } catch (IOException ex) {
            System.out.println("[COLLECTOR] Result ERROR");
            ex.printStackTrace();
            Logger.getLogger(JobRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void spawnChildProcess(Object toSendToChild,
            String outputPrefix) {
        String fileToRun = "testrunner.Job";
        String[] params = {"java", "-server", "-cp", "build/classes", fileToRun, "test"};
        ProcessBuilder pb = new ProcessBuilder(params);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();

            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            BufferedOutputStream bos = new BufferedOutputStream(p.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(toSendToChild);
            oos.close();

            String line;
            boolean transferingResult = false;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (line.equals(Job.BEGIN_RESULT_TRANSER)) {
                    transferingResult = true;
                    System.out.println(outputPrefix + "[THREAD: RESULT ACCEPTED]");
                    continue;
                }
                if (!transferingResult)
                    System.out.println(outputPrefix + line);
                else if (line.trim().length() > 0)
                    result.append(line);
            }
            System.out.println(outputPrefix + "[RESULT]" + result);
            collectResult(result.toString());
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(JobRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class jobExecutorThread implements Runnable {

        private static int not = 0;

        @Override
        public void run() {
            int no = not++;
            JobConfiguration c;
            while ((c = getNextJob()) != null) {
                singleTestPointStart();
                spawnChildProcess(c.getConfiguration(), "[THREAD" + no + "]");
                c.releaseAndDestroy();
                singleTestPointStop(no);
            }
        }
        private final Clock singleTestPointClock = new Clock();
        private static int singleTestPointTotal = 0;
        private int singleTestPointNo = 0;

        synchronized private void singleTestPointStart() {
            singleTestPointTotal++;
            singleTestPointNo = singleTestPointTotal;
            singleTestPointClock.Reset();
            singleTestPointClock.Start();
        }

        private void singleTestPointStop(int no) {
            singleTestPointClock.Pause();
            System.out.println("[MAIN-THREAD" + no + "] czas trwania przebiegu: "
                    + singleTestPointNo + ", czas : " + singleTestPointClock);
        }
    }

    private static final String lock = "lock?";
    static JobConfiguration getNextJob() {
        synchronized (lock) {
            for (String s : jobsDir()) {
                File f = new File(JobScheduler.jobDir + "/" + s);
                // special case to avoid behaviour that showed in tests
                // update: doesn't help
                if (f.exists() && f.length() == 0) {
                    f.delete();
                    continue;
                }
                JobConfiguration jc = JobConfiguration.tryToAcquire(f);
                if (jc != null)
                    return jc;
            }
        }
        return null;
    }

    private static String[] jobsDir() {
        File jobDir = new File(JobScheduler.jobDir);
        String[] list = jobDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(JobScheduler.jobSuffix);
            }
        });
        if (list == null)
            list = new String[]{};
        return list;
    }
}
