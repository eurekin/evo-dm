package pl.eurekin.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.eurekin.test.server.JobRunner;
import utils.Clock;

/**
 *
 * @author eurekin
 */
public class Client {

    static List<Process> processes = new ArrayList();
    // GUI only
    static boolean running = false;

    private synchronized static void killChildProcesses() {
        for (Process process : processes) {
            process.destroy();
            processes.remove(process);
        }
    }

    public static void run() {
        if (!running) {
            final int threadNo = Runtime.getRuntime().availableProcessors();
            executor = Executors.newFixedThreadPool(threadNo);
            for (int i = 0; i < threadNo; i++) {
                executor.submit(new jobExecutorThread());
            }
            running = true;
        }
    }

    public static void stop() {
        if (running) {
            killChildProcesses();
            executor.shutdownNow();
            running = false;
        }
    }
    
    // commandline
    static ExecutorService executor;

    public static void main(String... args) {
        // use every available power
        final int threadNo = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(threadNo);
        for (int i = 0; i < threadNo; i++) {
            executor.submit(new jobExecutorThread());
        }

        // shutdown gracefully
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                killChildProcesses();
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));

        // start
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
    }

    private static class jobExecutorThread implements Runnable {

        private static int not = 0;

        @Override
        public void run() {
            int no = not++;
            singleTestPointStart();
            spawnChildProcess(no);
            singleTestPointStop(no);
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

    private static void spawnChildProcess(int no) {
        String outputPrefix = "[THREAD" + no + "]";
        String computername = "no-name";
        try {
            computername = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        String fileToRun = "pl.eurekin.test.client.JobClient";
        String idenitiy = "[THREAD-" + no + "@" + computername + "]";
        String[] params = {"java", "-server", "-cp", "build/classes", fileToRun, idenitiy};
        ProcessBuilder pb = new ProcessBuilder(params);
        pb.redirectErrorStream(true);
        Process p = null;
        try {
            p = pb.start();

            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);


            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(outputPrefix + line);
            }
            processes.add(p);
            p.waitFor();
            no--;
        } catch (InterruptedException ex) {
            System.out.println("interrupt!");
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(JobRunner.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            p.destroy();
        }
    }
}
