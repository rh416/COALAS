package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfacePayloadFirmwareInfo extends PWCInterfaceEventPayload {

    private String version;

    public PWCInterfacePayloadFirmwareInfo(PWCInterface chairInterface, String response) throws Exception{

        super(response);

        version = response.substring(1);
    }

    public String getVersion(){

        return version;
    }
}
