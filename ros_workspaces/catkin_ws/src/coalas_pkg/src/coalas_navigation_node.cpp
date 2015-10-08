/*
 * coalas_navigation_node.cpp
 *
 *  Created on: 18 May 2015
 *      Author: Paul Oprea
 */

// %Tag(FULLTEXT)%
#include <ros/init.h>
#include <ros/node_handle.h>
#include <ros/publisher.h>
#include <ros/subscriber.h>
#include <rosconsole/macros_generated.h>
#include <std_msgs/String.h>
#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <string>
#include <queue>
#include <boost/foreach.hpp>
#include <coalas_pkg/EncodersOutput.h>
#include <coalas_pkg/SimpleJoystick.h>

//#define mapEncoder(x) (x < 32767 ? (int)x : x - 65535)
//
//// the circumference of the wheel perimeter
//const float WHEEL_CIRCUMFERINCE = 996;
//// encoder tick counts per full wheel revolution
//const float ENCODER_FULL_CIRCLE = 2048;
//// number of encoder ticks for each Millimetre travelled
//const float TICKS_4_MM = ENCODER_FULL_CIRCLE / WHEEL_CIRCUMFERINCE;
//// Millimetres travelled with each encoder tick
//const float MMS_4_TICK = WHEEL_CIRCUMFERINCE / ENCODER_FULL_CIRCLE;
//// distance between the middle points of the two rear wheels
//const float LENGTH_AXELS = 530;
//// the radius of the rear wheels
//const float WHEEL_RADIUS = WHEEL_CIRCUMFERINCE / (2 * M_PI);
//// the diameter of the rear wheels
//const float WHEEL_DIAMETER = WHEEL_CIRCUMFERINCE / M_PI;
//// encoder ticks per full in place rotation
//const float TICKS_360_ROTATION = LENGTH_AXELS / WHEEL_DIAMETER
//		* ENCODER_FULL_CIRCLE;
//const float RADIANS_PER_COUNT = M_PI * (WHEEL_DIAMETER / LENGTH_AXELS)
//		/ ENCODER_FULL_CIRCLE;
//
//#define travelDistance(L, R) ((L + R) / 2.0 * MMS_4_TICK)
///*
// * Main.cpp
// *
// *  Created on: 30 Jan 2015
// *      Author: coalas-kent
// */
//
//#include <math.h>
//#include <stdio.h>
//#include <cstdlib>
//#include <stdint.h>
//
//#include "PID_v1.h"
//
//#define arr_size(arr) (sizeof(arr) / sizeof(*arr))
//#define int2hex(val) ((val + 0x80) % 0xff)
//#define hex2int(val) ((val - 0x80))
//
//float wheel_diameter = 996;
//float mm_per_tick = (wheel_diameter / 1024);
//
//int getD() {
//	return mm_per_tick * 12;
//}
//
//float getD2() {
//	return mm_per_tick * 12;
//}
//
//#define check(val) ((val & 0xff) ^ ((val>>8) & 0xff))
//#define mapEncoder(x) (x < 32767 ? (int)x : x - 65535)

// Converts degrees to radians.
#define deg2Rad(angleDegrees) (angleDegrees * M_PI / 180.0)
// Converts radians to degrees.
#define rad2Deg(angleRadians) (angleRadians * 180.0 / M_PI)

//const bool DEBUG_MODE = true;
const bool DEBUG_MODE = false;
const double CONS_KP = 1;
const double CONS_KI = 0.5;
const double CONS_KD = 2;
const float LENGTH_AXELS = 526;
const int WHEEL_CIRCUMFERINCE = 996;
//const float TICKS_4_MM = 1024 / (float) WHEEL_CIRCUMFERINCE;
const float WHEEL_RADIUS = WHEEL_CIRCUMFERINCE / (2 * M_PI);
const float ENCODER_FULL_CIRCLE = 2048;
const float RADIANS_PER_COUNT = M_PI * (2 * WHEEL_RADIUS / LENGTH_AXELS)
		/ ENCODER_FULL_CIRCLE;

