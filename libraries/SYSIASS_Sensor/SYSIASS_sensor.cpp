/*
  SYSIASS sensor node communication library
  
  File: SYSIASS_sensor.cpp

  Author: Martin Henderson
  Creation Date: 31/07/2013
  
            Name                Date           Comment
  Edited:
  
 */
 
#include "SYSIASS_sensor.h"
#include "SYSIASS_485_comms.h"
#include <Arduino.h>

#define SENSOR_PROTOCOL_BYTE '&'

int test;

class Sensor_Data
{
public:
  Sensor_Data() : current_data(0){}
  int current_data;
};

  
/* Clears Rx buffer */
void Sensor_Node::clear_rx()
{
  delay(5);
  while (comms->available() > 0) comms->read();
}

// construct object and set defaults
Sensor_Node::Sensor_Node(unsigned char id, Comms_485* comms)
{
  this->id = id;
  this->comms = comms;
  // empty pointers
  conf.zone_1.ir = 0;
  conf.zone_2.ir = 0;
  conf.zone_3.ir = 0;
  conf.zone_1.us = 0;
  conf.zone_2.us = 0;
  conf.zone_3.us = 0;
  conf.zone_1.fused_sensors = 0;
  conf.zone_2.fused_sensors = 0;
  conf.zone_3.fused_sensors = 0;
  configured = false;
}

/* Tests whether node exists - timeout 500ms */
bool Sensor_Node::exists()
{
  clear_rx();
  clear_rx();
  clear_rx();
  clear_rx();
  clear_rx();
  clear_rx();
  char string[] = "&0S"; // scan
  string[0] = SENSOR_PROTOCOL_BYTE;
  string[1] = id;
  comms->send(string, 3);
  for (int t = 0; t < 10; t++)
  {
    if (comms->available() > 0)
	{
	  char c = comms->read();
	  if (c == 'Y')	  return true;
	  else {/*Serial.write(c);*/return false;}
	}
	delay(25);
  }
  return false;
}

/* helper function for setup() */
void continue_setup(Zone* z, int count, char c)
{
  switch (count)
  {
  case 0: // position
  case 1: // orientation
    switch (c)
	{
	case 'F':
	  z->position = FRONT;
	  break;
	case 'B':
	  z->position = BACK;
	  break;
	case 'L':
	  z->position = LEFT;
	  break;
	case 'R':
	  z->position = RIGHT;
	  break;
	case '1':
	  z->position = FRONT_LEFT;
	  break;
	case '2':
	  z->position = FRONT_RIGHT;
	  break;
	case '3':
	  z->position = BACK_RIGHT;
	  break;
	case '4':
	  z->position = BACK_LEFT;
	  break;
	case 'a':
	  z->orientation = ZERO_DEG;
	  break;
	case 'b':
	  z->orientation = FORTY_FIVE_DEG;
	  break;
	case 'c':
	  z->orientation = NINETY_DEG;
	  break;
	case 'd':
	  z->orientation = ONE_THIRTY_FIVE_DEG;
	  break;
	case 'e':
	  z->orientation = ONE_EIGHTY_DEG;
	  break;
	case 'f':
	  z->orientation = TWO_TWENTY_FIVE_DEG;
	  break;
	case 'g':
	  z->orientation = TWO_SEVENTY_DEG;
	  break;
	case 'h':
	  z->orientation = THREE_FIFTEEN_DEG;
	  break;
	}
    break;
  case 2: // sensor 1
  case 3: // sensor 2
    switch (c & 0b11100)
	{
	case 0b1100: // fused IR + US
	  z-> fused_sensors = new Sensor;
	  z-> fused_sensors-> sensor_type = FUSED;
	  z-> fused_sensors-> enabled = true;
	  z-> fused_sensors-> raw = false;
	  z-> fused_sensors-> sensor_number = c-1;
	  z-> fused_sensors-> latest_data = 0;
	  break;
	case 0b0100: // US
	  z-> us = new Sensor;
	  z-> us-> sensor_type = US;
	  z-> us-> enabled = true;
	  z-> us-> raw = false;
	  z-> us-> sensor_number = c-1;
	  z-> us-> latest_data = 0;
	  break;
	case 0b1000: // IR
	  z-> ir = new Sensor;
	  z-> ir-> sensor_type = IR;
	  z-> ir-> enabled = true;
	  z-> ir-> raw = false;
	  z-> ir-> sensor_number = c-1;
	  z-> ir-> latest_data = 0;
	  break;
	}
    break;
  default:;
  }
}

