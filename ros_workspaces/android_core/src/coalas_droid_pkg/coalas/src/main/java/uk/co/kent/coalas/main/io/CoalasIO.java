package uk.co.kent.coalas.main.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import uk.co.kent.coalas.chair.ChairState;
import uk.co.kent.coalas.chair.RemoteMsgs;

import static uk.co.kent.coalas.Coalas.toastHandler;
/**
 * Created by Paul Oprea on 30/03/15.
 */
public class CoalasIO {

    private static CoalasIO INSTANCE;
    //    private static Context appContext;
    private static String dstAddress;
    private static final int dstPort = 4440;
    private static Socket socket;
    private static Runnable sockRunner;
    private static Runnable sockListener;
    private static PrintStream out;
    private static InputStreamReader in;

    private long previousTime;
    private static class SockRunner implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket(dstAddress, dstPort);
                out = new PrintStream(socket.getOutputStream());
                in = new InputStreamReader(socket.getInputStream());
                toastHandler.toast("Connected to COALAS server,\n" + dstAddress + ":" + dstPort);
                sockListener.run();
                CoalasIO.getInstance().write2Socket("?#"); // sync with system
            } catch (IOException e) {
//                e.printStackTrace();
                toastHandler.toast("Cannot connect to server\n" + dstAddress + ":" + dstPort);
            }
        }
    }

    private static class SockListener implements Runnable {
        @Override
        public void run() {
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            try {
                int ch;
                while ((ch = br.read()) != -1) {
                    if (ch == '#') {
                        sb.append((char) ch);
                        String message = sb.toString();
                        if (RemoteMsgs.match(message) && RemoteMsgs.DIRECTIVE.hasNewData)
                            ChairState.getInstance().setDirective(
                                    (byte) RemoteMsgs.DIRECTIVE.getData()[1],
                                    ChairState.Platforms.UDOO);
                        // process message
                        sb.setLength(0);
                    } else {
                        sb.append((char) ch);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized CoalasIO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CoalasIO();
        }
        return INSTANCE;
    }

    public void connect() {
        previousTime = 0;
        dstAddress = SettingsDialogue.IPFields.getIP();
        sockRunner = new SockRunner();
        sockListener = new SockListener();
        new Thread(sockRunner).start();

    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public void disconnect() {
        if (!checkSocket())
            return; // nothing to do
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            toastHandler.toast("Could not close socket\n");
            e.printStackTrace();
        }
    }


    public boolean checkSocket() {
        return !(socket == null || !socket.isConnected());
    }


    public void write2Socket(String b) {
        if (!checkSocket()) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - previousTime) < 2000)
                return;
            previousTime = currentTime;
            toastHandler.toast("Not connected to client. Please check network settings.\n");
            return;
        }
        try {

            out.write(b.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
