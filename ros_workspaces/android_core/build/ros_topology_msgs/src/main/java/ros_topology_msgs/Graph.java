package ros_topology_msgs;

public interface Graph extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ros_topology_msgs/Graph";
  static final java.lang.String _DEFINITION = "Header header\n\n# Master URI\nstring master\n\nNode[] nodes\nTopic[] topics\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  java.lang.String getMaster();
  void setMaster(java.lang.String value);
  java.util.List<ros_topology_msgs.Node> getNodes();
  void setNodes(java.util.List<ros_topology_msgs.Node> value);
  java.util.List<ros_topology_msgs.Topic> getTopics();
  void setTopics(java.util.List<ros_topology_msgs.Topic> value);
}
