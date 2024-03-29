#include "ProtocolHandler.h"
#include <stdio.h>

//#define PRINT_SAFE(str)std::cout << str << std::endl

ProtocolHandler::ProtocolHandler(Print* protocol_serial_bus, Comms_485* sensor_serial_bus, const char* firmware_version, Logger* logger, IHaptic* haptic, IPotentialFields* fields){

  _protocol_serial_bus = protocol_serial_bus;
  _sensor_serial_bus = sensor_serial_bus;
  _firmware_version = firmware_version;
  _logger = logger;
  _haptic = haptic;
  _potential_fields = fields;
  
  // Initialise the command buffer
  resetCommandBuffer();
}



void ProtocolHandler::loop(){

  timing_log(TIMING_TAG_PROTOCOL, F("Start"));

  if(commandSendComplete){
    // Debugging code - safely print that the sending of the command is now complete
    PRINT_SAFE("Command Send Complete");

    if(isCommandValid()){
      // Debugging code - safely print that the command passed validation
      PRINT_SAFE("Command Valid");
      
      char response[RESPONSE_MAX_LENGTH] = "";

      //String response;                          // Initiate the string that will store the response
      //response.reserve(RESPONSE_MAX_LENGTH);    // Reserve some space in memory for the response
      //response = "";                            // Set its initial value

      char additionalData[RESPONSE_MAX_LENGTH - INDEX_COMMAND_ADDITIONAL_DATA] = "";
      //String additionalData;
      //additionalData.reserve(RESPONSE_MAX_LENGTH - INDEX_COMMAND_ADDITIONAL_DATA);
      //additionalData = "";

      // Create a buffer in which to store the command to be sent
      char command[COMMAND_MAX_LENGTH] = {0};

      // Define how many bytes to send - defaults to all of them
      byte command_bytes_to_send = COMMAND_MAX_LENGTH;

      // Set the first 3 characters in the new command
      command[0] = SENSOR_PROTOCOL_BYTE;
      command[1] = getCommandNode();
      command[2] = getCommandChar();
      
      PRINT_SAFE(command_info);
      PRINT_SAFE(additionalData);

      int commandIndex = 3;
      // Loop though the incoming command to check for any additional data that needs to be added to this command
      for(int i = INDEX_COMMAND_ADDITIONAL_DATA; i < COMMAND_MAX_LENGTH; i++){
        // Get the next piece of additional data
        char additionalDataChar = command_info[i];
        // Append the single character
        strncat(additionalData, &additionalDataChar, 1);
        //additionalData += command_info[i];
        // If the piece of additional data is a Null Character, record that we only want to send the characters up to this point to the RS-485 bus
        if(additionalDataChar == CHAR_NULL){
          command_bytes_to_send = commandIndex;
          // And break out of the loop
          break;
        }
        
        // Store the next piece of additional data in the command buffer
        command[commandIndex] = additionalDataChar;
        commandIndex++;
      }

      PRINT_SAFE(getCommandChar());

      // Determine what the command was asking for
      switch(getCommandChar()){

        // Return the boot message
        case 'B':
        {
          strcat(response, "B: Boot complete");
          break;
        }

        // Return the current firmware version
        case 'I':
        {
          strcat(response, "I:");
          strcat(response, _firmware_version);
          //response = "I:" + _firmware_version;
          break;
        }

        // Return whether or not the given Node is connected to the bus
        case 'S':
        {
          char command_node = getCommandNode();
          // Create an instance of the Node that we would like to check
          Sensor_Node * node = new Sensor_Node(command_node, _sensor_serial_bus);

          response[0] = 'S';
          response[1] = command_node;
          response[2] = ':';
          //response = "S" + String(getCommandNode()) + ":";
          if(node->exists()){
              response[3] = RESPONSE_YES;
            //strcat(response, &RESPONSE_YES);
            //response += "Y";
          }
          else {
              response[3] = RESPONSE_NO;
            //strcat(response, &RESPONSE_NO);
            //response += "N";
          }
          break;
        }

        // Logging
        case 'L':
        {
          char loggingCommand = (char)additionalData[0];
          PRINT_SAFE(loggingCommand);
          //String loggingCommand = additionalData.substring(0, 1);
          if(loggingCommand == 'E'){
            // End logging
            _logger->end();
          } else if (loggingCommand == '?'){
            // List log files
            _logger->printHistory();
          } else if (loggingCommand == 'S') {
            // Log file filename is all but the first two characters of additionalData
            char* filename = additionalData + 2;
            
            PRINT_SAFE("start logging");
            PRINT_SAFE(filename);
			            
            _logger->start(filename);
          }
          response[0] = RESPONSE_YES;
          break;
        }
		
		// Haptic Control
		case 'H':
		{
			char* controlPointer = additionalData;
			int intensity = strtol(controlPointer, &controlPointer, 16);
			int on_duration = strtol(controlPointer + 1, &controlPointer, 16);
			int off_duration = strtol(controlPointer + 1, &controlPointer, 16);
			
			_haptic->set_intensity(intensity);
			_haptic->set_vibration_pattern(on_duration, off_duration);
			
			response[0] = RESPONSE_YES;
			break;
		}
		
		// Potential Fields
		case 'P':
		{
			char* controlPointer = additionalData;
			int forwards = strtol(controlPointer, &controlPointer, 16);
			int backwards = strtol(controlPointer + 1, &controlPointer, 16);
			int sideways = strtol(controlPointer + 1, &controlPointer, 16);
			
			_potential_fields->set_field_forwards(forwards);
			_potential_fields->set_field_backwards(backwards);
			_potential_fields->set_field_sideways(sideways);
			
			response[0] = RESPONSE_YES;
			break;
		}
		
        // Log Event
        case 'E':
        {
          // Log event code is all but the first character of additionalData
          char* event_code = additionalData + 1;
          _logger->recordEvent(event_code);
          response[0] = RESPONSE_YES;
          break;
        }

        // Set the time
        case 'Z':
        {
          PRINT_SAFE("Set time");
          PRINT_SAFE(additionalData);
          uint32_t new_time = atoi(additionalData);
          PRINT_SAFE(new_time);
          _logger->setTime(new_time);
          response[0] = RESPONSE_YES;
		  PRINT_SAFE(response);
          break;
        }

        // The cases below can all be handled in (almost) the same way, so group them together
        case 'R':        // Return the given Node's configuration
        case 'C':        // Set the given Node's configuration
        case 'F':        // Return the data format of the given Node
        case 'D':        // Return the current sensor data for the given Node
        case 'T':        // Set the zone thresholds for the given Node
        case 'M':        // Set the ultrasound mode for the given Node
        case 'V':        // Request the firmware version from the given node
        {
          // Set the default response terminator to a Null Character
          char responseTerminationChar = CHAR_NULL;
          if(getCommandChar() == 'C'){
            // For configuration requests, we need to look for a . to indicate the end of the response
            responseTerminationChar = '.';
          }

          // Set response initial value
          switch(getCommandChar()){

            case 'R':
            {
              response[0] = 'C';
              break;
            }
            case 'F':
            case 'D':
            case 'V':
            {
              response[0] = getCommandChar();
              break;
            }
          }

          // Add the Node Id to the response
          if(response != ""){
			response[1] = getCommandNode();
			response[2] = ':';
            //response += String(getCommandNode()) + ":";
          }


          // These commands just need to be sent to the Node, and then have the response sent to back to the host software
          char passThroughResponse[RESPONSE_MAX_LENGTH];
          commandPassthrough(command, command_bytes_to_send, responseTerminationChar, passThroughResponse, RESPONSE_MAX_LENGTH);

          // If there was a response from the Node, append it to the output
          if(passThroughResponse[0] != '\0'){
            strcat(response, passThroughResponse);
            //response += passThroughResponse;
            } else {
            // Otherwise, set the output to be blank
            response[0] = '\0';
          }
          break;
        }
      }
	  
      if(isResponseAckNack(response)){
		  PRINT_SAFE(response);
		response[1] = getCommandNode();
		response[2] = ':';
		PRINT_SAFE(response);
	  
        char command_type[3] = "";
        getCommandType(command_type);
        strcat(response, command_type);
	  
	    //response += String(getCommandNode()) + ":";
        //response += String(getCommandType()); // + String(getCommandType());
      }

      if(response){
        // Send the response back to the host pc
		  PRINT_SAFE("Got here");
		  PRINT_SAFE(response);
		  PRINT_SAFE("Got here");
		  PRINT_SAFE(strlen(response));
        _protocol_serial_bus->println(response);
		  PRINT_SAFE("Got here");
        // Also print it with characters marking the beginning and end; uncomment for debugging purposes
        //PRINT_SAFE("Response: START_" + response + "_END");
      }
    } else {
        _protocol_serial_bus->println("_Invalid command");
        _protocol_serial_bus->print("_");
        _protocol_serial_bus->println(command_info);
    }

    // Reset the command buffer back to a known state, ready to receive new instructions
    resetCommandBuffer();
  }

  timing_log(TIMING_TAG_PROTOCOL, F("End"));
}

