#include <string.h>
#include <stdlib.h>

const int ACCIDENT_VALUE = 400;

class Knock_sensor {
	private:
		int value;
	public:
		void set_value(char v[]);
		int get_value();
		int isAccident();
};

void Knock_sensor::set_value(char v[])
{
	value = atoi(v);	
}

int Knock_sensor::get_value()
{
	return value;
}

int Knock_sensor::isAccident()
{
	if(value < ACCIDENT_VALUE)
		return 1;
	else
		return 0;
}
