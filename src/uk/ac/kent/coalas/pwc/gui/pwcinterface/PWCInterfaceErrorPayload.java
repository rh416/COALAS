package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfaceErrorPayload extends PWCInterfaceEventPayload {

    Exception exception;

    public PWCInterfaceErrorPayload(String response, Exception e) {

        super(response);
        exception = e;
    }

    public Exception getException(){

        return exception;
    }
}
