package coalas_msgs;

public interface ChairState extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "coalas_msgs/ChairState";
  static final java.lang.String _DEFINITION = "Header header  \nuint8 chairState\nbool isReply\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  byte getChairState();
  void setChairState(byte value);
  boolean getIsReply();
  void setIsReply(boolean value);
}
