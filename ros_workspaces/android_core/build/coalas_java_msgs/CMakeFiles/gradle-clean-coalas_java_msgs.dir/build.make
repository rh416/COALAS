# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 2.8

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list

# Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/user/android_core/src

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/user/android_core/build

# Utility rule file for gradle-clean-coalas_java_msgs.

# Include the progress variables for this target.
include coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/progress.make

coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs:
	$(CMAKE_COMMAND) -E cmake_progress_report /home/user/android_core/build/CMakeFiles $(CMAKE_PROGRESS_1)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold "Cleaning gradle project for coalas_java_msgs"
	cd /home/user/android_core/src/coalas_java_msgs && /home/user/android_core/build/catkin_generated/env_cached.sh /home/user/android_core/src/coalas_java_msgs/gradlew clean

gradle-clean-coalas_java_msgs: coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs
gradle-clean-coalas_java_msgs: coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/build.make
.PHONY : gradle-clean-coalas_java_msgs

# Rule to build all files generated by this target.
coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/build: gradle-clean-coalas_java_msgs
.PHONY : coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/build

coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/clean:
	cd /home/user/android_core/build/coalas_java_msgs && $(CMAKE_COMMAND) -P CMakeFiles/gradle-clean-coalas_java_msgs.dir/cmake_clean.cmake
.PHONY : coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/clean

coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/depend:
	cd /home/user/android_core/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/user/android_core/src /home/user/android_core/src/coalas_java_msgs /home/user/android_core/build /home/user/android_core/build/coalas_java_msgs /home/user/android_core/build/coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : coalas_java_msgs/CMakeFiles/gradle-clean-coalas_java_msgs.dir/depend

