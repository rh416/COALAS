#include "AxxTest.h"
#include "ProtocolHandler.h"

class LoggingTests : public CxxTest::TestSuite{

  public:
    
    Print* testPrint = NULL;
    Comms_485* test485 = NULL;
    ProtocolHandler* ph;
    char incoming_data[50];
	Haptic* haptic;
	PotentialFields* pf;
  
    void setUp(){
    
		testPrint = new Print();
		ph = new ProtocolHandler(testPrint, test485, "Verion ID", &logger, haptic, pf);
    }
    
    void testInitialState(){
      // Ensure the initial state of logging is as expected
	  char filename_buffer[LOG_FILE_MAX_FILENAME_LENGTH];
	  logger.getFilename(filename_buffer, LOG_FILE_MAX_FILENAME_LENGTH);
      TS_ASSERT_SAME_DATA(filename_buffer, "datalog.txt", strlen(filename_buffer));
      TS_ASSERT_EQUALS(logger.getEnabled(), false);   
    }
    
    void testLoggingStart(){
    
		snprintf(incoming_data, 30, "%s", "&03&L0S:file.txt\n");
      //incoming_data = (char*)"&03&L0S:file.txt\n";
	  std::string expected_response = "Y0:03\n";
    
      for(uint8_t i = 0; i < strlen(incoming_data); i++){
        ph->buffer(incoming_data[i]);
      }
          
      const char* expected_filename = "file.txt";
	  char filename_buffer[LOG_FILE_MAX_FILENAME_LENGTH];
	  logger.getFilename(filename_buffer, LOG_FILE_MAX_FILENAME_LENGTH);
  
      TS_ASSERT_SAME_DATA(filename_buffer, expected_filename, strlen(filename_buffer));
      TS_ASSERT_EQUALS(logger.getEnabled(), true);   
	  TS_ASSERT_EQUALS(expected_response, testPrint->buffer);
    }
     
    void testFilenameLength(){
    
		snprintf(incoming_data, 30, "%s", "&03&L0S:filename.txt\n");
      //incoming_data = (char*)"&03&L0S:filename.txt\n";
    
      for(uint8_t i = 0; i < strlen(incoming_data); i++){
        ph->buffer(incoming_data[i]);
      }
          
      const char* expected_filename = "filename.txt";
	  char filename_buffer[LOG_FILE_MAX_FILENAME_LENGTH];
	  logger.getFilename(filename_buffer, LOG_FILE_MAX_FILENAME_LENGTH);
	  
      TS_ASSERT_SAME_DATA(filename_buffer, expected_filename, strlen(filename_buffer));
    } 
	
	void testLoggingEnd(){
		
		snprintf(incoming_data, 30, "%s", "&03&L0E\n");
		//incoming_data = (char*)"&03&L0E\n";
		std::string expected_response = "Y0:03\n";
    
      for(uint8_t i = 0; i < strlen(incoming_data); i++){
        ph->buffer(incoming_data[i]);
      }
	  
	  TS_ASSERT_EQUALS(false, logger.getEnabled());
	  TS_ASSERT_EQUALS(expected_response, testPrint->buffer);
	}
};

/*

 g++ -o runner -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\cxxtest-4.4 runner.cpp -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\axxtest\ -I D:\GitHub\COALAS\libraries\SYSIASS_Sensor\ -I D:\GitHub\COALAS\libraries\SYSIASS_485_Comms\ -std=c++11 -I D:\GitHub\COALAS\libraries\Logging\ -I D:\GitHub\COALAS\libraries\Timing stubs\Logging.cpp -I D:\GitHub\COALAS\libraries\ErrorReporting\ ..\ProtocolHandler.cpp stubs\Timing.cpp stubs\SYSIASS_485_Comms.cpp stubs\SYSIASS_Sensor.cpp stubs\Arduino.cpp axxtest\Print.cpp
 
 */