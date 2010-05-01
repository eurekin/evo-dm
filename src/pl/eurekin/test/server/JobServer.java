/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.eurekin.test.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 * @author eurekin
 */
public class JobServer {

    public static final int SERVER_PORT = 4444;
    public static ArrayList<JobServerThread> threads = new ArrayList();

    public static void main(String... args) {
        try {
            start();
        } catch (IOException ex) {
            System.err.println("Error while starting server");
            ex.printStackTrace();
        }
    }

    public static void start() throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("listening...");
        while (listening) {
            JobServerThread thread = new JobServerThread(serverSocket.accept());
            System.out.println("... connected client");
            threads.add(thread);
            thread.start();
        }
    }

    static void handleClientOutput(String str, String id, JobServerThread aThis) {
        System.out.println(id + " " + str);
    }

    static void clientDisconnected(JobServerThread thread) {
        System.out.println("... disconnected client");
        threads.remove(thread);
        boolean done = !areThereMoreJobs();
        if (done)
            System.out.println("There are no jobs to do");
    }

    private static boolean areThereMoreJobs() {
        int jobs =  new File(JobScheduler.jobDir).list().length;
        System.out.println("THERE ARE " + jobs + " JOBS LEFT");
        return jobs > 0;
    }
}
