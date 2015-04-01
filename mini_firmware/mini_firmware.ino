/*
  COALAS Sensor Communication
  
  This allows the Arduino Mini to use the RS485 bus

  Authors: Martin Henderson (Edited by - Yassine NASRI)  
  Last Edited: 24/10/2014
*/
#include <SoftwareSerial.h>
#include <Wire.h>

int comms_id = 0; //Change according to the Node ID ([1, 2, 3, 4])

#define FIRMWARE_VERSION 0x100 // (1.0) Change according the the node Firmware Version
#define BAUD_485 57600

#define interrupt_trigger 0

enum fire_modes {ONE_BY_ONE, TRIGGERED, INDEPENDANT} fire_mode = INDEPENDANT;

#define txrxPin 10

#define srfAddress1 0x01
#define srfAddress2 0x02
#define srfAddress3 0x03

#define getLatestRange 0x5E
#define getNewRange    0x54 /* blocking */

SoftwareSerial UltrasonicBus(txrxPin, txrxPin);

#define SENSOR_PRODUCTION // comment out for testing, uncomment for production



  unsigned long range_t = 0;
  boolean awaiting_range = false;


class Dummy_Comms
{
public:
  Dummy_Comms(){}
  void begin(int){}
  int available(){return 0;}
  int read(){return -1;}
  void write(const char){}
  void write(const unsigned char){}
  void write(const int){}
  void write(const long){}
  void write(const String){}
  void write(const char*){}
  void print(const int){}
};

//#ifdef SENSOR_PRODUCTION
  Dummy_Comms PC;
  #define Due Serial
//#else
//  Dummy_Comms Due;
//  #define PC Serial
//#endif

// pin assignments
const int sw_0 = 4;
const int sw_1 = 5;
const int sw_2 = 6;
const int sw_3 = 7;
const int sr = 8; // RS485 read_write pin

// states
boolean interested = false;
boolean addressed = false;
boolean accepted_command = false;

// commands
#define NO_COMMAND            0
#define REQUEST_CONFIGURATION 1
#define CONFIGURE_SENSORS     2
#define SET_MODE              3
#define GET_DATA_FORMAT       4
#define GET_CURRENT_DATA      5
#define SCAN                  6
#define SET_THRESHOLD         7
#define GET_FIRMWARE_VERSION  8
int current_command = NO_COMMAND;

// where commands store their state during interpretation

// sensor related defines
#define NO_SENSOR  0
#define ULTRASOUND 0b00000100
#define INFRARED   0b00001000
#define FRONT_LEFT  '1'
#define FRONT_RIGHT '2'
#define BACK_LEFT   '4'
#define BACK_RIGHT  '3'
#define FRONT       'F'
#define BACK        'B'
#define LEFT        'L'
#define RIGHT       'R'
#define ZERO_DEG             'a' // forwards
#define FORTY_FIVE_DEG       'b'
#define NINETY_DEG           'c' // right
#define ONE_THIRTY_FIVE_DEG  'd'
#define ONE_EIGHTY_DEG       'e' // backwards
#define TWO_TWENTY_FIVE_DEG  'f'
#define TWO_SEVENTY_DEG      'g' // left
#define THREE_FIFTEEN_DEG    'h'  

#define DEFAULT_THRESHOLD 30
#define INIT_DISTANCE    120

// ============== Comms ====================
void data_mode_sending()
{
  digitalWrite(sr, HIGH);
  delay(8);
}

void data_mode_receiving()
{
  delay(8);
  digitalWrite(sr, LOW);
}

class Data_sender
{
  public:
  Data_sender()
  {
    data_mode_sending();
  }
  ~Data_sender()
  {
    delay(1);
    data_mode_receiving();
  }
};

/* place macro inside block statement before sending 
   data to ensure that S/R* is set back to recieve
   at end of block statement
*/
#define SEND_MODE Data_sender d = Data_sender();

// ================ Structs =====================

// sensor settings with most current sensor values
struct Zone
{ 
  /* This code assumes that ther are two sensors 
     in each zone.
     Use vectors if designing a more flexible node.
   */
  boolean fused;
  
  int mount_position;
  int mount_orientation;
  int threshold_value;
  
  boolean sensor_1_thresholded;
  boolean sensor_1_en;
  boolean sensor_1_raw; // true if raw, false if cm
  int sensor_1_data;
  int sensor_1_code;
  
  boolean sensor_2_thresholded;
  boolean sensor_2_en;
  boolean sensor_2_raw; // true if raw, false if cm
  int sensor_2_data;
  int sensor_2_code;
} zone_1, zone_2, zone_3;

typedef struct Zone Zone_t;

struct Configuration
{
  int counter;
  int zone;
  boolean ok;
  
} configuration;

typedef struct Configuration Conf_t;

bool new_trigger = false;
long last_trigger_time = 0;

// ============= Ultrasound Syncronisation ===========

void trigger_range()
{
  // ping all sensors
  SRF01_Cmd(0, 0x51);
  // record time of trigger
  last_trigger_time = millis();
  // alert main loop (incase triggered by interrupt)
  new_trigger = true;
}


