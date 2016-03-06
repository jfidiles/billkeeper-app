package com.work.jfidiles.BillKeeper.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.work.jfidiles.BillKeeper.Activity.MainActivity;
import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.R;

import java.util.Random;

public class Notify extends Service{
    Random random;
    int rand;
    String titleSufix="";
    NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        random = new Random();
        rand = random.nextInt(999999);

        int Snr = NotificationPreference.getPosition("Snr", this);

        String title = AppConfig.TITLE_NOTIFICATION + Integer.toString(Snr);
        String FinalTitle = NotificationPreference.get(title, this);

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notifIntent = new Intent(this, MainActivity.class);
        notifIntent.putExtra(AppConfig.FRAGMENT, "payable" );
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent,
                PendingIntent.FLAG_ONE_SHOT);

        int icon = R.drawable.info32;
        Snr++;
        NotificationPreference.incrementValues("Snr", Snr, this);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(AppConfig.NOTIFICATION_TITLE)
                .setContentText(AppConfig.NOTIFICATION_CONTENT + FinalTitle)
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .addAction(R.drawable.paid24, "Pay", pendingIntent)
                .build();
        notificationManager.notify(rand, notification);
        NotificationPreference.delete(FinalTitle, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(rand);
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }
}
