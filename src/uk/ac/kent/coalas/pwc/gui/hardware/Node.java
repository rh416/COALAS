package uk.ac.kent.coalas.pwc.gui.hardware;

import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterface;

import java.util.ArrayList;

/**
 * Created by rm538 on 11/08/2014.
 */
public class Node {

    public static final int MAX_ZONES = 3;

    private PWCInterface chairInterface;
    private int id;
    private ArrayList<Zone> zones = new ArrayList<Zone>(MAX_ZONES);
    private ArrayList<Integer> sensorDataOrder = new ArrayList<Integer>();
    private boolean connected = false;

    public Node(PWCInterface charInterface, int nodeId){

        this.chairInterface = charInterface;
        id = nodeId;

        // Each node has a maximum of 3 zones - lets create some empty ones ready for use later
        for(int i = 0; i < MAX_ZONES; i++){
            zones.add(i, new Zone(this, i+1, Zone.Position.UNKNOWN, Zone.Orientation.UNKNOWN));
        }
    }

    public boolean isConnectedToBus(){

        return connected;
    }

    public void setConnectedToBus(boolean connectedToBus){

        connected = connectedToBus;
    }

    public int getId(){

        return this.id;
    }

    public void addSensorToDataRequestList(Integer sensorId){

        sensorDataOrder.add(sensorId);
    }

    public void clearSensorDataRequestList(){

        sensorDataOrder.clear();
    }

    public int getSensorCount(){

        return sensorDataOrder.size();
    }

    public Sensor getSensorFromDataOrder(int index){

        return getSensor(sensorDataOrder.get(index));
    }

    public Sensor getSensor(int sensorId){

        return getZone(sensorId).getSensorByTypeBitmask(sensorId);
    }

    public Zone getZone(int zoneNumber){

        int zoneNumberMasked = zoneNumber  & 0x3; // Bitmask = Ox3 -> 11 (as we only want to get the first 2 bits)
        return zones.get(zoneNumberMasked - 1);
    }

    public void setZone(int zoneNumber, Zone newZone){

        zones.set(zoneNumber - 1, newZone);
    }






    public void checkExistsOnBus(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "S");
    }

    public void configureSensors(String configString){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "C" + configString);
    }

    public void requestConfiguration(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "R");
    }

    public void requestCurrentData(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "D");
    }

    public void requestDataFormat(){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "F");
    }

    public void setThresholds(int zone1Threshold, int zone2Threshold, int zone3Threshold){

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "T" +
                chairInterface.intTo12BitHex(zone1Threshold) +
                chairInterface.intTo12BitHex(zone2Threshold) +
                chairInterface.intTo12BitHex(zone3Threshold));
    }

    public void setUltrasoundMode(PWCInterface.UltrasoundMode mode){

        char modeStr = mode.getCharCode();

        chairInterface.sendCommand("&" + String.valueOf(getId()) + "M" + modeStr);
    }
}
