#include "Haptic.h"

Haptic::Haptic(int pin){
	
	_pin = pin;
	pinMode(pin, OUTPUT);
	analogWrite(pin, LOW);
	_last_change_timestamp = millis();
    
    set_vibration_pattern(OFF);
}

void Haptic::set_vibration_pattern(HapticPattern pattern){
	
	switch(pattern){
		case OFF:
			set_vibration_pattern(0, -1);
			break;
			
		case SUBTLE:
			set_vibration_pattern(85, 800);
			break;
		
		case WARNING:
			set_vibration_pattern(100, 400);
			break;		
		
		case INSISTANT:
			set_vibration_pattern(200, 200);
			break;

		case CONTINUOUS:
			set_vibration_pattern(-1, 0);
			break;
		
	}	
}

void Haptic::set_vibration_pattern(int on_duration, int off_duration){
	
	_on_duration = on_duration;
	_off_duration = off_duration;	
    _next_delay = 0;
}

void Haptic::set_intensity(int intensity){
	
	_intensity = intensity;
}

void Haptic::update_vibration(){
	
	uint32_t current_timestamp = millis();
	if(current_timestamp - _last_change_timestamp >= _next_delay){
		
		if(_current_state){
			analogWrite(_pin, LOW);
			_next_delay = _off_duration;
		} else {
			analogWrite(_pin, _intensity);
			_next_delay = _on_duration;
		}
		
		_current_state = !_current_state;
		_last_change_timestamp = current_timestamp;			
	}	
}