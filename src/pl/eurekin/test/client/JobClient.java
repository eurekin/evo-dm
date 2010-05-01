package pl.eurekin.test.client;

import pl.eurekin.test.server.JobServer;
import EvolutionaryAlgorithm.Configuration;
import EvolutionaryAlgorithm.EvoAlgorithm;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import static pl.eurekin.test.JobProtocol.*;
import static pl.eurekin.test.util.Base64.*;

/**
 *
 * @author eurekin
 */
public class JobClient {

    @SuppressWarnings("empty-statement")
    public static void main(String... args) {
        try {
            String badge = args[0];
            System.out.println("BADGE: " + badge);
            while (!handleOneJob(badge));
        } catch (ConnectException ex) {
            System.out.println("Connection refused.");
            ex.printStackTrace();
        }
    }

    /**
     *
     * @return true if there are no more jobs to do
     */
    private static boolean handleOneJob(String badge) throws ConnectException {
        String str;
        Socket socket;
        PrintStream out;
        PrintStream origOut = System.out;
        PrintStream origErr = System.err;
        InputStream ins;
        OutputStream sout;
        BufferedReader in;
        boolean noMoreJobs = false;
        try {
            socket = new Socket("eurekin.pl", JobServer.SERVER_PORT);
            sout = socket.getOutputStream();
            out = new PrintStream(sout);
            ins = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(ins));

            // Say hello
            out.println(CLIENT_CONNECTED);

            // And introduce oneself
            out.println(badge);
            out.flush();

            // Is there any work to do?
            str = in.readLine();
            if (ALL_JOBS_ARE_DONE.equals(str)) {
                System.out.println("No work to do");
                noMoreJobs = true;
            } else if (BEGIN_JOB_DEFINITION_TRANSFER.equals(str)) {
                StringBuilder sb = new StringBuilder();
                while (!(str = in.readLine()).equals(END_JOB_DEFINITION_TRANSFER))
                    sb.append(str);
                Configuration configuration = (Configuration) deserializeObjectFrom(sb.toString());
                Configuration.setConfiguration(configuration);

                // swap streams 
                System.setOut(new SplitStream(out, origOut));
                System.setErr(new SplitStream(out, origErr));

//                System.setOut(out);
//                System.setErr(out);

                File tmp = Job.prepareConfigurationForReporting(configuration);
                EvoAlgorithm e = new EvoAlgorithm(configuration);
                e.start();
                String result = Job.retrieveResultsFromTempFile(tmp);
                out.println("\n" + BEGIN_RESULT_TRANSFER);
                out.println(result);
                out.flush();
                out.close();
                tmp.delete();
            }
            in.close();
            sout.close();
            socket.close();
        } catch (IOException e) {
            if (e instanceof ConnectException)
                throw (ConnectException) e;
            e.printStackTrace();
        }

        // restore streams
        System.setOut(origOut);
        System.setErr(origErr);

        return noMoreJobs;
    }

    private static class SplitStream extends PrintStream {

        private final PrintStream two;

        public SplitStream(PrintStream one, PrintStream two) {
            super(one);
            this.two = two;
        }

        @Override
        public void print(String x) {
            two.print(x);
            super.print(x);
        }

        @Override
        public void println(String x) {
            two.println(x);
            super.println(x);
        }
    }
}
