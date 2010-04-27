package testrunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import static testrunner.JobProtocol.*;
import static testrunner.utils.Base64.*;

/**
 *
 * @author eurekin
 */
public class JobServerThread extends Thread {

    private static int totalConnected = 0;

    private synchronized static void incrementCount() {
        totalConnected++;
    }

    private synchronized static void decrementCount() {
        totalConnected--;
    }
    private Socket socket;

    public JobServerThread(Socket socket) {
        super("JobServerThread");
        this.socket = socket;
    }
    PrintWriter out;
    BufferedReader in;
    JobConfiguration c;

    @Override
    public void run() {
        try {
            incrementCount();

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String str;

            str = in.readLine();

            // check if client uses the same protocol as server
            if (!CLIENT_CONNECTED.equals(str)) {
                cleanMess();
                return;
            }

            String identity = in.readLine();
            System.out.println(identity + " connected");
            // let's service our client
            c = JobRunner.getNextJob();
            if (c == null) {
                out.println(ALL_JOBS_ARE_DONE);
                cleanMess();
                return;
            }

            // it's eager to work, so we help
            out.println(BEGIN_JOB_DEFINITION_TRANSFER);
            out.println(serializeObjectToString(c.getConfiguration()));
            out.println(END_JOB_DEFINITION_TRANSFER);
            // important
            // - otherwise configuration doesn't come out of buffer

            // now let's listen to client
            boolean receivingResult = false;
            StringBuilder resBldr = new StringBuilder();
            while ((str = in.readLine()) != null) {
                // distinguish between normal output and result
                if (str.equals(BEGIN_RESULT_TRANSFER)) {
                    System.out.println("[SERVER] Got RESULTS");
                    receivingResult = true;
                    continue;
                }

                // receive output or result
                if (!receivingResult)
                    handleClientOutput(str, identity);
                else
                    resBldr.append(str);
            }

            // finish
            String result = resBldr.toString();
            if (accepted(result)) {
                c.releaseAndDestroy();
                JobRunner.collectResult(result);
            } else
                c.release();
            in.close();
            socket.close();
            System.out.println("");

        } catch (IOException e) {
            e.printStackTrace();
            cleanMess();
        } finally {
            decrementCount();
            notifyServerByeBye();
        }
    }

    private void cleanMess() {
        try {
            if (c != null)
                c.release();
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
        } catch (IOException ex) {
            System.err.println("error while closing jobServerThread");
            ex.printStackTrace();
        }
    }

    private void handleClientOutput(String str, String identity) {
        JobServer.handleClientOutput(str, identity, this);
    }

    /**
     * Czy uznajemy <code>result</code> jako dobry wynik?
     * Jeśli nie to znaczyć to będzie, że <code>job</code> trzeba
     * powtórzyć.
     *
     * @param result
     * @return
     */
    private boolean accepted(String result) {
        System.out.println("[SERVER] vertfying results...");
        return result.trim().length() > 0;
    }

    private void notifyServerByeBye() {
        JobServer.clientDisconnected(this);
    }
}
