package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;

/**
 * Created by rm538 on 06/08/2014.
 */
public abstract class PWCInterfaceEventPayload {

    private PWCInterface chairInterface;
    private String response;
    protected Node _node;

    public PWCInterfaceEventPayload(PWCInterface chairInterface, String response){

        this.chairInterface = chairInterface;
        this.response = response;
    }

    public Node getNode(){

        return _node;
    }

    protected Node getResponseNodeFromId(int nodeId){

        _node = chairInterface.getNode(nodeId);

        // Reset the timed out status of this node - if a Node has received an Interface Event, it can't have timed out
        if(_node != null) {
            _node.setTimedOut(false);
        }
        this._node = _node;
        return _node;
    }

    public String getResponse(){

        return response;
    }

    protected String s(String stringName){

        return WheelchairGUI.s(stringName);
    }
}
