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

        toggleSwitch = (Switch)findViewById(R.id.toggle);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    enableAlarm();
                }
                else
                {
                    disableAlarm();
                }
            }
        });
    }

    private void enableAlarm() {
        antiIntent = new Intent(this, AntiTheftServiceImpl.class);
        startService(antiIntent);
    }

    private void disableAlarm() {
        stopService(antiIntent);
    }
}
