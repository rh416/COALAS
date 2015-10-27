package uk.co.kent.coalas.chair.joystick;

/**
 * Created by coalas-kent on 03/04/15.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import uk.co.kent.coalas.chair.Chair;


public class GUIJoystick extends Circle2D {

    /**
     *
     */
    private static int STICK_SIZE;
    private static JoyStick joystick;
    private static JoyStickPosition joyStickPos;

    private double min, max, center, radius;
    private String text;
    private Rect bounds;
    private boolean drawStraight;

    private class JoyStickPosition extends Point {
        /**
         *
         */

        Point upLeftPoint;
        Point upRightPoint;
        private JoyStickPosition() {
            setLocation(center, center);
            upLeftPoint = new Point(0, 0);
            upRightPoint = new Point((int) max, 0);
        }

        public void setLocation(double x, double y) {
            if (drawStraight) {
                Point currPoint = new Point((int) x, (int) y);
                boolean above1stD = isAboveDiag(this, upRightPoint, currPoint);
                boolean above2ndD = isAboveDiag(this, upLeftPoint, currPoint);
                if (above1stD && above2ndD || !above1stD && !above2ndD)
                    joystick.setValues(remapValue(center), remapValue(x));
                else
                    joystick.setValues(remapValue(max - y), remapValue(center));
            } else {
                set((int) x, (int) y);
                joystick.setValues(remapValue(max - y), remapValue(x));
            }
        }


        private int remapValue(double d) {
            // low2 + (value - low1) * (high2 - low2) / (high1 - low1)
            return (int) Math.round((Chair.MIN_VAL + (d - min)
                    * (Chair.MAX_VAL - Chair.MIN_VAL) / (max - min)));
        }

        private boolean isAboveDiag(Point a, Point b, Point c) {
            return ((b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)) > 0;
        }

    }


    public void dragged(double x, double y) {
        if (contains(x, y)) {
            joyStickPos.setLocation(x, y);
        } else {
            double X = x - center, Y = y - center;
            float distance = (float) Math.sqrt(X * X + Y * Y);
            joyStickPos.setLocation(center + radius * X / distance, center
                    + radius * Y / distance);
        }
    }

    public void reset() {
        joyStickPos.setLocation(center, center);
    }

    public GUIJoystick(int radius) {
        super(radius, radius, radius);
        this.radius = radius;
        STICK_SIZE = radius / 5;
        min = 0;
        max = 2 * radius;
        // set the frame for the circle
        setFrame(min, min, max, max);
        drawStraight = false;
        center = getCenterX();
        joystick = JoyStick.getInstance();
        joyStickPos = new JoyStickPosition();
        joyStickPos.setLocation(center, center);
        bounds = new Rect();
    }

    public void toggleStr8(boolean isChecked) {
        drawStraight = isChecked;
        joystick.setStr8(isChecked);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.GRAY);
        canvas.drawCircle((float) centerX, (float) centerY, (float) radius, paint);
        paint.setColor(Color.RED);
        if (drawStraight) {
            canvas.rotate(45, (float) centerX, (float) centerY);
        }
        canvas.drawLine((float) min, (float) center, (float) max, (float) center, paint);
        canvas.drawLine((float) center, (float) min, (float) center, (float) max, paint);
        if (drawStraight) {
            canvas.rotate(-45, (float) centerX, (float) centerY);
        } else {

            paint.setColor(Color.BLUE);
            canvas.drawCircle((float) joyStickPos.x, (float) joyStickPos.y,
                    STICK_SIZE, paint);
        }
        paint.setColor(Color.WHITE);
        text = joystick.toString();// + " " + JoystickView.WorkerThread.stop;
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, 0, (float) max + STICK_SIZE + (bounds.height() * 2), paint);
    }

}