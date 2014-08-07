package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public abstract class PWCInterfaceEventPayload {

    private String response;

    public PWCInterfaceEventPayload(String response){

        this.response = response;
    }

    public String getResponse(){

        return response;
    }
}
