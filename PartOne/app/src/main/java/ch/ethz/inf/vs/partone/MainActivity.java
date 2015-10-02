package ch.ethz.inf.vs.partone;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        List<Sensor> sList = sManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sNames = new ArrayList();
        for (int i = 0; i <sList.size(); i++){
            sNames.add(((Sensor)sList.get(i)).getName());
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sNames));

        getListView().setTextFilterEnabled(true);

    }

    public void onClickTest(View v)	{

        Intent myIntent	= new Intent(this, ActuatorsActivity.class);
        this.startActivity(myIntent);
    }



}
