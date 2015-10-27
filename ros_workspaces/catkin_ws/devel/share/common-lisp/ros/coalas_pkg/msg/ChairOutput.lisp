; Auto-generated. Do not edit!


(cl:in-package coalas_pkg-msg)


;//! \htmlinclude ChairOutput.msg.html

(cl:defclass <ChairOutput> (roslisp-msg-protocol:ros-message)
  ((header
    :reader header
    :initarg :header
    :type std_msgs-msg:Header
    :initform (cl:make-instance 'std_msgs-msg:Header))
   (joy_speed_output
    :reader joy_speed_output
    :initarg :joy_speed_output
    :type cl:fixnum
    :initform 0)
   (joy_turn_output
    :reader joy_turn_output
    :initarg :joy_turn_output
    :type cl:fixnum
    :initform 0)
   (left_encoder_pos
    :reader left_encoder_pos
    :initarg :left_encoder_pos
    :type cl:fixnum
    :initform 0)
   (right_encoder_pos
    :reader right_encoder_pos
    :initarg :right_encoder_pos
    :type cl:fixnum
    :initform 0)
   (sensdor_data
    :reader sensdor_data
    :initarg :sensdor_data
    :type (cl:vector cl:fixnum)
   :initform (cl:make-array 11 :element-type 'cl:fixnum :initial-element 0)))
)

(cl:defclass ChairOutput (<ChairOutput>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <ChairOutput>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'ChairOutput)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-msg:<ChairOutput> is deprecated: use coalas_pkg-msg:ChairOutput instead.")))

(cl:ensure-generic-function 'header-val :lambda-list '(m))
(cl:defmethod header-val ((m <ChairOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:header-val is deprecated.  Use coalas_pkg-msg:header instead.")
  (header m))

(cl:ensure-generic-function 'joy_speed_output-val :lambda-list '(m))
(cl:defmethod joy_speed_output-val ((m <ChairOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:joy_speed_output-val is deprecated.  Use coalas_pkg-msg:joy_speed_output instead.")
  (joy_speed_output m))

(cl:ensure-generic-function 'joy_turn_output-val :lambda-list '(m))
(cl:defmethod joy_turn_output-val ((m <ChairOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:joy_turn_output-val is deprecated.  Use coalas_pkg-msg:joy_turn_output instead.")
  (joy_turn_output m))

(cl:ensure-generic-function 'left_encoder_pos-val :lambda-list '(m))
(cl:defmethod left_encoder_pos-val ((m <ChairOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:left_encoder_pos-val is deprecated.  Use coalas_pkg-msg:left_encoder_pos instead.")
  (left_encoder_pos m))

(cl:ensure-generic-function 'right_encoder_pos-val :lambda-list '(m))
(cl:defmethod right_encoder_pos-val ((m <ChairOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:right_encoder_pos-val is deprecated.  Use coalas_pkg-msg:right_encoder_pos instead.")
  (right_encoder_pos m))

(cl:ensure-generic-function 'sensdor_data-val :lambda-list '(m))
(cl:defmethod sensdor_data-val ((m <ChairOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:sensdor_data-val is deprecated.  Use coalas_pkg-msg:sensdor_data instead.")
  (sensdor_data m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <ChairOutput>) ostream)
  "Serializes a message object of type '<ChairOutput>"
  (roslisp-msg-protocol:serialize (cl:slot-value msg 'header) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed_output)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn_output)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'left_encoder_pos)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'right_encoder_pos)) ostream)
  (cl:map cl:nil #'(cl:lambda (ele) (cl:write-byte (cl:ldb (cl:byte 8 0) ele) ostream))
   (cl:slot-value msg 'sensdor_data))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <ChairOutput>) istream)
  "Deserializes a message object of type '<ChairOutput>"
  (roslisp-msg-protocol:deserialize (cl:slot-value msg 'header) istream)
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed_output)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn_output)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'left_encoder_pos)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'right_encoder_pos)) (cl:read-byte istream))
  (cl:setf (cl:slot-value msg 'sensdor_data) (cl:make-array 11))
  (cl:let ((vals (cl:slot-value msg 'sensdor_data)))
    (cl:dotimes (i 11)
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:aref vals i)) (cl:read-byte istream))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<ChairOutput>)))
  "Returns string type for a message object of type '<ChairOutput>"
  "coalas_pkg/ChairOutput")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'ChairOutput)))
  "Returns string type for a message object of type 'ChairOutput"
  "coalas_pkg/ChairOutput")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<ChairOutput>)))
  "Returns md5sum for a message object of type '<ChairOutput>"
  "198ba088e7494b92607b05fbbfe58e4f")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'ChairOutput)))
  "Returns md5sum for a message object of type 'ChairOutput"
  "198ba088e7494b92607b05fbbfe58e4f")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<ChairOutput>)))
  "Returns full string definition for message of type '<ChairOutput>"
  (cl:format cl:nil "Header header~%uint8 joy_speed_output~%uint8 joy_turn_output~%uint8 left_encoder_pos~%uint8 right_encoder_pos~%uint8[11] sensdor_data~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'ChairOutput)))
  "Returns full string definition for message of type 'ChairOutput"
  (cl:format cl:nil "Header header~%uint8 joy_speed_output~%uint8 joy_turn_output~%uint8 left_encoder_pos~%uint8 right_encoder_pos~%uint8[11] sensdor_data~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <ChairOutput>))
  (cl:+ 0
     (roslisp-msg-protocol:serialization-length (cl:slot-value msg 'header))
     1
     1
     1
     1
     0 (cl:reduce #'cl:+ (cl:slot-value msg 'sensdor_data) :key #'(cl:lambda (ele) (cl:declare (cl:ignorable ele)) (cl:+ 1)))
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <ChairOutput>))
  "Converts a ROS message object to a list"
  (cl:list 'ChairOutput
    (cl:cons ':header (header msg))
    (cl:cons ':joy_speed_output (joy_speed_output msg))
    (cl:cons ':joy_turn_output (joy_turn_output msg))
    (cl:cons ':left_encoder_pos (left_encoder_pos msg))
    (cl:cons ':right_encoder_pos (right_encoder_pos msg))
    (cl:cons ':sensdor_data (sensdor_data msg))
))
