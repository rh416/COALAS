package uk.ac.kent.coalas.pwc.gui.ui;

import java.util.ArrayList;

/**
 * Created by rm538 on 14/08/2014.
 */
public class UIZoneRow extends UIObject {

    private ArrayList<UISensorRow>uiSensorRows = new ArrayList<UISensorRow>();

    public void draw(){

        for(UISensorRow sensorRow : uiSensorRows){
            sensorRow.draw();
        }

    }
}
