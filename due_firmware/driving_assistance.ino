/*
 SYSIASS Master code file for the clinical trial powered wheelchair, Version 1.0
 Communicates with sensor nodes and GPSB and runs the embeded collision avoidance algorithms
 Authors: Michael Gillham and Martin Henderson from the University of Kent (Contribution by Bruce Ferrer from ISEN Lille)
 Last edited 18/02/15 
 
 NOTES:
         1. Explanation of the obstacle avoidance and dynamic model (DLAFF) are dealt with elsewhere and should not be changed without understanding the principle
         2. The enumeration of the nodes, and sensor location, does not match the documentation; if changes are made please update code if necessary
         
  * Communication with PC *
  
 == Commands from PC ==
 s  Scan for sensors
 j  return Joystick position
 n  send New sensor data
 c  Connect to gpsb
 d  Disconnect from gpsb
 g  set Gpsb drive packet contents
 
 == Info from DUE ==
 s Formatted details of each sensor node discovered
 j Formatted joystick position
 n [sensor id]:[zone 1 data][zone 2 data][zone 3 data]
 */

#include <GPSB_helper.h>
#include <SYSIASS_485_comms.h>
#include <SYSIASS_sensor.h>
#include <haptic.h>
#include <SPI.h>
#include <SD.h>
#include "errors.h"


//enum Verbosity {SILENT, VERBOSE}; // doesn't compile in Arduino IDE
#define Verbosity int
#define SILENT 0
#define VERBOSE 1

// ====== Obstacle avoidance Michael Gillham 31/08/14 ================================

#define A1pin 1      // Analog pin right camera
#define AOpin 0      // Analog pin left camera
#define CLKpin 60    // digital pin 2
#define SIpin 61     // digital pin 3
#define NPIXELS 128  // number of pixels in line camera array

// Line camera
int PixelA[NPIXELS];   // array to capture analogue pixel read
int PixelB[NPIXELS];   // array to capture analogue pixel read
int i;
int outputB = 0;
int outputA = 0;

// Dynamic model
double F_desired = 0;     // human input device desired drive trajectory
double T_desired = 0;     // human input device desired turn trajectory
int speed = 128;
int turn = 128;

const int mass = 1;          //  ### IMPORTANT DO NOT IGNORE THE NOTE BELOW #####
const int inertia = 1;       // In this application, because this is connected to the Dynamic controller mapping, mass and inertia MUST be <= 1

float width = 0.55;
double Left_F_desired = 128;    // left motor dynamic model desired trajectory
double Right_F_desired = 128;   // right motor dynamic model desired trajectory
float rightDamping = 1;
float leftDamping = 1;
float turnDamping = 1;
float leftDoorwayDamping = 1;
float rightDoorwayDamping = 1;

// Sensor data
int S_rightFront = 0;
int S_right45 = 0;
int S_right90 = 0;
int S_leftFront = 0;
int S_left45 = 0;
int S_left90 = 0; 
int S_rear = 0;
int S_leftRear45 = 0;
int S_leftRear90 = 0;
int S_rightRear45 = 0;
int S_rightRear90 = 0;
int IR_rightFront = 0;
int IR_right45 = 0;
int IR_right90 = 0;
int IR_leftFront = 0;
int IR_left45 = 0;
int IR_left90 = 0;
int rightFront = 0;
int right45 = 0;
int right90 = 0;
int leftFront = 0;
int left45 = 0;
int left90 = 0;

// Potenial force field adjustment, reads in from 4 pots to adjust the field in each zone
int doorwayFlag = 0;
int directionFlag = 0;
int rotationFlag = 0;
const int analogInPin1 = A8;  
const int analogInPin2 = A9;
const int analogInPin3 = A10;
const int analogInPin4 = A11;
const int digital = 66;
int potValue1 = 0;        
int potValue2 = 0;
int potValue3 = 0;
int potValue4 = 0;

// #### WARNING these nodes run anticlockwise on the EDECT/SYSIASS chair ######

int zone_2_forward = 0;     // values set by potentiometers DLAFF_collision_avoidance
int zone_2_left = 0;        // values set by potentiometers DLAFF_collision_avoidance
int zone_1_forward = 0;     // values set by potentiometers DLAFF_collision_avoidance
int zone_1_right = 0;       // values set by potentiometers DLAFF_collision_avoidance

int zone_4_backward = 150;    // value pre-set by experimentation, not during operation
int zone_4_right = 100;       // value pre-set by experimentation, not during operation
int zone_3_backward = 150;    // value pre-set by experimentation, not during operation
int zone_3_left = 100;        // value pre-set by experimentation, not during operation

const int spare_relay = 51;
const int illumination_relay = 49;

// Safety ======================================

volatile int isolated = 0;  //switch is on  means we don't want a correction
int oldIsolatedState = 0;   // store the old isolated state so that we know to increment the run counter
void scan_for_sensors(int);

const int kill_pin = 46;
const int isolate_pin = 48;
const int haptic_pin = 2;

// tests the Isolate pin, NB: disconnection of switch should default to ISOLATE
void check_isolate_switch()
 {
  if (digitalRead(isolate_pin) == HIGH) // isolate when high
    { 
    isolated = 1;
    }
  else if (digitalRead(isolate_pin) == LOW) // engage when switch is closed (pulled to ground)
    {
    // engage_switch();
    isolated = 0;
   }    
}

// Helpful Misc ================================
unsigned long previousMillis = 0;

// Turn, speed and related ======================

// convert between a char value (0x80 is neutral)
// and an int value (0 is neutral)
int convert_to_int(const unsigned char value)
  {
  return value - 0x80;
  }

