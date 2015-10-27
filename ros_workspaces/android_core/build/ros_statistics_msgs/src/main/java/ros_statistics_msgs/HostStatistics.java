package ros_statistics_msgs;

public interface HostStatistics extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ros_statistics_msgs/HostStatistics";
  static final java.lang.String _DEFINITION = "string hostname\nstring ipaddress\n\ntime window_start\ntime window_stop\n\nuint16 samples\n\n# Load per CPU\nfloat32[] cpu_load_mean\nfloat32[] cpu_load_std\nfloat32[] cpu_load_max\n\n# mem_avialable\nfloat64 phymem_used_mean\nfloat64 phymem_used_std\nfloat64 phymem_used_max\nfloat64 phymem_avail_mean\nfloat64 phymem_avail_std\nfloat64 phymem_avail_max\n";
  java.lang.String getHostname();
  void setHostname(java.lang.String value);
  java.lang.String getIpaddress();
  void setIpaddress(java.lang.String value);
  org.ros.message.Time getWindowStart();
  void setWindowStart(org.ros.message.Time value);
  org.ros.message.Time getWindowStop();
  void setWindowStop(org.ros.message.Time value);
  short getSamples();
  void setSamples(short value);
  float[] getCpuLoadMean();
  void setCpuLoadMean(float[] value);
  float[] getCpuLoadStd();
  void setCpuLoadStd(float[] value);
  float[] getCpuLoadMax();
  void setCpuLoadMax(float[] value);
  double getPhymemUsedMean();
  void setPhymemUsedMean(double value);
  double getPhymemUsedStd();
  void setPhymemUsedStd(double value);
  double getPhymemUsedMax();
  void setPhymemUsedMax(double value);
  double getPhymemAvailMean();
  void setPhymemAvailMean(double value);
  double getPhymemAvailStd();
  void setPhymemAvailStd(double value);
  double getPhymemAvailMax();
  void setPhymemAvailMax(double value);
}
