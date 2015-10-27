package uk.co.kent.coalas.chair.joystick;

import uk.co.kent.coalas.chair.Chair;
import uk.co.kent.coalas.chair.RemoteMsgs;

/**
 * Created by coalas-kent on 30/03/15.
 */
public class JoyStick {
    private static JoyStick INSTANCE;
    public int speed; // from 1 to 255
    public int turn; // from 1 to 255
    private boolean isStr8;


    public static synchronized JoyStick getInstance() {
        INSTANCE = new JoyStick();
        return INSTANCE;
    }

    private JoyStick() {
        speed = Chair.NEUTRAL;
        turn = Chair.NEUTRAL;
    }

    public void setValues(int speed, int turn) {
        this.speed = checkLims(speed) ? speed : Chair.NEUTRAL;
        this.turn = checkLims(turn) ? turn : Chair.NEUTRAL;
        RemoteMsgs.JOYSTICK.sendData(speed, turn);
    }

    private boolean checkLims(int value) {
        return !(value < Chair.MIN_VAL || value > Chair.MAX_VAL);
    }

    public void setStr8(boolean val) {
        isStr8 = val;
    }

    public String toString() {
        double angle;
        if (speed == Chair.NEUTRAL && turn == Chair.NEUTRAL)
            angle = 0;
        else {
            angle = Math.atan2(speed - Chair.NEUTRAL, turn - Chair.NEUTRAL)
                    - (Math.PI / 2);
            angle += angle < 0 ? 2 * Math.PI : 0;
        }
        String txt = "speed: " + speed + " turn: " + turn;
        if (!isStr8)
            txt += " angle: " + Math.round(Math.toDegrees(angle));
        return txt;
    }

}
