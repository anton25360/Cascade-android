package anton25360.github.com.cascade2.Classes;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import anton25360.github.com.cascade2.Cascade;
import anton25360.github.com.cascade2.MainActivity;
import anton25360.github.com.cascade2.R;


public class AlarmReciever extends BroadcastReceiver {

    NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(final Context context, Intent intent) {

        notificationManager = NotificationManagerCompat.from(context);
        Intent activityIntent =  new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, Cascade.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(MainActivity.titleString)
                .setContentText("Here is your reminder")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(1, notification);

    }
}