bool srf_08_1 = false;
bool srf_08_2 = false;
bool srf_08_3 = false;

 
void setup() {               // --------------------------Setup------------------------------ //
  // initialise pins
  pinMode(sw_0, INPUT);
  pinMode(sw_1, INPUT);
  pinMode(sw_2, INPUT);
  pinMode(sw_3, INPUT);
  pinMode(sr,  OUTPUT);
  pinMode(A0, INPUT);
  pinMode(A1, INPUT);
  pinMode(A2, INPUT);
  pinMode(A3, INPUT);
  // initialise ID
  digitalWrite(11, LOW);
  digitalWrite(12, LOW);
  pinMode(11, OUTPUT);
  pinMode(12, OUTPUT);
  digitalWrite(11, LOW);
  digitalWrite(12, LOW);
  if (digitalRead(sw_0) == LOW){
    comms_id |= 0x1;
    
    digitalWrite(11, HIGH);
    digitalWrite(12, LOW);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }else{
    digitalWrite(11, LOW);
    digitalWrite(12, HIGH);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }
  if (digitalRead(sw_1) == LOW){
    comms_id |= 0x2;
    
    digitalWrite(11, HIGH);
    digitalWrite(12, LOW);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }else{
    digitalWrite(11, LOW);
    digitalWrite(12, HIGH);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }
  if (digitalRead(sw_2) == LOW){
    comms_id |= 0x4;
    
    digitalWrite(11, HIGH);
    digitalWrite(12, LOW);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }else{
    digitalWrite(11, LOW);
    digitalWrite(12, HIGH);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }
  if (digitalRead(sw_3) == LOW){
    comms_id |= 0x8;
    
    digitalWrite(11, HIGH);
    digitalWrite(12, LOW);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }else{
    digitalWrite(11, LOW);
    digitalWrite(12, HIGH);
    delay(100);
    digitalWrite(11, LOW);
    digitalWrite(12, LOW);
    delay(200);
  }
  comms_id = comms_id + '0';
  
  //comms_id = '0'; // FIXME Only for testing rear!!! ----<-------------<-------------<--
  // initialise serial
  PC.begin(9600);
  data_mode_receiving();
  //UltrasonicBus.begin(9600);
  Due.begin(BAUD_485);
  //Serial.print("Test: id is ");
  //Serial.println(comms_id);
  
  // initialise structures
  set_zone_to_default(zone_1);
  set_zone_to_default(zone_2);
  set_zone_to_default(zone_3);
  switch (comms_id) // Position is determined by DIP switches
  {
      case '1':
    zone_1.mount_position = FRONT_LEFT;
    zone_1.mount_orientation = TWO_SEVENTY_DEG;
    zone_2.mount_position = FRONT_LEFT;
    zone_2.mount_orientation = THREE_FIFTEEN_DEG;
    zone_3.mount_position = FRONT_LEFT;
    zone_3.mount_orientation = ZERO_DEG;
    break;
  case '2':
    zone_1.mount_position = FRONT_RIGHT;
    zone_1.mount_orientation = ZERO_DEG;
    zone_2.mount_position = FRONT_RIGHT;
    zone_2.mount_orientation = FORTY_FIVE_DEG;
    zone_3.mount_position = FRONT_RIGHT;
    zone_3.mount_orientation = NINETY_DEG;
    break;
  case '3':
    zone_1.mount_position = BACK_RIGHT;
    zone_1.mount_orientation = NINETY_DEG;
    zone_2.mount_position = BACK_RIGHT;
    zone_2.mount_orientation = ONE_THIRTY_FIVE_DEG;
    zone_3.mount_position = BACK_RIGHT;
    zone_3.mount_orientation = ONE_EIGHTY_DEG;
    break;
  case '4':
    zone_1.mount_position = BACK_LEFT;
    zone_1.mount_orientation = TWO_TWENTY_FIVE_DEG;
    zone_2.mount_position = BACK_LEFT;
    zone_2.mount_orientation = TWO_SEVENTY_DEG;
    zone_3.mount_position = BACK_LEFT;
    zone_3.mount_orientation = ONE_EIGHTY_DEG;
    break;
  case '5':
    zone_1.mount_position = BACK;
    zone_1.mount_orientation = ONE_EIGHTY_DEG;
    zone_2.mount_position = LEFT;
    zone_2.mount_orientation = TWO_SEVENTY_DEG;
    zone_3.mount_position = RIGHT;
    zone_3.mount_orientation = NINETY_DEG;
    break;
  default:
    zone_1.mount_position = BACK_RIGHT;
    zone_1.mount_orientation = ONE_THIRTY_FIVE_DEG;
    zone_2.mount_position = BACK;
    zone_2.mount_orientation = ONE_EIGHTY_DEG;
    zone_3.mount_position = BACK_LEFT;
    zone_3.mount_orientation = TWO_TWENTY_FIVE_DEG;
    break;
  }
  Wire.begin();
  
  if (srf_08_scan(112)) srf_08_1 = true;
  if (srf_08_scan(113)) srf_08_2 = true;
  if (srf_08_scan(114)) srf_08_3 = true;
  
  reset_config();
  fire_mode = ONE_BY_ONE;
  attachInterrupt(interrupt_trigger, trigger_range, RISING);
  
}  // -----------------------------------------------End of Setup-------------------------- //


void loop() {  // ---------------------------------------Loop------------------------------ //
//Serial.print("Comms address : ");
//Serial.println(char(comms_id));
  //process_serial(); // checked in between sensor scanning 22/05/14
  process_sensors();
  
     // Serial.println(SRF08_range(1));
   //   Serial.println(SRF08_range(2));
   //   Serial.println(SRF08_range(3));
} // -------------------------------------------------End of Loop-------------------------- //

