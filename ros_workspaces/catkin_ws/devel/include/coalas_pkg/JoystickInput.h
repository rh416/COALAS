// Generated by gencpp from file coalas_pkg/JoystickInput.msg
// DO NOT EDIT!


#ifndef COALAS_PKG_MESSAGE_JOYSTICKINPUT_H
#define COALAS_PKG_MESSAGE_JOYSTICKINPUT_H


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
struct JoystickInput_
{
  typedef JoystickInput_<ContainerAllocator> Type;

  JoystickInput_()
    : header()
    , joy_speed_input(0)
    , joy_turn_input(0)  {
    }
  JoystickInput_(const ContainerAllocator& _alloc)
    : header(_alloc)
    , joy_speed_input(0)
    , joy_turn_input(0)  {
    }



   typedef  ::std_msgs::Header_<ContainerAllocator>  _header_type;
  _header_type header;

   typedef uint8_t _joy_speed_input_type;
  _joy_speed_input_type joy_speed_input;

   typedef uint8_t _joy_turn_input_type;
  _joy_turn_input_type joy_turn_input;




  typedef boost::shared_ptr< ::coalas_pkg::JoystickInput_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::coalas_pkg::JoystickInput_<ContainerAllocator> const> ConstPtr;

}; // struct JoystickInput_

typedef ::coalas_pkg::JoystickInput_<std::allocator<void> > JoystickInput;

typedef boost::shared_ptr< ::coalas_pkg::JoystickInput > JoystickInputPtr;
typedef boost::shared_ptr< ::coalas_pkg::JoystickInput const> JoystickInputConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::coalas_pkg::JoystickInput_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::coalas_pkg::JoystickInput_<ContainerAllocator> >::stream(s, "", v);
return s;
}

} // namespace coalas_pkg

namespace ros
{
namespace message_traits
{



// BOOLTRAITS {'IsFixedSize': False, 'IsMessage': True, 'HasHeader': True}
// {'coalas_pkg': ['/home/user/catkin_ws/src/coalas_pkg/msg'], 'std_msgs': ['/opt/ros/indigo/share/std_msgs/cmake/../msg']}

// !!!!!!!!!!! ['__class__', '__delattr__', '__dict__', '__doc__', '__eq__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_parsed_fields', 'constants', 'fields', 'full_name', 'has_header', 'header_present', 'names', 'package', 'parsed_fields', 'short_name', 'text', 'types']




template <class ContainerAllocator>
struct IsFixedSize< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::coalas_pkg::JoystickInput_<ContainerAllocator> const>
  : FalseType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::JoystickInput_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::JoystickInput_<ContainerAllocator> const>
  : TrueType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
{
  static const char* value()
  {
    return "baf7b344ec0342ef1b9808e7c8b11566";
  }

  static const char* value(const ::coalas_pkg::JoystickInput_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0xbaf7b344ec0342efULL;
  static const uint64_t static_value2 = 0x1b9808e7c8b11566ULL;
};

template<class ContainerAllocator>
struct DataType< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
{
  static const char* value()
  {
    return "coalas_pkg/JoystickInput";
  }

  static const char* value(const ::coalas_pkg::JoystickInput_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
{
  static const char* value()
  {
    return "Header header\n\
uint8 joy_speed_input\n\
uint8 joy_turn_input\n\
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

  static const char* value(const ::coalas_pkg::JoystickInput_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.header);
      stream.next(m.joy_speed_input);
      stream.next(m.joy_turn_input);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct JoystickInput_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::coalas_pkg::JoystickInput_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::coalas_pkg::JoystickInput_<ContainerAllocator>& v)
  {
    s << indent << "header: ";
    s << std::endl;
    Printer< ::std_msgs::Header_<ContainerAllocator> >::stream(s, indent + "  ", v.header);
    s << indent << "joy_speed_input: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.joy_speed_input);
    s << indent << "joy_turn_input: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.joy_turn_input);
  }
};

} // namespace message_operations
} // namespace ros

#endif // COALAS_PKG_MESSAGE_JOYSTICKINPUT_H
