// Generated by gencpp from file coalas_pkg/SimpleJoystick.msg
// DO NOT EDIT!


#ifndef COALAS_PKG_MESSAGE_SIMPLEJOYSTICK_H
#define COALAS_PKG_MESSAGE_SIMPLEJOYSTICK_H


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
struct SimpleJoystick_
{
  typedef SimpleJoystick_<ContainerAllocator> Type;

  SimpleJoystick_()
    : header()
    , source_ID(0)
    , joy_speed(0)
    , joy_turn(0)  {
    }
  SimpleJoystick_(const ContainerAllocator& _alloc)
    : header(_alloc)
    , source_ID(0)
    , joy_speed(0)
    , joy_turn(0)  {
    }



   typedef  ::std_msgs::Header_<ContainerAllocator>  _header_type;
  _header_type header;

   typedef uint8_t _source_ID_type;
  _source_ID_type source_ID;

   typedef uint8_t _joy_speed_type;
  _joy_speed_type joy_speed;

   typedef uint8_t _joy_turn_type;
  _joy_turn_type joy_turn;


    enum { _ARDUINO_ = 1u };
     enum { _UDOO_ = 2u };
     enum { _ANDROID_ = 3u };
     enum { _HEADTRACKER_ = 4u };
 

  typedef boost::shared_ptr< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> const> ConstPtr;

}; // struct SimpleJoystick_

typedef ::coalas_pkg::SimpleJoystick_<std::allocator<void> > SimpleJoystick;

typedef boost::shared_ptr< ::coalas_pkg::SimpleJoystick > SimpleJoystickPtr;
typedef boost::shared_ptr< ::coalas_pkg::SimpleJoystick const> SimpleJoystickConstPtr;

// constants requiring out of line definition

   

   

   

   



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::coalas_pkg::SimpleJoystick_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >::stream(s, "", v);
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
struct IsFixedSize< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> const>
  : FalseType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> const>
  : TrueType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
{
  static const char* value()
  {
    return "e8b4828e3d92d35e249fce400cede618";
  }

  static const char* value(const ::coalas_pkg::SimpleJoystick_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0xe8b4828e3d92d35eULL;
  static const uint64_t static_value2 = 0x249fce400cede618ULL;
};

template<class ContainerAllocator>
struct DataType< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
{
  static const char* value()
  {
    return "coalas_pkg/SimpleJoystick";
  }

  static const char* value(const ::coalas_pkg::SimpleJoystick_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
{
  static const char* value()
  {
    return "Header header\n\
\n\
uint8 _ARDUINO_=1\n\
uint8 _UDOO_=2\n\
uint8 _ANDROID_=3\n\
uint8 _HEADTRACKER_=4\n\
\n\
uint8 source_ID		# the source platform that generates this message (UDOO, ARDUINO, etc)\n\
\n\
uint8 joy_speed  	# The joystick speed, range: 1 - 255 (full bkwd - full fwd)\n\
uint8 joy_turn		# The joystick turn, range: 1 - 255 (full left - full right)\n\
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

  static const char* value(const ::coalas_pkg::SimpleJoystick_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.header);
      stream.next(m.source_ID);
      stream.next(m.joy_speed);
      stream.next(m.joy_turn);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct SimpleJoystick_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::coalas_pkg::SimpleJoystick_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::coalas_pkg::SimpleJoystick_<ContainerAllocator>& v)
  {
    s << indent << "header: ";
    s << std::endl;
    Printer< ::std_msgs::Header_<ContainerAllocator> >::stream(s, indent + "  ", v.header);
    s << indent << "source_ID: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.source_ID);
    s << indent << "joy_speed: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.joy_speed);
    s << indent << "joy_turn: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.joy_turn);
  }
};

} // namespace message_operations
} // namespace ros

#endif // COALAS_PKG_MESSAGE_SIMPLEJOYSTICK_H