bool sending = false;

// convert between an int value (0 is neutral) and a char value (0x80 is neutral)
unsigned char convert_to_char(const int value)
  {
  return ((value + 0x80)%0xff);
  }

Sensor_Node* sensor_nodes[10] = {0,0,0,0,0,0,0,0,0,0}; //Sensor Node Abstraction

Comms_485* comms_485 = 0; // RS485 Comms


// logging
File myFile;             // SD card logging file
int  userSpeed = 128;      // logging
int  userTurn = 128;       // logging
int  returnedSpeed = 0;  // logging
int  returnedTurn = 0;   // logging
int  runNumber = 0;      // file number
int  previous = 0;       // set flag
unsigned int  timer = 0;        // run clock
int stopWatch = 0;
int currentTime = 0;

// Constant loop timing
uint32_t lastLoopTime = 0;
uint16_t constantLoopTime = 0; // Make every loop take at least this many miliseconds, whether or not the algorithm is running

// ============================================== Setup ===========

void algorithm_setup() { // logging on to RS485 and GPSB updated and modified by Michael Gillham 31/08/14 to prevent intialisation drop out.
  
  delay(100);  // Power-up delay
  SerialUSB.begin(115200);  // Setup comms to PC 
  delay(100);
  PRINT_SAFE("Startup begin...");
  
 // =============== initialise log on to GPSB
   
  GPSB_setup(SERIAL_2); // Setup comms to GPSB
  delay(100);
  engage_switch();   
  GPSB_log_off();
  delay(1000);
  GPSB_log_on();       // Log on to the GPSB interface with Dynamics Control PWC system
  delay(5000); 

  SerialUSB.println("Logged on to GPSB");
  delay(200);
  
  
  logging_init();
 
 
  comms_485 = new Comms_485();  // Setup comms to RS_485 node communication
  delay(100); 
  while (comms_485->available() > 0)
    {
    comms_485->read();
    }   

   
  // Pins ========================================
  
  init_haptic();
  pinMode(isolate_pin, INPUT);  // Set up isolate switch
  pinMode(digital, INPUT);      // Set up potential force field switch
  
  // ============== initialise data structures ==========

  scan_for_sensors(VERBOSE); // scan for sensors and report values to serial print
  SerialUSB.println();
  SerialUSB.println("Setup Complete: EDECT Version 1 18.02.15"); 
  delay(200);
  
  // ============== initialise line cameras ==== Michael Gillham 31/08/14
  
  pinMode (SIpin, OUTPUT);
  pinMode (CLKpin, OUTPUT);
  pinMode (49, OUTPUT);  
  digitalWrite (CLKpin, HIGH);  
  digitalWrite (49, LOW);       // switches on illumination relay when HIGH, call it elsewhere
   
 // ======== There is no kill switch attached. However if so desired one can be attached to kill code, although essentially the above fuction serves the same purpose ====  
 // attachInterrupt(isolate_pin, check_isolate_switch, CHANGE);


// =============== initialise communication to SPI data storage device

SerialUSB.print("Initializing SD card...");
  // On the Ethernet Shield, CS is pin 4. It's set as an output by default.
  // Note that even if it's not used as the CS pin, the hardware SS pin 
  // (10 on most Arduino boards, 53 on the Mega) must be left as an output 
  // or the SD library functions will not work. 
   pinMode(10, OUTPUT);
   
  if (!SD.begin(4)) {
    SerialUSB.println("initialization failed!");
    return;
   }
   SerialUSB.println("initialization done.");
  
  // open the file. note that only one file can be open at a time,
  // so you have to close this one before opening another.
  myFile = SD.open("test.txt", FILE_WRITE);
  

  // if the file opened okay, write to it:
  if (myFile) {
    SerialUSB.println("Writing to test.txt...");
    myFile.println("EDECT Version 1 18.02.15");
   // close the file:
    myFile.close();
    SerialUSB.println("done.");
    } 
  else {
    // if the file didn't open, print an error:
    SerialUSB.println("error opening test.txt");
    }
  
   // re-open the file for reading:
   myFile = SD.open("test.txt");
   if (myFile) {
     SerialUSB.println("test.txt:");
    
    // read from the file until there's nothing else in it:
     while (myFile.available()) {
    	SerialUSB.write(myFile.read());
     }
    // close the file:
     myFile.close();
     } 
    else {
  	// if the file didn't open, print an error:
    SerialUSB.println("error opening test.txt");
   }
 }


// ======================================== Main Loop: Michael Gillham 18th February 2015 ===========

