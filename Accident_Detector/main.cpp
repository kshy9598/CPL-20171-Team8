#include <iostream>
#include <wiringPi.h>

using namespace std;

int main()
{
	if(wiringPiSetup() == -1)
        return 1;
	
	for(int i = 0; i < 16; i++){
		pinMode(i, INPUT);
	}

	while(1){
		cout << digitalRead(0) << " " << digitalRead(2) << endl;
	}
	

    return 0;
}
