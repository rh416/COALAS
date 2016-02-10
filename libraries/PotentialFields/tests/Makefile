CC=g++
CFLAGS= -c -std=c++11 -I axxtest -I cxxtest-4.4 -I ../
LDFLAGS=
SOURCES=$(wildcard fakes/*.cpp) axxtest/Arduino.cpp
OBJECTS=$(SOURCES:.cpp=.o) PotentialFields.o


# Build all the Unit Tests ready to be run
all: buildTestsPotentialFields 

# Build and run all the Unit Tests
test: testPotentialFields
	
	
# Build the Unit Under Test
unitUnderTest: ../PotentialFields.cpp ../PotentialFields.h
	@echo "Compile Unit Under Test"
	@$(CC) $(CFLAGS) ../PotentialFields.cpp
	

###########################################
#####    PotentialFields Tests   ##########
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
	
	

.cpp.o:
	@echo "Compile source"
	@$(CC) $(CFLAGS) $< -o $@
	
clean:
	rm */*.o