#include <cstdio> 
#include <errno.h> 
#include <unistd.h>

 //wiring Pi
#include <wiringPi.h>
#include <wiringSerial.h>

#include "knock_sensor.h"
#include "accelerometer.h"
 
// Find Serial device on Raspberry with ~ls /dev/tty*
// ARDUINO_UNO "/dev/ttyACM0"
// FTDI_PROGRAMMER "/dev/ttyUSB0"
// HARDWARE_UART "/dev/ttyAMA0"
char DEVICE[]= "/dev/ttyACM0";
const unsigned long BAUD = 9600;
const int NUMBER_VALUE = 6;

// filedescriptor
int fd;
unsigned long time=0;
int setint;
int state;
int client;

void setup();
int get_number(char newValue[]);
void loop(Knock_sensor knock_sensor, Accelerometer accelerometer);
int isAccident(Knock_sensor knock_sensor, Accelerometer accelerometer);
