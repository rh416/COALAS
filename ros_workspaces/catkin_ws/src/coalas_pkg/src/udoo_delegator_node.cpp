/*
 * udoo_delegator_node.cpp
 *
 *  Created on: 14 May 2015
 *      Author: coalas-kent
 */

/*
 * arduino_serial_node.cpp
 *
 *  Created on: 14 May 2015
 *      Author: Paul Oprea
 */

// %Tag(FULLTEXT)%
#include <netinet/in.h>
#include <ros/ros.h>
#include <boost/foreach.hpp>

#include <rosconsole/macros_generated.h>
#include <std_msgs/String.h>
#include <cstdio>

#include <coalas_pkg/CollAvoidService.h>
#include <coalas_pkg/CollAvoidServiceRequest.h>
#include <coalas_pkg/CollAvoidServiceResponse.h>
#include <coalas_pkg/SimpleJoystick.h>
#include <coalas_pkg/EncodersOutput.h>
#include <coalas_pkg/ChairState.h>
#include <coalas_pkg/RangeArray.h>

ros::Publisher chair_state_pub;
ros::Publisher joy_input_pub;
ros::ServiceClient coll_avoid_client;

//const bool DEBUG_MODE = false;
const bool DEBUG_MODE = true;
const uint8_t NO_OF_SENSORS = 11;

/////////////////////////////////////////////////////////////////////////////////////////
// ========================== v UTILS FOR TESTING v =====================================
/////////////////////////////////////////////////////////////////////////////////////////

/**
 *	binary representation of uint8_t.  ONLY 4 DEBUGGING
 // */
//std::string bin(uint8_t n) {
//	std::stringstream ss;
//
//	uint8_t i;
//	for (i = 1 << 7; i > 0; i = i / 2)
//		ss << ((n & i) ? ("1 ") : ("0 "));
//	return ss.str();
//}
/////////////////////////////////////////////////////////////////////////////////////////
// ========================== ^ UTILS FOR TESTING ^ =====================================
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
// ======================= v CHAIR STATE HANDLING v =====================================
/////////////////////////////////////////////////////////////////////////////////////////
struct state { // structure to maintain and update the chair state

#define Udoo 1
#define Arduino 2
#define Remote 3

private:

	typedef int _system;

	_system platform;

	uint8_t DIRECTIVE;

public:
	/**
	 * CONSTRUCTOR
	 */
	state(_system _platform) {
		DIRECTIVE = 0x01;
		platform = _platform;
	}

	enum options {
		/**
		 *  Acknowledgement bit is to be sent ON for request to remote system
		 *  and echoed back OFF as a confirmation
		 */
		ACK, 		// LEAST SIGNIFICANT BIT
		/**
		 * Used only as a state bit to monitor if the GPSB has been switched ON
		 * or OFF. Must only be toggled locally on the Arduino. Will be ignored
		 * if toggled remotely.
		 */
		GPSB, 		// SECOND BIT
		/**
		 * TODO
		 */
		ROS, 		// TIRD BIT
		/**
		 * @DEBUG bit for running diagnostics !NOTE! - currently only isolates
		 * GPSB from system. If turned ON, the chair will ignore any joystick
		 * input
		 */
		DEBUG, 		// FOURTH BIT
		/**
		 * Informs Arduino that it should send encoder information to ROS
		 */
		ENCODERS, 	// FIFT BIT
		/**
		 * @REMOTE bit ON informs the Arduino to take joystick input remotely
		 * from ROS (e.g. in case of remote driving).
		 */
		REMOTE, 	// SIXTH BIT
		/**
		 * Collision avoidance bit informs ROS system it should correct the
		 * joystick input to avoid obstacles. While this bit is ON, GPSB must
		 * send out sensor data for corrections to occur. Also, it must take
		 * in the remote joystick input received over serial
		 */
		COLL_AVOID, 	// SEVENTH BIT
		/**
		 * SYSTEM bit switches the Arduino system ON/OFF i.e.:
		 * ON - Engages the GPBS and runs through the main loop
		 * OFF - Isolates the GPSB and prevents execution of the loop
		 */
		SYSTEM 		// MOST SIGNIFICANT BIT

	};

private:
	/**
	 * turns a state on
	 */
	bool turnOn(options option, _system caller_platform) {
		// if option is already on, don't bother
		if (isOn(option))
			return false;
		DIRECTIVE |= 1 << option;
		return true;
	}
	/**
	 * turns a state off
	 */
	bool turnOff(options option, _system caller_platform) {
		// if option is already off, don't bother
		if (!isOn(option) || (option == ACK))
			return false;
		DIRECTIVE &= ~(1 << option);
		if (option == ENCODERS) {
			ros::NodeHandle n;
			//	// publisher to arduino joystic command
			ros::Publisher self_drive_pub = n.advertise<std_msgs::String>(
					"udoo_input", 1);
			std_msgs::String message;
			message.data = "C#";
			self_drive_pub.publish(message);
		}
		return true;
	}
public:
	/**
	 * set the system in requested state
	 * parameters:  - @directive : the remote directive
	 * 				- @caller_platform : the platform from which this directive was called.
	 *
	 *!!!IMPORTANT!!! : never use this function to set a directive on the same platform
	 * 					that generated the directive. It will lead to inconsistent states
	 * 					between platforms
	 */
	void setDirectiveCallback(const coalas_pkg::ChairState chair_state) {
		const uint8_t directive = chair_state.chairState;
		const _system caller_platform = ROS; // TODO this is bs. fix it!
		bin(directive);
		if (caller_platform == platform) {
			// This should never happen!
			ROS_ERROR("You've just summoned the Kraken... Only call this  ..."
					"from serialEvent() when you get a directive from Udoo");
			return;
		}
		// if ACK is set to 0, this is just a confirmation message, so ignore
		if (!(directive & (1 << ACK))) {
			ROS_INFO("Confirmation received");
			return;
		}
		for (int i = 0; i < 8; i++) {
			options opt = static_cast<options>(i);
			if ((opt == GPSB) && (caller_platform != Arduino)) {
				ROS_ERROR("Cannot remotely turn on GPSB");
				// cannot remotely turn on GPSB
				continue;
			}
			directive & (1 << i) ?
					turnOn(opt, caller_platform) :
					turnOff(opt, caller_platform);
		}
		// send confirmation
		broadcastState(caller_platform == REMOTE);
//		bin(DIRECTIVE);
	}
	void bin(std::string txt, uint8_t n) {
		std::string binar;
		uint8_t i;
		for (i = 1 << 7; i > 0; i = i / 2)
			binar += ((n & i) ? "1" : "0");
		ROS_INFO("%s: %s", txt.c_str(), binar.c_str());
	}