void algorithm_loop() {
  
   uint32_t thisLoopTime = millis();
   
   timing_log(F("Loop Begin"));
  
   if(thisLoopTime - lastLoopTime >= constantLoopTime){
     lastLoopTime = thisLoopTime;
     
    // Check to see if the isolated switch has changed state
    if(isolated != oldIsolatedState){
      // Increment run number
      runNumber++;
      // Reset the run start time
      currentTime = millis();
      // Save the new state of isolate
      oldIsolatedState = isolated;
    }
  
  timing_log(F("Start protocol handling"));
  handleProtocolMessages();          // Handle any protocol messages that have been sent over the serial port
  timing_log(F("End protocol handling"));
  timing_log(F("Start logging to SD Card"));
  logging();
  timing_log(F("End logging to SD Card"));
  
    if (directionFlag == 0 && rotationFlag == 0){
      set_vibration_pattern(OFF); // Turn off vibration if joystick centred
    }
  
    check_isolate_switch();           // check for isolation of obstacle avoidance software, 0 means run obstacle avoidance 1 means isolate
    timing_log(F("Start getting Joystick data from the GPSB"));
    GPSB_update_joystick_position();  // continuously grab joystick data from GPSB
    timing_log(F("End getting Joystick data"));
    speed = get_joystick_speed();     // Take user speed input from data stream
    turn = get_joystick_turn();       // Take user turn input from data stream
    userSpeed = speed;                // logging
    userTurn = turn;                  // logging
  
    if(isolated == 1){
      timing_log(F("Start sending Joystick data back directly"));
      returnJoystickDirect();  
      timing_log(F("End sending Joystick data back directly"));
    } else if(isolated == 0){
      timing_log(F("Start collision avoidance"));
      DLAFF_collision_avoidance ();    // Doorway passing, use linecameras to improve doorway resolution and lock-in
      hapticFeedback ();               // Set haptic feedback to user
      update_vibration();              // Send haptic feedback to joystick vibration motor
      Dynamic_model();                 // Use the DLAFF 'dynamic model' to generate kinematic valid output back to GPSB
      timing_log(F("End collision avoidance"));
    }   
    timing_log(F("End of Loop"));
  }    
}




// ================================== Obstacle avoidance algorithms: Michael Gillham 31st August 2014 ======= 

void lineCameras() {
   
  digitalWrite (CLKpin, LOW);
  delayMicroseconds (6);
  digitalWrite (SIpin, HIGH);
  digitalWrite (CLKpin, HIGH);
  digitalWrite (SIpin, LOW);  
 
    for (i = 0; i < NPIXELS; i++) {
    PixelA[i] = analogRead (AOpin);
    PixelB[i] = analogRead (A1pin);
    digitalWrite (CLKpin, LOW);   
    digitalWrite (CLKpin, HIGH);
    }
       outputA = 0;
       for (i = 0; i < NPIXELS; i++) {
        if (PixelA[i] <150)                   // Threshold value empirically obtained
           outputA = outputA +1;
        else   outputA = outputA;
           }         
       outputB = 0;
       for (i = 0; i < NPIXELS; i++) {
        if (PixelB[i] <150)                  // Threshold value empirically obtained
           outputB = outputB +1;
        else  outputB = outputB;
          }        
}

void Dynamic_model() {      // DLAFF model not fully applied, in this case only!
                            //  ### IMPORTANT DO NOT IGNORE THE NOTE BELOW #####
                            // In this application, because this is connected to the Dynamic controller mapping, mass and inertia MUST be <= 1 
                            
#define TIMING_TAG_DYNAMIC_MODEL F("Dynamic Model")
                            
 timing_log(TIMING_TAG_DYNAMIC_MODEL, F("Start"));
 F_desired = mass * speed;             // Dynamic variable is from joystic and static variable determined by measurement                                    
 T_desired = inertia * turn;           
 F_desired = map (F_desired, 1, 255, -127, 127);   
 T_desired = map (T_desired, 1, 255, -127, 127);   
 
 timing_log(TIMING_TAG_DYNAMIC_MODEL, F("Position A"));
    
     Left_F_desired = rightDamping*(F_desired - (0.5 * T_desired));  
     Right_F_desired = leftDamping*(F_desired + (0.5 * T_desired)); 
 
     speed = 0.5*(Left_F_desired + Right_F_desired);
     turn = (Right_F_desired - Left_F_desired);
     timing_log(TIMING_TAG_DYNAMIC_MODEL, F("Position B"));
     
     speed = map (speed, -127, 127, 1, 255);   
     turn = map (turn, -127, 127, 1, 255); 
      
     speed = constrain(speed, 1, 255);
     turn = constrain(turn, 1, 255);
     timing_log(TIMING_TAG_DYNAMIC_MODEL, F("Position C"));
     
     if (directionFlag == 0){
       speed = 128;}
     if (rotationFlag == 0){  
       turn = 128;}
     
     returnedSpeed = speed;  // logging
     returnedTurn = turn;    // logging
     timing_log(TIMING_TAG_DYNAMIC_MODEL, F("Start sending data"));
     GPSB_set_speed_turn(speed, turn);  // Return modified joystick values (1-255) to system
     GPSB_send_drive_packet(); 
     timing_log(TIMING_TAG_DYNAMIC_MODEL, F("End"));
}

void hapticFeedback (){
    
    timing_log(F("Start haptic feedback")) ;
    if (leftDamping < 0.5 || rightDamping < 0.5)
         {set_vibration_pattern(SUBTLE);}      
    else if (leftDamping < 0.2 || rightDamping < 0.2)
         {set_vibration_pattern(CONTINUOUS);} 
    else {set_vibration_pattern(OFF);}    
      
    timing_log(F("End haptic feedback")) ;
}

