package uk.co.kent.coalas.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import uk.co.kent.coalas.chair.joystick.GUIJoystick;


/**
 * Created by coalas-kent on 30/03/15.
 */
public class JoystickView extends View implements CompoundButton.OnCheckedChangeListener {

    public static GUIJoystick joyStick;
    private Paint paint;
    private boolean initInside;

    public JoystickView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize(40);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        joyStick = new GUIJoystick(width < height ? width / 3 : height / 3);
        initInside = false;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initInside = joyStick.contains(event.getX(), event.getY());
                if (initInside) {
                    getParent().requestDisallowInterceptTouchEvent(initInside);
                    return initInside;
                }
            default:
                return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        joyStick.toggleStr8(isChecked);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!initInside)
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                joyStick.dragged(event.getX(), event.getY());
//                WorkerThread workerThread =new WorkerThread();
//                workerThread.setup(event);
//                workerThread.start();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                WorkerThread.stop = false;
                initInside = false;
                joyStick.reset();
                break;
            default:
//                WorkerThread.stop = false;
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        joyStick.draw(canvas, paint);
    }

    private static class WorkerThread extends Thread {

        public static volatile boolean stop = false;
        private MotionEvent event;

        public void setup(MotionEvent event) {
            this.event = event;
            stop = true;
        }

        @Override
        public void run() {
            while (!stop) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        joyStick.dragged(event.getX(), event.getY());
                    }
                }).start();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

