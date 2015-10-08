package rosserial_msgs;

public interface RequestParamResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "rosserial_msgs/RequestParamResponse";
  static final java.lang.String _DEFINITION = "\nint32[]   ints\nfloat32[] floats\nstring[]  strings";
  int[] getInts();
  void setInts(int[] value);
  float[] getFloats();
  void setFloats(float[] value);
  java.util.List<java.lang.String> getStrings();
  void setStrings(java.util.List<java.lang.String> value);
}
