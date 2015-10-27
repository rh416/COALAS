// Generated by gencpp from file coalas_pkg/CollAvoidService.msg
// DO NOT EDIT!


#ifndef COALAS_PKG_MESSAGE_COLLAVOIDSERVICE_H
#define COALAS_PKG_MESSAGE_COLLAVOIDSERVICE_H

#include <ros/service_traits.h>


#include <coalas_pkg/CollAvoidServiceRequest.h>
#include <coalas_pkg/CollAvoidServiceResponse.h>


namespace coalas_pkg
{

struct CollAvoidService
{

typedef CollAvoidServiceRequest Request;
typedef CollAvoidServiceResponse Response;
Request request;
Response response;

typedef Request RequestType;
typedef Response ResponseType;

}; // struct CollAvoidService
} // namespace coalas_pkg


namespace ros
{
namespace service_traits
{


template<>
struct MD5Sum< ::coalas_pkg::CollAvoidService > {
  static const char* value()
  {
    return "7c2dd6e553bf4bcac771ba0b4ab02457";
  }

  static const char* value(const ::coalas_pkg::CollAvoidService&) { return value(); }
};

template<>
struct DataType< ::coalas_pkg::CollAvoidService > {
  static const char* value()
  {
    return "coalas_pkg/CollAvoidService";
  }

  static const char* value(const ::coalas_pkg::CollAvoidService&) { return value(); }
};


// service_traits::MD5Sum< ::coalas_pkg::CollAvoidServiceRequest> should match 
// service_traits::MD5Sum< ::coalas_pkg::CollAvoidService > 
template<>
struct MD5Sum< ::coalas_pkg::CollAvoidServiceRequest>
{
  static const char* value()
  {
    return MD5Sum< ::coalas_pkg::CollAvoidService >::value();
  }
  static const char* value(const ::coalas_pkg::CollAvoidServiceRequest&)
  {
    return value();
  }
};

// service_traits::DataType< ::coalas_pkg::CollAvoidServiceRequest> should match 
// service_traits::DataType< ::coalas_pkg::CollAvoidService > 
template<>
struct DataType< ::coalas_pkg::CollAvoidServiceRequest>
{
  static const char* value()
  {
    return DataType< ::coalas_pkg::CollAvoidService >::value();
  }
  static const char* value(const ::coalas_pkg::CollAvoidServiceRequest&)
  {
    return value();
  }
};

// service_traits::MD5Sum< ::coalas_pkg::CollAvoidServiceResponse> should match 
// service_traits::MD5Sum< ::coalas_pkg::CollAvoidService > 
template<>
struct MD5Sum< ::coalas_pkg::CollAvoidServiceResponse>
{
  static const char* value()
  {
    return MD5Sum< ::coalas_pkg::CollAvoidService >::value();
  }
  static const char* value(const ::coalas_pkg::CollAvoidServiceResponse&)
  {
    return value();
  }
};

// service_traits::DataType< ::coalas_pkg::CollAvoidServiceResponse> should match 
// service_traits::DataType< ::coalas_pkg::CollAvoidService > 
template<>
struct DataType< ::coalas_pkg::CollAvoidServiceResponse>
{
  static const char* value()
  {
    return DataType< ::coalas_pkg::CollAvoidService >::value();
  }
  static const char* value(const ::coalas_pkg::CollAvoidServiceResponse&)
  {
    return value();
  }
};

} // namespace service_traits
} // namespace ros

#endif // COALAS_PKG_MESSAGE_COLLAVOIDSERVICE_H
