package ros_topology_msgs;

public interface Topic extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ros_topology_msgs/Topic";
  static final java.lang.String _DEFINITION = "string name\nstring type\n";
  java.lang.String getName();
  void setName(java.lang.String value);
  java.lang.String getType();
  void setType(java.lang.String value);
}
