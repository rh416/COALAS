package rosserial_msgs;

public interface RequestServiceInfoRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "rosserial_msgs/RequestServiceInfoRequest";
  static final java.lang.String _DEFINITION = "# service name\nstring service\n";
  java.lang.String getService();
  void setService(java.lang.String value);
}
