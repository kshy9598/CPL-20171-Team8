//include system librarys
#include <cstdio> //for printf
#include <errno.h> //error output

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
const int WAIT = 50000000;

// filedescriptor
int fd;
unsigned long time=0;
 
void setup(){
  printf("%s \n", "Raspberry Startup!");
  fflush(stdout);
  
  //get filedescriptor
  if ((fd = serialOpen (DEVICE, BAUD)) < 0){
    fprintf (stderr, "Unable to open serial device: %s\n", strerror (errno)) ;
    exit(1); //error
  }
 
  //setup GPIO in wiringPi mode
  if (wiringPiSetup () == -1){
    fprintf (stdout, "Unable to start wiringPi: %s\n", strerror (errno)) ;
    exit(1); //error
  }
  
  for(int i = 0; i < WAIT; i ++);
}

int get_number(char newValue[])
{
	int index = 0;
	while(1){
		newValue[index] = serialGetchar (fd);
		if(newValue[index] >= '0' && newValue[index] <= '9'){
			index++;
			break;
		}
	}
    while(1){
		newValue[index] = serialGetchar (fd);
		if(newValue[index] < '0' || newValue[index] > '9'){
			newValue[index] = '\0';
			break;
		}
		index++;
	}
}
 
void loop(Knock_sensor knock_sensor, Accelerometer accelerometer){
  // Pong every 3 seconds
  if(millis()-time>=3000){
    serialPuts (fd, "Pong!\n");
    // you can also write data from 0-255
    // 65 is in ASCII 'A'
    serialPutchar (fd, 'A');
    time=millis();
  }
 
  // read signal
  if(serialDataAvail (fd)){
    char newValue[NUMBER_VALUE] ;
    int index;
    
    index = get_number(newValue);
	if(index > 0){
		knock_sensor.set_value(newValue);
		printf("%d\n", knock_sensor.get_value());
	}
	
	index = get_number(newValue);
	if(index > 0){
		accelerometer.set_x(newValue);
		printf("%d\n", accelerometer.get_x());
	}
	index = get_number(newValue);
	if(index > 0){
		accelerometer.set_y(newValue);
		printf("%d\n", accelerometer.get_y());
	}
	index = get_number(newValue);
	if(index > 0){
		accelerometer.set_z(newValue);
		printf("%d\n", accelerometer.get_z());
	}
 
    fflush(stdout);
  }
}
 
// main function for normal c++ programs on Raspberry
int main(int argc, char *argv[]){
	Knock_sensor knock_sensor;
	Accelerometer accelerometer;
	
	setup();
  
	while(1) {
		loop(knock_sensor, accelerometer);
	}
	
  return 0;
}
