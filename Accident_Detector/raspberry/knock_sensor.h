#include <string.h>
#include <stdlib.h>

class Knock_sensor {
	private:
		int value;
	public:
		void set_value(char v[]);
		int get_value();
};

void Knock_sensor::set_value(char v[])
{
	value = atoi(v);	
}

int Knock_sensor::get_value()
{
	return value;
}
