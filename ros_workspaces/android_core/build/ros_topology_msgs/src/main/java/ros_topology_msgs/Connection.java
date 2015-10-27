package ros_topology_msgs;

public interface Connection extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ros_topology_msgs/Connection";
  static final java.lang.String _DEFINITION = "# directions\nuint8 IN=1\nuint8 OUT=2\nuint8 BOTH=3\n\n#destination node name\nstring destination\nstring topic\nuint8 direction\nstring transport\n";
  static final byte IN = 1;
  static final byte OUT = 2;
  static final byte BOTH = 3;
  java.lang.String getDestination();
  void setDestination(java.lang.String value);
  java.lang.String getTopic();
  void setTopic(java.lang.String value);
  byte getDirection();
  void setDirection(byte value);
  java.lang.String getTransport();
  void setTransport(java.lang.String value);
}
