#include "cxxtest-4.4/cxxtest/TestSuite.h"
#include "../ProtocolHandler.h"
#include "stubs/LoggingStub.h"

#include <iostream>

class LoggingTests : public CxxTest::TestSuite{

  public:
    
    Print* testPrint = NULL;
    Comms_485* test485 = NULL;
    ProtocolHandler* ph;
    char* incoming_data;
  
    void setUp(){
    
      ph = new  ProtocolHandler(testPrint, test485, "Hello", &logger);
    }
    
    void testInitialState(){
      // Ensure the initial state of logging is as expected
      TS_ASSERT_SAME_DATA(logging_filename, "datalog.txt", strlen(logging_filename));
      TS_ASSERT_EQUALS(logging_enabled, false);   
    }
    
    void testLoggingStart(){
    
      incoming_data = (char*)"&03&L:Sfile.txt\n";
    
      for(uint8_t i = 0; i < 20; i++){
        ph->buffer(incoming_data[i]);
      }
          
      ph->loop();
      const char* expected_filename = "file.txt";
      TS_ASSERT_SAME_DATA(logging_filename, expected_filename, strlen(logging_filename));
      TS_ASSERT_EQUALS(logging_enabled, true);   
    }
    
    void testFilenameLength(){
    
      incoming_data = (char*)"&03&L:Sfilename.txt\n";
    
      for(uint8_t i = 0; i < 20; i++){
        ph->buffer(incoming_data[i]);
      }
          
      ph->loop();
      const char* expected_filename = "filename.txt";
      TS_ASSERT_SAME_DATA(logging_filename, expected_filename, LOG_FILE_MAX_FILENAME_LENGTH);
    }
    
    void testRecordEvent(){
      
    
    }
};

/*

 g++ -o runner -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\cxxtest-4.4 runner.cpp -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\axxtest\ -I D:\GitHub\COALAS\libraries\SYSIASS_Sensor\ -I D:\GitHub\COALAS\libraries\SYSIASS_485_Comms\ -std=c++11 -I D:\GitHub\COALAS\libraries\Logging\ -I D:\GitHub\COALAS\libraries\Timing stubs\Logging.cpp -I D:\GitHub\COALAS\libraries\ErrorReporting\ ..\ProtocolHandler.cpp stubs\Timing.cpp stubs\SYSIASS_485_Comms.cpp stubs\SYSIASS_Sensor.cpp stubs\Arduino.cpp axxtest\Print.cpp
 
 */