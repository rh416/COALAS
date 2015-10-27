/*
 * coll_avoid_service.cpp
 *
 *  Created on: 8 May 2015
 *      Author: coalas-kent
 */

#include <ros/init.h>
#include <ros/node_handle.h>
#include <ros/service_server.h>
#include <rosconsole/macros_generated.h>
#include <cmath>
#include "coalas_pkg/CollAvoidService.h"

using namespace coalas_pkg;

#define map(value, a, b, x, y) ((value - a)*(y - x) / (b - a) + x)

static const uint8_t NEUTRAL = 128;
static const int MASS = 1;
static const int INERTIA = 1;

inline float _constrain(float value, int low, int high) {
	return value < low ? low : (value > high ? high : value);
}

inline float dampen(int corner, int ortho, int front, bool fwd_bkwd) {
	float number;
	if (corner < ortho && corner < front) {
		number = (float) corner;
		number = map(number, 0, 140, 0, 350);
		number = _constrain(number, 0, 350);
		number = number * 0.01;
		number = 1 - (1 / exp(number));
	} else if (ortho < corner && ortho < front) {
		number = (float) ortho;
		number = map(number, 0, 120, 0, 350);
		number = _constrain(number, 0, 350);
		number = number * 0.01;
		number = 1 - (1 / exp(number));
	} else {
		number = (float) front;
		if (fwd_bkwd) {
			// forward
			number = map(number, 30, 200, 0, 350);
		} else {
			// backward
			number = map(number, 20, 200, 0, 350);
		}
		number = _constrain(number, 0, 350);
		number = number * 0.01;
		number = 1 - (1 / exp(number));
	}
	return number;
}

//
bool correct4Collissions(CollAvoidServiceRequest &joystickInput,
		CollAvoidServiceResponse &correctedJoystick) {
	float leftDamping, rightDamping;
	int F_desired, T_desired, Left_F_desired, Right_F_desired, correctedSpeed,
			correctedTurn;

	bool speed_neutral = joystickInput.joy_speed_output == NEUTRAL;
	bool turn_neutral = joystickInput.joy_turn_output == NEUTRAL;
	if (speed_neutral)
		correctedJoystick.corrected_joy_speed = NEUTRAL;
	if (turn_neutral)
		correctedJoystick.corrected_joy_turn = NEUTRAL;
	// don't bother if speed and turn are both neutral
	if (speed_neutral && turn_neutral)
		return true;

	// v proceed with collision avoidance v
	rightDamping = dampen(joystickInput.sensdor_data[9],
			joystickInput.sensdor_data[10], joystickInput.sensdor_data[8],
			joystickInput.joy_speed_output > NEUTRAL);
	leftDamping = dampen(joystickInput.sensdor_data[7],
			joystickInput.sensdor_data[6], joystickInput.sensdor_data[8],
			joystickInput.joy_speed_output > NEUTRAL);

	if (joystickInput.sensdor_data[8] < joystickInput.sensdor_data[6]
			|| joystickInput.sensdor_data[8] < joystickInput.sensdor_data[7]
			|| joystickInput.sensdor_data[8] < joystickInput.sensdor_data[9]
			|| joystickInput.sensdor_data[8] < joystickInput.sensdor_data[10]) {
		rightDamping = (float) joystickInput.sensdor_data[8];
		rightDamping = map(rightDamping, 20, 200, 0, 350);
		rightDamping = _constrain(rightDamping, 0, 350);
		rightDamping = rightDamping * 0.01;
		rightDamping = 1 - (1 / exp(rightDamping));
		rightDamping = leftDamping;
	}

	F_desired = MASS * joystickInput.joy_speed_output - NEUTRAL;
	T_desired = INERTIA * joystickInput.joy_turn_output - NEUTRAL;
	Left_F_desired = rightDamping * (F_desired - (0.5 * T_desired));
	Right_F_desired = leftDamping * (F_desired + (0.5 * T_desired));

	correctedSpeed = 0.5 * (Left_F_desired + Right_F_desired) + NEUTRAL;
	correctedTurn = Right_F_desired - Left_F_desired + NEUTRAL;

	if (!speed_neutral)
		correctedJoystick.corrected_joy_speed = _constrain(correctedSpeed, 1,
				255);
	if (!turn_neutral)
		correctedJoystick.corrected_joy_turn = _constrain(correctedTurn, 1,
				255);
	return true;
}

int main(int argc, char **argv) {
	ros::init(argc, argv, "collion_avoidance_server");
	ros::NodeHandle n;
	CollAvoidService col_avoid;
	ros::ServiceServer service = n.advertiseService("collision_avoidance",
			correct4Collissions);
	ROS_INFO("Collision avoidance is ready for use.");
	ros::spin();

	return 0;
}

