
(cl:in-package :asdf)

(defsystem "coalas_pkg-srv"
  :depends-on (:roslisp-msg-protocol :roslisp-utils )
  :components ((:file "_package")
    (:file "ChairStateService" :depends-on ("_package_ChairStateService"))
    (:file "_package_ChairStateService" :depends-on ("_package"))
    (:file "CollAvoidService" :depends-on ("_package_CollAvoidService"))
    (:file "_package_CollAvoidService" :depends-on ("_package"))
  ))