package rosserial_arduino;

public interface TestRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "rosserial_arduino/TestRequest";
  static final java.lang.String _DEFINITION = "string input\n";
  java.lang.String getInput();
  void setInput(java.lang.String value);
}
