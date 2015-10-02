package ch.ethz.inf.vs.a1.fabianu.sensors;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import ch.ethz.inf.vs.a1.fabianu.sensors.R;

public class ActuatorsActivity extends AppCompatActivity {

    private Vibrator	vib =	null;
    private int duration =	50;
    private int vibTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuators);

        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        SeekBar seekDuration = (SeekBar) findViewById(R.id.seek_duration);
        seekDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                duration = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vib.vibrate(duration * 10);
                vibTime = duration*10;
            }


        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actuators, menu);
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

    public void onClickVibrate(View	v)	{
        Vibrator	vib	=	(Vibrator)	getSystemService(VIBRATOR_SERVICE);
        long[]	pattern	=	{	0,	100,	100,	200,	100,	100	};
        vib.vibrate(vibTime);
    }

    public void onClickSound(View	v)	{
        MediaPlayer mp	=	MediaPlayer.create(this, R.raw.sound);
        mp.setVolume(1.0f,	1.0f);
        mp.start();
    }


}