//const float TICKS_4_FULL_CIRCLE = (LENGTH_AXELS / (2 * WHEEL_RADIUS))
//		* ENCODER_FULL_CIRCLE;
const float MMS_4_TICK = M_PI * 2 * WHEEL_RADIUS / ENCODER_FULL_CIRCLE;

static const uint8_t NEUTRAL = 128;
const uint8_t FWD_MAX_SPEED = 180;
//const uint8_t BCK_MAX_SPEED = 90;
const uint8_t RIGHT_TURN = 172;
const uint8_t LEFT_TURN = 84;
//uint8_t fin_correction = 0;
//uint8_t start_correction = 40;

ros::Publisher self_drive_pub;
ros::Publisher chair_distance_pub;
ros::Publisher chair_rotation_pub;
std::queue<std::string> path;

class PID {
public:

//	PID();
	void set(double, double);
	double compute(double);
	double kp;                  // * (P)roportional Tuning Parameter
	double ki;                  // * (I)ntegral Tuning Parameter
	double kd;                  // * (D)erivative Tuning Parameter

	double myInput;  // * Pointers to the Input, Output, and Setpoint variables
	double myOutput; //   This creates a hard link between the variables and the
	double mySetpoint; //   PID, freeing the user from having to constantly tell us
					   //   what these values are.  with pointers we'll just know.

	unsigned long lastTime;
	double ITerm, lastInput;

	unsigned long SampleTime;
	double outMin, outMax;
} myPID;

void PID::set(double Input, double Output) {
	myInput = Input;
	myOutput = Output;
	ITerm = 0;
	mySetpoint = 0;
	SampleTime = 5;
	double SampleTimeInSec = ((double) SampleTime) / 1000;
	kp = CONS_KP;
	ki = CONS_KI * SampleTimeInSec;
	kd = CONS_KD / SampleTimeInSec;
	lastTime = clock() - SampleTime;
	outMin = NEUTRAL - 30;
	outMax = NEUTRAL + 30;
	lastInput = 0;

	ITerm = myOutput;
	lastInput = myInput;
	if (ITerm > outMax)
		ITerm = outMax;
	else if (ITerm < outMin)
		ITerm = outMin;
}

double PID::compute(double in) {
	myInput = in;
	unsigned long now = clock();
	unsigned long timeChange = (now - lastTime);
	if (timeChange >= SampleTime) {
		/*Compute all the working error variables*/
		double input = myInput;
		double error = mySetpoint - input;
		ITerm += (ki * error);
		if (ITerm > outMax)
			ITerm = outMax;
		else if (ITerm < outMin)
			ITerm = outMin;
		double dInput = (input - lastInput);

		/*Compute PID Output*/
		double output = kp * error + ITerm - kd * dInput;

		if (output > outMax)
			output = outMax;
		else if (output < outMin)
			output = outMin;
		myOutput = output;

		/*Remember some variables for next time*/
		lastInput = input;
		lastTime = now;
		return myOutput;
	}
	return 0;
}

struct chair_encs {

	int deltaLeft, deltaRight;
	float deltaDistance, deltaHeading, deltaX, deltaY;
	float chair_x, chair_y, chair_heading;

	coalas_pkg::EncodersOutput prev_encoders;
	coalas_pkg::EncodersOutput encoders;

