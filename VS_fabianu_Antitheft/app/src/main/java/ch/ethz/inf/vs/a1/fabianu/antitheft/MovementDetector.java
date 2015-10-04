package ch.ethz.inf.vs.a1.fabianu.antitheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class MovementDetector extends AbstractMovementDetector {
    //needed for sManager
    private Context context;
    private SensorManager sManager;
    private Sensor accel;

    public MovementDetector() {
        context = null;
    }

    public void setContext(Context c) {
        context = c;
        sManager = (SensorManager)c.getSystemService(c.SENSOR_SERVICE);
        accel = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected boolean doAlarmLogic(float[] values) {
        if(context == null)
        {
            return false;
        }
        else
        {
            //TODO: real alarm logic goes in here
            return false;
        }
    }

    public void close() {
        sManager.unregisterListener(this);
    }
}
