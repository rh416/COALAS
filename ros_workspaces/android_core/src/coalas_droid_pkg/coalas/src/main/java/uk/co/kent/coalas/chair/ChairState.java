package uk.co.kent.coalas.chair;


import uk.co.kent.coalas.Coalas;

/**
 * Created by Paul Oprea on 24/05/15.
 */
// structure to maintain and update the chair state
public class ChairState {

    public enum Platforms {
        UDOO,
        ARDUINO,
        ANDROID_APP
    }

    private static ChairState INSTANCE;

    public static synchronized ChairState getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ChairState();
        return INSTANCE;
    }

    /**
     * private CONSTRUCTOR. Use ChairState.getInstance() to retrieve a
     * the class INSTANCE
     */
    private ChairState() {
        platform = Platforms.ANDROID_APP;
        DIRECTIVE = 0x01;
    }

    private Platforms platform;

    private static byte DIRECTIVE;

    public enum Options {
        /**
         * Acknowledgement bit is to be sent ON for request to remote system
         * and echoed back OFF as a confirmation
         */
        ACK {
            @Override
            public void turnOff() { /*don't allow ACK to be turned off*/ }
        },        // LEAST SIGNIFICANT BIT
        /**
         * Used only as a state bit to monitor if the GPSB has been switched ON
         * or OFF. Must only be toggled locally on the Arduino. Will be ignored
         * if toggled remotely.
         */
        GBPS,        // SECOND BIT
        /**
         * Used only as a state bit to monitor if the ROS system in online and
         * available to respond. Must only be toggled locally on the UDOO.
         * Will be ignored if toggled remotely.
         */
        ROS,        // TIRD BIT
        /**
         * @DEBUG bit for running diagnostics !NOTE! - currently only isolates
         * GPSB from system. If turned ON, the chair will ignore any joystick
         * input
         */
        DEBUG,        // FOURTH BIT
        /**
         * Informs Arduino that it should send encoder information to ROS
         */
        ENCODERS,    // FIFT BIT
        /**
         * @REMOTE bit ON informs the Arduino to take joystick input remotely
         * from ROS (e.g. in case of remote driving). Bit will be turned ON
         * automatically if @COLL_AVOID is also turned ON, and cannot be set
         * to OFF while @COLL_AVOID bit is still ON
         */
        REMOTE,    // SIXTH BIT
        /**
         * Collision avoidance bit informs systems that it should correct the
         * joystick input to avoid obstacles. While this bit is ON, @REMOTE bit
         * cannot be set to OFF.
         */
        COLL_AVOID,    // SEVENTH BIT
        /**
         * SYSTEM bit switches the Arduino system ON/OFF i.e.:
         * ON - Engages the GPBS and runs through the main loop
         * OFF - Isolates the GPSB and prevents execution of the loop
         */
        SYSTEM;        // MOST SIGNIFICANT BIT

        public final int mask;

        Options() {
            mask = 1 << ordinal();
        }

        /**
         * checks if a state is on or off
         */
        public boolean isOn() {
            return (DIRECTIVE & mask) > 0;
        }

        /**
         * checks if bit of {@code directive} is on or off
         */
        public boolean isOn(byte directive) {
            return (directive & mask) > 0;
        }

        protected void turnOn() {
            DIRECTIVE |= mask;
        }

        protected void turnOff() { DIRECTIVE &= ~mask; }
    }

    /**
     * set the system in requested state
     * parameters:  - @directive : the remote directive
     * - @caller_platform : the platform from which this directive was called.
     * <p/>
     * !!!IMPORTANT!!! : never use this function to set a directive on the same platform
     * that generated the directive. It will lead to inconsistent states
     * between platforms
     */
    public void setDirective(byte directive, Platforms caller_platform) { // TODO
        if (caller_platform == platform) {
            // This should never happen!
            return;
        }
        // if ACK is set to 0, this is just a confirmation message, so ignore
        if (!Options.ACK.isOn(directive)) {
            return;
        }
        for (Options opt : Options.values()) {
            if ((opt == Options.GBPS) && (caller_platform != Platforms.ARDUINO)) {
                // cannot remotely turn on GPSB
                continue;
            }
            if (opt.isOn(directive))
                opt.turnOn();
            else
                opt.turnOff();
        }
        // send confirmation
        sendConfirmation();
    }

    void sendConfirmation() {
        Coalas.toastHandler.toast("Set to: " + bin());
    //TODO 
		// confirmations have the ACK bit set to 0 
//		char stateStr[5]; // "*ff#"
//		// broadcast to ROS that a change has happened locally and ROS state needs updating
//		snprintf(stateStr, 5, "*%02x#", DIRECTIVE & ~(1 << ACK));
//		Serial.write(stateStr);
	}

    void broadcastState() {
        // broadcast to all other components that a change has happened locally and system needs updating
//		Serial.write(getState());
//  TODO when broadcasting, send back directive with ACK flag to 0.
    }

    /**
     * convenience method to call turnOn or turnOff.
     */
    public void set(Options opt, boolean _ON_OFF, Platforms caller_platform) {
        if (_ON_OFF)
            opt.turnOn();
        else
            opt.turnOff();
//        // set ACK to 1 so ROS knows it needs to update
//        turnOn(Options.ACK);
//        broadcastState();
        Coalas.toastHandler.toast("Set to: " + bin());
    }

    public byte getState() {
        return DIRECTIVE;
    }

    public String bin() {
        return String.format("%8s", Integer.toBinaryString(DIRECTIVE & 0xFF)).replace(' ', '0');
    }

}


