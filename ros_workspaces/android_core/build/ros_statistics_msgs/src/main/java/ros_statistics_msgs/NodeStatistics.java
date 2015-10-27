package ros_statistics_msgs;

public interface NodeStatistics extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ros_statistics_msgs/NodeStatistics";
  static final java.lang.String _DEFINITION = "string node\nstring host\nstring uri\nstring pid\n\n# Statistics apply to this time window\ntime window_start\ntime window_stop\n\nuint16 samples\n\nuint16 threads\n\n# Process CPU usage, as percentage of total local use\nfloat32 cpu_load_mean\nfloat32 cpu_load_std\nfloat32 cpu_load_max\n\n# Virtual Memory use\nfloat64 virt_mem_mean\nfloat32 virt_mem_std\nfloat64 virt_mem_max\n\n# Real Memory Use\nfloat64 real_mem_mean\nfloat32 real_mem_std\nfloat64 real_mem_max\n\n";
  java.lang.String getNode();
  void setNode(java.lang.String value);
  java.lang.String getHost();
  void setHost(java.lang.String value);
  java.lang.String getUri();
  void setUri(java.lang.String value);
  java.lang.String getPid();
  void setPid(java.lang.String value);
  org.ros.message.Time getWindowStart();
  void setWindowStart(org.ros.message.Time value);
  org.ros.message.Time getWindowStop();
  void setWindowStop(org.ros.message.Time value);
  short getSamples();
  void setSamples(short value);
  short getThreads();
  void setThreads(short value);
  float getCpuLoadMean();
  void setCpuLoadMean(float value);
  float getCpuLoadStd();
  void setCpuLoadStd(float value);
  float getCpuLoadMax();
  void setCpuLoadMax(float value);
  double getVirtMemMean();
  void setVirtMemMean(double value);
  float getVirtMemStd();
  void setVirtMemStd(float value);
  double getVirtMemMax();
  void setVirtMemMax(double value);
  double getRealMemMean();
  void setRealMemMean(double value);
  float getRealMemStd();
  void setRealMemStd(float value);
  double getRealMemMax();
  void setRealMemMax(double value);
}
