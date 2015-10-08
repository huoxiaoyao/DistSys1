package ch.ethz.inf.vs.a1.fabianu.antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {
    public static final int REQUEST_START = 5486327;
    public static final String STOP_ALARM = "ch.ethz.inf.vs.a1.fabianu.antitheft.stop_the_alarm";
    private NotificationManager notMan;
    private NotificationCompat.Builder notBuild = null;
    private CountDownTimer alarmTimer = null;
    private Ringtone ring;
    private AudioManager aManager;
    private int formerVolume = 0;
    private boolean alarmStarted = false;
    private boolean countdownStarted = false;
    private int dischargeTime = 0;
    private float sensitivity;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        AntiTheftServiceImpl getService() {
            return AntiTheftServiceImpl.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MovementDetector)listener).setContext(this);
        //initialise ring
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null) {
            // if alarm was never set, use notification sound as backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ring = RingtoneManager.getRingtone(getApplicationContext(), alert);
        aManager = (AudioManager)getSystemService(AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //here initialise a new notificationbuilder
        notBuild = new NotificationCompat.Builder(this);
        notBuild.setContentTitle(this.getString(R.string.notificationTitle));
        notBuild.setContentText(this.getString(R.string.notificationText));
        //TODO: put a proper icon into drawable and set the icon correctly here
        notBuild.setSmallIcon(R.mipmap.ic_launcher);

        //normally start MainActivity fresh - because it's fresh
        Intent start = new Intent(this, MainActivity.class);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent startMain = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
                start, PendingIntent.FLAG_UPDATE_CURRENT);
        notBuild.setContentIntent(startMain);
        notMan = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        Notification theOne = notBuild.build();
        theOne.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notMan.notify(REQUEST_START, theOne);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void startAlarm() {
        if(! (alarmStarted || countdownStarted)) {
            countdownStarted = true;
            //first modify notification and leave the user some time to disable it
            Intent disarmIntent = new Intent(this, MainActivity.class);
            disarmIntent.putExtra(STOP_ALARM, true);
            disarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent disarm = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
                    disarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notBuild.setContentTitle(this.getString(R.string.armedNotificationTitle));
            notBuild.setContentText(this.getString(R.string.armedNotificationText));
            //clear normal click action
            notBuild.setContentIntent(disarm);
            Notification theOne = notBuild.build();
            theOne.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
            notMan.notify(REQUEST_START, theOne);

            //time until alarm rings
            //TODO: let user set this one
            alarmTimer = new CountDownTimer(dischargeTime, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //TODO: maybe progressbar in notification?
                }

                @Override
                public void onFinish() {
                    // timer wasn't interrupted, hence play tone
                    alarmStarted = true;
                    countdownStarted = false;
                    ringAlarm();
                }
            };
            alarmTimer.start();
        }
    }

    private void ringAlarm() {
        formerVolume = aManager.getStreamVolume(AudioManager.STREAM_RING);
        aManager.setStreamVolume(AudioManager.STREAM_RING,
                aManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                aManager.FLAG_ALLOW_RINGER_MODES|aManager.FLAG_PLAY_SOUND);
        ring.play();
    }

    private void stopAlarm() {
        if(ring.isPlaying()) {
            ring.stop();
            //restore proper volume
            aManager.setStreamVolume(AudioManager.STREAM_RING,
                    formerVolume,
                    aManager.FLAG_ALLOW_RINGER_MODES|aManager.FLAG_PLAY_SOUND);
        }
        if(alarmTimer != null) {
            alarmTimer.cancel();
        }
        alarmStarted = false;
    }

    public void setTimeout(int t) {
        dischargeTime = t;
    }

    public void setSensitivity(float s) {
        sensitivity = s;
        if(listener != null)
        {
            ((MovementDetector)listener).setThreshold(sensitivity);
        }
    }

    @Override
    public void onDestroy() {
        notMan.cancel(REQUEST_START);
        ((MovementDetector)listener).close();
        stopAlarm();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
