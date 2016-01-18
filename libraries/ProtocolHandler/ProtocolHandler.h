#ifndef ProtocolHandler_H_
#define ProtocolHandler_H_

#if defined(ARDUINO) && ARDUINO >= 100
  #include "arduino.h"
#else
  #include "WProgram.h"
#endif

#include "SYSIASS_485_comms.h"
#include "SYSIASS_sensor.h"
#include "Logging.h"
#include "Timing.h"

#define TIMING_TAG_PROTOCOL F("Protocol")

#define COMMAND_MAX_LENGTH 50
#define CHAR_NULL '\0'
#define RESPONSE_TIMEOUT_MS 100
#define RESPONSE_MAX_LENGTH 60

// Constants showing where command information is stored in the buffer
const byte INDEX_COMMAND_INDICATOR_1 = 0;
const byte INDEX_COMMAND_ID_1 = 1;
const byte INDEX_COMMAND_ID_2 = 2;
const byte INDEX_COMMAND_INDICATOR_2 = 3;
const byte INDEX_COMMAND_CHAR = 4;
const byte INDEX_COMMAND_NODE_ID = 5;
const byte INDEX_COMMAND_ADDITIONAL_DATA = 6;

const char COMMAND_INDICATOR = '&';        // This character indicates the start of a command
const char COMMAND_END_INDICATOR = '\n';   // This character indicates the end of a command

const char SENSOR_PROTOCOL_BYTE = '&';     // The byte that starts all commands over the RS-485 bus

const char RESPONSE_YES = 'Y';
const char RESPONSE_NO = 'N';



class ProtocolHandler{
  private:
    Print* _protocol_serial_bus;
    Comms_485* _sensor_serial_bus;
    const char* _firmware_version;
    Logger* _logger;
    
    // Store the information that represents a command from the Config UI
    char command_info[COMMAND_MAX_LENGTH];
    byte commandCurrentIndex = 0;
    boolean commandSendComplete = false;

    boolean isCommandValid();
    boolean isResponseAckNack(const char*);
    char getCommandNode();
    char getCommandChar();
    void getCommandType(char*);
    void commandPassthrough(char[], byte, char, char*, uint8_t);

  public:
    ProtocolHandler(Print*, Comms_485*, const char*, Logger*);
    boolean buffer(char);
    void loop();
    void resetCommandBuffer();
};

#endif // ProtocolHandler_H_