bool srf_08_scan(int address)
{
  bool srf_08_connected = false;
  
	Wire.beginTransmission(address);
	Wire.write(0);
	Wire.write(81);
	Wire.write(31);
	Wire.write(255);
	Wire.endTransmission();
        int c = -1;
        delay(100);

	Wire.beginTransmission(address);
	Wire.write(2);
	Wire.endTransmission();

        Wire.requestFrom(address, 2);

	if (Wire.available()>1)   {
	    int result = Wire.read() * 256;
	    result += Wire.read();
            srf_08_connected = true;
	}

	Wire.read(); // clear buffer
        
        return srf_08_connected;
}

void set_zone_to_default(struct Zone& z)
{
  z.fused = false;
  z.sensor_1_thresholded = false;
  z.sensor_2_thresholded = false;
  z.sensor_1_en = true;
  z.sensor_2_en = true;
  z.threshold_value = DEFAULT_THRESHOLD;
  z.sensor_1_raw = false;
  z.sensor_2_raw = false;
  z.sensor_1_data = INIT_DISTANCE;
  z.sensor_2_data = INIT_DISTANCE;
  z.sensor_1_code = INFRARED;
  z.sensor_2_code = ULTRASOUND;
}

/* Resets fields controlling configuration */
void reset_config()
{
  configuration.counter = 0;
  configuration.zone = 0;
  configuration.ok = true;
}

// Checks for input commands from DUE and responds
void process_serial()
{
  while (Due.available())
  {
    int c = Due.read();
    PC.write(c);
    if (interested)
    {
      if (addressed)
      {
        /* --- This is where commands are interpreted --- */
        
        if (accepted_command)
        {
          // parameters are handled here ---------------
          switch(current_command)
          {
          case CONFIGURE_SENSORS:
            continue_configuration(c);
            break;
          case SET_MODE:
            set_mode(c);
            reset_state();
            break;
          case SET_THRESHOLD:
            configure_threshold(c);
            break;
          case SCAN:
          case REQUEST_CONFIGURATION:
          case GET_DATA_FORMAT:
          case GET_CURRENT_DATA:
            // nothing to do, no parameters, shouldn't get here...
          default:
            reset_state();
          }
        }
        else
        {// commands are handled here ---------------
          switch(c)
          {
          case 'S':
            current_command = SCAN;
            PC.write("\nScan command recieved\n");
            send_scan_response();
            reset_state();
            break;
          case 'R':
            current_command = REQUEST_CONFIGURATION;
            PC.write("\nRequest Config command recieved\n");
            send_configuration();
            reset_state();
            break;
          case 'C':
            current_command = CONFIGURE_SENSORS;
            PC.write("\nConfigure Sensors command recieved\n");
            reset_config();
            accepted_command = true;
            break;
          case 'M':
            current_command = SET_MODE;
            PC.write("\nSet Mode command recieved\n");
            accepted_command = true;
            break;
          case 'T':
            current_command = SET_THRESHOLD;
            PC.write("\nSet threshold command recieved\n");
            accepted_command = true;
            break;
          case 'F':
            current_command = GET_DATA_FORMAT;
            PC.write("\nGet Data Format command recieved\n");
            send_format();
            reset_state();
            break;
          case 'D':
            current_command = GET_CURRENT_DATA;
            PC.write("\nGet Current Data command recieved\n");
            send_data();
            reset_state();
            break;
		case 'V':
            current_command = GET_FIRMWARE_VERSION;
            PC.write("\nGet Current Firmware version recieved\n");
            send_version();
            reset_state();
            break;
          default:
            reset_state();
          }
        }
        
        /* --- --- --- --- --- --- --- --- --- --- --- --- */
      }
      else
      {
        if (c == comms_id)
        {
          addressed = true;
        PC.write("\nAwaiting Command\n");
        }
        else
        {
          interested = false;
        }
      }
    }
    else
    {
      if (c == '&')
      {
        interested = true;
        PC.write("\nInterested...\n");
      }
    }
  }
}

/* reset the comms state machine */
void reset_state()
{
  addressed = false;
  interested = false;
  accepted_command = false;
  PC.write('\n');
}

char version_data[6];

void send_version()
{
	//send the Arduino Mini Firmware code version
	int f = FIRMWARE_VERSION ;
	unsigned char checksum = 0;
	  
    sprintf(version_data,"%04X", f);
    checksum ^= f & 0xff;
    checksum ^= (f >> 8) & 0xff;
    Due.print(version_data);
	sprintf(version_data, "%02X", checksum);
	Due.print(version_data);
}

void configure_threshold(char c)
{
  static char s[20] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
  static int p = 0;
  // buffer character
  s[p] = c;
  p++;
  
  // check if three chars have been recieved for each zone
  if (p > 8)
  {
    // set thresholds with recieved values
    String str = String(s);
    char t1[4] = "000"; str.substring(0,3).toCharArray(t1,4);
    char t2[4] = "000"; str.substring(3,6).toCharArray(t2,4);
    char t3[4] = "000"; str.substring(6,9).toCharArray(t3,4);
    zone_1.threshold_value = strtol(t1,0,16);
    zone_2.threshold_value = strtol(t2,0,16);
    zone_3.threshold_value = strtol(t3,0,16);
    // reset variables
    p = 0;
    memset(s, 0, 20);
    reset_state();
  }
}

