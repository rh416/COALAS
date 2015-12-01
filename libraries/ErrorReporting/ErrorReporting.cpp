#include "ErrorReporting.h"

void report_error(Error error){
  
  SerialUSB.print("E:");
  SerialUSB.println(error);
}