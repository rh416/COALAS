// ================================== Data logging on SD card function
struct LoggingStatus{
  
    boolean enabled = true;
    String filename = "datalog.txt";
    String nextEventDescription = "";
    unsigned long timeFromSystem = 0;
    unsigned long millisWhenTimeWasReceived = 0;
    boolean errorReported = false;
  };
  
LoggingStatus currentLoggingStatus = LoggingStatus();


void logging(){
  
  // Only carry on if logging is currently enabled
  if(currentLoggingStatus.enabled){
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
    currentLoggingStatus.nextEventDescription = "";

    // We need to convert the String filename to a char array
    const int dataFilenameBufferSize = 12;
    char dataFilenameBuffer[dataFilenameBufferSize];
    currentLoggingStatus.filename.toCharArray(dataFilenameBuffer, dataFilenameBufferSize);

    // open the file. note that only one file can be open at a time,
    // so you have to close this one before opening another.
    File dataFile = SD.open(dataFilenameBuffer, FILE_WRITE);

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
        // print to the serial port for debugging
        Serial.print("error opening "); 
        Serial.println(currentLoggingStatus.filename); 
        currentLoggingStatus.errorReported = true;
      }        
    }
  }    
}

void logging_start(String filename){
  
  currentLoggingStatus.enabled = true;
  currentLoggingStatus.filename = filename;
  currentLoggingStatus.errorReported = false;
  
   // We need to convert the String filename to a char array
   const int dataFilenameBufferSize = 12;
   char dataFilenameBuffer[dataFilenameBufferSize];
   currentLoggingStatus.filename.toCharArray(dataFilenameBuffer, dataFilenameBufferSize);
}

void logging_end(){
  
  // Prevent anymore logging
  currentLoggingStatus.enabled = false;
  // Output the last log file name and size
  Serial.print("L:");
  Serial.print(currentLoggingStatus.filename);
  Serial.print(":");
  
  char filename[12];
  currentLoggingStatus.filename.toCharArray(filename, 12);
  File log_file = SD.open(filename);
    
  Serial.println(log_file.size());
  
  log_file.close();
}

String logging_list(){
    
  File log_root = SD.open("/");
    
  while(true){
    File log_file = log_root.openNextFile();
    
    if(!log_file){
      log_root.rewindDirectory();
      break;
    } else if (!log_file.isDirectory()){
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
  
  currentLoggingStatus.nextEventDescription = eventDescription;
}