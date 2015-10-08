package uk.co.kent.coalas;


import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import std_msgs.String;
import coalas_msgs.*;
/**
 * Created by user on 06/10/15.
 */
public class CoalasAndroidNode implements NodeMain {


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.newAnonymous();
    }

    public void onStart(ConnectedNode connectedNode) {
    final Publisher<ChairState> publisher =
            connectedNode.newPublisher("aChatter", "std_msgs/String");

        ChairState msg = publisher.newMessage();
        msg.setChairState((byte) 3);
        publisher.publish(msg);
    }

    public void onShutdown(Node node) {
    }

    public void onShutdownComplete(Node node) {
    }

    public void onError(Node node, Throwable throwable) {
    }

}
