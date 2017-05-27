#include "main.h"
#include "myBluetooth.h"

// main function for normal c++ programs on Raspberry
int main(int argc, char *argv[]){
	Knock_sensor knock_sensor;
	Accelerometer accelerometer;
	
	setup();
    //init_server();
    
	while(1) {
		loop(knock_sensor, accelerometer);
	}
	
  return 0;
}
 
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
  
  setint = 1;
  sleep(3); //다른 하드웨어 준비 대기
  state = 1;
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
    
    if(setint){
		while(setint){
			index = get_number(newValue);
			if(index > 0){
				knock_sensor.set_value(newValue);
				if(knock_sensor.get_value() >= 1000)
					setint = 0;
			}
		}
	}
	else{
		index = get_number(newValue);
		if(index > 0){
			knock_sensor.set_value(newValue);
			printf("%d\n", knock_sensor.get_value());
		}
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
	
	if(state == 1)
		state = 2;
 
    fflush(stdout);
    
    if(state == 2 && isAccident(knock_sensor, accelerometer)){
		state = 0;
		printf("Accident!\n");
		system("raspistill -q 10 -o image.jpg");
	}
  }
}

int isAccident(Knock_sensor knock_sensor, Accelerometer accelerometer)
{
	//if(knock_sensor.isAccident() && accelerometer.isAccident()) 
	if(knock_sensor.isAccident()) 
		return 1;
		
	return 0;
}
