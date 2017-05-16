#include <string.h>
#include <stdlib.h>
#include <cmath>

const int ACCIDENT_XYZ = 50;

class Accelerometer {
	private:
		int x;
		int y;
		int z;
		int lastX;
		int lastY;
		int lastZ;
	public:
		void set_x(char v[]);
		void set_y(char v[]);
		void set_z(char v[]);
		int get_x();
		int get_y();
		int get_z();
		
		int isAccident();
};

void Accelerometer::set_x(char v[])
{
	lastX = x;
	x = atoi(v);	
}
void Accelerometer::set_y(char v[])
{
	lastY = y;
	y = atoi(v);	
}
void Accelerometer::set_z(char v[])
{
	lastZ = z;
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

int Accelerometer::isAccident()
{
	if(abs(z - lastZ) > ACCIDENT_XYZ || abs(y - lastY) > ACCIDENT_XYZ || abs(x - lastX) > ACCIDENT_XYZ)
		return 1;
	return 0;
}
