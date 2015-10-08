; Auto-generated. Do not edit!


(cl:in-package coalas_pkg-srv)


;//! \htmlinclude CollAvoidService-request.msg.html

(cl:defclass <CollAvoidService-request> (roslisp-msg-protocol:ros-message)
  ((joy_speed_output
    :reader joy_speed_output
    :initarg :joy_speed_output
    :type cl:fixnum
    :initform 0)
   (joy_turn_output
    :reader joy_turn_output
    :initarg :joy_turn_output
    :type cl:fixnum
    :initform 0)
   (sensdor_data
    :reader sensdor_data
    :initarg :sensdor_data
    :type (cl:vector cl:fixnum)
   :initform (cl:make-array 11 :element-type 'cl:fixnum :initial-element 0)))
)

(cl:defclass CollAvoidService-request (<CollAvoidService-request>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <CollAvoidService-request>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'CollAvoidService-request)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-srv:<CollAvoidService-request> is deprecated: use coalas_pkg-srv:CollAvoidService-request instead.")))

(cl:ensure-generic-function 'joy_speed_output-val :lambda-list '(m))
(cl:defmethod joy_speed_output-val ((m <CollAvoidService-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:joy_speed_output-val is deprecated.  Use coalas_pkg-srv:joy_speed_output instead.")
  (joy_speed_output m))

(cl:ensure-generic-function 'joy_turn_output-val :lambda-list '(m))
(cl:defmethod joy_turn_output-val ((m <CollAvoidService-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:joy_turn_output-val is deprecated.  Use coalas_pkg-srv:joy_turn_output instead.")
  (joy_turn_output m))

(cl:ensure-generic-function 'sensdor_data-val :lambda-list '(m))
(cl:defmethod sensdor_data-val ((m <CollAvoidService-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:sensdor_data-val is deprecated.  Use coalas_pkg-srv:sensdor_data instead.")
  (sensdor_data m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <CollAvoidService-request>) ostream)
  "Serializes a message object of type '<CollAvoidService-request>"
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed_output)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn_output)) ostream)
  (cl:map cl:nil #'(cl:lambda (ele) (cl:write-byte (cl:ldb (cl:byte 8 0) ele) ostream))
   (cl:slot-value msg 'sensdor_data))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <CollAvoidService-request>) istream)
  "Deserializes a message object of type '<CollAvoidService-request>"
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_speed_output)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'joy_turn_output)) (cl:read-byte istream))
  (cl:setf (cl:slot-value msg 'sensdor_data) (cl:make-array 11))
  (cl:let ((vals (cl:slot-value msg 'sensdor_data)))
    (cl:dotimes (i 11)
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:aref vals i)) (cl:read-byte istream))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<CollAvoidService-request>)))
  "Returns string type for a service object of type '<CollAvoidService-request>"
  "coalas_pkg/CollAvoidServiceRequest")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'CollAvoidService-request)))
  "Returns string type for a service object of type 'CollAvoidService-request"
  "coalas_pkg/CollAvoidServiceRequest")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<CollAvoidService-request>)))
  "Returns md5sum for a message object of type '<CollAvoidService-request>"
  "7c2dd6e553bf4bcac771ba0b4ab02457")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'CollAvoidService-request)))
  "Returns md5sum for a message object of type 'CollAvoidService-request"
  "7c2dd6e553bf4bcac771ba0b4ab02457")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<CollAvoidService-request>)))
  "Returns full string definition for message of type '<CollAvoidService-request>"
  (cl:format cl:nil "uint8 joy_speed_output~%uint8 joy_turn_output~%uint8[11] sensdor_data~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'CollAvoidService-request)))
  "Returns full string definition for message of type 'CollAvoidService-request"
  (cl:format cl:nil "uint8 joy_speed_output~%uint8 joy_turn_output~%uint8[11] sensdor_data~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <CollAvoidService-request>))
  (cl:+ 0
     1
     1
     0 (cl:reduce #'cl:+ (cl:slot-value msg 'sensdor_data) :key #'(cl:lambda (ele) (cl:declare (cl:ignorable ele)) (cl:+ 1)))
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <CollAvoidService-request>))
  "Converts a ROS message object to a list"
  (cl:list 'CollAvoidService-request
    (cl:cons ':joy_speed_output (joy_speed_output msg))
    (cl:cons ':joy_turn_output (joy_turn_output msg))
    (cl:cons ':sensdor_data (sensdor_data msg))
))
;//! \htmlinclude CollAvoidService-response.msg.html

(cl:defclass <CollAvoidService-response> (roslisp-msg-protocol:ros-message)
  ((corrected_joy_speed
    :reader corrected_joy_speed
    :initarg :corrected_joy_speed
    :type cl:fixnum
    :initform 0)
   (corrected_joy_turn
    :reader corrected_joy_turn
    :initarg :corrected_joy_turn
    :type cl:fixnum
    :initform 0))
)

(cl:defclass CollAvoidService-response (<CollAvoidService-response>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <CollAvoidService-response>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'CollAvoidService-response)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-srv:<CollAvoidService-response> is deprecated: use coalas_pkg-srv:CollAvoidService-response instead.")))

(cl:ensure-generic-function 'corrected_joy_speed-val :lambda-list '(m))
(cl:defmethod corrected_joy_speed-val ((m <CollAvoidService-response>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:corrected_joy_speed-val is deprecated.  Use coalas_pkg-srv:corrected_joy_speed instead.")
  (corrected_joy_speed m))

(cl:ensure-generic-function 'corrected_joy_turn-val :lambda-list '(m))
(cl:defmethod corrected_joy_turn-val ((m <CollAvoidService-response>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:corrected_joy_turn-val is deprecated.  Use coalas_pkg-srv:corrected_joy_turn instead.")
  (corrected_joy_turn m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <CollAvoidService-response>) ostream)
  "Serializes a message object of type '<CollAvoidService-response>"
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'corrected_joy_speed)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'corrected_joy_turn)) ostream)
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <CollAvoidService-response>) istream)
  "Deserializes a message object of type '<CollAvoidService-response>"
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'corrected_joy_speed)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'corrected_joy_turn)) (cl:read-byte istream))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<CollAvoidService-response>)))
  "Returns string type for a service object of type '<CollAvoidService-response>"
  "coalas_pkg/CollAvoidServiceResponse")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'CollAvoidService-response)))
  "Returns string type for a service object of type 'CollAvoidService-response"
  "coalas_pkg/CollAvoidServiceResponse")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<CollAvoidService-response>)))
  "Returns md5sum for a message object of type '<CollAvoidService-response>"
  "7c2dd6e553bf4bcac771ba0b4ab02457")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'CollAvoidService-response)))
  "Returns md5sum for a message object of type 'CollAvoidService-response"
  "7c2dd6e553bf4bcac771ba0b4ab02457")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<CollAvoidService-response>)))
  "Returns full string definition for message of type '<CollAvoidService-response>"
  (cl:format cl:nil "uint8 corrected_joy_speed~%uint8 corrected_joy_turn~%~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'CollAvoidService-response)))
  "Returns full string definition for message of type 'CollAvoidService-response"
  (cl:format cl:nil "uint8 corrected_joy_speed~%uint8 corrected_joy_turn~%~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <CollAvoidService-response>))
  (cl:+ 0
     1
     1
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <CollAvoidService-response>))
  "Converts a ROS message object to a list"
  (cl:list 'CollAvoidService-response
    (cl:cons ':corrected_joy_speed (corrected_joy_speed msg))
    (cl:cons ':corrected_joy_turn (corrected_joy_turn msg))
))
(cl:defmethod roslisp-msg-protocol:service-request-type ((msg (cl:eql 'CollAvoidService)))
  'CollAvoidService-request)
(cl:defmethod roslisp-msg-protocol:service-response-type ((msg (cl:eql 'CollAvoidService)))
  'CollAvoidService-response)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'CollAvoidService)))
  "Returns string type for a service object of type '<CollAvoidService>"
  "coalas_pkg/CollAvoidService")