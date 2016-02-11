#include "AxxTest.h"
#include "ProtocolHandler.h"

class PotenetialFieldsTests : public CxxTest::TestSuite{

  public:
    
    Print* testPrint = NULL;
    Comms_485* test485 = NULL;
    ProtocolHandler* ph;
    char incoming_data[50];
	Haptic* haptic;
	PotentialFields* pf;
  
    void setUp(){
    
		testPrint = new Print();
		pf = new PotentialFields();
		ph = new  ProtocolHandler(testPrint, test485, "Version ID", &logger, haptic, pf);
    }
    
    void testInitialState(){
    
      TS_ASSERT_EQUALS(pf->get_field_forwards(), 0);
      TS_ASSERT_EQUALS(pf->get_field_backwards(), 0);
      TS_ASSERT_EQUALS(pf->get_field_sideways(), 0);
    }
	
	void testSetFields(){
    
		int forwards = 130;
		int backwards = 200;
		int sideways = 90;	
		
		std::string expected_response = "Y0:04\n";
	
		snprintf(incoming_data, 30, "&04&P0%03X,%03X,%03X\n", forwards, backwards, sideways);
		
		
		for(uint8_t i = 0; i < strlen(incoming_data); i++){
			ph->buffer(incoming_data[i]);
		}
		
		TS_ASSERT_EQUALS(forwards, pf->get_field_forwards());   
		TS_ASSERT_EQUALS(backwards, pf->get_field_backwards());
		TS_ASSERT_EQUALS(sideways, pf->get_field_sideways());
		
		TS_ASSERT_EQUALS(expected_response, testPrint->buffer);
	}
};

/*

 g++ -o runner -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\cxxtest-4.4 runner.cpp -I D:\GitHub\COALAS\libraries\ProtocolHandler\tests\axxtest\ -I D:\GitHub\COALAS\libraries\SYSIASS_Sensor\ -I D:\GitHub\COALAS\libraries\SYSIASS_485_Comms\ -std=c++11 
 -I D:\GitHub\COALAS\libraries\Logging\ 
 -I D:\GitHub\COALAS\libraries\Timing 
 stubs\Logging.cpp
 -I D:\GitHub\COALAS\libraries\ErrorReporting\ 
 ..\ProtocolHandler.cpp 
 stubs\Timing.cpp 
 stubs\SYSIASS_485_Comms.cpp
 stubs\SYSIASS_Sensor.cpp 
 stubs\Arduino.cpp 
 axxtest\Print.cpp
 
 */