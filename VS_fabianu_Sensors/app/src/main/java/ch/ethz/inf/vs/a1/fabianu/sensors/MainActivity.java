package ch.ethz.inf.vs.a1.fabianu.sensors;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.vs.a1.fabianu.sensors.R;

public class MainActivity extends AppCompatActivity {

    public final static String SENSOR_INDEX = "ch.ethz.inf.vs.a1.fabianu.sensors.SENSOR_AC";

    private ListView listView;
    private SensorManager sManager;
    private List<Sensor> sList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        sManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        sList = sManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sNames = new ArrayList();
        for (int i = 0; i <sList.size(); i++){
            sNames.add(((Sensor)sList.get(i)).getName());
        }

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sNames));
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startSensorAc = new Intent(MainActivity.this, SensorActivity.class);
                startSensorAc.putExtra(MainActivity.SENSOR_INDEX, id);
                MainActivity.this.startActivity(startSensorAc);
            }
        });

    }

    public void onClickTest(View v)	{
        Intent myIntent	= new Intent(this, ActuatorsActivity.class);
        this.startActivity(myIntent);
    }



}
