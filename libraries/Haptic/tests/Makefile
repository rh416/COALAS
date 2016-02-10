CC=g++
CFLAGS= -c -std=c++11 -I axxtest -I cxxtest-4.4 -I ../
LDFLAGS=
SOURCES=$(wildcard fakes/*.cpp) axxtest/Arduino.cpp
OBJECTS=$(SOURCES:.cpp=.o) Haptic.o


# Build all the Unit Tests ready to be run
all: buildTestsHaptic 

# Build and run all the Unit Tests
test: testHaptic
	
	
# Build the Unit Under Test
unitUnderTest: ../Haptic.cpp ../Haptic.h
	@echo "Compile Unit Under Test"
	@$(CC) $(CFLAGS) ../Haptic.cpp
	

###########################################
#####        Haptic Tests       ##########
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
	
	

.cpp.o:
	@echo "Compile source"
	@$(CC) $(CFLAGS) $< -o $@
	
clean:
	rm */*.o