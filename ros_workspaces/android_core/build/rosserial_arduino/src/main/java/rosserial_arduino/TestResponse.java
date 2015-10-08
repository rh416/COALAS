package rosserial_arduino;

public interface TestResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "rosserial_arduino/TestResponse";
  static final java.lang.String _DEFINITION = "string output";
  java.lang.String getOutput();
  void setOutput(java.lang.String value);
}