void DLAFF_collision_avoidance (){    // Returns DLAFF sensor modified drive signals to dynamic model
 
// Check the desired inputs from joystick data and set direction and rotation flags for switching obstacle avoidance zones 
// Use bandwidth to allow some room for not quite moving forward/backward due to user joystick error
   if (speed < 127){
    directionFlag = 1;}           // Platform is traversing backward
       else if (speed > 129){
       directionFlag = 2;}        // Platform is traversing forward
       else if (speed >= 127 && speed <=129){
       directionFlag = 0;}        // Platform has no forward or backward motion
   
   if (turn < 128){
    rotationFlag = 1;}            // Platform is turning left
       else if (turn > 128){
       rotationFlag = 2;}         // Platform is turning right
       else if (turn == 128){
       rotationFlag = 0;}         // Platform has no rotational component
       
// Read in the potential field adjustment form the potentiometer box

if (digitalRead(digital) == HIGH){ // set forward potential force field values from pots, else not
    potValue1 = analogRead (analogInPin1);        
    potValue2 = analogRead (analogInPin2);
    potValue3 = analogRead (analogInPin3);
    potValue4 = analogRead (analogInPin4);
    
    // map the potetiometer values to the range 0cm to 350cm, 
    
    zone_2_forward = map (potValue1, 0, 1024, 0, 50);
    zone_2_left = map (potValue4, 0, 1024, 0, 50);
    zone_1_forward = map (potValue2, 0, 1024, 0, 50);
    zone_1_right = map (potValue3, 0, 1024, 0, 50);
  
}

// Clear all sensor values
   S_rightFront = 0;
   S_right45 = 0;
   S_right90 = 0;
   S_leftFront = 0;
   S_left45 = 0;
   S_left90 = 0; 
   S_rear = 0;
   S_leftRear45 = 0;
   S_leftRear90 = 0;
   S_rightRear45 = 0;
   S_rightRear90 = 0;
   IR_rightFront = 0;
   IR_right45 = 0;
   IR_right90 = 0;
   IR_leftFront = 0;
   IR_left45 = 0;
   IR_left90 = 0;
   rightFront = 0;
   right45 = 0;
   right90 = 0;
   leftFront = 0;
   left45 = 0;
   left90 = 0;
   
       
   if (directionFlag == 2){   // If platform is travelling forward use the frontal sensor zones and also check for doorway    
      // Read in forward sensor data, zones 1 and 2 and map them to the desired potetial force field     
     forwardObstacleAvoidance ();
  //   doorwayLineCameras ();       // doorway turned off for edect testing
     }
    
   else if (directionFlag == 1){        
      // Read in backward sensor data, zones 3 and 4
     backwardObstacleAvoidance ();} 
    
   else if (directionFlag == 0){        
        
         if (rotationFlag == 2){ 
            // If platform is rotating right 
            clockwiseObstacleAvoidance ();
             } 
         else if (rotationFlag == 1){ 
               // If platform is rotating left 
              antiClockwiseObstacleAvoidance ();
             }   
  
     }
    
}
     
void doorwayLineCameras (){  //  Simple doorway detection assumption if doorway is detected this function turns on the linecameras and illuminators, 
                             //  then it compares the data with sonar and infrared to deterine which sensor type has detected closest obstacle
    
        if ((leftFront < 50 || left45 < 50 || left90 < 20) && (rightFront < 50 || right45 < 50 || right90 < 20))  
           {doorwayFlag = 1;}                                          
        if ((leftFront > 49 && rightFront > 49) || directionFlag != 2) // If we reverse back and not enter doorway, abort.
           {doorwayFlag = 0;}    
                     
        if (doorwayFlag == 1){                             // Run doorway edge detection using line cameras
              digitalWrite (illumination_relay, HIGH);     // Turn on infrared illumination
              lineCameras();                               
              leftDoorwayDamping = (128-outputA);
              leftDoorwayDamping = map(leftDoorwayDamping, 30, 80, 0, 350);    // adjust values according to callibration with doorway
              leftDoorwayDamping = constrain(leftDoorwayDamping, 0, 350);
              leftDoorwayDamping = 1-(1/(exp(leftDoorwayDamping)));
              rightDoorwayDamping = (128-outputB);
              rightDoorwayDamping = map(rightDoorwayDamping, 30, 80, 0, 350);  // adjust values according to callibration with doorway
              rightDoorwayDamping = constrain(rightDoorwayDamping, 0, 350);
              rightDoorwayDamping = 1-(1/(exp(rightDoorwayDamping)));            
              }
        else if (doorwayFlag == 0)
              {digitalWrite (illumination_relay, LOW);       // Turn off infrared illumination
              rightDoorwayDamping = 128;
              leftDoorwayDamping = 128;}  
              
     // Mitigation for doorway detection, if there is no doorway then ignore it           
      if (leftDoorwayDamping < leftDamping)                   
         {leftDamping = leftDoorwayDamping;} 
         else if (leftDoorwayDamping >= leftDamping)  
                 {leftDamping = leftDamping;}
      if (rightDoorwayDamping < rightDamping)
         {rightDamping = rightDoorwayDamping;} 
         else if (rightDoorwayDamping >= rightDamping)  
                 {rightDamping = rightDamping;}   
            
}  
  