// response to &[id]S
void send_scan_response()
{
  send_ack(); // i.e. 'Y'
}

// processes each new char and configure device once recieved enough info
void continue_configuration(char c)
{
  if (configuration.counter > 2)
  {
    configuration.zone++;
    configuration.counter = 0;
  }
  switch(configuration.zone)
  {
  case 0: // Zone 1
    {
      switch (configuration.counter)
      {
      case 0:
        switch(c)
        {
        case 'S':
          zone_1.fused = false;
          PC.write("\nUnusing zone 1\n");
          break;
        case 'F':
          zone_1.fused = true;
          PC.write("\nFusing zone 1\n");
          break;
        default:
          configuration.ok = false;
        }
        break;
      case 1:
        switch(c)
        {
        case '0':
          zone_1.sensor_1_en = false;
          break;
        case '1':
          zone_1.sensor_1_en = true;
          zone_1.sensor_1_thresholded = true;
          break;
        case 'R':
          zone_1.sensor_1_en = true;
          zone_1.sensor_1_thresholded = false;
          if (zone_1.fused) zone_1.sensor_1_raw = false;
          else zone_1.sensor_1_raw = true;
          break;
        case 'C':
          zone_1.sensor_1_en = true;
          zone_1.sensor_1_thresholded = false;
          zone_1.sensor_1_raw = false;
          break;
        default:
          configuration.ok = false;
        }
        break;
      case 2:
        switch(c)
        {
        case '0':
          zone_1.sensor_2_en = false;
          break;
        case '1':
          zone_1.sensor_2_en = true;
          zone_1.sensor_2_thresholded = true;
          break;
        case 'R':
          zone_1.sensor_2_en = true;
          zone_1.sensor_2_thresholded = false;
          if (zone_1.fused) zone_1.sensor_2_raw = false;
          else zone_1.sensor_2_raw = true;
          break;
        case 'C':
          zone_1.sensor_2_en = true;
          zone_1.sensor_2_thresholded = false;
          zone_1.sensor_2_raw = false;
          break;
        default:
          configuration.ok = false;
        }
        break;
      default:
          configuration.ok = false;
      }
    }
    break;
  case 1: // Zone 2
    {
      switch (configuration.counter)
      {
      case 0:
        switch(c)
        {
        case 'S':
          zone_2.fused = false;
          PC.write("\nUnfusing zone 2\n");
          break;
        case 'F':
          zone_2.fused = true;
          PC.write("\nFusing zone 2\n");
          break;
        default:
          configuration.ok = false;
        }
        break;
      case 1:
        switch(c)
        {
        case '0':
          zone_2.sensor_1_en = false;
          break;
        case '1':
          zone_2.sensor_1_en = true;
          zone_2.sensor_1_thresholded = true;
          break;
        case 'R':
          zone_2.sensor_1_en = true;
          zone_2.sensor_1_thresholded = false;
          if (zone_2.fused) zone_2.sensor_1_raw = false;
          else zone_2.sensor_1_raw = true;
          break;
        case 'C':
          zone_2.sensor_1_en = true;
          zone_2.sensor_1_thresholded = false;
          zone_2.sensor_1_raw = false;
          break;
        default:
          configuration.ok = false;
        }
        break;
      case 2:
        switch(c)
        {
        case '0':
          zone_2.sensor_2_en = false;
          break;
        case '1':
          zone_2.sensor_2_en = true;
          zone_2.sensor_2_thresholded = true;
          break;
        case 'R':
          zone_2.sensor_2_en = true;
          zone_2.sensor_2_thresholded = false;
          if (zone_2.fused) zone_2.sensor_2_raw = false;
          else zone_2.sensor_2_raw = true;
          break;
        case 'C':
          zone_2.sensor_2_en = true;
          zone_2.sensor_2_thresholded = false;
          zone_2.sensor_2_raw = false;
          break;
        default:
          configuration.ok = false;
        }
        break;
      default:
        reset_state();
        reset_config();
      }
    }
    break;
  case 2: // Zone 3
    {
      switch (configuration.counter)
      {
      case 0:
        switch(c)
        {
        case 'S':
          zone_3.fused = false;
          PC.write("\nUnusing zone 3\n");
          break;
        case 'F':
          zone_3.fused = true;
          PC.write("\nFusing zone 3\n");
          break;
        default:
          configuration.ok = false;
        }
        break;
      case 1:
        switch(c)
        {
        case '0':
          zone_3.sensor_1_en = false;
          break;
        case '1':
          zone_3.sensor_1_en = true;
          zone_3.sensor_1_thresholded = true;
          break;
        case 'R':
          zone_3.sensor_1_en = true;
          zone_3.sensor_1_thresholded = false;
          if (zone_3.fused) zone_3.sensor_1_raw = false;
          else zone_3.sensor_1_raw = true;
          break;
        case 'C':
          zone_3.sensor_1_en = true;
          zone_3.sensor_1_thresholded = false;
          zone_3.sensor_1_raw = false;
          break;
        default:
          configuration.ok = false;
        }
        break;
      case 2:
        switch(c)
        {
        case '0':
          zone_3.sensor_2_en = false;
          break;
        case '1':
          zone_3.sensor_2_en = true;
          zone_3.sensor_2_thresholded = true;
          break;
        case 'R':
          zone_3.sensor_2_en = true;
          zone_3.sensor_2_thresholded = false;
          if (zone_3.fused) zone_3.sensor_2_raw = false;
          else zone_3.sensor_2_raw = true;
          break;
        case 'C':
          zone_3.sensor_2_en = true;
          zone_3.sensor_2_thresholded = false;
          zone_3.sensor_2_raw = false;
          break;
        default:
          configuration.ok = false;
        }
        break;
      default:
          configuration.ok = false;
      }
    }
    break;
  default:
    reset_state();
    reset_config();
  }
  configuration.counter++;
  if (configuration.ok == false)
  {
    // some problem, send nack
    send_nack();
    reset_config();
    reset_state();
  }
  else if (configuration.zone == 2 && configuration.counter == 3)
  {
    // configuration done - send ACK
    send_ack();
    reset_config();
    reset_state();
  }
}