boolean ProtocolHandler::isCommandValid(){

  // Do a quick check that the command at least COULD be valid - does it have the & symbol in the correct places
  PRINT_SAFE(COMMAND_INDICATOR);
  PRINT_SAFE(command_info[INDEX_COMMAND_INDICATOR_1]);
  PRINT_SAFE(command_info[INDEX_COMMAND_INDICATOR_2]);
  return (command_info[INDEX_COMMAND_INDICATOR_1] == COMMAND_INDICATOR && command_info[INDEX_COMMAND_INDICATOR_2] == COMMAND_INDICATOR);
}

boolean ProtocolHandler::isResponseAckNack(const char* response){

  return (response[0] == RESPONSE_YES || response[0] == RESPONSE_NO);
}

char ProtocolHandler::getCommandNode(){

  // Retrieve the Node ID from the command buffer
  return (char) command_info[INDEX_COMMAND_NODE_ID];
}

char ProtocolHandler::getCommandChar(){

  // Retrieve the character that defines the desired command from the buffer
  return (char) command_info[INDEX_COMMAND_CHAR];
}

void ProtocolHandler::getCommandType(char* command_type){
	
	command_type[0] = command_info[INDEX_COMMAND_ID_1];
	command_type[1] = command_info[INDEX_COMMAND_ID_2];
	command_type[2] = '\0';
}

