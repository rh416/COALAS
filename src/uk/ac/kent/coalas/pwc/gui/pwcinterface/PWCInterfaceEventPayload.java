package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public abstract class PWCInterfaceEventPayload {

    private boolean parsed_successfully = true;

    public void parseFailed(){

        parsed_successfully = false;
    }

    public boolean parseWasSuccessful(){

        return parsed_successfully;
    }
}