// Configures how the Ultrasound fires, i.e. continuously or upon reciept of a sync pulse
void set_mode(char c)
{
  /* Set the firing mode */
  fire_mode = ONE_BY_ONE;
  return;
  // redundant
  switch(c)
  {
  case 'P':
    fire_mode = TRIGGERED;
    send_ack();
    break;
  case 'C':
    fire_mode = INDEPENDANT;
    send_ack();
    break;
  default:
    break;
  }
}

int ir_lut(int raw_value)  // ---------------------------------- LUT -------------------------- //
{
  int converted_value;
  
  if (raw_value <= 60)
  {
    converted_value = 200;
  }
  else if (raw_value > 60 && raw_value <= 65)
  {
    converted_value = 165;
  }
  else if (raw_value > 65 && raw_value <= 70)
  {
    converted_value = 160;
  }
  else if (raw_value > 70 && raw_value <= 80)
  {
    converted_value = 155;
  }
  else if (raw_value > 80 && raw_value <= 90)
  {
    converted_value = 150;
  }
  else if (raw_value > 90 && raw_value <= 110)
  {
    converted_value = 140;
  }
  else if (raw_value > 110 && raw_value <= 130)
  {
    converted_value = 125;
  }
  else if (raw_value > 130 && raw_value <= 155)
  {
    converted_value = 100;
  }
  else if (raw_value > 155 && raw_value <= 175)
  {
    converted_value = 90;
  }
  else if (raw_value > 175 && raw_value <= 200)
  {
    converted_value = 75;
  }
  else if (raw_value > 200 && raw_value <= 210)
  {
    converted_value = 70;
  }
  else if (raw_value > 210 && raw_value <= 220)
  {
    converted_value = 65;
  }
  else if (raw_value > 220 && raw_value <= 240)
  {
    converted_value = 55;
  }
  else if (raw_value > 240 && raw_value <= 270)
  {
    converted_value = 50;
  }
  else if (raw_value > 270 && raw_value <= 320)
  {
    converted_value = 45;
  }
  else if (raw_value > 320 && raw_value <= 380)
  {
    converted_value = 40;
  }
  else if (raw_value > 380 && raw_value <= 450)
  {
    converted_value = 35;
  }
  else if (raw_value > 450 && raw_value <= 610)
  {
    converted_value = 32;
  }
  else if (raw_value > 610)
  {
    converted_value = 30;
  }
  else
  {
    converted_value = -1;
  }
  
  return converted_value;   // -----------------------------End of LUT -------------------------- //
} 

void process_sensors()  // ---------------------------Sensor Handling-----------------------------//
{
  // Analog sensors
  int val_a = -1, val_b = -1, val_c = -1, val_d = -1;
  
  val_a = analogRead(A0);
  val_b = analogRead(A1);
  val_c = analogRead(A2);
  //val_d = analogRead(A3);
  process_serial();
  // zone 1 IR
  if (zone_1.sensor_1_raw)
  {
      zone_1.sensor_1_data = val_a;
  }
  else
  {
      zone_1.sensor_1_data = ir_lut(val_a);
  }
  // zone 2 IR
  if (zone_2.sensor_1_raw)
  {
      zone_2.sensor_1_data = val_b;
  }
  else
  {
      zone_2.sensor_1_data = ir_lut(val_b);
  }
  // zone 3 IR
  if (zone_3.sensor_1_raw)
  {
      zone_3.sensor_1_data = val_c;
  }
  else
  {
      zone_3.sensor_1_data = ir_lut(val_c);
  }
  // check comms
  process_serial();
  // Digital Sensors
  long time = millis();
  switch(fire_mode)
  {
    case INDEPENDANT: // each sensor node controls when its sensors ping
      if (time < last_trigger_time) last_trigger_time = 0;
      if (time - last_trigger_time > 100)
      {
       trigger_range();
      }
    case TRIGGERED:  // sensor node waits for central instruction to ping
    if (new_trigger)
    {
      time = millis();
      if (time < last_trigger_time) last_trigger_time = 0;
      if (time - last_trigger_time > 80)
      {
        if (zone_1.sensor_2_en) zone_1.sensor_2_data = checkRange(srfAddress1);
        if (zone_2.sensor_2_en) zone_2.sensor_2_data = checkRange(srfAddress2);
        if (zone_3.sensor_2_en) zone_3.sensor_2_data = checkRange(srfAddress3);
        new_trigger = false;
      }
      break;
    case ONE_BY_ONE: // cycle through sensors
      if (zone_1.sensor_2_en && srf_08_1) zone_1.sensor_2_data = SRF08_range(1);//doRange(srfAddress1);
      else zone_1.sensor_2_data = 0;
      process_serial();
      if (zone_2.sensor_2_en && srf_08_2) zone_2.sensor_2_data = SRF08_range(2);//doRange(srfAddress2);
      else zone_2.sensor_2_data = 0;
      process_serial();
      if (zone_3.sensor_2_en && srf_08_3) zone_3.sensor_2_data = SRF08_range(3);//doRange(srfAddress3);
      else zone_3.sensor_2_data = 0;
      process_serial();
      break;
    default:;
  }
  }
  /*if (!awaiting_range)
  {
    SRF08_fire_all();
  }
  else if(millis() - range_t > 80)
  {
    if (zone_1.sensor_2_en && srf_08_1) zone_1.sensor_2_data = range(1); //SRF08_collect_result(1);
    else zone_1.sensor_2_data = 0;
    if (zone_2.sensor_2_en && srf_08_2) zone_2.sensor_2_data = range(2); //SRF08_collect_result(2);
    else zone_2.sensor_2_data = 0;
    if (zone_3.sensor_2_en && srf_08_3) zone_3.sensor_2_data = range(3); //SRF08_collect_result(3);
    else zone_3.sensor_2_data = 0;
    awaiting_range = false;
  }
  else
  {
    process_serial();
  }*/
  
  /*
  PC.write("\nAnalog 0: ");
  PC.print(analogRead(A0));
  PC.write("\nAnalog 0 LUT: ");
  PC.print(ir_lut(analogRead(A0)));
  PC.write('\n');*/
}// ---------------------------End of Sensor Handling-----------------------------------------//

