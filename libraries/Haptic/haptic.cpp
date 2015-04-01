
/***********************************************
Haptic feedback for SYSIASS wheelchair platform
@author Martin Henderson
@date 04/12/2013
***********************************************/
#include "haptic.h"
#include "Arduino.h"

const int VIBRATION_PIN = 2;

static bool initialised = false;
static Pattern current_pattern = OFF;
static unsigned long last_change = 0;
static int *delays;
static int *values;
static int current_index = 0;

static int off_delay[] = {500, 0};
static int off_value[] = {  0, 0};
static int continuous_delay[] = {500, 0};
static int continuous_value[] = {  1, 0};
static int subtle_delay[] = {800, 85, 0};
static int subtle_value[] = {  0,  1, 0};
static int warning_delay[] = {100, 100, 100, 400, 0};
static int warning_value[] = {  1,  0,   1,    0, 0};
static int insistant_delay[] = {200, 200, 0};
static int insistant_value[] = {  1,   0, 0};

/* define initial conditions and behaviour */
void init_haptic()
{
  current_index = 0;
  delays = off_delay;
  values = off_value;
  last_change = millis();
  
  pinMode(VIBRATION_PIN, OUTPUT);
  digitalWrite(VIBRATION_PIN, LOW);
  
  initialised = true;
}

void update_vibration()
{
  if (!initialised) init_haptic();
  
  unsigned long elapsed;
  unsigned long current_time = millis();
  
  elapsed = current_time - last_change;
  
  if (elapsed > delays[current_index])
  {
    last_change = current_time;
    
    current_index++;
    /* reset once a delay of '0' is found */
    if (0 == delays[current_index]) current_index = 0;
    
    
    if (0 == values[current_index])
    {
      digitalWrite(VIBRATION_PIN, LOW);
    }
    else
    {
      digitalWrite(VIBRATION_PIN, HIGH);
    }
  }
}

void set_vibration_pattern(Pattern p)
{
  if (!initialised) init_haptic();
  
  /* can't be sure the current value is safe */
  current_index = 0;
  
  switch (p)
  {
  case OFF:
    delays = off_delay;
    values = off_value;
    break;
  case SUBTLE:
    delays = subtle_delay;
    values = subtle_value;
    break;
  case WARNING:
    delays = warning_delay;
    values = warning_value;
    break;
  case INSISTANT:
    delays = insistant_delay;
    values = insistant_value;
    break;
  case CONTINUOUS:
    delays = continuous_delay;
    values = continuous_value;
    break;
  default:
    delays = off_delay;
    values = off_value;
  }
}
