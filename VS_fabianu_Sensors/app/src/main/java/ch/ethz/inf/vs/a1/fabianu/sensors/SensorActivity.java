package ch.ethz.inf.vs.a1.fabianu.sensors;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private long sensorIndex;
    private SensorManager sManager;
    private List<Sensor> sList;
    private Sensor currentSensor;
    private ListView sensorView;
    private ArrayAdapter<String> sensorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        sManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        sList = sManager.getSensorList(Sensor.TYPE_ALL);
        sensorView = (ListView) findViewById(R.id.valueList);
        sensorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        sensorView.setAdapter(sensorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent starter = getIntent();
        Bundle extras = starter.getExtras();
        Object o = extras.get(MainActivity.SENSOR_INDEX);
        sensorIndex = starter.getLongExtra(MainActivity.SENSOR_INDEX, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorIndex != -1)
        {
            currentSensor = sList.get((int)sensorIndex);
            setTitle(currentSensor.getName());
            sManager.registerListener(this, currentSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorAdapter.clear();
        ArrayList<String> values = new ArrayList<>();
        for(int i=0; i<event.values.length;i++)
        {
            values.add(String.valueOf(event.values[i]));
        }
        sensorAdapter.addAll(values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
