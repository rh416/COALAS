package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.LogFile;

/**
 * Created by rm538 on 06/08/2014.
 *
 * A class representing the payload for a firmware information event from the Interface
 *
 */
public class PWCInterfacePayloadLogFileInfo extends PWCInterfaceEventPayload {

    private LogFile logFile;

    public PWCInterfacePayloadLogFileInfo(PWCInterface chairInterface, String response) throws Exception{

        super(chairInterface, response);

        // Response is in the format L:FILENAME:12345       where 12345 is the file size in bytes
        String filename = response.substring(2, response.indexOf(":", 2));
        int size = Integer.parseInt(response.substring(response.indexOf(":", 2) + 1));

        logFile = new LogFile(filename);
        logFile.setSize(size);
    }

    public LogFile getLogFile(){

        return logFile;
    }
}
