#include "GPSB_helper.h"
#include "haptic.h"
#include "Logging.h"
#include "ProtocolHandler.h"
#include "SYSIASS_485_comms.h"
#include "SYSIASS_sensor.h"
#include "Timing.h"

#define DEBUG_MODE_PIN 52

boolean DEBUG_MODE_ENABLED = false;

// Firmware information
const String firmware_version = "v0.1";  // Store the firmware version

Comms_485* comms_485 = 0; // RS485 Comms

ProtocolHandler serialProtocolHandler(&SerialUSB, comms_485, firmware_version);

void setup(){
  
  // Detect the state of the debug enable pin. If it is high, go into debug mode. Otherwise, launch into normal mode.
  // Make the mode pin an input
  pinMode(DEBUG_MODE_PIN, INPUT);
  // Read the mode pin
  DEBUG_MODE_ENABLED = (digitalRead(DEBUG_MODE_PIN) == LOW);
  // Force the program into driving assist mode
  DEBUG_MODE_ENABLED = true;
  
  if(DEBUG_MODE_ENABLED){
    
    // Make the haptic feedback vibrate for 1 second
    unsigned long bootupToneStartTime = millis();
    
    set_vibration_pattern(INSISTANT);
    
    while(1000 > millis() - bootupToneStartTime){
      update_vibration();
    }
    
    // Turn off the haptic feedback
    set_vibration_pattern(OFF);
    update_vibration();
    
    diagnostics_setup();
  } else {   
    algorithm_setup(); 
  }
  
  // Send a notification that the boot process is complete
  SerialUSB.println("B:Boot Complete");
}

void loop(){
  
  if(DEBUG_MODE_ENABLED){
    diagnostics_loop();
  } else {
    algorithm_loop();
  }

  // Make serialEvent run for the Native port
  if(SerialUSB.available()){
    serialEvent();
  }
}

void serialEvent() {

  

  // Get all the bytes in the buffer
  while(SerialUSB.available()){
    // Get the next available character
    if(serialProtocolHandler.buffer(SerialUSB.read())){
      break;
    }
  }
}