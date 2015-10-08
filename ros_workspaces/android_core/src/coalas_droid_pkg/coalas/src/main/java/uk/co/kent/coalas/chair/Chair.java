package uk.co.kent.coalas.chair;

/**
 * Created by coalas-kent on 30/03/15.
 */
public class Chair {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // chair specifications v
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // speed and turn values from 1 to 255
    public static final int MIN_VAL = 1;
    public static final int NEUTRAL = 128;
    public static final int MAX_VAL = 255;
    // chair max speed
    public static final float CHAIR_MAX_SPEED = 1666; // mm per second
    // wheel circumference in millimetres
    public static final float WHEEL_CIRCUMFERINCE = 996;
    // wheel diameter in millimetres
    public static final float WHEEL_DIAMETER = (float) (WHEEL_CIRCUMFERINCE / Math.PI);
    // wheel radius in millimetres
    public static final float WHEEL_RADIUS = (float) (WHEEL_CIRCUMFERINCE / (2.f * Math.PI));
    // encoder counts per each 360 revolution the wheel
    private static final double COUNTS_PER_REVOLUTION = 2048;
    // distance the wheel travels with each count of the encoder
    public static final float DISTANCE_PER_COUNT = (float) (Math.PI
            * WHEEL_DIAMETER / COUNTS_PER_REVOLUTION);

    public static final float TRACK_WIDTH = 530; // millimetres
    public static final float CHAIR_RADIUS = TRACK_WIDTH / 2.f; // millimetres
    public static final float DEFAULT_SPEED = 1400; // default speed
    private static final float RADIANS_PER_COUNT = (float) (Math.PI
            * (WHEEL_DIAMETER / TRACK_WIDTH) / COUNTS_PER_REVOLUTION);
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // chair specifications ^
    // /////////////////////////////////////////////////////////////////////////////////////////////

}