/* populates the sensor configuration */
bool Sensor_Node::setup()
{
  clear_rx();
  char string[] = "&0R"; // request configuration
  string[0] = SENSOR_PROTOCOL_BYTE;
  string[1] = id;
  comms->send(string, 3);
  
  long start = millis();
  
  bool done = false;
  int retried = 0;
  
  int zone = 1;
  delete conf.zone_1.ir; conf.zone_1.ir = 0;
  delete conf.zone_1.us; conf.zone_1.us = 0;
  delete conf.zone_1.fused_sensors; conf.zone_1.fused_sensors = 0;
  int count = 0;
  
  while (!done)
  {
    long now = millis();
	if (now < start) start = 0;
	if (now - start > 50 + retried*30)
	{
	  retried ++;
      clear_rx();
      zone = 1;
	  count = 0;
	  comms->send(string, 3);
	}
	if (now - start > 400)
    { // timeout
		return false;
    	break;
	}
    if (comms->available() > 0)
	{
	  char c = comms->read();
	  if ('.' == c)
	  {
	    done = true;
	  }
	  else if (',' == c)
	  {
	    zone ++;
		if (zone == 2)
		{
          delete conf.zone_2.ir; conf.zone_2.ir = 0;
          delete conf.zone_2.us; conf.zone_2.us = 0;
          delete conf.zone_2.fused_sensors; conf.zone_2.fused_sensors = 0;
		}
		else if (zone == 3)
		{
          delete conf.zone_3.ir; conf.zone_3.ir = 0;
          delete conf.zone_3.us; conf.zone_3.us = 0;
          delete conf.zone_3.fused_sensors; conf.zone_3.fused_sensors = 0;
		}
		count = 0;
	  }
	  else
	  {
	    switch (zone)
		{
		case 1:
	      continue_setup(&(conf.zone_1), count, c);
		  break;
		case 2:
	      continue_setup(&(conf.zone_2), count, c);
		  break;
		case 3:
	      continue_setup(&(conf.zone_3), count, c);
		  break;
		}
		count++;
	  }
	}
  }
  configured = true;
  return true;
}

/* Returns the most recent configuration data for the sensor */
const Sensor_Node_Configuration Sensor_Node::get_configuration()
{
  if (configured) return conf;
  else
  {
    return conf; // Better way?
  }
}

/* Enables or disables a sensor in a zone */
void Sensor_Node::enable_sensor(Zone_No n, Sensor_Type t, bool enable)
{
  // select zone
  Zone * z;
  switch(n)
  {
  case ZONE_1:
    z = &conf.zone_1;
    break;
  case ZONE_2:
    z = &conf.zone_2;
    break;
  case ZONE_3:
    z = &conf.zone_3;
    break;
  }
  // select sensor
  Sensor * s;
  switch(t)
  {
  case US:
    s = z->us;
    break;
  case IR:
    s = z->ir;
    break;
  case FUSED:
    s = z->fused_sensors;
    break;
  }
  // (en/dis)able
  if (s)
  {
    s->enabled = enable;
  }
}

/* Enables or disables an entire zone */
void Sensor_Node::enable_zone(Zone_No n, bool enable)
{
  // select zone
  Zone * z;
  switch(n)
  {
  case ZONE_1:
    z = &conf.zone_1;
    break;
  case ZONE_2:
    z = &conf.zone_2;
    break;
  case ZONE_3:
    z = &conf.zone_3;
    break;
  }
  // enable or disable
  z->enabled =  enable;
}

/* Fuses or Unfuses sensors in a zone */
void Sensor_Node::fuse_zone(Zone_No n, bool fuse)
{
  // select zone
  Zone * z;
  switch(n)
  {
  case ZONE_1:
    z = &conf.zone_1;
    break;
  case ZONE_2:
    z = &conf.zone_2;
    break;
  case ZONE_3:
    z = &conf.zone_3;
    break;
  }
  // check fusable sensors exist
  if (z->ir && z->us && fuse)
  {
    delete z->ir; z->ir = 0;
    delete z->us; z->us = 0;
    delete z->fused_sensors; z->fused_sensors = 0; // delete all three to prevent leaks
	
	z-> fused_sensors = new Sensor;
	z-> fused_sensors-> sensor_type = FUSED;
	z-> fused_sensors-> enabled = true;
	z-> fused_sensors-> raw = false;
    z-> fused_sensors-> sensor_number = 1;
	z-> fused_sensors-> latest_data = 0;
  }
  else if (z->fused_sensors && !fuse)
  {
    delete z->ir; z->ir = 0;
    delete z->us; z->us = 0;
    delete z->fused_sensors; z->fused_sensors = 0; // delete all three to prevent leaks
	
	z-> us = new Sensor;
	z-> us-> sensor_type = US;
	z-> us-> enabled = true;
	z-> us-> raw = false;
	z-> us-> sensor_number = 2; // assumption! FIXME
	z-> us-> latest_data = 0;
	
	z-> ir = new Sensor;
	z-> ir-> sensor_type = IR;
	z-> ir-> enabled = true;
	z-> ir-> raw = false;
	z-> ir-> sensor_number = 1; // assumption! FIXME
	z-> ir-> latest_data = 0;
  }
}

