#include "Logging.h"
#include <stdio.h>

void Logger::log(uint8_t runNumber, uint32_t currentTime, uint8_t userSpeed, uint8_t userTurn, uint8_t returnedSpeed, uint8_t returnedTurn, uint8_t potValue1, uint8_t potValue2, uint8_t potValue3, uint8_t potValue4){
}

void Logger::start(const char* filename){

  PRINT_SAFE("in start");
  PRINT_SAFE(filename);
  PRINT_SAFE(_filename);


  strcpy(_filename, filename);
  _enabled = true;
  PRINT_SAFE("exit");
}

void Logger::end(){
  
  // Prevent anymore logging
  _enabled = false;
}

void Logger::printHistory(){

  
}

void Logger::recordEvent(const char* eventDescription){
  
  strncpy(_next_event_code, eventDescription, 5);
}

void Logger::setTime(uint32_t unix_timestamp){

  // Record the new time and when it was set
  _time_from_system = unix_timestamp;
  _millis_when_time_was_received = millis();
}

uint32_t Logger::getTime(){
	
	return _time_from_system;
}

void Logger::getFilename(char* filename_buffer, byte buffer_size){
	
	snprintf(filename_buffer, buffer_size, "%s", _filename);
}

boolean Logger::getEnabled(){
	
	return _enabled;
}


Logger logger = Logger();