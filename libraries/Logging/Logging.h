#ifndef Logging_H_
#define Logging_H_

#if defined(ARDUINO) && ARDUINO >= 100
  #include "arduino.h"
#else
  #include "WProgram.h"
#endif

// A macro used to print messages to the serial port that will be ignored by the host software
#define PRINT_SAFE(str) ( SerialUSB.println("_" + String(str)) )
#define PRINT_SAFE_LEN(str,len) ( SerialUSB.println("_" + String(str).substring(0, len)) )

#define LOG_FILE_MAX_FILENAME_LENGTH 12
#define LOG_FILE_EVENT_CODE_LENGTH 4

#define TIMING_TAG_LOGGING F("Logging")

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
  
void logging_init();
void logging(uint8_t, uint32_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t);
void logging_start(String);
void logging_end();
void logging_list();
void logging_event(String);
void logging_set_time(uint32_t);

extern LoggingStatus currentLoggingStatus;

#endif // Logging_H_