// For some requests we can simply pass through a slightly altered version of the request to the RS-485 bus, as much of the
//    protocol has been replicated in the UI
void ProtocolHandler::commandPassthrough(char command[], byte command_bytes_to_send, char responseTerminator, char* passthrough_response, uint8_t passthrough_response_max_length){

  // Send the command
  _sensor_serial_bus->send(command, command_bytes_to_send);
  PRINT_SAFE("Sent to RS-485:");
  PRINT_SAFE_LEN(command, command_bytes_to_send);

  // Record when this request was sent, so that we can use the timeout method if we need to
  long start_time = millis();

  uint8_t response_index = 0;
  
  // Wait for the response
  while(true){

    // If there is data available
    if(_sensor_serial_bus->available()){

      // Read it into a local variable
      char readChar = _sensor_serial_bus->read();

      // Add it to the response
      passthrough_response[response_index] = readChar;
      //response += readChar;

      // If a termination character is provided, and we see it - exit the loop
      if(responseTerminator != CHAR_NULL){
        if(readChar == responseTerminator){
          break;
        }
      }
    }

    // If no terminator character is provided, we have to use a timeout method - if RESPONSE_TIMEOUT_MS milliseconds
    //   have passed since we requested the data, then return what we have
    if(responseTerminator == CHAR_NULL){
      if(millis() - start_time > RESPONSE_TIMEOUT_MS){
        break;
      }
    }
        
    // If the response buffer is full, quite
    response_index++;
    if(response_index == passthrough_response_max_length){
      break;
    }
  }
}

void ProtocolHandler::resetCommandBuffer(){

  // Overwrite all values in the command buffer to a Null Character
  for(int i = 0; i < COMMAND_MAX_LENGTH; i++){
    command_info[i] = CHAR_NULL;
  }
  // Reset command variables
  commandSendComplete = false;
  commandCurrentIndex = 0;
}

void ProtocolHandler::reportObstacle(int node, int zone, int distance){
    
    char response[15];
    
    snprintf(response, 15, "O%d:%d%03X", node, zone, distance);
    
    _protocol_serial_bus->println(response);
}

void ProtocolHandler::clearAllObstacles(){
    
    _protocol_serial_bus->println("O0:C");
}

boolean ProtocolHandler::buffer(char inChar){
  
  //PRINT_SAFE(inChar);

  // If it's the end of the line, the command is complete
  if(inChar == COMMAND_END_INDICATOR){
    // Flag that the command has finished being sent and break out of this loop, so that we can process it
    commandSendComplete = true;
	// Add a NULL terminator to the command string so that it's treated as a string correctly
	command_info[commandCurrentIndex] = '\0';
    loop();
  }
  else if(commandCurrentIndex < COMMAND_MAX_LENGTH){
    // Otherwise, add the character to the command info buffer
    command_info[commandCurrentIndex] = inChar;
    // Uncomment for debugging
    //PRINT_SAFE("Received - Index = " + String(commandCurrentIndex) + "; Char = " + String(inChar));
    commandCurrentIndex++;
  }
  else {
    PRINT_SAFE("The command sent was too long");
	return false;
  }

  return true;
}