void forwardObstacleAvoidance (){ 
  
   sensor_nodes[1]->refresh_data();     // Update data from sensor node 1
   S_rightFront = sensor_nodes[1]->get_sensor_data(ZONE_1, US);
   S_right45 = sensor_nodes[1]->get_sensor_data(ZONE_3, US);
   S_right90 = sensor_nodes[1]->get_sensor_data(ZONE_2, US);
   IR_rightFront = sensor_nodes[1]->get_sensor_data(ZONE_1, IR);
   IR_rightFront = map (IR_rightFront, 32, 140, 20, 120);   // max range 160cm
   IR_right45 = sensor_nodes[1]->get_sensor_data(ZONE_2, IR);
   IR_right45 = map (IR_right45, 66, 160, 10, 30);          // max range 35cm
   IR_right90 = sensor_nodes[1]->get_sensor_data(ZONE_3, IR);
   IR_right90 = map (IR_right90, 66, 160, 10, 30);          // max range 35cm    
   
   sensor_nodes[2]->refresh_data();    // Update data from sensor node 2
   S_leftFront = sensor_nodes[2]->get_sensor_data(ZONE_3, US);
   S_left45 = sensor_nodes[2]->get_sensor_data(ZONE_2, US);
   S_left90 = sensor_nodes[2]->get_sensor_data(ZONE_1, US);  
   IR_leftFront = sensor_nodes[2]->get_sensor_data(ZONE_2, IR);
   IR_leftFront = map (IR_leftFront, 32, 140, 20, 120);     // max range 160cm
   IR_left45 = sensor_nodes[2]->get_sensor_data(ZONE_1, IR);
   IR_left45 = map (IR_left45, 55, 160, 10, 35);            // max range 35cm
   IR_left90 = sensor_nodes[2]->get_sensor_data(ZONE_3, IR);
   IR_left90 = map (IR_left90, 55, 160, 10, 35);            // max range 35cm   
   
   if (IR_rightFront > 159)
      {rightFront = S_rightFront;}
   else if (S_rightFront < (1 + IR_rightFront))
      {rightFront = S_rightFront;}
   else if (S_rightFront > IR_rightFront)   
      {rightFront = IR_rightFront;}
   if (IR_right45 > 34)
      {right45 = S_right45;}
   else if (S_right45 < (1 + IR_right45))
      {right45 = S_right45;}
   else if (S_right45 > IR_right45)   
      {right45 = IR_right45;}
   if (IR_right90 > 34)
      {right90 = S_right90;}      
   else if (S_right90 < (1 + IR_right90))
      {right90 = S_right90;}
   else if (S_right90 > IR_right90)   
      {right90 = IR_right90;}
      
   if (IR_leftFront > 159)
      {leftFront = S_leftFront;}      
   else if (S_leftFront < (1 + IR_leftFront))
      {leftFront = S_leftFront;}
   else if (S_leftFront > IR_leftFront)   
      {leftFront = IR_leftFront;} 
   if (IR_left45 > 34)
      {left45 = S_left45;}      
   else if (S_left45 < (1 + IR_left45))
      {left45 = S_left45;}
   else if (S_left45 > IR_left45)   
      {left45 = IR_left45;}
   if (IR_left90 > 34)
      {left90 = S_left90;}      
   else if (S_left90 < (1 + IR_left90))
      {left90 = S_left90;}
   else if (S_left90 > IR_left90)   
      {left90 = IR_left90;}    
   
    
    if (right45 < rightFront && right45 < right90)
       {leftDamping = right45;
         leftDamping = map(leftDamping, (0+zone_1_forward), (120+zone_1_forward), 0, 350);    
         leftDamping = constrain(leftDamping, 0, 350);
         leftDamping = leftDamping*0.01;
         leftDamping = 1-(1/(exp(leftDamping)));}
    else if (right90 < rightFront && right90 < right45)
            {leftDamping = right90;
             leftDamping = map(leftDamping, (0+zone_1_right), (100+zone_1_right), 0, 350);    
             leftDamping = constrain(leftDamping, 0, 350);
             leftDamping = leftDamping*0.01;
             leftDamping = 1-(1/(exp(leftDamping)));} 
    else {leftDamping = rightFront;
          leftDamping = map(leftDamping, (20+zone_1_forward), (160+zone_1_forward), 0, 350);    
          leftDamping = constrain(leftDamping, 0, 350);
          leftDamping = leftDamping*0.01;
          leftDamping = 1-(1/(exp(leftDamping)));}  
 
   if (left45 < leftFront && left45 < left90)
      {rightDamping = left45;
       rightDamping = map(rightDamping, (0+zone_2_forward), (120+zone_2_forward), 0, 350);
       rightDamping = constrain(rightDamping, 0, 350);
       rightDamping = rightDamping*0.01;
       rightDamping = 1-(1/(exp(rightDamping)));}
   else if (left90 < leftFront && left90 < left45)
           {rightDamping = left90;
            rightDamping = map(rightDamping, (0+zone_2_left), (100+zone_2_left), 0, 350);
            rightDamping = constrain(rightDamping, 0, 350);
            rightDamping = rightDamping*0.01;
            rightDamping = 1-(1/(exp(rightDamping)));} 
   else {rightDamping = leftFront;
         rightDamping = map(rightDamping, (20+zone_2_forward), (160+zone_2_forward), 0, 350);
         rightDamping = constrain(rightDamping, 0, 350);
         rightDamping = rightDamping*0.01;
         rightDamping = 1-(1/(exp(rightDamping)));} 
}

