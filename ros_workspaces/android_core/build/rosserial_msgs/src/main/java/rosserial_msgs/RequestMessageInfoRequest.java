package rosserial_msgs;

public interface RequestMessageInfoRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "rosserial_msgs/RequestMessageInfoRequest";
  static final java.lang.String _DEFINITION = "# Full message datatype, eg \"std_msgs/String\"\nstring type\n";
  java.lang.String getType();
  void setType(java.lang.String value);
}
