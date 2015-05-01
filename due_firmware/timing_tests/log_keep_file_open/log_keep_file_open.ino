#include <spi.h>
#include <SD.h>

File testLog;

void setup(){
  // Initiliase SD card
  SD.begin(4);
}

void loop(){
  
  // http://forum.arduino.cc/index.php?topic=49649.0
  
  // Open log file
  testLog = SD.open("log_test.csv");
  
  // Build the log string: timestamp and twenty bytes of data
  String logData = millis() + ",ABCD,EFGH,IJKL,MNOP";
  
  // Write log string
  testLog.println(logData);
  
  // Close log file
  testLog.close();  
}