void backwardObstacleAvoidance (){       //Backward collison with obstacle avoidance model

   sensor_nodes[3]->refresh_data();      // Update data from sensor node 3
   S_rear = sensor_nodes[3]->get_sensor_data(ZONE_1, US);   
   S_leftRear45 = sensor_nodes[3]->get_sensor_data(ZONE_3, US);
   S_leftRear90 = sensor_nodes[3]->get_sensor_data(ZONE_2, US);  
   
   sensor_nodes[4]->refresh_data();      // Update data from sensor node 4      
   S_rightRear45 = sensor_nodes[4]->get_sensor_data(ZONE_2, US);
   S_rightRear90 = sensor_nodes[4]->get_sensor_data(ZONE_1, US);  

    if (S_rightRear45 < S_rightRear90){
         leftDamping = S_rightRear45;
         leftDamping = map(leftDamping, 0, zone_4_backward, 0, 350);    
         leftDamping = constrain(leftDamping, 0, 350);
         leftDamping = leftDamping*0.01;
         leftDamping = 1-(1/(exp(leftDamping)));}
    else if (S_rightRear90 <= S_rightRear45)
            {leftDamping = S_rightRear90;
             leftDamping = map(leftDamping, 0,  zone_4_right, 0, 350);    
             leftDamping = constrain(leftDamping, 0, 350);
             leftDamping = leftDamping*0.01;
             leftDamping = 1-(1/(exp(leftDamping)));} 

 
   if (S_leftRear45 < S_leftRear90){
       rightDamping = S_leftRear45;
       rightDamping = map(rightDamping, 0, zone_3_backward, 0, 350);
       rightDamping = constrain(rightDamping, 0, 350);
       rightDamping = rightDamping*0.01;
       rightDamping = 1-(1/(exp(rightDamping)));}
   else if (S_leftRear90 <= S_leftRear45){
            rightDamping = S_leftRear90;
            rightDamping = map(rightDamping, 0, zone_3_left, 0, 350);
            rightDamping = constrain(rightDamping, 0, 350);
            rightDamping = rightDamping*0.01;
            rightDamping = 1-(1/(exp(rightDamping)));} 

         
    if (S_rear < S_rightRear90 || S_rear < S_rightRear45 || S_rear < S_leftRear90 || S_rear < S_leftRear45){
         rightDamping = S_rear;
         rightDamping = map(rightDamping, 10, (10 + zone_3_backward), 0, 350);
         rightDamping = constrain(rightDamping, 0, 350);
         rightDamping = rightDamping*0.01;
         rightDamping = 1-(1/(exp(rightDamping)));
         leftDamping = rightDamping;
    }
}

void clockwiseObstacleAvoidance (){       //rotate right collison with obstacle avoidance model

   sensor_nodes[1]->refresh_data();     // Update data from sensor node 1
   
   S_right45 = sensor_nodes[1]->get_sensor_data(ZONE_3, US);
   S_right90 = sensor_nodes[1]->get_sensor_data(ZONE_2, US);
   IR_right45 = sensor_nodes[1]->get_sensor_data(ZONE_2, IR);
   IR_right45 = map (IR_right45, 66, 160, 10, 30);          // max range 35cm
   IR_right90 = sensor_nodes[1]->get_sensor_data(ZONE_3, IR);
   IR_right90 = map (IR_right90, 66, 160, 10, 30);          // max range 35cm    
   
   sensor_nodes[3]->refresh_data();      // Update data from sensor node 3
   
   S_leftRear45 = sensor_nodes[3]->get_sensor_data(ZONE_3, US);
   S_leftRear90 = sensor_nodes[3]->get_sensor_data(ZONE_2, US); 
   
   if (IR_right45 > 34)
      {right45 = S_right45;}
   else if (S_right45 < (1 + IR_right45))
      {right45 = S_right45;}
   else if (S_right45 > IR_right45)   
      {right45 = IR_right45;}
   if (IR_right90 > 34)
      {right90 = S_right90;}      
   else if (S_right90 < (1 + IR_right90))
      {right90 = S_right90;}
   else if (S_right90 > IR_right90)   
      {right90 = IR_right90;}
      
      if (S_leftRear45 < S_leftRear90 && S_leftRear45 < right45 && S_leftRear45 < right90){
       turnDamping = S_leftRear45;
       turnDamping = map(turnDamping, 0, zone_3_backward, 0, 350);
       turnDamping = constrain(turnDamping, 0, 350);
       turnDamping = turnDamping*0.01;
       turnDamping = 1-(1/(exp(turnDamping)));}
      else if (S_leftRear90 <= S_leftRear45 && S_leftRear90 <= right45 && S_leftRear90 <= right90){
            turnDamping = S_leftRear90;
            turnDamping = map(turnDamping, 0, zone_3_left, 0, 350);
            turnDamping = constrain(turnDamping, 0, 350);
            turnDamping = turnDamping*0.01;
            turnDamping = 1-(1/(exp(turnDamping)));} 
      else if (right45 < right90 && right45 <= S_leftRear45 && right45 <= S_leftRear90){
            turnDamping = right45;
            turnDamping = map(turnDamping, 0, zone_2_forward, 0, 350);    
            turnDamping = constrain(turnDamping, 0, 350);
            turnDamping = turnDamping*0.01;
            turnDamping = 1-(1/(exp(turnDamping)));}
       else{
             turnDamping = right90;
             turnDamping = map(turnDamping, 0, zone_2_left, 0, 350);    
             turnDamping = constrain(turnDamping, 0, 350);
             turnDamping = turnDamping*0.01;
             turnDamping = 1-(1/(exp(turnDamping)));}  
           
             leftDamping = turnDamping;
             rightDamping = turnDamping;
}

