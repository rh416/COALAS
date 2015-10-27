; Auto-generated. Do not edit!


(cl:in-package coalas_pkg-msg)


;//! \htmlinclude ChairState.msg.html

(cl:defclass <ChairState> (roslisp-msg-protocol:ros-message)
  ((header
    :reader header
    :initarg :header
    :type std_msgs-msg:Header
    :initform (cl:make-instance 'std_msgs-msg:Header))
   (chairState
    :reader chairState
    :initarg :chairState
    :type cl:fixnum
    :initform 0)
   (isReply
    :reader isReply
    :initarg :isReply
    :type cl:boolean
    :initform cl:nil))
)

(cl:defclass ChairState (<ChairState>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <ChairState>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'ChairState)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-msg:<ChairState> is deprecated: use coalas_pkg-msg:ChairState instead.")))

(cl:ensure-generic-function 'header-val :lambda-list '(m))
(cl:defmethod header-val ((m <ChairState>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:header-val is deprecated.  Use coalas_pkg-msg:header instead.")
  (header m))

(cl:ensure-generic-function 'chairState-val :lambda-list '(m))
(cl:defmethod chairState-val ((m <ChairState>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:chairState-val is deprecated.  Use coalas_pkg-msg:chairState instead.")
  (chairState m))

(cl:ensure-generic-function 'isReply-val :lambda-list '(m))
(cl:defmethod isReply-val ((m <ChairState>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:isReply-val is deprecated.  Use coalas_pkg-msg:isReply instead.")
  (isReply m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <ChairState>) ostream)
  "Serializes a message object of type '<ChairState>"
  (roslisp-msg-protocol:serialize (cl:slot-value msg 'header) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'chairState)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:if (cl:slot-value msg 'isReply) 1 0)) ostream)
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <ChairState>) istream)
  "Deserializes a message object of type '<ChairState>"
  (roslisp-msg-protocol:deserialize (cl:slot-value msg 'header) istream)
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'chairState)) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'isReply) (cl:not (cl:zerop (cl:read-byte istream))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<ChairState>)))
  "Returns string type for a message object of type '<ChairState>"
  "coalas_pkg/ChairState")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'ChairState)))
  "Returns string type for a message object of type 'ChairState"
  "coalas_pkg/ChairState")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<ChairState>)))
  "Returns md5sum for a message object of type '<ChairState>"
  "770dd4f310939c7d3448eaff5bc868c9")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'ChairState)))
  "Returns md5sum for a message object of type 'ChairState"
  "770dd4f310939c7d3448eaff5bc868c9")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<ChairState>)))
  "Returns full string definition for message of type '<ChairState>"
  (cl:format cl:nil "Header header  ~%uint8 chairState~%bool isReply~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'ChairState)))
  "Returns full string definition for message of type 'ChairState"
  (cl:format cl:nil "Header header  ~%uint8 chairState~%bool isReply~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <ChairState>))
  (cl:+ 0
     (roslisp-msg-protocol:serialization-length (cl:slot-value msg 'header))
     1
     1
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <ChairState>))
  "Converts a ROS message object to a list"
  (cl:list 'ChairState
    (cl:cons ':header (header msg))
    (cl:cons ':chairState (chairState msg))
    (cl:cons ':isReply (isReply msg))
))
