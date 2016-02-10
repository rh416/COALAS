#ifndef _HAPTIC_H
#define _HAPTIC_H

#if defined(ARDUINO) && ARDUINO >= 100
  #include "arduino.h"
#else
  #include "WProgram.h"
#endif

#include "IHaptic.h"

class Haptic : public IHaptic{
	
	private:
		int _pin;
		int _intensity = 255;
		int _on_duration = 0;
		int _off_duration = 0;
		boolean _current_state = false;
		int _next_delay;
		uint32_t _last_change_timestamp;
	
	public:
		Haptic(int);
		virtual void set_vibration_pattern(HapticPattern);
		virtual void set_vibration_pattern(int, int);
		virtual void set_intensity(int);
		virtual void update_vibration();	
};





#endif // _HAPTIC_H