/* Swiches a sensor in a zone between raw and cm data */
void Sensor_Node::select_raw_data(Zone_No n, Sensor_Type t, bool raw)
{
  // select zone
  Zone * z;
  switch(n)
  {
  case ZONE_1:
    z = &conf.zone_1;
    break;
  case ZONE_2:
    z = &conf.zone_2;
    break;
  case ZONE_3:
    z = &conf.zone_3;
    break;
  }
  // select sensor
  Sensor * s;
  switch(t)
  {
  case US:
    s = z->us;
    break;
  case IR:
    s = z->ir;
    break;
  case FUSED:
    return; // fused can't be raw
  }
  // raw / cm
  if (s)
  {
    s->raw = raw;
  }
}


/* Sends modified configuration to sensor node */
bool Sensor_Node::update_node()
{
  clear_rx();
  char instruction[40];
  int len_instruction = 0;
  
  instruction[0] = SENSOR_PROTOCOL_BYTE;
  instruction[1] = id;
  instruction[2] = 'C';
  
  len_instruction = 3;
  
  // assumes two sensors per zone and order IR, US. FIXME
  for (int i = 0; i < 3; i++)
  {
    Zone * z;
	// select zone
    switch (i)
	{
	case 0:
	  z = &conf.zone_1;
	  break;
	case 1:
	  z = &conf.zone_2;
	  break;
	case 2:
	  z = &conf.zone_3;
	  break;
	}
	// compile instruction
    if (z->fused_sensors && z->enabled)
	{
	  // fused
	  instruction[len_instruction] = 'F';
      len_instruction += 1;
	  instruction[len_instruction] = 'C';
      len_instruction += 1;
	  instruction[len_instruction] = 'C';
      len_instruction += 1;
	}
	else
	{
	  // unfused
	  instruction[len_instruction] = 'S';
      len_instruction += 1;
	  if (!z->enabled || !z->ir || !z->ir->enabled) instruction[len_instruction] = '0';
	  else if (z->ir->raw) instruction[len_instruction] = 'R';
	  else instruction[len_instruction] = 'C';
      len_instruction += 1;
	  if (!z->enabled || !z->us || !z->us->enabled) instruction[len_instruction] = '0';
	  else if (z->us->raw) instruction[len_instruction] = 'R';
	  else instruction[len_instruction] = 'C';
      len_instruction += 1;
	}
  }
  
  comms->send(instruction, len_instruction);
  delay(1);
  if (comms->read() == 'Y') return true;
  comms->send(instruction, len_instruction);
  delay(1);
  if (comms->read() == 'Y') return true;
  return false;
}

/* Learns the data format of the node */
void Sensor_Node::get_data_format()
{
  clear_rx();
  char instruction[] = "&0F"; // get data format
  instruction[0] = SENSOR_PROTOCOL_BYTE;
  instruction[1] = id;
  comms->send(instruction, 3); // read reply TODO
}

