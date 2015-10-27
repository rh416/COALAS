package ros_topology_msgs;

public interface Node extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ros_topology_msgs/Node";
  static final java.lang.String _DEFINITION = "string name\nstring uri\n# Topics this node has registered with master as publishing and subscribing\nstring[] publishes\nstring[] subscribes\n\n# Topic connections established with peers\nConnection[] connections\n\n# Services this node provides\nService[] provides\n";
  java.lang.String getName();
  void setName(java.lang.String value);
  java.lang.String getUri();
  void setUri(java.lang.String value);
  java.util.List<java.lang.String> getPublishes();
  void setPublishes(java.util.List<java.lang.String> value);
  java.util.List<java.lang.String> getSubscribes();
  void setSubscribes(java.util.List<java.lang.String> value);
  java.util.List<ros_topology_msgs.Connection> getConnections();
  void setConnections(java.util.List<ros_topology_msgs.Connection> value);
  java.util.List<ros_topology_msgs.Service> getProvides();
  void setProvides(java.util.List<ros_topology_msgs.Service> value);
}
