#include "AxxTest.h"
#include "PotentialFields.h"

class PotentialFieldTests : public CxxTest::TestSuite{

  public:
	
	PotentialFields* pf;
  
	void setUp(){
		
		// Create an instance of PotentialFields
		pf = new PotentialFields();
	}
	
	void testInitialState(){
		
		TS_ASSERT_EQUALS(0, pf->get_field_forwards());
		TS_ASSERT_EQUALS(0, pf->get_field_backwards());
		TS_ASSERT_EQUALS(0, pf->get_field_sideways());
	}
  
	void testSetAndGetForwards(){
		
		int field_value = 34;
		
		pf->set_field_forwards(field_value);
		
		TS_ASSERT_EQUALS(field_value, pf->get_field_forwards());
	}
  
	void testSetAndGetBackwards(){
		
		int field_value = 29;
		
		pf->set_field_backwards(field_value);
		
		TS_ASSERT_EQUALS(field_value, pf->get_field_backwards());
	}
  
	void testSetAndGetSideways(){
		
		int field_value = 17;
		
		pf->set_field_sideways(field_value);
		
		TS_ASSERT_EQUALS(field_value, pf->get_field_sideways());
	}
    
    void testForwardsLimitsLow(){
        
        int low_value = -5;
        int expected_value = 0;
        
        pf->set_field_forwards(low_value);
        
        TS_ASSERT_EQUALS(expected_value, pf->get_field_forwards());
    }
    
    void testForwardsLimitsHigh(){
        
        int high_value = 60;
        int expected_value = 50;
        
        pf->set_field_forwards(high_value);
        
        TS_ASSERT_EQUALS(expected_value, pf->get_field_forwards());
    }
    
    void testBackwardsLimitsLow(){
        
        int low_value = -15;
        int expected_value = 0;
        
        pf->set_field_backwards(low_value);
        
        TS_ASSERT_EQUALS(expected_value, pf->get_field_backwards());
    }
    
    void testBackwardsLimitsHigh(){
        
        int high_value = 70;
        int expected_value = 50;
        
        pf->set_field_backwards(high_value);
        
        TS_ASSERT_EQUALS(expected_value, pf->get_field_backwards());
    }
    
    void testSidewaysLimitsLow(){
        
        int low_value = -5;
        int expected_value = 0;
        
        pf->set_field_sideways(low_value);
        
        TS_ASSERT_EQUALS(expected_value, pf->get_field_sideways());
    }
    
    void testSidewaysLimitsHigh(){
        
        int high_value = 70;
        int expected_value = 50;
        
        pf->set_field_sideways(high_value);
        
        TS_ASSERT_EQUALS(expected_value, pf->get_field_sideways());
    }
  
};