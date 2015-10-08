; Auto-generated. Do not edit!


(cl:in-package coalas_pkg-msg)


;//! \htmlinclude JoystickInput.msg.html

(cl:defclass <JoystickInput> (roslisp-msg-protocol:ros-message)
  ((header
    :reader header
    :initarg :header
    :type std_msgs-msg:Header
    :initform (cl:make-instance 'std_msgs-msg:Header))
   (joy_speed_input
    :reader joy_speed_input
    :initarg :joy_speed_input
    :type cl:fixnum
    :initform 0)
   (joy_turn_input
    :reader joy_turn_input
    :initarg :joy_turn_input
    :type cl:fixnum
    :initform 0))
)

(cl:defclass JoystickInput (<JoystickInput>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <JoystickInput>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'JoystickInput)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-msg:<JoystickInput> is deprecated: use coalas_pkg-msg:JoystickInput instead.")))

(cl:ensure-generic-function 'header-val :lambda-list '(m))
(cl:defmethod header-val ((m <JoystickInput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:header-val is deprecated.  Use coalas_pkg-msg:header instead.")
  (header m))

(cl:ensure-generic-function 'joy_speed_input-val :lambda-list '(m))
(cl:defmethod joy_speed_input-val ((m <JoystickInput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:joy_speed_input-val is deprecated.  Use coalas_pkg-msg:joy_speed_input instead.")
  (joy_speed_input m))

(cl:ensure-generic-function 'joy_turn_input-val :lambda-list '(m))
(cl:defmethod joy_turn_input-val ((m <JoystickInput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:joy_turn_input-val is deprecated.  Use coalas_pkg-msg:joy_turn_input instead.")
  (joy_turn_input m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <JoystickInput>) ostream)
  "Serializes a message object of type '<JoystickInput>"
  (roslisp-msg-protocol:serialize (cl:slot-value msg 'header) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed_input)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn_input)) ostream)
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <JoystickInput>) istream)
  "Deserializes a message object of type '<JoystickInput>"
  (roslisp-msg-protocol:deserialize (cl:slot-value msg 'header) istream)
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed_input)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn_input)) (cl:read-byte istream))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<JoystickInput>)))
  "Returns string type for a message object of type '<JoystickInput>"
  "coalas_pkg/JoystickInput")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'JoystickInput)))
  "Returns string type for a message object of type 'JoystickInput"
  "coalas_pkg/JoystickInput")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<JoystickInput>)))
  "Returns md5sum for a message object of type '<JoystickInput>"
  "baf7b344ec0342ef1b9808e7c8b11566")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'JoystickInput)))
  "Returns md5sum for a message object of type 'JoystickInput"
  "baf7b344ec0342ef1b9808e7c8b11566")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<JoystickInput>)))
  "Returns full string definition for message of type '<JoystickInput>"
  (cl:format cl:nil "Header header~%uint8 joy_speed_input~%uint8 joy_turn_input~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'JoystickInput)))
  "Returns full string definition for message of type 'JoystickInput"
  (cl:format cl:nil "Header header~%uint8 joy_speed_input~%uint8 joy_turn_input~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <JoystickInput>))
  (cl:+ 0
     (roslisp-msg-protocol:serialization-length (cl:slot-value msg 'header))
     1
     1
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <JoystickInput>))
  "Converts a ROS message object to a list"
  (cl:list 'JoystickInput
    (cl:cons ':header (header msg))
    (cl:cons ':joy_speed_input (joy_speed_input msg))
    (cl:cons ':joy_turn_input (joy_turn_input msg))
))
