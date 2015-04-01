/*
  SYSIASS RS485 communication library
  
  File: SYSIASS_485_comms.cpp

  Author: Martin Henderson
  Creation Date: 31/07/2013
  
            Name                Date           Comment
  Edited:
  
 */
 
#include "SYSIASS_485_comms.h"
#include <Arduino.h>
 
const int rw_pin = 32;

#define BAUD_485 57600 //*38400*/115200

void data_mode_sending()
{
  digitalWrite(rw_pin, HIGH);
  delay(3); // to hold line idle high
}

void data_mode_receiving()
{
  delay(3); // to prevent end of message being cut off
  digitalWrite(rw_pin, LOW); 
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
    data_mode_receiving();
	if (Serial1.peek() == 0) Serial1.read();
  }
};

/* place macro inside block statement before sending
   data to ensure that S/R* is set back to recieve
   at end of block statement
*/
#define SEND_MODE Data_sender d = Data_sender();

 
bool Comms_485::initialised = false;
unsigned char * Comms_485::buffer = 0;
 
Comms_485::Comms_485()
{
  if (initialised == false)
  {
    initialised = true;
    Serial1.begin(BAUD_485);
    pinMode(rw_pin, OUTPUT);
	buffer = new unsigned char[200];
  }
}

/* Returns the number of available bytes */
int Comms_485::available()
{
  return Serial1.available();
}

/* Returns a byte */
unsigned char Comms_485::read()
{
  return Serial1.read();
}

/* Sends a byte */
void Comms_485::send(const char c)
{
  SEND_MODE
  Serial1.write(c);
}

/* Sends a string */
void Comms_485::send(const char* s, int n)
{
  SEND_MODE
  for (int i = 0; i < n; i++)
  {
    Serial1.write(s[i]);
  }
}
