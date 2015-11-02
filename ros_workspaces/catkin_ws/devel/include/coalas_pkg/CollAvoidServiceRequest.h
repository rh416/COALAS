// Generated by gencpp from file coalas_pkg/CollAvoidServiceRequest.msg
// DO NOT EDIT!


#ifndef COALAS_PKG_MESSAGE_COLLAVOIDSERVICEREQUEST_H
#define COALAS_PKG_MESSAGE_COLLAVOIDSERVICEREQUEST_H


#include <string>
#include <vector>
#include <map>

#include <ros/types.h>
#include <ros/serialization.h>
#include <ros/builtin_message_traits.h>
#include <ros/message_operations.h>


namespace coalas_pkg
{
template <class ContainerAllocator>
struct CollAvoidServiceRequest_
{
  typedef CollAvoidServiceRequest_<ContainerAllocator> Type;

  CollAvoidServiceRequest_()
    : joy_speed_output(0)
    , joy_turn_output(0)
    , sensdor_data()  {
      sensdor_data.assign(0);
  }
  CollAvoidServiceRequest_(const ContainerAllocator& _alloc)
    : joy_speed_output(0)
    , joy_turn_output(0)
    , sensdor_data()  {
      sensdor_data.assign(0);
  }



   typedef uint8_t _joy_speed_output_type;
  _joy_speed_output_type joy_speed_output;

   typedef uint8_t _joy_turn_output_type;
  _joy_turn_output_type joy_turn_output;

   typedef boost::array<uint8_t, 11>  _sensdor_data_type;
  _sensdor_data_type sensdor_data;




  typedef boost::shared_ptr< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> const> ConstPtr;

}; // struct CollAvoidServiceRequest_

typedef ::coalas_pkg::CollAvoidServiceRequest_<std::allocator<void> > CollAvoidServiceRequest;

typedef boost::shared_ptr< ::coalas_pkg::CollAvoidServiceRequest > CollAvoidServiceRequestPtr;
typedef boost::shared_ptr< ::coalas_pkg::CollAvoidServiceRequest const> CollAvoidServiceRequestConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >::stream(s, "", v);
return s;
}

} // namespace coalas_pkg

namespace ros
{
namespace message_traits
{



// BOOLTRAITS {'IsFixedSize': True, 'IsMessage': True, 'HasHeader': False}
// {'coalas_pkg': ['/home/user/catkin_ws/src/coalas_pkg/msg'], 'sensor_msgs': ['/opt/ros/indigo/share/sensor_msgs/cmake/../msg'], 'std_msgs': ['/opt/ros/indigo/share/std_msgs/cmake/../msg'], 'geometry_msgs': ['/opt/ros/indigo/share/geometry_msgs/cmake/../msg']}

// !!!!!!!!!!! ['__class__', '__delattr__', '__dict__', '__doc__', '__eq__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_parsed_fields', 'constants', 'fields', 'full_name', 'has_header', 'header_present', 'names', 'package', 'parsed_fields', 'short_name', 'text', 'types']




template <class ContainerAllocator>
struct IsFixedSize< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "87cf6d3d77ac07cce2aeae7d3497d452";
  }

  static const char* value(const ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x87cf6d3d77ac07ccULL;
  static const uint64_t static_value2 = 0xe2aeae7d3497d452ULL;
};

template<class ContainerAllocator>
struct DataType< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "coalas_pkg/CollAvoidServiceRequest";
  }

  static const char* value(const ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "uint8 joy_speed_output\n\
uint8 joy_turn_output\n\
uint8[11] sensdor_data\n\
";
  }

  static const char* value(const ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.joy_speed_output);
      stream.next(m.joy_turn_output);
      stream.next(m.sensdor_data);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct CollAvoidServiceRequest_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::coalas_pkg::CollAvoidServiceRequest_<ContainerAllocator>& v)
  {
    s << indent << "joy_speed_output: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.joy_speed_output);
    s << indent << "joy_turn_output: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.joy_turn_output);
    s << indent << "sensdor_data[]" << std::endl;
    for (size_t i = 0; i < v.sensdor_data.size(); ++i)
    {
      s << indent << "  sensdor_data[" << i << "]: ";
      Printer<uint8_t>::stream(s, indent + "  ", v.sensdor_data[i]);
    }
  }
};

} // namespace message_operations
} // namespace ros

#endif // COALAS_PKG_MESSAGE_COLLAVOIDSERVICEREQUEST_H