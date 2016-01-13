#include "cxxtest-4.4/cxxtest/TestSuite.h"
#include "../ProtocolHandler.h"

#include <iostream>

class TimeSettingTests : public CxxTest::TestSuite{

  public:
    
    Print* testPrint = NULL;
    Comms_485* test485 = NULL;
    ProtocolHandler* ph;
    char* incoming_data;
  
    void setUp(){
    
      ph = new  ProtocolHandler(testPrint, test485, "Hello", &logger);
    }
    
    void testInitialState(){
    
      TS_ASSERT_EQUALS(logger.getTime(), 0);
    }
    
    void testTimeSet1(){
    
      incoming_data = (char*)"&03&Z0fdfdsf\n";
    
      for(uint8_t i = 0; i < 20; i++){
        ph->buffer(incoming_data[i]);
      }
          
      ph->loop();
      TS_ASSERT_EQUALS(logger.getTime(), 0);   
    }
    
    void testTimeSet2(){
    
      incoming_data = (char*)"&03&Z0-654646\n";
    
      for(uint8_t i = 0; i < 20; i++){
        ph->buffer(incoming_data[i]);
      }
          
      ph->loop();
      TS_ASSERT_EQUALS(logger.getTime(), -654646);   
    }
    
    void testTimeSet3(){
    
      incoming_data = (char*)"&03&Z0123445\n";
    
      for(uint8_t i = 0; i < 20; i++){
        ph->buffer(incoming_data[i]);
      }
          
      ph->loop();
      TS_ASSERT_EQUALS(logger.getTime(), 123445);   
    }
};

/*

 g++ -o runner -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\cxxtest-4.4 runner.cpp -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\axxtest\ -I D:\GitHub\COALAS\libraries\SYSIASS_Sensor\ -I D:\GitHub\COALAS\libraries\SYSIASS_485_Comms\ -std=c++11 -I D:\GitHub\COALAS\libraries\Logging\ -I D:\GitHub\COALAS\libraries\Timing stubs\Logging.cpp -I D:\GitHub\COALAS\libraries\ErrorReporting\ ..\ProtocolHandler.cpp stubs\Timing.cpp stubs\SYSIASS_485_Comms.cpp stubs\SYSIASS_Sensor.cpp stubs\Arduino.cpp axxtest\Print.cpp
 
 */