void send_configuration()
{
  SEND_MODE
  delay(1);
  // Zone 1 configuration ==================
  Due.write(zone_1.mount_position);
  Due.write(zone_1.mount_orientation);
  if (zone_1.fused)
  {
    Due.write(0b01000001 | zone_1.sensor_1_code | zone_1.sensor_2_code);
  }
  else
  {
    if (zone_1.sensor_1_code > 0) Due.write(0b01000001 | zone_1.sensor_1_code);
    if (zone_1.sensor_2_code > 0) Due.write(0b01000001 | zone_1.sensor_2_code);
    if (zone_1.sensor_1_code == 0 && zone_1.sensor_2_code == 0) Due.write('u');
  }
  Due.write(',');
  
  // Zone 2 configuration ==================
  Due.write(zone_2.mount_position);
  Due.write(zone_2.mount_orientation);
  if (zone_2.fused)
  {
    Due.write(0b01000010 | zone_2.sensor_1_code | zone_2.sensor_2_code);
  }
  else
  {
    if (zone_2.sensor_1_code > 0) Due.write(0b01000010 | zone_2.sensor_1_code);
    if (zone_2.sensor_2_code > 0) Due.write(0b01000010 | zone_2.sensor_2_code);
    if (zone_2.sensor_1_code == 0 && zone_2.sensor_2_code == 0) Due.write('u'); 
  }
  Due.write(',');
  
  // Zone 3 configuration ==================
  Due.write(zone_3.mount_position);
  Due.write(zone_3.mount_orientation);
  if (zone_3.fused)
  {
    Due.write(0b01000011 | zone_3.sensor_1_code | zone_3.sensor_2_code);
  }
  else
  {
    if (zone_3.sensor_1_code > 0) Due.write(0b01000011 | zone_3.sensor_1_code);
    if (zone_3.sensor_2_code > 0) Due.write(0b01000011 | zone_3.sensor_2_code);
    if (zone_3.sensor_1_code == 0 && zone_3.sensor_2_code == 0) Due.write('u');
  }
  Due.write('.');
  
}

