package uk.ac.kent.coalas.pwc.gui.ui;

import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

/**
 * Created by rm538 on 19/08/2014.
 */
public class UIZoneDataRow extends UIObject {

    private Zone dataZone;
    private RowPositionTracker positionTracker;

    private String headingText = "";
    private int headingX = 0;
    private int headingY = 0;


    public UIZoneDataRow(Zone zone, RowPositionTracker positionTracker){

        this.dataZone = zone;
        this.positionTracker = positionTracker;

        this.headingText = "Node " + zone.getParentNode().getId() + " - Zone " + zone.getZoneNumber();
        this.headingX = positionTracker.getX();
        this.headingY = positionTracker.getY();
    }

    @Override
    public void draw(){

    }
}
