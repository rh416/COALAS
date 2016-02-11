#ifndef COALAS_TIMING_H
#define COALAS_TIMING_H

#if ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

void timing_log(const char*);
void timing_log(const __FlashStringHelper*);
void timing_log(const __FlashStringHelper*, const __FlashStringHelper*);

#endif // COALAS_TIMING_H