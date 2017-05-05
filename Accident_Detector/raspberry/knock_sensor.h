#include <string.h>
#include <stdlib.h>

class KNOCK_SENSOR {
	private:
		int value;
	public:
		void set_value(char v[]);
		int get_value();
};

void KNOCK_SENSOR::set_value(char v[])
{
	value = atoi(v);	
}

int KNOCK_SENSOR::get_value()
{
	return value;
}
