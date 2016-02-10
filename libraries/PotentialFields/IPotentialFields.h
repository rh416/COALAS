#ifndef _I_POTENTIALFIELDS_H
#define _I_POTENTIALFIELDS_H

class IPotentialFields{
	
	public:
		virtual ~IPotentialFields(){};
		virtual void set_field_forwards(int)=0;
		virtual void set_field_backwards(int)=0;
		virtual void set_field_sideways(int)=0;
		virtual int get_field_forwards()=0;
		virtual int get_field_backwards()=0;
		virtual int get_field_sideways()=0;
	
};

#endif /// _I_POTENTIALFIELDS_H