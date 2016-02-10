#include "AxxTest.h"
#include "ProtocolHandler.h"

class ObstacleTests : public CxxTest::TestSuite{

    public:

        Print* testPrint = NULL;
        Comms_485* test485 = NULL;
        ProtocolHandler* ph;
        char incoming_data[50];
        Haptic* haptic;
        PotentialFields* pf;

        void setUp(){

            testPrint = new Print();
            
            ph = new  ProtocolHandler(testPrint, test485, "Version ID", &logger, haptic, pf);
        }

        void testObstacleReporting1(){
            
            int node = 1;
            int zone = 2;
            int distance = 0;
            
            char expected_respone[15];
           
            snprintf(expected_respone, 15, "O%d:%d%03X\n", node, zone, distance);
            ph->reportObstacle(node, zone, distance);
            TS_ASSERT_EQUALS(expected_respone, testPrint->buffer);
        }

        void testObstacleReporting2(){
            
            int node = 6;
            int zone = 2;
            int distance = 50;
            
            char expected_respone[15];
           
            snprintf(expected_respone, 15, "O%d:%d%03X\n", node, zone, distance);
            ph->reportObstacle(node, zone, distance);
            TS_ASSERT_EQUALS(expected_respone, testPrint->buffer);
        }

        void testObstacleReporting3(){
            
            int node = 3;
            int zone = 3;
            int distance = 120;
            
            char expected_respone[15];
           
            snprintf(expected_respone, 15, "O%d:%d%03X\n", node, zone, distance);
            ph->reportObstacle(node, zone, distance);
            TS_ASSERT_EQUALS(expected_respone, testPrint->buffer);
        }

        void testObstacleReporting4(){
            
            int node = 2;
            int zone = 2;
            int distance = 194;
            
            char expected_respone[15];
           
            snprintf(expected_respone, 15, "O%d:%d%03X\n", node, zone, distance);
            ph->reportObstacle(node, zone, distance);
            TS_ASSERT_EQUALS(expected_respone, testPrint->buffer);
        }

        void testObstacleReporting5(){
            
            int node = 5;
            int zone = 1;
            int distance = 250;
            
            char expected_respone[15];
           
            snprintf(expected_respone, 15, "O%d:%d%03X\n", node, zone, distance);
            ph->reportObstacle(node, zone, distance);
            TS_ASSERT_EQUALS(expected_respone, testPrint->buffer);
        }
        
        void testObstacleClearing(){
            
            char expected_response[15];
            
            snprintf(expected_response, 15, "%s", "O0:C\n");
            ph->clearAllObstacles();
            TS_ASSERT_EQUALS(expected_response, testPrint->buffer);
        }
};