/* Requests new data from the node */
bool Sensor_Node::refresh_data()
{
  timing_log(F("Start refreshing data for node: ") + this->id);
  clear_rx();
  char instruction[] = "&0D"; // get current data
  instruction[0] = SENSOR_PROTOCOL_BYTE;
  instruction[1] = id;
  comms->send(instruction, 3);
  
  long start = millis();
  
  bool done = false;
  int retried = 0;
  while (!done)
  {
    long now = millis();
	if (now < start) start = 0;
	if (now - start > 50 + retried*30)
	{
	  retried ++;
	  //while (comms->available() > 0) Serial.write(comms->read());Serial.println();
      clear_rx();
	  comms->send(instruction, 3);
	}
	if (now - start > 400)
    { // timeout
	    conf.zone_1.us->latest_data->current_data = 0;
	    conf.zone_2.us->latest_data->current_data = 0;
	    conf.zone_3.us->latest_data->current_data = 0;
	    conf.zone_1.ir->latest_data->current_data = 0;
	    conf.zone_2.ir->latest_data->current_data = 0;
	    conf.zone_3.ir->latest_data->current_data = 0;
		return false;
    	break;
	}
    if (comms->available() >= 26)
	{
	  char recieved[33] = "0000 0000 0000 0000 0000 0000 00";
	  char **ptr;
	  for (int i = 0; i < 32; i++)
	  {
	    if (4 == i || 9 == i || 14 == i || 19 == i || 24 == i || 29 == i) recieved[i] = ' ';
	    else recieved[i] = comms->read();
	  }
	  //Serial.write(recieved);Serial.println();
	 
	  //Serial.write("converted values:");
	  long int ir1 = strtol(recieved, 0, 16);//Serial.print(ir1);
	  long int us1 = strtol(&recieved[5], 0, 16);//Serial.print(us1);
	  long int ir2 = strtol(&recieved[10], 0, 16);//Serial.print(ir2);
	  long int us2 = strtol(&recieved[15], 0, 16);//Serial.print(us2);
	  long int ir3 = strtol(&recieved[20], 0, 16);//Serial.print(ir3);
	  long int us3 = strtol(&recieved[25], 0, 16);//Serial.print(us3);
	  long int checksum = strtol(&recieved[30], 0, 16);//Serial.print(checksum);
	  //Serial.write(".\n");
	  
	  // check checksum
	  if ( 0 != (
	   (ir1 & 0xff) ^ ((ir1>>8) & 0xff) ^
	   (ir2 & 0xff) ^ ((ir2>>8) & 0xff) ^
	   (ir3 & 0xff) ^ ((ir3>>8) & 0xff) ^
	   (us1 & 0xff) ^ ((us1>>8) & 0xff) ^
	   (us2 & 0xff) ^ ((us2>>8) & 0xff) ^
	   (us3 & 0xff) ^ ((us3>>8) & 0xff) ^
	   (checksum & 0xff)) & 0xff )
	  {
	    //recieved response, but misread some data, so ignore
	    return true;
	  }
	  
	  if (!conf.zone_1.ir->latest_data) conf.zone_1.ir->latest_data = new Sensor_Data;
	  conf.zone_1.ir->latest_data->current_data = ir1;
	  
	  if (!conf.zone_1.us->latest_data) conf.zone_1.us->latest_data = new Sensor_Data;
	  conf.zone_1.us->latest_data->current_data = us1;
	  
	  if (!conf.zone_2.ir->latest_data) conf.zone_2.ir->latest_data = new Sensor_Data;
	  conf.zone_2.ir->latest_data->current_data = ir2;
	  
	  if (!conf.zone_2.us->latest_data) conf.zone_2.us->latest_data = new Sensor_Data;
	  conf.zone_2.us->latest_data->current_data = us2;
	  
	  if (!conf.zone_3.ir->latest_data) conf.zone_3.ir->latest_data = new Sensor_Data;
	  conf.zone_3.ir->latest_data->current_data = ir3;
	  
	  if (!conf.zone_3.us->latest_data) conf.zone_3.us->latest_data = new Sensor_Data;
	  conf.zone_3.us->latest_data->current_data = us3;
	  
	  done = true;
	}
  }
  timing_log(F("End refreshing data for node: ") + this->id);
  return true;
}

/* Returns the latest data from a given sensor (-1 if unavailable) */
int Sensor_Node::get_sensor_data(Zone_No n, Sensor_Type t)
{
  Zone * z;
  // select zone
  switch (n)
  {
  case ZONE_1:
    z = &conf.zone_1;
    break;
  case ZONE_2:
    z = &conf.zone_2;
    break;
  case ZONE_3:
    z = &conf.zone_3;
    break;
  }
  // select sensor
  Sensor * s;
  switch(t)
  {
  case US:
    s = z->us;
    break;
  case IR:
    s = z->ir;
    break;
  case FUSED:
    s = z->fused_sensors;
    break;
  }
  
  if (s && s->latest_data) return s->latest_data->current_data;
  return -1;
}


  /* sets the ultrasound fire mode */
  void Sensor_Node::set_mode(US_Fire_Pattern p)
  {
    clear_rx();
    char instruction[] = "&0MS"; // set mode
    instruction[0] = SENSOR_PROTOCOL_BYTE;
    instruction[1] = id;
    switch (p)
	{
	case ONE_BY_ONE:
	  return;
	case FIRE_TOGETHER:
      instruction[3] = 'S';
	  break;
	case ON_RCV_PULSE:
      instruction[3] = 'P';
	  break;
	default:;
	}
    comms->send(instruction, 4);
  }