	void bin(uint8_t n) {
		std::string binar;
		uint8_t i;
		for (i = 1 << 7; i > 0; i = i / 2)
			binar += ((n & i) ? "1" : "0");
		ROS_INFO("%s", binar.c_str());
	}

	void broadcastState(bool isConfirmation) {
		// broadcast to all other components that a change has happened locally and system needs updating
		// confirmations have the ACK bit set to 0
		bin("send 2 Ard:",
				isConfirmation ? DIRECTIVE & ~(1 << ACK) : DIRECTIVE);
		coalas_pkg::ChairState cstMsg;
		cstMsg.chairState = DIRECTIVE;
		cstMsg.isReply = isConfirmation;
		chair_state_pub.publish(cstMsg);
	}

	/**
	 * convenience method to call turnOn or turnOff.
	 * NOTE! Don't call this method from within the structure or Serial will
	 * needlessly bee flooded with broadcasts
	 */
	void set(options opt, bool _ON_OFF, _system caller_platform) {
//		_ON_OFF ? turnOn(opt, caller_platform) : turnOff(opt, caller_platform);
//		// set ACK to 1 so ROS knows it needs to update
//		turnOn(ACK, caller_platform);
//		broadcastState();
		bool resp =
				_ON_OFF ?
						turnOn(opt, caller_platform) :
						turnOff(opt, caller_platform);
		if (resp) {
			// send state as an update, with ACK on 1
			broadcastState(false);
		}
	}

	/**
	 * checks if a state is on or off
	 */
	bool isOn(options option) {
		return DIRECTIVE & (1 << option);
	}

	uint8_t getState() {
		return DIRECTIVE;
	}

} chair_state(Udoo);

/////////////////////////////////////////////////////////////////////////////////////////
// ======================== v COLLISSION HANDLING v =====================================
/////////////////////////////////////////////////////////////////////////////////////////

struct collHandler {
private:
	bool haveJoystick;
	bool haveData;
	::coalas_pkg::CollAvoidServiceRequest coll_avoid_req;
	::coalas_pkg::CollAvoidServiceResponse coll_avoid_resp;

public:
	collHandler() {
		haveJoystick = false;
		haveData = false;
	}

	void digestJoystickCallback(const coalas_pkg::SimpleJoystick joystickOut) {
		coll_avoid_req.joy_speed_output = joystickOut.joy_speed;
		coll_avoid_req.joy_turn_output = joystickOut.joy_turn;
		haveJoystick = true;
	}

	void digestSensorCallback(const coalas_pkg::RangeArray range) {
		// populate the request's sensors array with the hex values from message
		BOOST_FOREACH(uint8_t &sensor, coll_avoid_req.sensdor_data) {
			// TODO make decision on which information to send US vs IR
//			strncpy(val, pos, 2);
//			pos += 2;
//			val[2] = 0;
//			sensor = (uint8_t) strtol(val, NULL, 16);
		}
		haveData = true;
	}

