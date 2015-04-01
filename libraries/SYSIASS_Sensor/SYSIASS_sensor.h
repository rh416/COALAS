/*
  SYSIASS sensor node communication library
  
  File: SYSIASS_sensor.h

  Author: Martin Henderson
  Creation Date: 30/07/2013
  
            Name                Date           Comment
  Edited:
  
 */
 
#ifndef SYSIASS_SENSOR_H
#define SYSIASS_SENSOR_H
 
class Comms_485;

/* Enumerated ultrasonic modes */
enum US_Fire_Pattern {ONE_BY_ONE, FIRE_TOGETHER, ON_RCV_PULSE};
 
/* Enumerated zones */
enum Zone_No {ZONE_1, ZONE_2, ZONE_3};

/* Enumerated sensor types */
enum Sensor_Type {FUSED, IR, US};
 
/* Enumerated positions */
enum Position {FRONT, BACK, LEFT, RIGHT
  , FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT};
   
/* Enumerated orientations - starting forwards, turning to the right*/
enum Orientation {ZERO_DEG, FORTY_FIVE_DEG, NINETY_DEG, ONE_THIRTY_FIVE_DEG,
  ONE_EIGHTY_DEG, TWO_TWENTY_FIVE_DEG, TWO_SEVENTY_DEG, THREE_FIFTEEN_DEG};
 
/* Contains the data from a sensor */
class Sensor_Data;
 
/* Structure containing all information on a particular sensor */
struct Sensor
{
  bool enabled;
  bool raw;
  
  Sensor_Type sensor_type;
  
  int sensor_number;
  
  Sensor_Data * latest_data;
};
 
/* Structure containing all information on a particular zone */
struct Zone
{
  bool enabled;
  //bool fused; redundant: test 0==fused_sensors
  
  bool raw;
  
  Sensor * ir;
  Sensor * us;
  Sensor * fused_sensors;
  
  Position position;
  Orientation orientation;
};

/* Structure containing all information on a particular node */
struct Sensor_Node_Configuration
{
  Zone zone_1;
  Zone zone_2;
  Zone zone_3;
};

/* Handles master-sensor comms */
class Sensor_Node
{
public:
  Sensor_Node(unsigned char id, Comms_485*);
  
  /* Tests whether node exists */
  bool exists();
  
  /* populates the sensor configuration */
  bool setup();
  
  /* Returns the configuration of the sensor */
  const Sensor_Node_Configuration get_configuration();
  /* Fuses or unfuses a zone */
  void fuse_zone(Zone_No, bool fuse = true);
  /* Enables or disables an entire zone */
  void enable_zone(Zone_No, bool enable = true);
  /* Enables or disables a sensor in a zone */
  void enable_sensor(Zone_No, Sensor_Type, bool enable = true);
  /* Selects raw or cm data from a sensor in a zone */
  void select_raw_data(Zone_No, Sensor_Type, bool raw = true);
  
  /* sets the ultrasound fire mode */
  void set_mode(US_Fire_Pattern);
  
  /* Sends modified configuration to sensor node */
  bool update_node();
  
  /* Learns the data format of the node */
  void get_data_format();
   
  /* Requests new data from the node */
  bool refresh_data();
  
  /* Returns the latest data from a given sensor (-1 if unavailable) */
  int get_sensor_data(Zone_No, Sensor_Type);
  
  
  
private:
  void clear_rx();
  
  bool configured;
  char id;
  Sensor_Node_Configuration conf;
  Comms_485* comms;
};
 
#endif
