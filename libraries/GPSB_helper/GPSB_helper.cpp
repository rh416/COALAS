/*
  GPSB Helper Library
  
  File: GPSB_helper.cpp

  Author: Martin Henderson
  Creation Date: 26/07/2013
  
            Name                Date           Comment
  Edited:
  
 */

#include "GPSB_helper.h"
#include <Arduino.h>
#ifndef SERIAL_8N2
#define S8N2
#else
#define S8N2 , SERIAL_8N2
#endif
#define boolean bool


static boolean comms_open = false;

/* ======================= Safety ========================= */

static boolean kill = false;
static boolean killed = false; // past tense, makes whole library unresponsive
static boolean isolate = true;

/* Ceases all movement - must be restarted to continue */
void kill_switch()
{
  //kill = true;
  // If possible, hard-kill here
}

/* Enters loopback mode - injects the current joystick position
   back into the system */
void isolate_switch()
{
  isolate = true;
  digitalWrite(13, HIGH);
  if (!comms_open) return;
  //GPSB_log_off();
}

/* Exits loopback mode */
void engage_switch()
{
  isolate = false;
  digitalWrite(13, LOW);
  if (!comms_open) return;
  //GPSB_log_on();
}


/* ========================= Comms ======================== */
 
static Port GPSB_port = SERIAL_1;

//static boolean comms_open = false;
 
/* Opens communication with the GPSB module on the desired port */
void GPSB_setup(const Port port)
{
  switch (port)
  {
  case SERIAL:
    // Serial doesn't support config parameter. All other serial ports do.
    Serial.begin(38400);
    break;
  case SERIAL_1:
    Serial1.begin(38400 S8N2);
    break;
  case SERIAL_2:
    Serial2.begin(38400 S8N2);
    break;
  case SERIAL_3:
    Serial3.begin(38400 S8N2);
    break;
  default:
    return;
  }
  
  pinMode(13, OUTPUT);
  GPSB_port = port;
  comms_open = true;
  kill = false;
  killed = false;
}

/* Returns the number of buffered incoming bytes in the queue */
int GPSB_available()
{
  int bytes = -1;
  if (comms_open)
  {
    switch (GPSB_port)
	{
	case SERIAL:
	  bytes = Serial.available();
	  break;
	case SERIAL_1:
	  bytes = Serial1.available();
	  break;
	case SERIAL_2:
	  bytes = Serial2.available();
	  break;
	case SERIAL_3:
	  bytes = Serial3.available();
	  break;
	default:
	  return -1;
	}
  }
  return bytes;
}

/* Returns the byte at the head of the buffered queue */
unsigned char GPSB_read_byte()
{
  int val = -1;
  if (comms_open)
  {
    switch (GPSB_port)
	{
	case SERIAL:
	  val = Serial.read();
	  break;
	case SERIAL_1:
	  val = Serial1.read();
	  break;
	case SERIAL_2:
	  val = Serial2.read();
	  break;
	case SERIAL_3:
	  val = Serial3.read();
	  break;
	default:
	  return 0;
	}
  }
  if (val > 0) return char(val);
  else return 0;
}

/* Transmits a byte to the GPSB module */
void GPSB_send_byte(const unsigned char value)
{
  if (killed) return;
  
  if (comms_open)
  {
    switch (GPSB_port)
	{
	case SERIAL:
	  Serial.write(value);
	  break;
	case SERIAL_1:
	  Serial1.write(value);
	  break;
	case SERIAL_2:
	  Serial2.write(value);
	  break;
	case SERIAL_3:
	  Serial3.write(value);
	  break;
	default:
	  return;
	}
  }
}


/* ==================== Functionality ===================== */

static boolean logged_on_system = false;
static boolean in_control = false;

static unsigned char wheelchair_speed = 0x80;
static unsigned char wheelchair_turn  = 0x80;

static unsigned char joystick_speed   = 0x80;
static unsigned char joystick_turn    = 0x80;


