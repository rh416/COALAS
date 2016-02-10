#include "HapticFake.h"

HapticFake::HapticFake(int pin){
	
	// Do nothing
}

void HapticFake::set_vibration_pattern(HapticPattern pattern){
	
	_pattern = pattern;
}

void HapticFake::set_vibration_pattern(int on_duration, int off_duration){
	
	_on_duration = on_duration;
	_off_duration = off_duration;
}

void HapticFake::set_intensity(int intensity){
	
	_intensity = intensity;
}

void HapticFake::update_vibration(){
	
	// Do nothing
}

int HapticFake::get_on_duration(){
	
	return _on_duration;
}

int HapticFake::get_off_duration(){
	
	return _off_duration;
}

int HapticFake::get_intensity(){
	
	return _intensity;
}