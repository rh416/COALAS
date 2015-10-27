; Auto-generated. Do not edit!


(cl:in-package coalas_pkg-msg)


;//! \htmlinclude EncodersOutput.msg.html

(cl:defclass <EncodersOutput> (roslisp-msg-protocol:ros-message)
  ((header
    :reader header
    :initarg :header
    :type std_msgs-msg:Header
    :initform (cl:make-instance 'std_msgs-msg:Header))
   (left_encoder_pos
    :reader left_encoder_pos
    :initarg :left_encoder_pos
    :type cl:fixnum
    :initform 0)
   (right_encoder_pos
    :reader right_encoder_pos
    :initarg :right_encoder_pos
    :type cl:fixnum
    :initform 0))
)

(cl:defclass EncodersOutput (<EncodersOutput>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <EncodersOutput>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'EncodersOutput)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name coalas_pkg-msg:<EncodersOutput> is deprecated: use coalas_pkg-msg:EncodersOutput instead.")))

(cl:ensure-generic-function 'header-val :lambda-list '(m))
(cl:defmethod header-val ((m <EncodersOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:header-val is deprecated.  Use coalas_pkg-msg:header instead.")
  (header m))

(cl:ensure-generic-function 'left_encoder_pos-val :lambda-list '(m))
(cl:defmethod left_encoder_pos-val ((m <EncodersOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:left_encoder_pos-val is deprecated.  Use coalas_pkg-msg:left_encoder_pos instead.")
  (left_encoder_pos m))

(cl:ensure-generic-function 'right_encoder_pos-val :lambda-list '(m))
(cl:defmethod right_encoder_pos-val ((m <EncodersOutput>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader coalas_pkg-msg:right_encoder_pos-val is deprecated.  Use coalas_pkg-msg:right_encoder_pos instead.")
  (right_encoder_pos m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <EncodersOutput>) ostream)
  "Serializes a message object of type '<EncodersOutput>"
  (roslisp-msg-protocol:serialize (cl:slot-value msg 'header) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'left_encoder_pos)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 8) (cl:slot-value msg 'left_encoder_pos)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'right_encoder_pos)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 8) (cl:slot-value msg 'right_encoder_pos)) ostream)
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <EncodersOutput>) istream)
  "Deserializes a message object of type '<EncodersOutput>"
  (roslisp-msg-protocol:deserialize (cl:slot-value msg 'header) istream)
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'left_encoder_pos)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 8) (cl:slot-value msg 'left_encoder_pos)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'right_encoder_pos)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 8) (cl:slot-value msg 'right_encoder_pos)) (cl:read-byte istream))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<EncodersOutput>)))
  "Returns string type for a message object of type '<EncodersOutput>"
  "coalas_pkg/EncodersOutput")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'EncodersOutput)))
  "Returns string type for a message object of type 'EncodersOutput"
  "coalas_pkg/EncodersOutput")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<EncodersOutput>)))
  "Returns md5sum for a message object of type '<EncodersOutput>"
  "20a3e3da2d2961e33a8a6c60b0380df1")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'EncodersOutput)))
  "Returns md5sum for a message object of type 'EncodersOutput"
  "20a3e3da2d2961e33a8a6c60b0380df1")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<EncodersOutput>)))
  "Returns full string definition for message of type '<EncodersOutput>"
  (cl:format cl:nil "Header header~%uint16 left_encoder_pos~%uint16 right_encoder_pos~%~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'EncodersOutput)))
  "Returns full string definition for message of type 'EncodersOutput"
  (cl:format cl:nil "Header header~%uint16 left_encoder_pos~%uint16 right_encoder_pos~%~%~%================================================================================~%MSG: std_msgs/Header~%# Standard metadata for higher-level stamped data types.~%# This is generally used to communicate timestamped data ~%# in a particular coordinate frame.~%# ~%# sequence ID: consecutively increasing ID ~%uint32 seq~%#Two-integer timestamp that is expressed as:~%# * stamp.sec: seconds (stamp_secs) since epoch (in Python the variable is called 'secs')~%# * stamp.nsec: nanoseconds since stamp_secs (in Python the variable is called 'nsecs')~%# time-handling sugar is provided by the client library~%time stamp~%#Frame this data is associated with~%# 0: no frame~%# 1: global frame~%string frame_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <EncodersOutput>))
  (cl:+ 0
     (roslisp-msg-protocol:serialization-length (cl:slot-value msg 'header))
     2
     2
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <EncodersOutput>))
  "Converts a ROS message object to a list"
  (cl:list 'EncodersOutput
    (cl:cons ':header (header msg))
    (cl:cons ':left_encoder_pos (left_encoder_pos msg))
    (cl:cons ':right_encoder_pos (right_encoder_pos msg))
))
