package std_msgs;

public interface UInt8MultiArray extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "std_msgs/UInt8MultiArray";
  static final java.lang.String _DEFINITION = "# Please look at the MultiArrayLayout message definition for\n# documentation on all multiarrays.\n\nMultiArrayLayout  layout        # specification of data layout\nuint8[]           data          # array of data\n\n";
  std_msgs.MultiArrayLayout getLayout();
  void setLayout(std_msgs.MultiArrayLayout value);
  org.jboss.netty.buffer.ChannelBuffer getData();
  void setData(org.jboss.netty.buffer.ChannelBuffer value);
}
