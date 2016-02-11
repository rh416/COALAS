#include "AxxTest.h"
#include "ProtocolHandler.h"
#include "fakes/HapticFake.h"

#define RESPONSE_SIZE 50

class HapticTests : public CxxTest::TestSuite{

  public:
    
	Print* testPrint = NULL;
	Comms_485* test485 = NULL;
	ProtocolHandler* ph;
	char incoming_data[50];
	HapticFake* haptic;
	PotentialFields* pf;
  
    void setUp(){
		haptic = new HapticFake(4);
		testPrint = new Print();
		ph = new  ProtocolHandler(testPrint, test485, "Version ID", &logger, haptic, pf);
    }
    
    void testInitialState(){
    
      TS_ASSERT_EQUALS(0, haptic->get_on_duration());
      TS_ASSERT_EQUALS(0, haptic->get_off_duration());
      TS_ASSERT_EQUALS(0, haptic->get_intensity());
    }
    
    void testHapticControl_On(){
    
		int intensity = 130;
		int on_duration = 300;
		int off_duration = 1500;
		
		std::string expected_response = "Y0:04\n";
	
		snprintf(incoming_data, 20, "&04&H0%03X,%03X,%03X\n", intensity, on_duration, off_duration);
		
		for(uint8_t i = 0; i < strlen(incoming_data); i++){
			ph->buffer(incoming_data[i]);
		}
			  
		TS_ASSERT_EQUALS(intensity, haptic->get_intensity());   
		TS_ASSERT_EQUALS(on_duration, haptic->get_on_duration());
		TS_ASSERT_EQUALS(off_duration, haptic->get_off_duration());
		
		TS_ASSERT_EQUALS(expected_response, testPrint->buffer);
    }
	
	void testHapticControl_Off(){
		
		int intensity = 0;
		int on_duration = 0;
		int off_duration = 0;
	
		std::string expected_response = "Y0:04\n";
	
		snprintf(incoming_data, 20, "&04&H0%03X,%03X,%03X\n", intensity, on_duration, off_duration);
		
		for(uint8_t i = 0; i < strlen(incoming_data); i++){
			ph->buffer(incoming_data[i]);
		}
			  
		TS_ASSERT_EQUALS(intensity, haptic->get_intensity());   
		TS_ASSERT_EQUALS(on_duration, haptic->get_on_duration());
		TS_ASSERT_EQUALS(off_duration, haptic->get_off_duration());
		TS_ASSERT_EQUALS(expected_response, testPrint->buffer);
	}
};