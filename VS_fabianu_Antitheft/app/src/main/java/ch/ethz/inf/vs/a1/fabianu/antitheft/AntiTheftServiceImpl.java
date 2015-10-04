package ch.ethz.inf.vs.a1.fabianu.antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {
    public static final int REQUEST_START = 5486327;
    private NotificationManager notMan;

    @Override
    public void onCreate() {
        super.onCreate();
        ((MovementDetector)listener).setContext(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder notBuild = new NotificationCompat.Builder(this);
        notBuild.setContentTitle(this.getString(R.string.notificationTitle));
        notBuild.setContentText(this.getString(R.string.notificationText));
        //TODO: put a proper icon into drawable and set the icon correctly here
        notBuild.setSmallIcon(R.mipmap.ic_launcher);

        //normally start MainActivity fresh - because it's fresh
        Intent start = new Intent(this, MainActivity.class);
        //start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent startMain = PendingIntent.getActivity(this, REQUEST_START, start, PendingIntent.FLAG_UPDATE_CURRENT);
        notBuild.setContentIntent(startMain);
        notMan = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        Notification theOne = notBuild.build();
        theOne.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notMan.notify(REQUEST_START, theOne);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void startAlarm() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null) {
            // if alarm was never set, use notification sound as backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
        r.play();
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
