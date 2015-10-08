package ch.ethz.inf.vs.a1.fabianu.antitheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class MovementDetector extends AbstractMovementDetector {
    private class SensorData {
        long timeStamp = 0;
        float[] values;
    }

    private final long timeToLog = 5000;
    private float threshold = 1;
    //needed for sManager
    private Context context;
    private SensorManager sManager;
    private Sensor accel;
    private LinkedBlockingQueue<SensorData> valueQueue;
    private long startMillis;
    //private boolean doneOnce = false;

    public MovementDetector() {
        context = null;
        valueQueue = new LinkedBlockingQueue();
        startMillis = System.currentTimeMillis();
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
            long currentTime = System.currentTimeMillis();
            SensorData current = new SensorData();
            current.timeStamp = currentTime;
            current.values = new float[values.length];
            System.arraycopy(values, 0, current.values, 0, values.length);
            try {
                valueQueue.put(current);
            } catch (InterruptedException e) {
                return false;
            }

            if(currentTime - startMillis < timeToLog)
            {
                //queue not yet fully initialised, always return false
                return false;
            }
            else
            {
                shorten(valueQueue, currentTime - timeToLog);

                //average distance between each value and it's predecessor
                float[] avgDist = new float[values.length];
                float[] pred = valueQueue.peek().values;
                Iterator<SensorData> it = valueQueue.iterator();
                while(it.hasNext())
                {
                    float[] elem = it.next().values;
                    for(int i=0; i<avgDist.length; i++)
                    {
                        avgDist[i] += Math.abs(elem[i] - pred[i]);
                    }
                    pred = elem;
                }
                //real logic
                float avgTotal = 0;
                for(int i=0; i<avgDist.length; i++)
                {
                    avgTotal += avgDist[i];
                }
                avgTotal /= avgDist.length * valueQueue.size();
                return avgTotal > threshold;
            }


            /*//TODO: real alarm logic goes in here
            //this is just testlogic
            if(!doneOnce && System.currentTimeMillis() - startMillis > 5000) {
                doneOnce = true;
                return true;
            }
            else {
                return false;
            }*/
        }
    }

    public void close() {
        sManager.unregisterListener(this);
    }

    private void shorten(LinkedBlockingQueue<SensorData> q, long startingTime)
    {
        Iterator<SensorData> it = q.iterator();
        while(it.hasNext() && it.next().timeStamp < startingTime)
        {
            it.remove();
        }
    }

    public void setThreshold(float t) {
        threshold = t;
    }
}
