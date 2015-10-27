# generated from genmsg/cmake/pkg-genmsg.cmake.em

message(STATUS "coalas_pkg: 5 messages, 2 services")

set(MSG_I_FLAGS "-Icoalas_pkg:/home/user/catkin_ws/src/coalas_pkg/msg;-Istd_msgs:/opt/ros/indigo/share/std_msgs/cmake/../msg")

# Find all generators
find_package(gencpp REQUIRED)
find_package(genlisp REQUIRED)
find_package(genpy REQUIRED)

add_custom_target(coalas_pkg_generate_messages ALL)

# verify that message/service dependencies have not changed since configure



get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg" NAME_WE)
add_custom_target(_coalas_pkg_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "coalas_pkg" "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg" "std_msgs/Header"
)

get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg" NAME_WE)
add_custom_target(_coalas_pkg_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "coalas_pkg" "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg" "std_msgs/Header"
)

get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg" NAME_WE)
add_custom_target(_coalas_pkg_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "coalas_pkg" "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg" "std_msgs/Header"
)

get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg" NAME_WE)
add_custom_target(_coalas_pkg_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "coalas_pkg" "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg" "std_msgs/Header"
)

get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv" NAME_WE)
add_custom_target(_coalas_pkg_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "coalas_pkg" "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv" ""
)

get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg" NAME_WE)
add_custom_target(_coalas_pkg_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "coalas_pkg" "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg" "std_msgs/Header"
)

get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv" NAME_WE)
add_custom_target(_coalas_pkg_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "coalas_pkg" "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv" ""
)

#
#  langs = gencpp;genlisp;genpy
#

### Section generating for lang: gencpp
### Generating Messages
_generate_msg_cpp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_cpp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_cpp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_cpp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_cpp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
)

### Generating Services
_generate_srv_cpp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
)
_generate_srv_cpp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
)

### Generating Module File
_generate_module_cpp(coalas_pkg
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
  "${ALL_GEN_OUTPUT_FILES_cpp}"
)

add_custom_target(coalas_pkg_generate_messages_cpp
  DEPENDS ${ALL_GEN_OUTPUT_FILES_cpp}
)
add_dependencies(coalas_pkg_generate_messages coalas_pkg_generate_messages_cpp)

# add dependencies to all check dependencies targets
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_cpp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_cpp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_cpp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_cpp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_cpp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_cpp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_cpp _coalas_pkg_generate_messages_check_deps_${_filename})

# target for backward compatibility
add_custom_target(coalas_pkg_gencpp)
add_dependencies(coalas_pkg_gencpp coalas_pkg_generate_messages_cpp)

# register target for catkin_package(EXPORTED_TARGETS)
list(APPEND ${PROJECT_NAME}_EXPORTED_TARGETS coalas_pkg_generate_messages_cpp)

### Section generating for lang: genlisp
### Generating Messages
_generate_msg_lisp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_lisp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_lisp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_lisp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
)
_generate_msg_lisp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
)

### Generating Services
_generate_srv_lisp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
)
_generate_srv_lisp(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
)

### Generating Module File
_generate_module_lisp(coalas_pkg
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
  "${ALL_GEN_OUTPUT_FILES_lisp}"
)

add_custom_target(coalas_pkg_generate_messages_lisp
  DEPENDS ${ALL_GEN_OUTPUT_FILES_lisp}
)
add_dependencies(coalas_pkg_generate_messages coalas_pkg_generate_messages_lisp)

# add dependencies to all check dependencies targets
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_lisp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_lisp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_lisp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_lisp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_lisp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_lisp _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_lisp _coalas_pkg_generate_messages_check_deps_${_filename})

# target for backward compatibility
add_custom_target(coalas_pkg_genlisp)
add_dependencies(coalas_pkg_genlisp coalas_pkg_generate_messages_lisp)

# register target for catkin_package(EXPORTED_TARGETS)
list(APPEND ${PROJECT_NAME}_EXPORTED_TARGETS coalas_pkg_generate_messages_lisp)

### Section generating for lang: genpy
### Generating Messages
_generate_msg_py(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
)
_generate_msg_py(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
)
_generate_msg_py(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
)
_generate_msg_py(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
)
_generate_msg_py(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg"
  "${MSG_I_FLAGS}"
  "/opt/ros/indigo/share/std_msgs/cmake/../msg/Header.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
)

### Generating Services
_generate_srv_py(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
)
_generate_srv_py(coalas_pkg
  "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
)

### Generating Module File
_generate_module_py(coalas_pkg
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
  "${ALL_GEN_OUTPUT_FILES_py}"
)

add_custom_target(coalas_pkg_generate_messages_py
  DEPENDS ${ALL_GEN_OUTPUT_FILES_py}
)
add_dependencies(coalas_pkg_generate_messages coalas_pkg_generate_messages_py)

# add dependencies to all check dependencies targets
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/EncodersOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_py _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_py _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/ChairState.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_py _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickOutput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_py _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/ChairStateService.srv" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_py _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/msg/JoystickInput.msg" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_py _coalas_pkg_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/user/catkin_ws/src/coalas_pkg/srv/CollAvoidService.srv" NAME_WE)
add_dependencies(coalas_pkg_generate_messages_py _coalas_pkg_generate_messages_check_deps_${_filename})

# target for backward compatibility
add_custom_target(coalas_pkg_genpy)
add_dependencies(coalas_pkg_genpy coalas_pkg_generate_messages_py)

# register target for catkin_package(EXPORTED_TARGETS)
list(APPEND ${PROJECT_NAME}_EXPORTED_TARGETS coalas_pkg_generate_messages_py)



if(gencpp_INSTALL_DIR AND EXISTS ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg)
  # install generated code
  install(
    DIRECTORY ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/coalas_pkg
    DESTINATION ${gencpp_INSTALL_DIR}
  )
endif()
add_dependencies(coalas_pkg_generate_messages_cpp std_msgs_generate_messages_cpp)

if(genlisp_INSTALL_DIR AND EXISTS ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg)
  # install generated code
  install(
    DIRECTORY ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/coalas_pkg
    DESTINATION ${genlisp_INSTALL_DIR}
  )
endif()
add_dependencies(coalas_pkg_generate_messages_lisp std_msgs_generate_messages_lisp)

if(genpy_INSTALL_DIR AND EXISTS ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg)
  install(CODE "execute_process(COMMAND \"/usr/bin/python\" -m compileall \"${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg\")")
  # install generated code
  install(
    DIRECTORY ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/coalas_pkg
    DESTINATION ${genpy_INSTALL_DIR}
  )
endif()
add_dependencies(coalas_pkg_generate_messages_py std_msgs_generate_messages_py)