	bool update(coalas_pkg::EncodersOutput encodersOut) {
		prev_encoders.left_encoder_pos = encoders.left_encoder_pos;
		prev_encoders.right_encoder_pos = encoders.right_encoder_pos;

		encoders.left_encoder_pos = encodersOut.left_encoder_pos;
		encoders.right_encoder_pos = encodersOut.right_encoder_pos;
		int deltaLeft = encoders.left_encoder_pos
				- prev_encoders.left_encoder_pos;
		int deltaRight = encoders.right_encoder_pos
				- prev_encoders.right_encoder_pos;
//		if (abs(deltaLeft) > 32767 || abs(deltaRight) > 32767)
//			return false;
		deltaDistance = 0.5f * (float) (deltaLeft + deltaRight) * MMS_4_TICK;
		deltaHeading = (float) (deltaRight - deltaLeft) * RADIANS_PER_COUNT;

		float deltaX = deltaDistance * (float) cos(chair_heading);
		float deltaY = deltaDistance * (float) sin(chair_heading);
		chair_x += deltaX;
		chair_y += deltaY;
		chair_heading += deltaHeading;
		// limit heading to -Pi <= heading < Pi
		if (chair_heading > M_PI)
			chair_heading -= 2 * M_PI;
		else if (chair_heading <= -M_PI)
			chair_heading += 2 * M_PI;
		if (DEBUG_MODE)
			ROS_INFO("chair heading %4.2f delta left %d delta right %d",
					chair_heading, deltaLeft, deltaRight);
		return true;
	}

} chair_encoders;

class NavigationState {
private:
	static const uint8_t NEUTRAL = 128;
	int internal_state;

	float current_distance, target_distance, current_angle, target_angle;
	uint8_t speed, turn;
//	, turn_correction;
public:
	bool left_right;
	enum states {
		NONE, WAIT, MOVE, TURN, NEXT
	};

	bool turnAt();
	bool goStr8();
	void reset();
	void sendJoyStick();
	void set(const states & state_, int value = 0);
	void handle();

	NavigationState() {
		internal_state = NONE;
		speed = NEUTRAL;
		turn = NEUTRAL;
		left_right = true;
		current_distance = 0;
		current_angle = 0;
		target_distance = 0;
		target_angle = 0;
//		turn_correction = 0;
		myPID.set(0, NEUTRAL);
	}
} navig_state;

void NavigationState::reset() {
	if (DEBUG_MODE)
		ROS_INFO("Resetting distance and angle");
	current_distance = 0;
	current_angle = 0;
//	fin_correction = 0;
//	turn_correction = 40;
}

bool NavigationState::turnAt() {
	current_angle += chair_encoders.deltaHeading;
	if (!(abs(current_angle) < target_angle)) { // target condition
		if (DEBUG_MODE)
			ROS_INFO("done turning current ang %4.2f vs target %4.2f",
					rad2Deg(current_angle), rad2Deg(target_angle));
		return false;
	}
	speed = NEUTRAL;
//	if (turn_correction > 0) {
//		turn_correction--;
//	}
	turn = left_right ? LEFT_TURN
//			- turn_correction
			: RIGHT_TURN;
//			  + turn_correction;
	std_msgs::String msg;
	std::stringstream ss;
	ss << current_angle;
	msg.data = ss.str();
	chair_rotation_pub.publish(msg);
	if (DEBUG_MODE)
		ROS_INFO("target angle: %4.2f current angle: %4.2f",
				rad2Deg(target_angle), rad2Deg(current_angle));
	return true;
}

bool NavigationState::goStr8() {
	current_distance += chair_encoders.deltaDistance;
	if (!(current_distance < target_distance)) { // target condition
		if (DEBUG_MODE)
			ROS_INFO("done going str8 current %4.2f vs target %4.2f",
					current_distance, target_distance);
		return false;
	}
	//TODO
//	float ratio = current_distance / target_distance;
//	if (ratio > 0.7) {
//		if (fin_correction < 40) {
//			fin_correction++;
//		}
//	}
//	if (ratio < 0.3) {
//		if (start_correction > 0) {
//			correction++;
//		}
//	}
	if (DEBUG_MODE)
		ROS_INFO("going str8");
	speed = FWD_MAX_SPEED; // - fin_correction;
	turn = myPID.compute((double) chair_encoders.deltaHeading);
	std_msgs::String msg;
	std::stringstream ss;
	ss << current_distance;
	msg.data = ss.str();
	chair_distance_pub.publish(msg);
	if (DEBUG_MODE)
		ROS_INFO("target dist: %4.2f current dist: %4.2f", target_distance,
				current_distance);
	return true;
}

