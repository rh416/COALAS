//
// Created by Richard on 01/12/2015.
//

#ifndef TESTS_ARDUINO_H
#define TESTS_ARDUINO_H

#include <iostream>
#include <stdint.h>
#include <stdlib.h>
//#include <string.h>
#include <math.h>

typedef unsigned char byte;
typedef unsigned char boolean;

uint32_t millis(void);

#define PSTR(str)(str)

#define _PRINT_SAFE(str)(std::cout << str << std::endl)
#define _PRINT_SAFE_LEN(str, len)(std::cout << str << ":" << len << std::endl)
//#define _PRINT_SAFE(str)
//#define _PRINT_SAFE_LEN(str, len)

#include "Print.h"
#include "File.h"
#include "Serial.h"
#include "WString.h"

#endif //TESTS_ARDUINO_H
