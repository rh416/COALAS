; Auto-generated. Do not edit!


(cl:in-package coalas_pkg-msg)


;//! \htmlinclude JoystickOutput.msg.html

(cl:defclass <JoystickOutput> (roslisp-msg-protocol:ros-message)
  ((header
    :reader header
    :initarg :header
    :type std_msgs-msg:Header
    :initform (cl:make-instance 'std_msgs-msg:Header))
   (source_ID
    :reader source_ID
    :initarg :source_ID
    :type cl:fixnum
    :initform 0)
   (joy_speed
    :reader joy_speed
    :initarg :joy_speed
    :type cl:fixnum
    :initform 0)
   (joy_turn
    :reader joy_turn
    :initarg :joy_turn
    :type cl:fixnum
    :initform 0))
)

(cl:defclass JoystickOutput (<JoystickOutput>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <JoystickOutput>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'JoystickOutput)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-msg:<JoystickOutput> is deprecated: use coalas_pkg-msg:JoystickOutput instead.")))

(cl:ensure-generic-function 'header-val :lambda-list '(m))
(cl:defmethod header-val ((m <JoystickOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:header-val is deprecated.  Use coalas_pkg-msg:header instead.")
  (header m))

(cl:ensure-generic-function 'source_ID-val :lambda-list '(m))
(cl:defmethod source_ID-val ((m <JoystickOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:source_ID-val is deprecated.  Use coalas_pkg-msg:source_ID instead.")
  (source_ID m))

(cl:ensure-generic-function 'joy_speed-val :lambda-list '(m))
(cl:defmethod joy_speed-val ((m <JoystickOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:joy_speed-val is deprecated.  Use coalas_pkg-msg:joy_speed instead.")
  (joy_speed m))

(cl:ensure-generic-function 'joy_turn-val :lambda-list '(m))
(cl:defmethod joy_turn-val ((m <JoystickOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:joy_turn-val is deprecated.  Use coalas_pkg-msg:joy_turn instead.")
  (joy_turn m))
(cl:defmethod roslisp-msg-protocol:symbol-codes ((msg-type (cl:eql '<JoystickOutput>)))
    "Constants for message type '<JoystickOutput>"
  '((:UDOO . 1)
    (:ARDUINO . 2)
    (:ANDROID . 3)
    (:HEADTRACKER . 4))
)
(cl:defmethod roslisp-msg-protocol:symbol-codes ((msg-type (cl:eql 'JoystickOutput)))
    "Constants for message type 'JoystickOutput"
  '((:UDOO . 1)
    (:ARDUINO . 2)
    (:ANDROID . 3)
    (:HEADTRACKER . 4))
)
(cl:defmethod roslisp-msg-protocol:serialize ((msg <JoystickOutput>) ostream)
  "Serializes a message object of type '<JoystickOutput>"
  (roslisp-msg-protocol:serialize (cl:slot-value msg 'header) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'source_ID)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn)) ostream)
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <JoystickOutput>) istream)
  "Deserializes a message object of type '<JoystickOutput>"
  (roslisp-msg-protocol:deserialize (cl:slot-value msg 'header) istream)
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'source_ID)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn)) (cl:read-byte istream))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<JoystickOutput>)))
  "Returns string type for a message object of type '<JoystickOutput>"
  "coalas_pkg/JoystickOutput")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'JoystickOutput)))
  "Returns string type for a message object of type 'JoystickOutput"
  "coalas_pkg/JoystickOutput")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<JoystickOutput>)))
  "Returns md5sum for a message object of type '<JoystickOutput>"
  "b5ba8045f9650fcf441fb8e07b9608dd")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'JoystickOutput)))
  "Returns md5sum for a message object of type 'JoystickOutput"
  "b5ba8045f9650fcf441fb8e07b9608dd")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<JoystickOutput>)))
  "Returns full string definition for message of type '<JoystickOutput>"
  (cl:format cl:nil "Header header~%~%uint8 UDOO=1~%uint8 ARDUINO=2~%uint8 ANDROID=3~%uint8 HEADTRACKER=4~%~%uint8 source_ID		# the source platform that generates this message (UDOO, ARDUINO, etc)~%~%uint8 joy_speed  	# The joystick speed, range: 1 - 255 (full bkwd - full fwd)~%uint8 joy_turn		# The joystick turn, range: 1 - 255 (full left - full right)~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'JoystickOutput)))
  "Returns full string definition for message of type 'JoystickOutput"
  (cl:format cl:nil "Header header~%~%uint8 UDOO=1~%uint8 ARDUINO=2~%uint8 ANDROID=3~%uint8 HEADTRACKER=4~%~%uint8 source_ID		# the source platform that generates this message (UDOO, ARDUINO, etc)~%~%uint8 joy_speed  	# The joystick speed, range: 1 - 255 (full bkwd - full fwd)~%uint8 joy_turn		# The joystick turn, range: 1 - 255 (full left - full right)~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <JoystickOutput>))
  (cl:+ 0
     (roslisp-msg-protocol:serialization-length (cl:slot-value msg 'header))
     1
     1
     1
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <JoystickOutput>))
  "Converts a ROS message object to a list"
  (cl:list 'JoystickOutput
    (cl:cons ':header (header msg))
    (cl:cons ':source_ID (source_ID msg))
    (cl:cons ':joy_speed (joy_speed msg))
    (cl:cons ':joy_turn (joy_turn msg))
))
