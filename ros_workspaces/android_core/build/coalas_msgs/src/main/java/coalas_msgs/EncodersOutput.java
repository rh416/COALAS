package coalas_msgs;

public interface EncodersOutput extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "coalas_msgs/EncodersOutput";
  static final java.lang.String _DEFINITION = "Header header\nuint16 left_encoder_pos\nuint16 right_encoder_pos\n\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  short getLeftEncoderPos();
  void setLeftEncoderPos(short value);
  short getRightEncoderPos();
  void setRightEncoderPos(short value);
}
