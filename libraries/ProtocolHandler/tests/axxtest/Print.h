//
// Created by Richard on 01/12/2015.
//

#ifndef TESTS_PRINT_H
#define TESTS_PRINT_H

// Interface copied from Arduino
// https://github.com/arduino/Arduino/blob/master/hardware/arduino/avr/cores/arduino/Print.h

class Print {

  public:
    void print(char*);
    void print(int);
    void println(char*);
    void println(int);
    

};


#endif //TESTS_PRINT_H