void send_format()
{
  SEND_MODE
  delay(1);
  // zone 1 ====================
  if (zone_1.fused && zone_1.sensor_1_en && zone_1.sensor_2_en)
  {
    // zone 1 fused
    Due.write(0b01001101);
    if (zone_1.sensor_1_thresholded || zone_1.sensor_2_thresholded)
    {
      Due.write('L');
    }
    else
    {
      Due.write('W');
      Due.write('c');
    }
  }
  else
  {
    if (zone_1.sensor_1_en)
    {
      // zone 1 sensor 1
      Due.write(0b01000001 | zone_1.sensor_1_code);
      if (zone_1.sensor_1_thresholded) Due.write('L');
      else
      {
        Due.write('W');
        if (zone_1.sensor_1_raw) Due.write('r'); else Due.write('c');
      }
    }
    if (zone_1.sensor_2_en)
    {
      // zone 1 sensor 2
      Due.write(0b01000001 | zone_1.sensor_2_code);
      if (zone_1.sensor_2_thresholded) Due.write('L');
      else
      {
        Due.write('W');
        if (zone_1.sensor_2_raw) Due.write('r'); else Due.write('c');
      }
    }
  }
  
  // zone 2 =====================
  if (zone_2.fused && zone_2.sensor_1_en && zone_2.sensor_2_en)
  {
    // zone 2 fused
    Due.write(0b01001110);
    if (zone_2.sensor_1_thresholded || zone_2.sensor_2_thresholded)
    {
      Due.write('L');
    }
    else
    {
      Due.write('W');
      Due.write('c');
    }
  }
  else
  {
    if (zone_2.sensor_1_en)
    {
      // zone 2 sensor 1
      Due.write(0b01000010 | zone_2.sensor_1_code);
      if (zone_2.sensor_1_thresholded) Due.write('L');
      else
      {
        Due.write('W');
        if (zone_2.sensor_1_raw) Due.write('r'); else Due.write('c');
      }
    }
    if (zone_2.sensor_2_en)
    {
      // zone 2 sensor 2
      Due.write(0b01000010 | zone_2.sensor_2_code);
      if (zone_2.sensor_2_thresholded) Due.write('L');
      else
      {
        Due.write('W');
        if (zone_2.sensor_2_raw) Due.write('r'); else Due.write('c');
      }
    }
  }
  
  // zone 3 =====================
  if (zone_3.fused && zone_3.sensor_1_en && zone_3.sensor_2_en)
  {
    // zone 3 fused
    Due.write(0b01001111);
    if (zone_3.sensor_1_thresholded || zone_3.sensor_2_thresholded)
    {
      Due.write('L');
    }
    else
    {
      Due.write('W');
      Due.write('c');
    }
  }
  else
  {
    if (zone_3.sensor_1_en)
    {
      // zone 3 sensor 1
      Due.write(0b01000011 | zone_3.sensor_1_code);
      if (zone_3.sensor_1_thresholded) Due.write('L');
      else
      {
        Due.write('W');
        if (zone_3.sensor_1_raw) Due.write('r'); else Due.write('c');
      }
    }
    if (zone_3.sensor_2_en)
    {
      // zone 3 sensor 2
      Due.write(0b01000011 | zone_3.sensor_2_code);
      if (zone_3.sensor_2_thresholded) Due.write('L');
      else
      {
        Due.write('W');
        if (zone_3.sensor_2_raw) Due.write('r'); else Due.write('c');
      }
    }
  }
}

char string_data[] = {0,0,0,0,0,0,0,0,0,0};

void send_data()
{
  SEND_MODE
  delay(1);
  
  unsigned char checksum = 0;
  int v;
  
  // zone 1
  if (zone_1.fused)
  {
    v = min(zone_1.sensor_1_data, zone_1.sensor_2_data);
    sprintf(string_data,"%04X", v);
    checksum ^= v & 0xff;
    checksum ^= (v >> 8) & 0xff;
    Due.print(string_data);
  }
  else
  {
    if (zone_1.sensor_1_en)
    {
      v = zone_1.sensor_1_data;
      sprintf(string_data,"%04X", v);
      checksum ^= v & 0xff;
      checksum ^= (v >> 8) & 0xff;
      Due.print(string_data);
    }
    if (zone_1.sensor_2_en)
    { 
      v = zone_1.sensor_2_data;
      sprintf(string_data,"%04X", v);
      checksum ^= v & 0xff;
      checksum ^= (v >> 8) & 0xff;
      Due.print(string_data);
    }
  }
  // zone 2
  if (zone_2.fused)
  {
    v = min(zone_2.sensor_1_data, zone_2.sensor_2_data);
    sprintf(string_data,"%04X", v);
    checksum ^= v & 0xff;
    checksum ^= (v >> 8) & 0xff;
    Due.print(string_data);
  }
  else
  {
    if (zone_2.sensor_1_en)
    { 
      v = zone_2.sensor_1_data;
      sprintf(string_data,"%04X", v);
      checksum ^= v & 0xff;
      checksum ^= (v >> 8) & 0xff;
      Due.print(string_data);
    }
    if (zone_2.sensor_2_en)
    { 
      v = zone_2.sensor_2_data;
      sprintf(string_data,"%04X", v);
      checksum ^= v & 0xff;
      checksum ^= (v >> 8) & 0xff;
      Due.print(string_data);
    }
  }
  // zone 3
  if (zone_3.fused)
  {
    v = min(zone_3.sensor_1_data, zone_3.sensor_2_data);
    sprintf(string_data,"%04X", v);
    checksum ^= v & 0xff;
    checksum ^= (v >> 8) & 0xff;
    Due.print(string_data);
  }
  else
  {
    if (zone_3.sensor_1_en)
    { 
      v = zone_3.sensor_1_data;
      sprintf(string_data,"%04X", v);
      checksum ^= v & 0xff;
      checksum ^= (v >> 8) & 0xff;
      Due.print(string_data);
    }
    if (zone_3.sensor_2_en)
    { 
      v = zone_3.sensor_2_data;
      sprintf(string_data,"%04X", v);
      checksum ^= v & 0xff;
      checksum ^= (v >> 8) & 0xff;
      Due.print(string_data);
    }
  }
  
  //send checksum
  sprintf(string_data, "%02X", checksum);
  Due.print(string_data);
}

void send_ack()
{
  SEND_MODE
  delay(1);
  Due.write('Y');
}

void send_nack()
{
  SEND_MODE
  delay(1);
  Due.write('N');
}