	void correctDrive() {
		if (!(haveJoystick & haveData)) {
			return; // only call the server when we have all the data
		}
		// reset flags
		haveJoystick = false;
		haveData = false;
		// check if connected to CollisionAvoidanceServer
		if (!coll_avoid_client) {
			ros::NodeHandle n;
			// we are not connected to service, reconnect before calling it
			coll_avoid_client = n.serviceClient<coalas_pkg::CollAvoidService>(
					"collision_avoidance", true);
		}
		if (!coll_avoid_client.call(coll_avoid_req, coll_avoid_resp)) {
			ROS_ERROR_STREAM(" Call to CollisionAvoidanceServer failed!");
			return;
		}
		// construct message to be shot off to the Arduino
		coalas_pkg::SimpleJoystick joyMsg;
		joyMsg.source_ID = joyMsg._UDOO_;
		//TODO change coll_avoid_resp to SimpleJoy
		joyMsg.joy_speed = coll_avoid_resp.corrected_joy_speed;
		joyMsg.joy_turn = coll_avoid_resp.corrected_joy_turn;
		joy_input_pub.publish(joyMsg);
	}
} collision_handler;
/////////////////////////////////////////////////////////////////////////////////////////
// ======================== ^ COLLISSION HANDLING ^ =====================================
/////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////
// =============================== v MAIN v =============================================
/////////////////////////////////////////////////////////////////////////////////////////

int main(int argc, char **argv) {
	if (DEBUG_MODE)
		ROS_INFO("Udoo Delegator Node");

	ros::init(argc, argv, "udoo_delegator_node");

	ros::NodeHandle n;
	//SUBSCRIBERS
	// subscribe to wifi input from devices
//	ros::Subscriber remote_input_sub = n.subscribe("udoo_input", 1,
//			rmtDevicesCallback);
//	ros::Subscriber self_drive_sub = n.subscribe("self_drive_output", 1,
//			rmtDevicesCallback);
//	// subscribe to arduino output from chair
//	ros::Subscriber arduino_output_sub = n.subscribe("arduino_outputs", 20,
//			arduinoOutputCallback);

//	ros::Publisher chair_state_pub("chair_state_output", &chair_state_msg);
//	ros::Publisher joy_pub("joystick_output", &joystick_msg);
//	ros::Publisher us_pub("ultrasound_sensors", &us_sensors_msg);
//	ros::Publisher ir_pub("infrared_sensors", &ir_sensors_msg);
//	ros::Publisher encoder_pub("encoders_output", &encoders_msg);
	// subscribe to wifi input from devices
//	chair_state_output

	// subscriber to chair state outputs
	ros::Subscriber chair_state_sub = n.subscribe("chair_state_output", 1,
			&state::setDirectiveCallback, &chair_state); // TODO

	// subscriber to joystick outputs
	ros::Subscriber joystick_input_sub = n.subscribe("joy_input", 1,
			&collHandler::digestJoystickCallback, &collision_handler);

	// subscriber to IR sensor outputs
	ros::Subscriber ir_sensor_sub = n.subscribe("infrared_output", 1,
			&collHandler::digestSensorCallback, &collision_handler);

	// subscriber to US sensor outputs
	ros::Subscriber us_sensor_sub = n.subscribe("ultrasound_output", 1,
			&collHandler::digestSensorCallback, &collision_handler);

	// subscriber to encoder outputs

//	ros::Subscriber self_drive_sub = n.subscribe("self_drive_output", 1,
//			rmtDevicesCallback);
//	// subscribe to arduino output from chair
//	ros::Subscriber arduino_output_sub = n.subscribe("arduino_outputs", 20,
//			arduinoOutputCallback);

//	arduino_input_pub = n.advertise<std_msgs::String>("arduino_inputs", 1);
	// PUBLISHERS
	// publisher to send chair state updates to Arduino serial
	chair_state_pub = n.advertise<coalas_pkg::ChairState>("chair_state", 1);
	// publisher to send joystick input to Arduino serial
	joy_input_pub = n.advertise<coalas_pkg::SimpleJoystick>("joystick_input", 1);
	//SERVICES
	// client for collision avoidance Service
	coll_avoid_client = n.serviceClient<coalas_pkg::CollAvoidService>(
			"collision_avoidance", true);
//	ros::Rate loop_rate(1);
	while (ros::ok()) {
		ros::spinOnce();
		if (chair_state.isOn(chair_state.COLL_AVOID))
			collision_handler.correctDrive();
//		loop_rate.sleep();
	}
	return 0;
}
/////////////////////////////////////////////////////////////////////////////////////////
// =============================== ^ MAIN ^ =============================================
/////////////////////////////////////////////////////////////////////////////////////////

// %EndTag(FULLTEXT)%