void antiClockwiseObstacleAvoidance (){       //rotate left collison with obstacle avoidance model

   sensor_nodes[2]->refresh_data();    // Update data from sensor node 2
 
   S_left45 = sensor_nodes[2]->get_sensor_data(ZONE_2, US);
   S_left90 = sensor_nodes[2]->get_sensor_data(ZONE_1, US);  
   IR_left45 = sensor_nodes[2]->get_sensor_data(ZONE_1, IR);
   IR_left45 = map (IR_left45, 55, 160, 10, 35);            // max range 35cm
   IR_left90 = sensor_nodes[2]->get_sensor_data(ZONE_3, IR);
   IR_left90 = map (IR_left90, 55, 160, 10, 35);            // max range 35cm   
  
   sensor_nodes[4]->refresh_data();      // Update data from sensor node 4    
   
   S_rightRear45 = sensor_nodes[4]->get_sensor_data(ZONE_2, US);
   S_rightRear90 = sensor_nodes[4]->get_sensor_data(ZONE_1, US);    
   
     if (IR_leftFront > 159){
        leftFront = S_leftFront;}      
        else if (S_leftFront < (1 + IR_leftFront)){
                leftFront = S_leftFront;}
        else if (S_leftFront > IR_leftFront){   
                leftFront = IR_leftFront;} 
     if (IR_left45 > 34)
        {left45 = S_left45;}      
        else if (S_left45 < (1 + IR_left45)){
                left45 = S_left45;}
        else if (S_left45 > IR_left45){   
                left45 = IR_left45;}
     if (IR_left90 > 34){
        left90 = S_left90;}      
        else if (S_left90 < (1 + IR_left90)){
                left90 = S_left90;}
        else if (S_left90 > IR_left90){   
                left90 = IR_left90;}     
 
         
      if (S_rightRear45 < S_rightRear90 && S_rightRear45 < left45 && S_rightRear45 < left90){
       turnDamping = S_rightRear45;
       turnDamping = map(turnDamping, 0, zone_4_backward, 0, 350);
       turnDamping = constrain(turnDamping, 0, 350);
       turnDamping = turnDamping*0.01;
       turnDamping = 1-(1/(exp(turnDamping)));}
      else if (S_rightRear90 <= S_rightRear45 && S_rightRear90 <= left45 && S_rightRear90 <= left90){
            turnDamping = S_rightRear90;
            turnDamping = map(turnDamping, 0, zone_4_right, 0, 350);
            turnDamping = constrain(turnDamping, 0, 350);
            turnDamping = turnDamping*0.01;
            turnDamping = 1-(1/(exp(turnDamping)));} 
      else if (left45 < left90 && left45 <= S_rightRear45 && left45 <= S_rightRear90){
            turnDamping = left45;
            turnDamping = map(turnDamping, 0, zone_2_forward, 0, 350);    
            turnDamping = constrain(turnDamping, 0, 350);
            turnDamping = turnDamping*0.01;
            turnDamping = 1-(1/(exp(turnDamping)));}
       else{
             turnDamping = left90;
             turnDamping = map(turnDamping, 0, zone_2_left, 0, 350);    
             turnDamping = constrain(turnDamping, 0, 350);
             turnDamping = turnDamping*0.01;
             turnDamping = 1-(1/(exp(turnDamping)));}  
           
             leftDamping = turnDamping;
             rightDamping = turnDamping;

 }

// ================================== Obstacle avoidance algorithms end ======= Michael Gillham 31/08/14

// ============================================ Helper functions =================

