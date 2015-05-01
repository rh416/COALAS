#include <spi.h>
#include <SD.h>

void setup(){
  // Initiliase SD card
  SD.begin(4);
}

void loop(){
  
  // Open log file
  File testLog = SD.open("log_test.csv");
  
  // Build the log string: timestamp and ten bytes of data
  String logData = millis() + ",ABCD,EFGH";
  
  // Write log string
  testLog.println(logData);
  
  // Close log file
  testLog.close();  
}