void NavigationState::sendJoyStick() {
	coalas_pkg::SimpleJoystick joyMsg;
	joyMsg.source_ID = joyMsg._UDOO_;
	joyMsg.joy_speed = speed;
	joyMsg.joy_turn = turn;
	self_drive_pub.publish(joyMsg);
	// reset local variables to neutral for safety
	speed = turn = NEUTRAL;
}

void NavigationState::handle() {
	switch (internal_state) {
	case NONE:
	case WAIT:
		if (!path.empty()) {
			if (DEBUG_MODE)
				ROS_INFO("We have something");
			set(NEXT);
		}
		return;

	case MOVE:
		goStr8() ? sendJoyStick() : set(NEXT);
		break;

	case TURN:
		turnAt() ? sendJoyStick() : set(NEXT);
		break;

	case NEXT:
		if (path.empty()) {
			set(WAIT);
			if (DEBUG_MODE)
				ROS_INFO("Path empty. Wait");
			return;
		}
		reset();
		const char *message;
		message = path.front().c_str();
		int value = strtol(&message[1], NULL, 10);
		switch (message[0]) {
		case 'M':
			set(MOVE, value);
			break;
		case 'T':
			set(TURN, value);
		}
		path.pop();
		break;
	}
}

void NavigationState::set(const states & state_, int value) {
	std_msgs::String message;
	message.data = "N#";

	switch (state_) {
	case NONE:
	case WAIT:
		self_drive_pub.publish(message);
		break;

	case MOVE:
		target_distance = (float) value * 10; // from cm to mm
		break;

	case TURN:
		if (value > 360) {
			value = value % 360;
		}
		left_right = value < 180;
		if (!left_right) {
			value = 360 - value;
		}

		target_angle = (float) deg2Rad(value); // from degrees to radians
		break;

	case NEXT:
		break;
	}
	internal_state = state_;
}

void selfDriveCallback(const std_msgs::String msg) {
	char* message = const_cast<char*>(msg.data.c_str());
	switch (message[0]) {
	case 'M': // move straight
	case 'T': // turn angle
		if (DEBUG_MODE)
			ROS_INFO("Navigator received %s", message);
		path.push(msg.data); // put them in the queue
		break;

	case 'P': // pause
		if (DEBUG_MODE)
			ROS_INFO("Operation paused");
		break;
	case 'C': // cancel
		// empty the path
		while (!path.empty())
			path.pop();
		// set state to wait
		navig_state.set(navig_state.WAIT);
		if (DEBUG_MODE)
			ROS_INFO("Operation cancelled");
		break;
	default:
		break;
	}
}

void encodersCallback(const coalas_pkg::EncodersOutput encOut) {
		if (chair_encoders.update(encOut))
			navig_state.handle();
}

int main(int argc, char **argv) {
	ROS_INFO("COALAS Navigation Node");
	ros::init(argc, argv, "coalas_navig");

	ros::NodeHandle n;

//	// publisher to arduino joystic command
	self_drive_pub = n.advertise<std_msgs::String>("self_drive_output", 1);

//// subscribe to arduino output from chair for encoder data
//	ros::Subscriber encoders_sub = n.subscribe("arduino_outputs", 10,
//			encodersCallback);
// subscribe to udoo output from chair for destination data
	ros::Subscriber self_drive_sub = n.subscribe("udoo_input", 10,
			selfDriveCallback);

	// subscribe to encoder output from Arduino
	ros::Subscriber encoders_sub = n.subscribe<coalas_pkg::EncodersOutput>("encoders_output", 10,
			encodersCallback);

	// chair motion info publishers
	chair_distance_pub = n.advertise<std_msgs::String>("chair_distance", 1);
	chair_rotation_pub = n.advertise<std_msgs::String>("chair_rotation", 1);

	while (ros::ok()) {
		ros::spinOnce();
	}

	return 0;
}
// %EndTag(FULLTEXT)%

