#ifndef HAPTIC
#define HAPTIC
/***********************************************
Haptic feedback for SYSIASS wheelchair platform
@author Martin Henderson
@date 04/12/2013
***********************************************/

enum Pattern {OFF, SUBTLE, WARNING, INSISTANT, CONTINUOUS};

void  init_haptic();
/* Call this in main loop to 'wake' haptic thread periodically */
void update_vibration();

/* Set the type of feedback that the user will experience */
void set_vibration_pattern(Pattern);


#endif
