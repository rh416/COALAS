/*
  SYSIASS RS485 communication library
  
  File: SYSIASS_485_comms.h

  Author: Martin Henderson
  Creation Date: 31/07/2013
  
            Name                Date           Comment
  Edited:
  
 */
 
 #ifndef SYSIASS_485_COMMS_H
 #define SYSIASS_485_COMMS_H

 /* Handles RS485 comms */
 class Comms_485
 {
 public:
   Comms_485();
   
   /* Returns the number of available bytes */
   int available();
   /* Returns a byte */
   unsigned char read();
   /* Sends a byte */
   void send(const char c);
   /* Sends a string */
   void send(const char* s, int n);
   
 private: /* monostate */
   static unsigned char *buffer;
   static bool initialised;
 };
 
 #endif
 