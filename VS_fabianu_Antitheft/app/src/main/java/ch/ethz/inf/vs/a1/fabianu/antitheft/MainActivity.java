package ch.ethz.inf.vs.a1.fabianu.antitheft;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private Switch toggleSwitch;
    private Intent antiIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        antiIntent = new Intent(this, AntiTheftServiceImpl.class);
        toggleSwitch = (Switch)findViewById(R.id.toggle);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableAlarm();
                } else {
                    disableAlarm();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        //disable the alarm if called from the correct source
        Intent callingIntent = getIntent();
        if(callingIntent.getBooleanExtra(AntiTheftServiceImpl.STOP_ALARM, false))
        {
            if(toggleSwitch.isChecked()) {
                toggleSwitch.setChecked(false);
            }
            else {
                disableAlarm();
            }
        }
        super.onStart();
    }

    private void enableAlarm() {
        startService(antiIntent);
    }

    private void disableAlarm() {
        stopService(antiIntent);
    }
}
