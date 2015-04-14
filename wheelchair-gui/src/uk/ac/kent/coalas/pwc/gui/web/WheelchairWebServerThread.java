package uk.ac.kent.coalas.pwc.gui.web;

import java.io.IOException;
import java.net.BindException;

/**
 * Created by Richard on 01/04/2015.
 */
public class WheelchairWebServerThread implements Runnable {

    // Currently only supports one thread - could be adapted to support more
    private static Thread runnerThread;

    public static void start(){

        runnerThread = new Thread(new WheelchairWebServerThread());
        runnerThread.setName("Wheelchair Webserver Thread");
        runnerThread.start();
    }

    public static void stop(){

        runnerThread.interrupt();
    }

    public void run(){

        WheelchairWebServer server = WheelchairWebServer.getInstance();

        try {
            server.start();
        } catch(IOException e){
            WheelchairWebServer.reportException(server.getPort(), e);
        }
    }
}
