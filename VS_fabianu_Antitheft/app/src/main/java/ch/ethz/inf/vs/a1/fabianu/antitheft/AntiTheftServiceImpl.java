package ch.ethz.inf.vs.a1.fabianu.antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {
    public static final int REQUEST_START = 5486327;
    private NotificationManager notMan;
    private NotificationCompat.Builder notBuild;
    private CountDownTimer alarmTimer;
    private Ringtone ring;

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
        //first modify notification and leave the user some time to disable it
        //just use the old notification builder and add some buttons
        //TODO: proper disarming (maybe automatically disable alarm
        Intent disarmIntent = new Intent(this, MainActivity.class);
        PendingIntent disarm = PendingIntent.getActivity(this, (int)System.currentTimeMillis(),
                disarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notBuild.addAction(R.mipmap.ic_launcher, "Disarm", disarm);
        Notification theOne = notBuild.build();
        theOne.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notMan.notify(REQUEST_START, theOne);

        //time until alarm rings
        //TODO: let user set this one
        int dischargeTime = 5000;
        alarmTimer = new CountDownTimer(dischargeTime, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                //TODO: maybe progressbar in notification?
            }

            @Override
            public void onFinish() {
                // timer wasn't interrupted, hence play tone
                ringAlarm();
            }
        };
        alarmTimer.start();
    }

    private void ringAlarm() {
        ring.play();
    }

    private void stopAlarm() {
        ring.stop();
        alarmTimer.cancel();
    }

    @Override
    public void onDestroy() {
        notMan.cancel(REQUEST_START);
        ((MovementDetector)listener).close();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
