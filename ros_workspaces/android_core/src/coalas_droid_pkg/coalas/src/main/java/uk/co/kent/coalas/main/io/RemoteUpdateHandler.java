package uk.co.kent.coalas.main.io;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import uk.co.kent.coalas.chair.RemoteMsgs;
import uk.co.kent.coalas.Coalas;

/**
 * Created by Paul Oprea on 01/04/15.
 */
public class RemoteUpdateHandler extends AsyncTask<Void, Void, Void> {


    private static Socket socket;
    private static Context appContext;
    private static final StringBuilder message = new StringBuilder();


    public RemoteUpdateHandler(Context context, Socket _socket) {
        appContext = context;
        socket = _socket;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (!CoalasIO.getInstance().checkSocket())
            return null;
        try {

            byte[] buffer = new byte[1024];
            buffer[0] = 1;
            int bytesRead;
            InputStream in = socket.getInputStream();
            do {
                while (!(in.available() > 0))
                    wait(100);
                while ((bytesRead = in.read(buffer)) != -1) {
                    if (message.length() > 0 && (bytesRead == '\r' || bytesRead == '\n')) {
                        String toProcess = message.toString();
                        if (!RemoteMsgs.match(toProcess)) {
                            Coalas.toastHandler.toast(toProcess);
                        }
                        message.setLength(0);
                    } else {
                        message.append((char) bytesRead);
                    }
                }
            } while (true);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

