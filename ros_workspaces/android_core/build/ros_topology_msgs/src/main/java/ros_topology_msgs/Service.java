package ros_topology_msgs;

public interface Service extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ros_topology_msgs/Service";
  static final java.lang.String _DEFINITION = "string name\nstring uri\n";
  java.lang.String getName();
  void setName(java.lang.String value);
  java.lang.String getUri();
  void setUri(java.lang.String value);
}
