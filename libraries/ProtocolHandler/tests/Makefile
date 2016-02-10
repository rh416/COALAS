CC=g++
CFLAGS= -c -std=c++11 -I ../ -I axxtest -I cxxtest-4.4 -I ../../SYSIASS_485_Comms -I ../../SYSIASS_Sensor -I ../../Haptic -I ../../Logging -I ../../Timing -I ../../PotentialFields
LDFLAGS=
SOURCES=$(wildcard fakes/*.cpp) ../../PotentialFields/PotentialFields.cpp axxtest/Arduino.cpp axxtest/Print.cpp
OBJECTS=$(SOURCES:.cpp=.o) ProtocolHandler.o


# Build all the Unit Tests ready to be run
all: buildTestsLogging buildTestsTimeSetting buildTestsHaptic buildTestsPotentialFields buildTestsObstacles

# Build and run all the Unit Tests
test: testLogging testTimeSetting testHaptic testPotentialFields testObstacles
	
	
# Build the Unit Under Test
unitUnderTest: ../ProtocolHandler.cpp ../ProtocolHandler.h
	@echo "Compile Unit Under Test"
	@$(CC) $(CFLAGS) ../ProtocolHandler.cpp
	

###########################################
#####        Logging Tests       ##########
###########################################

testLogging: buildTestsLogging
	@runners/LoggingTests.exe || true
	

buildTestsLogging: unitUnderTest updateTestsLogging $(OBJECTS)  
	@echo "Build LoggingTests"
	@$(CC) $(LDFLAGS) $(OBJECTS) LoggingTests.o -o runners/LoggingTests.exe
	
	
updateTestsLogging: LoggingTests.h
	@echo "Generate LoggingTests class"
	@test -d runners || mkdir runners
	@cxxtest-4.4/bin/cxxtestgen --error-printer -w "LoggingTests" -o runners/LoggingTests.cpp LoggingTests.h
	@$(CC) $(CFLAGS) runners/LoggingTests.cpp
	
	
###########################################
#####    Timing Setting Tests    ##########
###########################################

testTimeSetting: buildTestsTimeSetting
	@runners/TimeSettingTests.exe || true
	
buildTestsTimeSetting: unitUnderTest updateTestsTimeSetting $(OBJECTS)
	@echo "Build TimeSettingTests"
	@$(CC) $(LDFLAGS) $(OBJECTS) TimeSettingTests.o -o runners/TimeSettingTests.exe
	
updateTestsTimeSetting: TimeSettingTests.h
	@echo "Generate TimeSettingTests class"
	@test -d runners || mkdir runners
	@cxxtest-4.4/bin/cxxtestgen --error-printer -w "TimeSettingTests" -o runners/TimeSettingTests.cpp TimeSettingTests.h
	@$(CC) $(CFLAGS) runners/TimeSettingTests.cpp
	
	
###########################################
#####        Haptic Tests        ##########
###########################################

testHaptic: buildTestsHaptic
	@runners/HapticTests.exe || true
	
buildTestsHaptic: unitUnderTest updateTestsHaptic $(OBJECTS)
	@echo "Build HapticTests"
	@$(CC) $(LDFLAGS) $(OBJECTS) HapticTests.o -o runners/HapticTests.exe
	
updateTestsHaptic: HapticTests.h
	@echo "Generate HapticTests class"
	@test -d runners || mkdir runners
	@cxxtest-4.4/bin/cxxtestgen --error-printer -w "HapticTests" -o runners/HapticTests.cpp HapticTests.h
	@$(CC) $(CFLAGS) runners/HapticTests.cpp
	
	
###########################################
#####    Potential Field Tests   ##########
###########################################

testPotentialFields: buildTestsPotentialFields
	@runners/PotentialFieldsTests.exe || true
	
buildTestsPotentialFields: unitUnderTest updateTestsPotentialFields $(OBJECTS)
	@echo "Build PotentialFieldsTests"
	@$(CC) $(LDFLAGS) $(OBJECTS) PotentialFieldsTests.o -o runners/PotentialFieldsTests.exe
	
updateTestsPotentialFields: PotentialFieldsTests.h
	@echo "Generate PotentialFieldsTests class"
	@test -d runners || mkdir runners
	@cxxtest-4.4/bin/cxxtestgen --error-printer -w "PotentialFieldsTests" -o runners/PotentialFieldsTests.cpp PotentialFieldsTests.h
	@$(CC) $(CFLAGS) runners/PotentialFieldsTests.cpp
	
	
###########################################
#####       Obstacles Tests      ##########
###########################################

testObstacles: buildTestsObstacles
	@runners/ObstaclesTests.exe || true
	
buildTestsObstacles: unitUnderTest updateTestsObstacles $(OBJECTS)
	@echo "Build ObstaclesTests"
	@$(CC) $(LDFLAGS) $(OBJECTS) ObstaclesTests.o -o runners/ObstaclesTests.exe
	
updateTestsObstacles: ObstaclesTests.h
	@echo "Generate ObstaclesTests class"
	@test -d runners || mkdir runners
	@cxxtest-4.4/bin/cxxtestgen --error-printer -w "ObstaclesTests" -o runners/ObstaclesTests.cpp ObstaclesTests.h
	@$(CC) $(CFLAGS) runners/ObstaclesTests.cpp

.cpp.o:
	@echo "Compile source"
	@$(CC) $(CFLAGS) $< -o $@
	
clean:
	rm */*.o