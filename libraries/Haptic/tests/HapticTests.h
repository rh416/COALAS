#include "AxxTest.h"
#include "Haptic.h"

class HapticTests : public CxxTest::TestSuite{

  public:
	
	int pin = 4;
	Haptic* h;
  
	void setUp(){
		
		// Create an instance of Haptic
		h = new Haptic(pin);
		// Reset the clock
		AxxTest_millis(0);
	}
  
	void testIntialisation(){
		
		TS_ASSERT_PIN_MODE(pin, OUTPUT);
	}
	
	void testInitialPinState(){
		
		TS_ASSERT_ANALOG_WRITE(pin, LOW);
	}
	
	void testIntensity(){
		
		int intensity = 189;
		
		h->set_vibration_pattern(-1, 0);
		h->set_intensity(intensity);
		h->update_vibration();
		AxxTest_millis(5);
		h->update_vibration();
		TS_ASSERT_ANALOG_WRITE(pin, intensity);
	}
	
	void testPattern(){
		
		h->set_vibration_pattern(500, 500);
		
		h->update_vibration();
		TS_ASSERT_ANALOG_WRITE(pin, 255);
		
		h->update_vibration();
		TS_ASSERT_ANALOG_WRITE(pin, 255);
		
		AxxTest_millis(499);
		h->update_vibration();
		TS_ASSERT_ANALOG_WRITE(pin, 255);
		
		AxxTest_millis(500);
		h->update_vibration();
		TS_ASSERT_ANALOG_WRITE(pin, LOW);
		
		AxxTest_millis(999);
		h->update_vibration();
		TS_ASSERT_ANALOG_WRITE(pin, LOW);
		
		AxxTest_millis(1000);
		h->update_vibration();
		TS_ASSERT_ANALOG_WRITE(pin, 255);
	}
  
};