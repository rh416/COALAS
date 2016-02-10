#include "Arduino.h"
#include "SYSIASS_485_Comms.h"

int Comms_485::available(){
  return 2;
}

unsigned char Comms_485::read(){

  return 'a';
}

void Comms_485::send(const char* data, int length){

}