/* Logs on, removing control from the joystick */
void GPSB_log_on()
{
  if (kill || killed || !comms_open || in_control || isolate) return;
  if (!logged_on_system)
  {
    // logon
    GPSB_send_byte(0x25);
    GPSB_send_byte(0xc1);
    GPSB_send_byte(0x30);
    GPSB_send_byte(0x39);
    GPSB_send_byte(0x39);
    GPSB_send_byte(0x39);
    GPSB_send_byte(0x30);
    GPSB_send_byte(0x33);
    GPSB_send_byte(0x33);
    GPSB_send_byte(0x33);
    GPSB_send_byte(0x30);
    GPSB_send_byte(0xDE);
	
	logged_on_system = true; // need a way to make sure...
	
    // write neutral frame
    GPSB_send_byte(0x25);
    GPSB_send_byte(0x70);
    GPSB_send_byte(0x80);
    GPSB_send_byte(0x80);
    GPSB_send_byte(0x02);
    GPSB_send_byte(0x00);
    GPSB_send_byte(0x93);
	
	in_control = true;
  }
  else
  {
    // log back on
    GPSB_send_byte(0x25);
    GPSB_send_byte(0x42);
    GPSB_send_byte(0x01);
    GPSB_send_byte(0x66);
	
    // write neutral frame
    GPSB_send_byte(0x25);
    GPSB_send_byte(0x70);
    GPSB_send_byte(0x80);
    GPSB_send_byte(0x80);
    GPSB_send_byte(0x02);
    GPSB_send_byte(0x00);
    GPSB_send_byte(0x93);
	
	in_control = true;
  }
}

void GPSB_log_off()
{
    if (!in_control || kill || killed || !logged_on_system) return;
    // log off
    GPSB_send_byte(0x25);
    GPSB_send_byte(0x42);
    GPSB_send_byte(0x02);
    GPSB_send_byte(0x65);
	
	in_control = false;
}

static int safety_counter = 1;

/* Injects a joystick command into the system 
   - set joystick position using GPSB_set_speed_turn() 
     and GPSB_reset_speed_turn()
*/
void GPSB_send_drive_packet()
{
  if (killed || !in_control) return;

  unsigned char speed, turn;
  if (kill)
  {
    speed = 0x80;
    turn  = 0x80;
	killed = true; // prevents further messages being sent
  }
  else if (isolate) // ensures isolation, even when logged on manually
  {
    speed = joystick_speed;
    turn  = joystick_turn;
  }
  else
  {
    speed = wheelchair_speed;
    turn  = wheelchair_turn;
  }
  // write drive frame
  GPSB_send_byte(0x25);
  GPSB_send_byte(0x70);
  GPSB_send_byte(speed);
  GPSB_send_byte(turn);
  GPSB_send_byte(0x02);
  GPSB_send_byte(safety_counter);
  GPSB_send_byte(0x25 ^ 0x70 ^ speed ^ turn
              ^ 0x02 ^ safety_counter);
  
  safety_counter++;
}

/* Sets speed and turn to 0 */
void GPSB_reset_speed_turn()
{
  wheelchair_speed = 0x80;
  wheelchair_turn  = 0x80;
}

/* Sets speed and turn
   - Expects 0x80 for neutral position, 0x01 for full 
     left/reverse and 0xFF for full right/forwards */
void GPSB_set_speed_turn(const unsigned char speed, const unsigned char turn)
{
  wheelchair_speed = speed;
  wheelchair_turn  = turn;
}

/* Sets speed and turn
   - expects 0 for neutral position, -127 for full 
     left/reverse and 127 for full right/forwards */
void GPSB_set_speed_turn_int(const int speed, const int turn)
{
  int bounded_speed = (speed < -127)? -127 : speed;
  bounded_speed = (speed > 127)? 127 : speed;
  int bounded_turn  = (turn  < -127)? -127 : turn;
  bounded_turn = (turn > 127)? 127 : turn;
  wheelchair_speed = bounded_speed + 0x80;
  wheelchair_turn  = bounded_turn  + 0x80;
}


// buffer for GPSB
unsigned char GPSB_buffer[500];
// counter for buffer
unsigned int GPSB_buffer_counter = 0;
// data_update_flag
boolean GPSB_new_data = false;

/* Clears the comms buffer and stores the most recent joystick position */
void GPSB_update_joystick_position()
{
  if (killed) return;
  
  while (GPSB_available() > 0)
  {
    unsigned char c = GPSB_read_byte();
    if (GPSB_buffer_counter != 0 || c == 64)
    {
      if (c == 64)
      {
        GPSB_buffer_counter = 0;
      } 
      GPSB_buffer[GPSB_buffer_counter] = c;
      GPSB_buffer_counter++;
      if (GPSB_buffer_counter > 3)
      {
        joystick_speed = GPSB_buffer[1];
        joystick_turn  = GPSB_buffer[2];
        GPSB_new_data = true;
		GPSB_buffer_counter = 0;
      }
    }
    
    if (kill)
    {
      break;
    }
  }
}

/* Returns the current joystick position, Y axis */
unsigned char get_joystick_speed()
{
  return joystick_speed;
}

/* Returns the current joystick position, X axis */
unsigned char get_joystick_turn()
{
  return joystick_turn;
}
