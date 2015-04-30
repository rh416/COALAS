#include "errors.h"

#define LOG_FILE_MAX_FILENAME_LENGTH 12
#define LOG_FILE_EVENT_CODE_LENGTH 4

// ================================== Data logging on SD card function
struct LoggingStatus{
  
    // Default to logging being disabled
    boolean enabled = false;
    char filename[LOG_FILE_MAX_FILENAME_LENGTH] = "datalog.txt";
    char nextEventDescription[LOG_FILE_EVENT_CODE_LENGTH] = "";
    unsigned long timeFromSystem = 0;
    unsigned long millisWhenTimeWasReceived = 0;
    boolean errorReported = false;
  };
  
LoggingStatus currentLoggingStatus = LoggingStatus();

void logging(){
  
  // Only carry on if logging is currently enabled and no error has been reported
  if(currentLoggingStatus.enabled && currentLoggingStatus.errorReported === false){
    timer = millis();
    stopWatch = timer - currentTime;
    String dataString = "";
  
    // Keep these fields to maintain compatibility with Michael's system
    dataString += String(runNumber);
    dataString += ",";
    dataString += String(stopWatch);
    dataString += ",";
    
    // Timestamp - needs to have been set via serial port to be anything useful
    unsigned long currentSeconds = 0;
    unsigned int currentMilliseconds = 0;
    
    if(currentLoggingStatus.timeFromSystem > 0){
      unsigned long millisSinceTimeSet = timer - currentLoggingStatus.millisWhenTimeWasReceived;
      currentSeconds = currentLoggingStatus.timeFromSystem + (millisSinceTimeSet / 1000);
      currentMilliseconds = millisSinceTimeSet % 1000;
    }
    dataString += String(currentSeconds);
    dataString += ",";
    dataString += String(currentMilliseconds);
    dataString += ",";
    
    
    // Log variables
    dataString += String(userSpeed);
    dataString += ",";
    dataString += String(userTurn);
    dataString += ",";
    dataString += String(returnedSpeed);
    dataString += ",";
    dataString += String(returnedTurn);
    dataString += ",";
    dataString += String(potValue1);
    dataString += ",";
    dataString += String(potValue2);
    dataString += ",";
    dataString += String(potValue3);
    dataString += ",";
    dataString += String(potValue4);
    dataString += ",";
    dataString += String(currentLoggingStatus.nextEventDescription);
    
    // Reset the next event description to be blank
    //      setting the first character to be a terminating character achieves this
    // See - http://arduino.stackexchange.com/a/3178 for explanation
    currentLoggingStatus.nextEventDescription[0] = (char)0;

    // open the file. note that only one file can be open at a time,
    // so you have to close this one before opening another.
    File dataFile = SD.open(currentLoggingStatus.filename, FILE_WRITE);

    // if the file is available, write to it:
    if (dataFile) {
      dataFile.println(dataString);
      dataFile.close();
    
      // print to the serial port for debugging
      //Serial.println(dataString);
    }
    // if the file isn't open, pop up an error:
    else {
      if(!currentLoggingStatus.errorReported){
        report_error(LOG_SD_CARD_INACCESSIBLE);
        currentLoggingStatus.errorReported = true;
      }        
    }
  }    
}

void logging_start(String filename){
    
  filename.toCharArray(currentLoggingStatus.filename, LOG_FILE_MAX_FILENAME_LENGTH);
  currentLoggingStatus.enabled = true;
  currentLoggingStatus.errorReported = false;
}

void logging_end(){
  
  // Prevent anymore logging
  currentLoggingStatus.enabled = false;
  // Output the last log file name and size
  Serial.print("L:");
  Serial.print(currentLoggingStatus.filename);
  Serial.print(":");
  
  // Set the default filesize to 0
  uint32_t filesize = 0;
  
  // If we're able to open the log file, read its filesize
  if(File log_file = SD.open(currentLoggingStatus.filename)){
    filesize = log_file.size();
    log_file.close();
  }
    
  Serial.println(filesize);
}

void logging_list(){
    
  File log_root = SD.open("/");
  
  // Make sure we're at the start of the root directory
  log_root.rewindDirectory();
    
  while(File log_file = log_root.openNextFile()){
    if (!log_file.isDirectory()){
      Serial.print("L:");
      Serial.print(log_file.name());
      Serial.print(":");
      Serial.println(log_file.size());
    }
    log_file.close();
  }
  log_root.close();
}

void logging_event(String eventDescription){
  
  eventDescription.toCharArray(currentLoggingStatus.nextEventDescription, LOG_FILE_EVENT_CODE_LENGTH);
}