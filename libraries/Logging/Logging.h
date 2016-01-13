#ifndef Logging_H_
#define Logging_H_

#if defined(ARDUINO) && ARDUINO >= 100
  #include "arduino.h"
#else
  #include "WProgram.h"
#endif

// A macro used to print messages to the serial port that will be ignored by the host software
//#define PRINT_SAFE(str) ( SerialUSB.println("_" + String(str)) )
//#define PRINT_SAFE_LEN(str,len) ( SerialUSB.println("_" + String(str).substring(0, len)) )
#define PRINT_SAFE(str)
#define PRINT_SAFE_LEN(str, len)

#define LOG_FILE_MAX_FILENAME_LENGTH 12
#define LOG_FILE_MAX_EVENT_CODE_LENGTH 4

#define TIMING_TAG_LOGGING F("Logging")

class Logger{
  protected:
    char* _buffer[100];
    char _filename[LOG_FILE_MAX_FILENAME_LENGTH]; // = "datalog.txt";
    char _next_event_code[LOG_FILE_MAX_EVENT_CODE_LENGTH]; // = "";
    boolean _enabled = false;
    boolean _error_reported = false;
    uint32_t _time_from_system = 0;
    uint32_t _millis_when_time_was_received = 0;
  
  public:
    void start(const char*);
    void end();
    void log(uint8_t, uint32_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t, uint8_t);
    void printHistory();
    void recordEvent(const char*);
    void setTime(uint32_t);
    
    void getFilename(char*);
    boolean getEnabled();
    boolean getErrorReported();
    uint32_t getTime();
    uint32_t getTimeSet();

};

extern Logger logger;

#endif // Logging_H_