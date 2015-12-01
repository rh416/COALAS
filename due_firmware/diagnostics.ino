#include "SYSIASS_sensor.h"
#include "SYSIASS_485_comms.h"

// A macro used to print messages to the serial port that will be ignored by the host software
//#define PRINT_SAFE(str) ( SerialUSB.println("_" + String(str)) )
//#define PRINT_SAFE_LEN(str,len) ( SerialUSB.println("_" + String(str).substring(0, len)) )

#define PC_BAUD_RATE 115200


// Make comms_485_diag available globally within the code
Comms_485* comms_485_diag = 0; // RS485 Comms

void diagnostics_setup(){

  // Connect to the PC
  SerialUSB.begin(PC_BAUD_RATE);

  // Connect to the RS-485 bus
  comms_485_diag = new Comms_485();

  // Empty the 485 buffer
  while(comms_485_diag->available()){
    comms_485_diag->read();
  }
   
  // Reset the command buffer to a known state
  serialProtocolHandler.resetCommandBuffer();
}

void diagnostics_loop(){
   
  serialProtocolHandler.loop();
}