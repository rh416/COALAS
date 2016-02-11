#ifndef _IHAPTIC_H
#define _IHAPTIC_H

enum HapticPattern {OFF, SUBTLE, WARNING, INSISTANT, CONTINUOUS};

class IHaptic{
	
	public:
		virtual ~IHaptic(){};
		virtual void set_vibration_pattern(HapticPattern)=0;
		virtual void set_vibration_pattern(int, int)=0;
		virtual void set_intensity(int)=0;
		virtual void update_vibration()=0;
	
};

#endif