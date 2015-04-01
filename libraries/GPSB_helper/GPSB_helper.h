/*
  GPSB Helper Library
  
  File: GPSB_helper.h

  Author: Martin Henderson
  Creation Date: 26/07/2013
  
            Name                Date           Comment
  Edited:
  
 */
 
 #ifndef GPSB_HELPER_H
 #define GPSB_HELPER_H

/* ======================= Safety ========================= */

/* Ceases all movement - must be restarted to continue */
void kill_switch();

/* Enters loopback mode - injects the current joystick position
   back into the system */
void isolate_switch();

/* Exits loopback mode */
void engage_switch();


/* ========================= Comms ======================== */
 
enum Port {SERIAL, SERIAL_1, SERIAL_2, SERIAL_3};
 
/* Opens communication with the GPSB module on the desired port */
void GPSB_setup(const Port port = SERIAL_1);

/* Returns the number of buffered incoming bytes in the queue */
int GPSB_available();

/* Returns the byte at the head of the buffered queue */
unsigned char GPSB_read_byte();

/* Transmits a byte to the GPSB module */
void GPSB_send_byte(const unsigned char value);


/* ==================== Functionality ===================== */

/* Logs on, removing control from the joystick */
void GPSB_log_on();

/* Logs off, returning control to the joystick */
void GPSB_log_off();

/* Injects a joystick command into the system 
   - set joystick position using GPSB_set_speed_turn() 
     and GPSB_reset_speed_turn()
*/
void GPSB_send_drive_packet();

/* Sets speed and turn to 0 */
void GPSB_reset_speed_turn();

/* Sets speed and turn
   - Expects 0x80 for neutral position, 0x01 for full 
     left/reverse and 0xFF for full right/forwards */
void GPSB_set_speed_turn(const unsigned char speed, const unsigned char turn);

/* Sets speed and turn
   - expects 0 for neutral position, -127 for full 
     left/reverse and 127 for full right/forwards */
void GPSB_set_speed_turn_int(const int speed, const int turn);

/* Clears the comms buffer and stores the most recent joystick position */
void GPSB_update_joystick_position();

/* Returns the current joystick position, Y axis */
unsigned char get_joystick_speed();

/* Returns the current joystick position, X axis */
unsigned char get_joystick_turn();


#endif

