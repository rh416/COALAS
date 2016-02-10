#include "Haptic.h"

class HapticFake : public IHaptic{
	
	private:
		HapticPattern _pattern = OFF;
		int _on_duration = 0;
		int _off_duration = 0;
		int _intensity = 0;
	
	public:
		HapticFake(int);
		virtual void set_vibration_pattern(HapticPattern);
		virtual void set_vibration_pattern(int, int);
		virtual void set_intensity(int);
		virtual void update_vibration();
		int get_on_duration();
		int get_off_duration();
		int get_intensity();
	
};