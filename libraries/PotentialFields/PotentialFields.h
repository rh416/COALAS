#ifndef _POTENTIAL_FIELDS
#define _POTENTIAL_FIELDS

#if defined(ARDUINO) && ARDUINO >= 100
  #include "arduino.h"
#else
  #include "WProgram.h"
#endif

#include "IPotentialFields.h"

class PotentialFields : public IPotentialFields{
	
	private:
		int _field_forwards = 0;
		int _field_backwards = 0;
		int _field_sideways = 0;
        
        int _safety_field_forwards_min = 0;
        int _safety_field_forwards_max = 50;
        int _safety_field_backwards_min = 0;
        int _safety_field_backwards_max = 50;
        int _safety_field_sideways_min = 0;
        int _safety_field_sideways_max = 50;
        
        int _constrain_field_value(int, int, int);
	
	public:
		PotentialFields();
		virtual void set_field_forwards(int);
		virtual void set_field_backwards(int);
		virtual void set_field_sideways(int);
		virtual int get_field_forwards();
		virtual int get_field_backwards();
		virtual int get_field_sideways();
};





#endif // _POTENTIAL_FIELDS