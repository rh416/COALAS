package coalas_msgs;

public interface SimpleJoystick extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "coalas_msgs/SimpleJoystick";
  static final java.lang.String _DEFINITION = "Header header\n\nuint8 ARDUINO_=1\nuint8 UDOO_=2\nuint8 ANDROID_=3\nuint8 HEADTRACKER_=4\n\nuint8 source_ID\t\t# the source platform that generates this message (UDOO, ARDUINO, etc)\n\nuint8 joy_speed  \t# The joystick speed, range: 1 - 255 (full bkwd - full fwd)\nuint8 joy_turn\t\t# The joystick turn, range: 1 - 255 (full left - full right)\n";
  static final byte ARDUINO_ = 1;
  static final byte UDOO_ = 2;
  static final byte ANDROID_ = 3;
  static final byte HEADTRACKER_ = 4;
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  byte getSourceID();
  void setSourceID(byte value);
  byte getJoySpeed();
  void setJoySpeed(byte value);
  byte getJoyTurn();
  void setJoyTurn(byte value);
}
