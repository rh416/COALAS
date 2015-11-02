#include "timing.h"

// Comment out the line below to disable timing logging
#define COALAS_TIMING_LOGGING_ENABLED

#ifdef COALAS_TIMING_LOGGING_ENABLED
  // If logging is enabled, create the functions

  void timing_log(const char *label){
    SerialUSB.print(micros());
    SerialUSB.print(F(",General,"));
    SerialUSB.print(label);
    SerialUSB.print(F(","));
    SerialUSB.println(micros());
  }

  void timing_log(const __FlashStringHelper *label){
    SerialUSB.print(micros());
    SerialUSB.print(F(",General,"));
    SerialUSB.print(label);
    SerialUSB.print(F(","));
    SerialUSB.println(micros());
  }

  void timing_log(const __FlashStringHelper *tag, const __FlashStringHelper *label){
    SerialUSB.print(micros());
    SerialUSB.print(F(","));
    SerialUSB.print(tag);
    SerialUSB.print(F(","));
    SerialUSB.print(label);
    SerialUSB.print(F(","));
    SerialUSB.println(micros());
  }

#else
  // If logging is disabled, just create empty functions
  void timing_log(const char *label){}
  void timing_log(const __FlashStringHelper *label){}
  void timing_log(const __FlashStringHelper *tag, const __FlashStringHelper *label){}

#endif