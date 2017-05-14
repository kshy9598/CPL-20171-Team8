const int ledPin = 8;   
const int knockSensor = A0;
const int threshold = 100;  // threshold value to decide when the detected sound is a knock or not

const int xSensor = A2;
const int ySensor = A3;
const int zSensor = A4;

// these variables will change:
int knockReading = 0; 
int xReading = 0;
int yReading = 0;
int zReading = 0;
int ledState = LOW;     

void setup() {
 pinMode(ledPin, OUTPUT); // declare the ledPin as as OUTPUT
 
 Serial.begin(9600);       // use the serial port
}

void loop() {
  // read the sensor and store it in the variable sensorReading:
  knockReading = analogRead(knockSensor);
  xReading = analogRead(xSensor);
  yReading = analogRead(ySensor);
  zReading = analogRead(zSensor);
  
  ledState = !ledState;     
  digitalWrite(ledPin, ledState);
  Serial.println(knockReading);
  Serial.println(xReading);         
  Serial.println(yReading);         
  Serial.println(zReading);         
  
  delay(100);  // delay to avoid overloading the serial port buffer
}
