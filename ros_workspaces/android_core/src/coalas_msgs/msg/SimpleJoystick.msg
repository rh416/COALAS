Header header

uint8 ARDUINO_=1
uint8 UDOO_=2
uint8 ANDROID_=3
uint8 HEADTRACKER_=4

uint8 source_ID		# the source platform that generates this message (UDOO, ARDUINO, etc)

uint8 joy_speed  	# The joystick speed, range: 1 - 255 (full bkwd - full fwd)
uint8 joy_turn		# The joystick turn, range: 1 - 255 (full left - full right)
