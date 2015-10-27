
(cl:in-package :asdf)

(defsystem "coalas_pkg-msg"
  :depends-on (:roslisp-msg-protocol :roslisp-utils :sensor_msgs-msg
               :std_msgs-msg
)
  :components ((:file "_package")
    (:file "EncodersOutput" :depends-on ("_package_EncodersOutput"))
    (:file "_package_EncodersOutput" :depends-on ("_package"))
    (:file "RangeArray" :depends-on ("_package_RangeArray"))
    (:file "_package_RangeArray" :depends-on ("_package"))
    (:file "ChairState" :depends-on ("_package_ChairState"))
    (:file "_package_ChairState" :depends-on ("_package"))
    (:file "SimpleJoystick" :depends-on ("_package_SimpleJoystick"))
    (:file "_package_SimpleJoystick" :depends-on ("_package"))
  ))