// Generated by gencpp from file coalas_pkg/EncodersOutput.msg
// DO NOT EDIT!


#ifndef COALAS_PKG_MESSAGE_ENCODERSOUTPUT_H
#define COALAS_PKG_MESSAGE_ENCODERSOUTPUT_H


#include <string>
#include <vector>
#include <map>

#include <ros/types.h>
#include <ros/serialization.h>
#include <ros/builtin_message_traits.h>
#include <ros/message_operations.h>

#include <std_msgs/Header.h>

namespace coalas_pkg
{
template <class ContainerAllocator>
struct EncodersOutput_
{
  typedef EncodersOutput_<ContainerAllocator> Type;

  EncodersOutput_()
    : header()
    , left_encoder_pos(0)
    , right_encoder_pos(0)  {
    }
  EncodersOutput_(const ContainerAllocator& _alloc)
    : header(_alloc)
    , left_encoder_pos(0)
    , right_encoder_pos(0)  {
    }



   typedef  ::std_msgs::Header_<ContainerAllocator>  _header_type;
  _header_type header;

   typedef uint16_t _left_encoder_pos_type;
  _left_encoder_pos_type left_encoder_pos;

   typedef uint16_t _right_encoder_pos_type;
  _right_encoder_pos_type right_encoder_pos;




  typedef boost::shared_ptr< ::coalas_pkg::EncodersOutput_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::coalas_pkg::EncodersOutput_<ContainerAllocator> const> ConstPtr;

}; // struct EncodersOutput_

typedef ::coalas_pkg::EncodersOutput_<std::allocator<void> > EncodersOutput;

typedef boost::shared_ptr< ::coalas_pkg::EncodersOutput > EncodersOutputPtr;
typedef boost::shared_ptr< ::coalas_pkg::EncodersOutput const> EncodersOutputConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::coalas_pkg::EncodersOutput_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >::stream(s, "", v);
return s;
}

} // namespace coalas_pkg

namespace ros
{
namespace message_traits
{



// BOOLTRAITS {'IsFixedSize': False, 'IsMessage': True, 'HasHeader': True}
// {'coalas_pkg': ['/home/user/catkin_ws/src/coalas_pkg/msg'], 'sensor_msgs': ['/opt/ros/indigo/share/sensor_msgs/cmake/../msg'], 'std_msgs': ['/opt/ros/indigo/share/std_msgs/cmake/../msg'], 'geometry_msgs': ['/opt/ros/indigo/share/geometry_msgs/cmake/../msg']}

// !!!!!!!!!!! ['__class__', '__delattr__', '__dict__', '__doc__', '__eq__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_parsed_fields', 'constants', 'fields', 'full_name', 'has_header', 'header_present', 'names', 'package', 'parsed_fields', 'short_name', 'text', 'types']




template <class ContainerAllocator>
struct IsFixedSize< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::coalas_pkg::EncodersOutput_<ContainerAllocator> const>
  : FalseType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::EncodersOutput_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::EncodersOutput_<ContainerAllocator> const>
  : TrueType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
{
  static const char* value()
  {
    return "20a3e3da2d2961e33a8a6c60b0380df1";
  }

  static const char* value(const ::coalas_pkg::EncodersOutput_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x20a3e3da2d2961e3ULL;
  static const uint64_t static_value2 = 0x3a8a6c60b0380df1ULL;
};

template<class ContainerAllocator>
struct DataType< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
{
  static const char* value()
  {
    return "coalas_pkg/EncodersOutput";
  }

  static const char* value(const ::coalas_pkg::EncodersOutput_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
{
  static const char* value()
  {
    return "Header header\n\
uint16 left_encoder_pos\n\
uint16 right_encoder_pos\n\
\n\
\n\
================================================================================\n\
MSG: std_msgs/Header\n\
# Standard metadata for higher-level stamped data types.\n\
# This is generally used to communicate timestamped data \n\
# in a particular coordinate frame.\n\
# \n\
# sequence ID: consecutively increasing ID \n\
uint32 seq\n\
#Two-integer timestamp that is expressed as:\n\
# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')\n\
# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')\n\
# time-handling sugar is provided by the client library\n\
time stamp\n\
#Frame this data is associated with\n\
# 0: no frame\n\
# 1: global frame\n\
string frame_id\n\
";
  }

  static const char* value(const ::coalas_pkg::EncodersOutput_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.header);
      stream.next(m.left_encoder_pos);
      stream.next(m.right_encoder_pos);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct EncodersOutput_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::coalas_pkg::EncodersOutput_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::coalas_pkg::EncodersOutput_<ContainerAllocator>& v)
  {
    s << indent << "header: ";
    s << std::endl;
    Printer< ::std_msgs::Header_<ContainerAllocator> >::stream(s, indent + "  ", v.header);
    s << indent << "left_encoder_pos: ";
    Printer<uint16_t>::stream(s, indent + "  ", v.left_encoder_pos);
    s << indent << "right_encoder_pos: ";
    Printer<uint16_t>::stream(s, indent + "  ", v.right_encoder_pos);
  }
};

} // namespace message_operations
} // namespace ros

#endif // COALAS_PKG_MESSAGE_ENCODERSOUTPUT_H