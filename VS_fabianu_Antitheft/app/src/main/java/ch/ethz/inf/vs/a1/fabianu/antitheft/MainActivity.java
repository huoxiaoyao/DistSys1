package ch.ethz.inf.vs.a1.fabianu.antitheft;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    interface SeekBarInitialiser {
        void functionHolder(SeekBar seekBar, int progress, boolean fromUser);
    }

    private Switch toggleSwitch;
    private SeekBar timeOutSeek;
    private SeekBar sensSeek;
    private Intent antiIntent;

    private int currentTimeout = 0;
    private float currentSensitivity = 0;
    private int timeOutmax = 20000; //in ms
    private float sensitivityMax = 3;
    private float sensStepSize= 0.05f;

    private AntiTheftServiceImpl antiService = null;

    private ServiceConnection mConnection = null;
    private boolean mIsBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        antiIntent = new Intent(this, AntiTheftServiceImpl.class);
        timeOutSeek = (SeekBar)findViewById(R.id.timeoutSeek);
        sensSeek = (SeekBar)findViewById(R.id.sensitivitySeek);
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

        timeOutSeek.setMax(timeOutmax);
        sensSeek.setMax((int)(sensitivityMax / sensStepSize));

        installSeekbarListener(timeOutSeek, new SeekBarInitialiser() {
            @Override
            public void functionHolder(SeekBar seekBar, int progress, boolean fromUser) {
                setTimeout(progress);
            }
        });

        installSeekbarListener(sensSeek, new SeekBarInitialiser() {
            @Override
            public void functionHolder(SeekBar seekBar, int progress, boolean fromUser) {
                setSensitivity((float)progress * sensStepSize);
            }
        });
    }

    private void installSeekbarListener(SeekBar bar, final SeekBarInitialiser init) {
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                init.functionHolder(seekBar, progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        //connect with the service to transfer sensitivity and timeout
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                antiService = ((AntiTheftServiceImpl.LocalBinder) service).getService();
                antiService.setSensitivity(currentSensitivity);
                antiService.setTimeout(currentTimeout);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                antiService = null;
            }
        };
        doBindService();
    }

    private void disableAlarm() {
        stopService(antiIntent);
        mConnection = null;
    }

    private void setTimeout(int timeout) {
        currentTimeout = timeout;
        if(antiService != null)
        {
            antiService.setTimeout(currentTimeout);
        }
    }

    private void setSensitivity(float sensitivity) {
        currentSensitivity = sensitivity;
        if(antiService != null)
        {
            antiService.setSensitivity(currentSensitivity);
        }
    }

    private void doBindService() {
        bindService(new Intent(this, AntiTheftServiceImpl.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if(mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
