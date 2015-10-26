#define COMMAND_MAX_LENGTH 50
#define CHAR_NULL '\0'
#define RESPONSE_TIMEOUT_MS 100
#define RESPONSE_MAX_LENGTH 60

// Store the information that represents a command from the Config UI
char command_info[COMMAND_MAX_LENGTH];
byte commandCurrentIndex = 0;
boolean commandSendComplete = false;

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

void handleProtocolMessages(){
  
  if(commandSendComplete){
    // Debugging code - safely print that the sending of the command is now complete
    //PRINT_SAFE("Command Send Complete");

    if(isCommandValid()){
      // Debugging code - safely print that the command passed validation
      //PRINT_SAFE("Command Valid");

      String response;                          // Initiate the string that will store the response
      response.reserve(RESPONSE_MAX_LENGTH);    // Reserve some space in memory for the response
      response = "";                            // Set its initial value
      
      String additionalData;
      additionalData.reserve(RESPONSE_MAX_LENGTH - INDEX_COMMAND_ADDITIONAL_DATA);
      additionalData = "";
    
      // Create a buffer in which to store the command to be sent
      char command[COMMAND_MAX_LENGTH];
    
      // Define how many bytes to send - defaults to all of them
      byte command_bytes_to_send = COMMAND_MAX_LENGTH;

      // Set the first 3 characters in the new command
      command[0] = SENSOR_PROTOCOL_BYTE;
      command[1] = getCommandNode();
      command[2] = getCommandChar();

      int commandIndex = 3;
      // Loop though the incoming command to check for any additional data that needs to be added to this command
      for(int i = INDEX_COMMAND_ADDITIONAL_DATA; i < COMMAND_MAX_LENGTH; i++){
        // Get the next piece of additional data
        char additionalDataChar = command_info[i];
        additionalData += command_info[i];
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

      //PRINT_SAFE(getCommandChar());

      // Determine what the command was asking for
      switch(getCommandChar()){

        // Return the current firmware version
        case 'I':
        {
          response = "I:" + firmware_version;
          break;
        }

        // Return whether or not the given Node is connected to the bus
        case 'S':
        {
          // Create an instance of the Node that we would like to check
          Sensor_Node * node = new Sensor_Node(getCommandNode(), comms_485_diag);

          response = "S" + String(getCommandNode()) + ":";
          if(node->exists()){
            response += "Y";
          }
          else {
            response += "N";
          }
          break;
        }
        
        // Logging
        case 'L':
        {
          String loggingCommand = additionalData.substring(0, 1);
          if(loggingCommand == "E"){
            // End logging
            logging_end();
          } else if (loggingCommand == "?"){
            // List log files
            logging_list();
          } else if (loggingCommand == "S") {
            logging_start(additionalData.substring(2));
          }
          response = "Y";
          break;
        }
        
        // Log Event
        case 'E':
        {
          logging_event(additionalData.substring(1));
          response = "Y";
          break;  
        }
        
        // Set the time
        case 'Z':
        {
          // Record the new time and when it was set
          currentLoggingStatus.timeFromSystem = additionalData.toInt();
          currentLoggingStatus.millisWhenTimeWasReceived = millis();
          PRINT_SAFE("Time set");
          PRINT_SAFE(currentLoggingStatus.timeFromSystem);
          response = "Y";
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
              response = "C";
              break;
            }
            case 'F':
            case 'D':
            case 'V':
            {
              response = String(getCommandChar());
              break;
            }
          }
        
          // Add the Node Id to the response
          if(response != ""){
            response += String(getCommandNode()) + ":";
          }
        

          // These commands just need to be sent to the Node, and then have the response sent to back to the host software
          String passThroughResponse = commandPassthrough(command, command_bytes_to_send, responseTerminationChar);
        
          // If there was a response from the Node, append it to the output
          if(passThroughResponse != ""){
            response += passThroughResponse;
            } else {
            // Otherwise, set the output to be blank
            response = "";
          }
        
          break;
        }
      }
    
      if(isResponseAckNack(response)){
        response += String(getCommandNode()) + ":";
        response += String(getCommandType()); // + String(getCommandType());
      }

      if(response){
        // Send the response back to the host pc
        Serial.println(response);
        // Also print it with characters marking the beginning and end; uncomment for debugging purposes
        //PRINT_SAFE("Response: START_" + response + "_END");
      }
    }

    // Reset the command buffer back to a known state, ready to receive new instructions
    resetCommandBuffer();
  }
}  

boolean isCommandValid(){

  // Do a quick check that the command at least COULD be valid - does it have the & symbol in the correct places
  return (command_info[INDEX_COMMAND_INDICATOR_1] == COMMAND_INDICATOR && command_info[INDEX_COMMAND_INDICATOR_2] == COMMAND_INDICATOR);
}

boolean isResponseAckNack(String response){

  return (response == "Y" || response == "N");
}

char getCommandNode(){

  // Retrieve the Node ID from the command buffer
  return (char) command_info[INDEX_COMMAND_NODE_ID];
}

char getCommandChar(){

  // Retrieve the character that defines the desired command from the buffer
  return (char) command_info[INDEX_COMMAND_CHAR];
}

String getCommandType(){

  char type[2];
  type[0] = command_info[INDEX_COMMAND_ID_1];
  type[1] = command_info[INDEX_COMMAND_ID_2];

  return String(type);
}

// For some requests we can simply pass through a slightly altered version of the request to the RS-485 bus, as much of the
//    protocol has been replicated in the UI
String commandPassthrough(char command[], byte command_bytes_to_send, char responseTerminator){

  // Send the command
  comms_485_diag->send(command, command_bytes_to_send);
  PRINT_SAFE("Sent to RS-485:");
  PRINT_SAFE_LEN(command, command_bytes_to_send);

  // Record when this request was sent, so that we can use the timeout method if we need to
  long start_time = millis();

  // Create a buffer to store the response in 
  String response;
  response.reserve(RESPONSE_MAX_LENGTH);

  // Wait for the response
  while(true){

    // If there is data available
    if(comms_485_diag->available()){

      // Read it into a local variable
      char readChar = comms_485_diag->read();

      // Add it to the response
      response += readChar;

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
  }

  return response;
}

void resetCommandBuffer(){

  // Overwrite all values in the command buffer to a Null Character
  for(int i = 0; i < COMMAND_MAX_LENGTH; i++){
    command_info[i] = CHAR_NULL;
  }
  // Reset command variables
  commandSendComplete = false;
  commandCurrentIndex = 0;
}

void serialEvent() {

  // Get all the bytes in the buffer
  while(Serial.available()){
    // Get the next available character
    char inChar = (char)Serial.read();
    
    //PRINT_SAFE(inChar);

    // If it's the end of the line, the command is complete
    if(inChar == COMMAND_END_INDICATOR){
      // Flag that the command has finished being sent and break out of this loop, so that we can process it
      commandSendComplete = true;
      break;
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
    }
  }    
}