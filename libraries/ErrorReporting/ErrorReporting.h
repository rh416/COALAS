
#ifndef ERRORS_H
#define ERRORS_H

#if defined(ARDUINO) && ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

// The errors found here must match with those defined in the documentation
//    see - https://github.com/rh416/COALAS/wiki/Wheelchair-Communication-Protocol
enum Error {
  UNKNOWN = 0,
  LOG_SD_CARD_INACCESSIBLE = 50
  //ANOTHER_KIND_OF_ERROR = 35
};

void report_error(Error error);

#endif // ERRORS