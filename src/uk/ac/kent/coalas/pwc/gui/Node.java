package uk.ac.kent.coalas.pwc.gui;

import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterface;

import java.util.ArrayList;

/**
 * Created by rm538 on 11/08/2014.
 */
public class Node {

    private PWCInterface chairInterface;
    private int id;
    private ArrayList<Zone> zones = new ArrayList<Zone>(3);
    private boolean connected = false;

    public Node(PWCInterface charInterface, int nodeId){

        this.chairInterface = charInterface;
        id = nodeId;

        // Each node has a maximum of 3 zones - lets create some empty ones ready for use later
        for(int i = 0; i < 3; i++){
            zones.add(i, new Zone(i+1, Zone.Position.UNKNOWN, Zone.Orientation.UNKNOWN));
        }
    }

    public boolean connectedToBus(){

        return connected;
    }

    public void setConnectedToBus(boolean connectedToBus){

        connected = connectedToBus;
    }

    public int getId(){

        return this.id;
    }

    public void setZone(int index, Zone newZone){

        zones.set(index - 1, newZone);
    }






    public void checkExistsOnBus(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "S");
    }

    public void configureSensors(String configString){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "C" + configString);
    }

    public void requestNodeConfiguration(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "R");
    }

    public void requestNodeCurrentData(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "D");
    }

    public void requestNodeDataFormat(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "F");
    }

    public void setThresholds(int zone1Threshold, int zone2Threshold, int zone3Threshold){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "T" +
                chairInterface.intTo12BitHex(zone1Threshold) +
                chairInterface.intTo12BitHex(zone2Threshold) +
                chairInterface.intTo12BitHex(zone3Threshold));
    }

    public void setUltrasoundMode(PWCInterface.UltrasoundMode mode){

        String modeStr = "";
        if(mode == PWCInterface.UltrasoundMode.CONTINUOUS){
            modeStr = "C";
        } else if(mode == PWCInterface.UltrasoundMode.PULSED){
            modeStr = "P";
        }

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "M" + modeStr);
    }
}