void SRF01_Cmd(byte Address, byte cmd){                      // Function to send commands to the SRF01
  pinMode(txrxPin, OUTPUT);                                  // Set pin to output and send break by sending pin low, waiting 2ms and sending it high again for 1ms
  digitalWrite(txrxPin, LOW);                              
  delay(2);                                               
  digitalWrite(txrxPin, HIGH);                            
  delay(1);                                                
  UltrasonicBus.write(Address);                              // Send the address of the SRF01
  UltrasonicBus.write(cmd);                                  // Send commnd byte to SRF01
  pinMode(txrxPin, INPUT);                                   // Make input ready for Rx
  int availableJunk = UltrasonicBus.available();             // Filter out the junk data
  for(int x = 0; x < availableJunk; x++){
    byte junk = UltrasonicBus.read();
  }
}

#define US_TIMEOUT 120
const byte NO_DATA = -1; // neccesary - trust me on this

int checkRange(int address) {
  SRF01_Cmd(address, getLatestRange);                       // Calls a function to get range from SRF01
  long time = millis();
  byte highByte = -1;
  while (NO_DATA == highByte)
  {
    if (millis() < time) time = 0; // overflow
    if (millis() - time > US_TIMEOUT)
    {
      return -1;
    }
    highByte = UltrasonicBus.read();                  // Get high byte
  }
  byte lowByte = -1;
  while (NO_DATA == lowByte)
  {
    if (millis() < time) time = 0; // overflow
    if (millis() - time > US_TIMEOUT)
    {
      return -1;
    }
    lowByte = UltrasonicBus.read();                      // Get low byte
  }
  int dist = ((highByte<<8)+lowByte);                    // Put them together
  return dist;
}

void SRF08_fire_all()
{
  Wire.beginTransmission(0);
  Wire.write(0);
  Wire.write(81);
  Wire.write(0);  // minimize echo
  Wire.write(24); // 1m range
  Wire.endTransmission();
  
  range_t = millis();
  awaiting_range = true;
}

int SRF08_collect_result(int zone)
{
  int i2c_address = 0;
  switch(zone)
  {
  case 1:
    i2c_address = 112;
    break;
  case 2:
    i2c_address = 113;
    break;
  case 3:
    i2c_address = 114;
    break;
  default:
    return 0;
  }
  int result = 0;
  
  Wire.beginTransmission(i2c_address);
  Wire.write(2);
  Wire.endTransmission();

  Wire.requestFrom(i2c_address, 2);

  while (Wire.available()<2)   {
 }
	// read the two bytes, and combine them into one int
  result = Wire.read() * 256;
  result += Wire.read();
  
  return result;
}


int SRF08_range(int zone)
{
  int i2c_address = 0;
  switch(zone)
  {
  case 1:
    i2c_address = 112;
    break;
  case 2:
    i2c_address = 113;
    break;
  case 3:
    i2c_address = 114;
    break;
  default:
    return 0;
  }
  //Serial.print(i2c_address);
  //Serial.print(" : ");
  int result = 0;
  Wire.beginTransmission(i2c_address);
  Wire.write(0);
  Wire.write(81);
  Wire.write(0);  // minimize echo
  Wire.write(24); // 1m range
  Wire.endTransmission();

  char c = -1;
  
  while (-1 == c )
  {
    Wire.beginTransmission(i2c_address);
    Wire.write(0); // 2?
    Wire.endTransmission();
    
    process_serial();
    
    Wire.requestFrom(i2c_address, 1);
    
    c = Wire.read();
  }
  
  Wire.beginTransmission(i2c_address);
  Wire.write(2);
  Wire.endTransmission();

  Wire.requestFrom(i2c_address, 2);

  while (Wire.available()<2)   {
    ;
 }
	// read the two bytes, and combine them into one int
  result = Wire.read() * 256;
  result += Wire.read();
  
  return result;
}

int doRange(int address) {
  SRF01_Cmd(address, getNewRange);                       // Calls a function to get range from SRF01
  long time = millis();
  byte highByte = -1;
  while (NO_DATA == highByte)
  {
    if (millis() < time) time = 0; // overflow
    if (millis() - time > US_TIMEOUT)
    {
      return -1;
    }
    highByte = UltrasonicBus.read();                  // Get high byte
  }
  byte lowByte = -1;
  while (NO_DATA == lowByte)
  {
    if (millis() < time) time = 0; // overflow
    if (millis() - time > US_TIMEOUT)
    {
      return -1;
    }
    lowByte = UltrasonicBus.read();                      // Get low byte
  }
  int dist = ((highByte<<8)+lowByte);                    // Put them together
  return dist;
}


int range(int addres)
{
	int address;
	switch(addres)
	  {
	  case 1:
		address = 112;
		break;
	  case 2:
		address = 113;
		break;
	  case 3:
		address = 114;
		break;
	  default:
		return 0;
	  }
  int result = 0;
	Wire.beginTransmission(address);
	Wire.write(0);
	Wire.write(81);
	Wire.write(0);  // minimize echo
	Wire.write(24); // 1m range
	Wire.endTransmission();

        char c = -1;
        while (-1 == c)
        {
	    Wire.beginTransmission(address);
	    Wire.write(0); // 2?
	    Wire.endTransmission();

            Wire.requestFrom(address, 1);
 
            c = Wire.read();
        }
  
	Wire.beginTransmission(address);
	Wire.write(2);
	Wire.endTransmission();

        Wire.requestFrom(address, 2);

	while (Wire.available()<2)   {
	}
	// read the two bytes, and combine them into one int
	result = Wire.read() * 256;
	result += Wire.read();
  
    return result;

/*   Serial.print("Result for address ");
  Serial.print(address);
  Serial.print(" :");
  Serial.println(result); */
}




