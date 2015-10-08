; Auto-generated. Do not edit!


(cl:in-package coalas_pkg-srv)


;//! \htmlinclude ChairStateService-request.msg.html

(cl:defclass <ChairStateService-request> (roslisp-msg-protocol:ros-message)
  ((isRequest
    :reader isRequest
    :initarg :isRequest
    :type cl:boolean
    :initform cl:nil)
   (state
    :reader state
    :initarg :state
    :type cl:fixnum
    :initform 0))
)

(cl:defclass ChairStateService-request (<ChairStateService-request>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <ChairStateService-request>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'ChairStateService-request)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-srv:<ChairStateService-request> is deprecated: use coalas_pkg-srv:ChairStateService-request instead.")))

(cl:ensure-generic-function 'isRequest-val :lambda-list '(m))
(cl:defmethod isRequest-val ((m <ChairStateService-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:isRequest-val is deprecated.  Use coalas_pkg-srv:isRequest instead.")
  (isRequest m))

(cl:ensure-generic-function 'state-val :lambda-list '(m))
(cl:defmethod state-val ((m <ChairStateService-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:state-val is deprecated.  Use coalas_pkg-srv:state instead.")
  (state m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <ChairStateService-request>) ostream)
  "Serializes a message object of type '<ChairStateService-request>"
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:if (cl:slot-value msg 'isRequest) 1 0)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'state)) ostream)
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <ChairStateService-request>) istream)
  "Deserializes a message object of type '<ChairStateService-request>"
    (cl:setf (cl:slot-value msg 'isRequest) (cl:not (cl:zerop (cl:read-byte istream))))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'state)) (cl:read-byte istream))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<ChairStateService-request>)))
  "Returns string type for a service object of type '<ChairStateService-request>"
  "coalas_pkg/ChairStateServiceRequest")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'ChairStateService-request)))
  "Returns string type for a service object of type 'ChairStateService-request"
  "coalas_pkg/ChairStateServiceRequest")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<ChairStateService-request>)))
  "Returns md5sum for a message object of type '<ChairStateService-request>"
  "42126302442a5f2c473cd1143313390c")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'ChairStateService-request)))
  "Returns md5sum for a message object of type 'ChairStateService-request"
  "42126302442a5f2c473cd1143313390c")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<ChairStateService-request>)))
  "Returns full string definition for message of type '<ChairStateService-request>"
  (cl:format cl:nil "bool isRequest~%uint8 state~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'ChairStateService-request)))
  "Returns full string definition for message of type 'ChairStateService-request"
  (cl:format cl:nil "bool isRequest~%uint8 state~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <ChairStateService-request>))
  (cl:+ 0
     1
     1
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <ChairStateService-request>))
  "Converts a ROS message object to a list"
  (cl:list 'ChairStateService-request
    (cl:cons ':isRequest (isRequest msg))
    (cl:cons ':state (state msg))
))
;//! \htmlinclude ChairStateService-response.msg.html

(cl:defclass <ChairStateService-response> (roslisp-msg-protocol:ros-message)
  ((responseState
    :reader responseState
    :initarg :responseState
    :type cl:fixnum
    :initform 0))
)

(cl:defclass ChairStateService-response (<ChairStateService-response>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <ChairStateService-response>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'ChairStateService-response)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-srv:<ChairStateService-response> is deprecated: use coalas_pkg-srv:ChairStateService-response instead.")))

(cl:ensure-generic-function 'responseState-val :lambda-list '(m))
(cl:defmethod responseState-val ((m <ChairStateService-response>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-srv:responseState-val is deprecated.  Use coalas_pkg-srv:responseState instead.")
  (responseState m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <ChairStateService-response>) ostream)
  "Serializes a message object of type '<ChairStateService-response>"
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'responseState)) ostream)
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <ChairStateService-response>) istream)
  "Deserializes a message object of type '<ChairStateService-response>"
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'responseState)) (cl:read-byte istream))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<ChairStateService-response>)))
  "Returns string type for a service object of type '<ChairStateService-response>"
  "coalas_pkg/ChairStateServiceResponse")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'ChairStateService-response)))
  "Returns string type for a service object of type 'ChairStateService-response"
  "coalas_pkg/ChairStateServiceResponse")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<ChairStateService-response>)))
  "Returns md5sum for a message object of type '<ChairStateService-response>"
  "42126302442a5f2c473cd1143313390c")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'ChairStateService-response)))
  "Returns md5sum for a message object of type 'ChairStateService-response"
  "42126302442a5f2c473cd1143313390c")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<ChairStateService-response>)))
  "Returns full string definition for message of type '<ChairStateService-response>"
  (cl:format cl:nil "uint8 responseState~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'ChairStateService-response)))
  "Returns full string definition for message of type 'ChairStateService-response"
  (cl:format cl:nil "uint8 responseState~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <ChairStateService-response>))
  (cl:+ 0
     1
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <ChairStateService-response>))
  "Converts a ROS message object to a list"
  (cl:list 'ChairStateService-response
    (cl:cons ':responseState (responseState msg))
))
(cl:defmethod roslisp-msg-protocol:service-request-type ((msg (cl:eql 'ChairStateService)))
  'ChairStateService-request)
(cl:defmethod roslisp-msg-protocol:service-response-type ((msg (cl:eql 'ChairStateService)))
  'ChairStateService-response)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'ChairStateService)))
  "Returns string type for a service object of type '<ChairStateService>"
  "coalas_pkg/ChairStateService")