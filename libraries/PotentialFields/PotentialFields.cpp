#include "PotentialFields.h"

PotentialFields::PotentialFields(){
	
}

void PotentialFields::set_field_forwards(int field_value){
	
	_field_forwards = _constrain_field_value(field_value, _safety_field_forwards_min, _safety_field_forwards_max);
}

void PotentialFields::set_field_backwards(int field_value){
	
	_field_backwards = _constrain_field_value(field_value, _safety_field_backwards_min, _safety_field_backwards_max);
}

void PotentialFields::set_field_sideways(int field_value){
	
	_field_sideways = _constrain_field_value(field_value, _safety_field_sideways_min, _safety_field_sideways_max);
}

int PotentialFields::get_field_forwards(){
	
	return _field_forwards;
}

int PotentialFields::get_field_backwards(){
	
	return _field_backwards;
}

int PotentialFields::get_field_sideways(){
	
	return _field_sideways;
}

int PotentialFields::_constrain_field_value(int incoming, int min, int max){
    
    if(incoming < min){
        return min;
    } else if (incoming > max){
        return max;
    } else {
        return incoming;
    }
    
}