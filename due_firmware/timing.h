#ifndef COALAS_TIMING_H
#define COALAS_TIMING_H

#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
  #include "pins_arduino.h"
#endif

void timing_log(const char*);
void timing_log(const __FlashStringHelper*);

#endif // COALAS_TIMING_H