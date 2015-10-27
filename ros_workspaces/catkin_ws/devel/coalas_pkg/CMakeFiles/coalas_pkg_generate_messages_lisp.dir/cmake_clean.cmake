FILE(REMOVE_RECURSE
  "CMakeFiles/coalas_pkg_generate_messages_lisp"
  "../devel/share/common-lisp/ros/coalas_pkg/msg/EncodersOutput.lisp"
  "../devel/share/common-lisp/ros/coalas_pkg/msg/JoystickOutput.lisp"
  "../devel/share/common-lisp/ros/coalas_pkg/msg/ChairOutput.lisp"
  "../devel/share/common-lisp/ros/coalas_pkg/msg/ChairState.lisp"
  "../devel/share/common-lisp/ros/coalas_pkg/msg/JoystickInput.lisp"
  "../devel/share/common-lisp/ros/coalas_pkg/srv/CollAvoidService.lisp"
  "../devel/share/common-lisp/ros/coalas_pkg/srv/ChairStateService.lisp"
)

# Per-language clean rules from dependency scanning.
FOREACH(lang)
  INCLUDE(CMakeFiles/coalas_pkg_generate_messages_lisp.dir/cmake_clean_${lang}.cmake OPTIONAL)
ENDFOREACH(lang)
