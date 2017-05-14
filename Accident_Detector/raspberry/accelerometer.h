#include <string.h>
#include <stdlib.h>

class Accelerometer {
	private:
		int x;
		int y;
		int z;
	public:
		void set_x(char v[]);
		void set_y(char v[]);
		void set_z(char v[]);
		int get_x();
		int get_y();
		int get_z();
};

void Accelerometer::set_x(char v[])
{
	x = atoi(v);	
}
void Accelerometer::set_y(char v[])
{
	y = atoi(v);	
}
void Accelerometer::set_z(char v[])
{
	z = atoi(v);	
}

int Accelerometer::get_x()
{
	return x;
}
int Accelerometer::get_y()
{
	return y;
}
int Accelerometer::get_z()
{
	return z;
}
