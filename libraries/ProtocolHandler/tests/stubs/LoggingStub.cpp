#include "Arduino.h"

#include "Logging.h"
#include "LoggingStub.h"

void Logger::log(uint8_t runNumber, uint32_t currentTime, uint8_t userSpeed, uint8_t userTurn, uint8_t returnedSpeed, uint8_t returnedTurn, uint8_t potValue1, uint8_t potValue2, uint8_t potValue3, uint8_t potValue4){
}

void Logger::start(const char* filename){

  _PRINT_SAFE("in start");
  _PRINT_SAFE(filename);
  _PRINT_SAFE(logging_filename);


  strcpy(logging_filename, filename);
  logging_enabled = true;
  _PRINT_SAFE("exit");
}

void Logger::end(){
  
  // Prevent anymore logging
  _enabled = false;
}

void Logger::printHistory(){

  
}

void Logger::recordEvent(const char* eventDescription){
  
  strncpy(_next_event_code, eventDescription, LOG_FILE_MAX_EVENT_CODE_LENGTH);
}

void Logger::setTime(uint32_t unix_timestamp){

  // Record the new time and when it was set
  _time_from_system = unix_timestamp;
  _millis_when_time_was_received = millis();
}


Logger logger = Logger();
char logging_filename[50] = "datalog.txt";
boolean logging_enabled = false;