// Locates newly connected sensors, if v == VERBOSE prints out details of each sensor
// Allow ~3s to scan for sensor nodes 0 to 9 with timeout of 250ms each
void scan_for_sensors(int/*Verbosity*/ v)
{
  for (int i = 0; i < 10; i++)
  {
    delete sensor_nodes[i];
    sensor_nodes[i] = 0;

    Sensor_Node * node = new Sensor_Node(i + '0', comms_485);

    if (v == VERBOSE)
     {
      SerialUSB.print("Sensor Node ");
      SerialUSB.print(i);      
     }

    if (node->exists())
      {
      if (!node->setup())
      {
        delete node;
        continue;
      }
      sensor_nodes[i] = node;
      if (v == VERBOSE)
      {
        SerialUSB.println(" found...................");       
      }
      Sensor_Node_Configuration conf = node->get_configuration();
      if (v == VERBOSE)
      {
        SerialUSB.println("=== Zone 1 ===");
        SerialUSB.print("Position: ");
        switch (conf.zone_1.position)
        {
        case FRONT:
          SerialUSB.println("front");
          break;
        case BACK:
          SerialUSB.println("back");
          break;
        case LEFT:
          SerialUSB.println("left");
          break;
        case RIGHT:
          SerialUSB.println("right");
          break;
        case FRONT_LEFT:
          SerialUSB.println("front-left");
          break;
        case FRONT_RIGHT:
          SerialUSB.println("front-right");
          break;
        case BACK_LEFT:
          SerialUSB.println("back-left");
          break;
        case BACK_RIGHT:
          SerialUSB.println("back-right");
          break;
        }
        SerialUSB.print("Orientation: ");
        switch (conf.zone_1.orientation)
        {
        case ZERO_DEG:
          SerialUSB.println("0 degrees");
          break;
        case FORTY_FIVE_DEG:
          SerialUSB.println("45 degrees");
          break;
        case NINETY_DEG:
          SerialUSB.println("90 degrees");
          break;
        case ONE_THIRTY_FIVE_DEG:
          SerialUSB.println("135 degrees");
          break;
        case ONE_EIGHTY_DEG:
          SerialUSB.println("180 degrees");
          break;
        case TWO_TWENTY_FIVE_DEG:
          SerialUSB.println("225 degrees");
          break;
        case TWO_SEVENTY_DEG:
          SerialUSB.println("270 degrees");
          break;
        case THREE_FIFTEEN_DEG:
          SerialUSB.println("315 degrees");
          break;
        }
        SerialUSB.print("Infrared? ");
        if (conf.zone_1.ir) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.print("Ultrasound? ");
        if (conf.zone_1.us) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.print("Fused? ");
        if (conf.zone_1.fused_sensors) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.println("=== Zone 2 ===");
        SerialUSB.print("Position: ");
        switch (conf.zone_2.position)
        {
        case FRONT:
          SerialUSB.println("front");
          break;
        case BACK:
          SerialUSB.println("back");
          break;
        case LEFT:
          SerialUSB.println("left");
          break;
        case RIGHT:
          SerialUSB.println("right");
          break;
        case FRONT_LEFT:
          SerialUSB.println("front-left");
          break;
        case FRONT_RIGHT:
          SerialUSB.println("front-right");
          break;
        case BACK_LEFT:
          SerialUSB.println("back-left");
          break;
        case BACK_RIGHT:
          SerialUSB.println("back-right");
          break;
        }
        SerialUSB.print("Orientation: ");
        switch (conf.zone_2.orientation)
        {
        case ZERO_DEG:
          SerialUSB.println("0 degrees");
          break;
        case FORTY_FIVE_DEG:
          SerialUSB.println("45 degrees");
          break;
        case NINETY_DEG:
          SerialUSB.println("90 degrees");
          break;
        case ONE_THIRTY_FIVE_DEG:
          SerialUSB.println("135 degrees");
          break;
        case ONE_EIGHTY_DEG:
          SerialUSB.println("180 degrees");
          break;
        case TWO_TWENTY_FIVE_DEG:
          SerialUSB.println("225 degrees");
          break;
        case TWO_SEVENTY_DEG:
          SerialUSB.println("270 degrees");
          break;
        case THREE_FIFTEEN_DEG:
          SerialUSB.println("315 degrees");
          break;
        }
        SerialUSB.print("Infrared? ");
        if (conf.zone_2.ir) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.print("Ultrasound? ");
        if (conf.zone_2.us) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.print("Fused? ");
        if (conf.zone_2.fused_sensors) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.println("=== Zone 3 ===");
        SerialUSB.print("Position: ");
        switch (conf.zone_3.position)
        {
        case FRONT:
          SerialUSB.println("front");
          break;
        case BACK:
          SerialUSB.println("back");
          break;
        case LEFT:
          SerialUSB.println("left");
          break;
        case RIGHT:
          SerialUSB.println("right");
          break;
        case FRONT_LEFT:
          SerialUSB.println("front-left");
          break;
        case FRONT_RIGHT:
          SerialUSB.println("front-right");
          break;
        case BACK_LEFT:
          SerialUSB.println("back-left");
          break;
        case BACK_RIGHT:
          SerialUSB.println("back-right");
          break;
        }
        SerialUSB.print("Orientation: ");
        switch (conf.zone_3.orientation)
        {
        case ZERO_DEG:
          SerialUSB.println("0 degrees");
          break;
        case FORTY_FIVE_DEG:
          SerialUSB.println("45 degrees");
          break;
        case NINETY_DEG:
          SerialUSB.println("90 degrees");
          break;
        case ONE_THIRTY_FIVE_DEG:
          SerialUSB.println("135 degrees");
          break;
        case ONE_EIGHTY_DEG:
          SerialUSB.println("180 degrees");
          break;
        case TWO_TWENTY_FIVE_DEG:
          SerialUSB.println("225 degrees");
          break;
        case TWO_SEVENTY_DEG:
          SerialUSB.println("270 degrees");
          break;
        case THREE_FIFTEEN_DEG:
          SerialUSB.println("315 degrees");
          break;
        }
        SerialUSB.print("Infrared? ");
        if (conf.zone_3.ir) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.print("Ultrasound? ");
        if (conf.zone_3.us) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.print("Fused? ");
        if (conf.zone_3.fused_sensors) SerialUSB.println("Yes.");
        else SerialUSB.println("No.");
        SerialUSB.println("==============");
      }

      // configure node
      node->set_mode(FIRE_TOGETHER);
    }
    else
    {
      delete node;
      if (v == VERBOSE)
      {
        SerialUSB.println(" not found.");
      }
    }
  }
}

void returnJoystickDirect(){  // Change to isolate switch function by Michael Gillham 31/08/14, returns adjustable joystick values to enable a comparison between obstacle help on or off states

   set_vibration_pattern(OFF);                // Turn off haptic, ensure previous state zeroed
   update_vibration();
   digitalWrite (illumination_relay, LOW);    // Turn off illumination, ensure previous state zeroed
   
   speed = map(speed, 1, 255, 28, 228);       // we can map the un-assisted system to have the-
   turn = map(turn, 1, 255, 32, 224);         // same velocity and rate of turn as the assisted for comparison when testing
   speed = constrain(speed, 28, 228);       
   turn = constrain(turn, 32, 224);         
   GPSB_set_speed_turn(speed, turn);          // Return modified joystick values (0-255) to system
   GPSB_send_drive_packet();  
}  

void send_data()
{
  for (int i = 0; i < 10; i++)
  {
    if (sensor_nodes[i])    
    {

      // new machine readable output, format example "2:001a00ff012d0011000200f2\n"
      char data[27] = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0      };
      sprintf(data, "%d:", i);
      sprintf(&data[2], "%04x", sensor_nodes[i]->get_sensor_data(ZONE_1, IR));
      sprintf(&data[6], "%04x", sensor_nodes[i]->get_sensor_data(ZONE_1, US));
      sprintf(&data[10], "%04x", sensor_nodes[i]->get_sensor_data(ZONE_2, IR));
      sprintf(&data[14], "%04x", sensor_nodes[i]->get_sensor_data(ZONE_2, US));
      sprintf(&data[18], "%04x", sensor_nodes[i]->get_sensor_data(ZONE_3, IR));
      sprintf(&data[22], "%04x", sensor_nodes[i]->get_sensor_data(ZONE_3, US));
      SerialUSB.write((const uint8_t*)data, 26);
      SerialUSB.println();
    }
  }
  // send joystick data, format example "j:08,f2\n"
  char joystick[8] = {
    'j',':',0,0,',',0,0,0  };
  sprintf(joystick, "j:%02x,%02x", get_joystick_speed(), get_joystick_turn());
  SerialUSB.write((const uint8_t*)joystick, 7);
  SerialUSB.println();
}