#include "Logging.h"

#include "SPI.h"
#include "SD.h"
#include "timing.h"
#include "ErrorReporting.h"


void Logger::log(uint8_t runNumber, uint32_t currentTime, uint8_t userSpeed, uint8_t userTurn, uint8_t returnedSpeed, uint8_t returnedTurn, uint8_t potValue1, uint8_t potValue2, uint8_t potValue3, uint8_t potValue4){
  
  timing_log(TIMING_TAG_LOGGING, F("Start"));
  
  // Only carry on if logging is currently enabled and no error has been reported
  if(_enabled && _error_reported == false){
    uint32_t timer = millis();
    uint32_t stopWatch = timer - currentTime;
  
    // Keep these fields to maintain compatibility with Michael's system
		
	/*
	
	TODO: Make work!
	strcat(_buffer, String(runNumber));
    strcat(_buffer, ",");
    strcat(_buffer, String(stopWatch));
    strcat(_buffer, ",");

	*/
    
    // Timestamp - needs to have been set via serial port to be anything useful
    unsigned long currentSeconds = 0;
    unsigned int currentMilliseconds = 0;
    
    /*
    if(currentLoggingStatus.timeFromSystem > 0){
      unsigned long millisSinceTimeSet = timer - currentLoggingStatus.millisWhenTimeWasReceived;
      currentSeconds = currentLoggingStatus.timeFromSystem + (millisSinceTimeSet / 1000);
      currentMilliseconds = millisSinceTimeSet % 1000;
    }
    loggingDataString += String(currentSeconds);
    loggingDataString += ",";
    loggingDataString += String(currentMilliseconds);
    loggingDataString += ",";
    
    
    // Log variables
    loggingDataString += String(userSpeed);
    loggingDataString += ",";
    loggingDataString += String(userTurn);
    loggingDataString += ",";
    loggingDataString += String(returnedSpeed);
    loggingDataString += ",";
    loggingDataString += String(returnedTurn);
    loggingDataString += ",";
    loggingDataString += String(potValue1);
    loggingDataString += ",";
    loggingDataString += String(potValue2);
    loggingDataString += ",";
    loggingDataString += String(potValue3);
    loggingDataString += ",";
    loggingDataString += String(potValue4);
    loggingDataString += ",";
    loggingDataString += String(currentLoggingStatus.nextEventDescription);
    
    // Reset the next event description to be blank
    //      setting the first character to be a terminating character achieves this
    // See - http://arduino.stackexchange.com/a/3178 for explanation
    currentLoggingStatus.nextEventDescription[0] = (char)0;

    SerialUSB.println(loggingDataString);
    loggingDataString = "";

    if(false){
    //if(loggingIterationCount == 10){
      // open the file. note that only one file can be open at a time,
      // so you have to close this one before opening another.
      File dataFile = SD.open(currentLoggingStatus.filename, FILE_WRITE);
        
      // if the file is available, write to it:
      if (dataFile) {
        dataFile.println(loggingDataString);
        dataFile.close();
      }
        // if the file isn't open, pop up an error:
      else {
        if(!currentLoggingStatus.errorReported){
          report_error(LOG_SD_CARD_INACCESSIBLE);
          currentLoggingStatus.errorReported = true;
        }
      }         
        
      loggingDataString = "";
      loggingIterationCount = 1;
    } else {
      loggingDataString += "\n";
      loggingIterationCount++;
    }  
  }
  */
  
  timing_log(TIMING_TAG_LOGGING, F("End"));
  }
}

void Logger::start(const char* filename){

  strcpy(_filename, filename);
  _enabled = true;
  _error_reported = false;
}

void Logger::end(){
  
  // Prevent anymore logging
  _enabled = false;
  
  // Output the last log file name and size
  SerialUSB.print("L:");
  SerialUSB.print(_filename);
  SerialUSB.print(":");
  
  // Set the default filesize to 0
  uint32_t filesize = 0;
  
  // If we're able to open the log file, read its filesize
  /*
  if(File log_file = SD.open(_filename)){
    filesize = log_file.size();
    log_file.close();
  }
  */
    
  SerialUSB.println(filesize);
}

void Logger::printHistory(){

	/*

  File log_root = SD.open("/");
  
  // Make sure we're at the start of the root directory
  log_root.rewindDirectory();
    
  while(File log_file = log_root.openNextFile()){
    if (!log_file.isDirectory()){
      SerialUSB.print("L:");
      SerialUSB.print(log_file.name());
      SerialUSB.print(":");
      SerialUSB.println(log_file.size());
    }
    log_file.close();
  }
  log_root.close(); 

  */
}

void Logger::recordEvent(const char* eventDescription){
  
  strncpy(_next_event_code, eventDescription, LOG_FILE_MAX_EVENT_CODE_LENGTH);
}

void Logger::setTime(uint32_t unix_timestamp){

  // Record the new time and when it was set
  _time_from_system = unix_timestamp;
  _millis_when_time_was_received = millis();
}

void Logger::getFilename(char* filename_buffer){

  // TODO: Make work
}

boolean Logger::getEnabled(){

  return _enabled;
}

boolean Logger::getErrorReported(){

  return _error_reported;
}

uint32_t Logger::getTime(){

  return _time_from_system;
}

uint32_t Logger::getTimeSet(){

  return _millis_when_time_was_received;
}