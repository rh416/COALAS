package coalas_msgs;

public interface RangeArray extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "coalas_msgs/RangeArray";
  static final java.lang.String _DEFINITION = "Header header\nsensor_msgs/Range[] ranges\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  java.util.List<sensor_msgs.Range> getRanges();
  void setRanges(java.util.List<sensor_msgs.Range> value);
}
