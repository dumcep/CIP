package com.example.curseinpanta.input;

import android.util.Log;
import android.view.MotionEvent;
import com.example.curseinpanta.entities.Car;

public class InputController {
    private static final String TAG = "InputController";

    private final Car car;
    private int gasPointerId   = -1;
    private int brakePointerId = -1;

    public InputController(Car car) {
        this.car = car;
    }

    /**
     * Call this from your View's onTouchEvent.
     * @param event       the MotionEvent
     * @param screenWidth width of the view, to split left/right
     * @return always true (we’re handling input)
     */
    public boolean onTouchEvent(MotionEvent event, int screenWidth) {
        int action       = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pid          = event.getPointerId(pointerIndex);
        float x          = event.getX(pointerIndex);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (x < screenWidth / 2f) {
                    car.setBraking(true);
                    brakePointerId = pid;
                    Log.d(TAG, "Brake pressed (ID=" + pid + ")");
                } else {
                    car.setAccelerating(true);
                    gasPointerId = pid;
                    Log.d(TAG, "Gas pressed (ID=" + pid + ")");
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (pid == brakePointerId) {
                    car.setBraking(false);
                    brakePointerId = -1;
                    Log.d(TAG, "Brake released (ID=" + pid + ")");
                } else if (pid == gasPointerId) {
                    car.setAccelerating(false);
                    gasPointerId = -1;
                    Log.d(TAG, "Gas released (ID=" + pid + ")");
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                car.setBraking(false);
                car.setAccelerating(false);
                brakePointerId = gasPointerId = -1;
                Log.d(TAG, "Touch cancelled");
                break;
        }
        return true;
    }
}
