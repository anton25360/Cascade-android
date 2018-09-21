package anton25360.github.com.cascade2.Classes;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import anton25360.github.com.cascade2.Cascade;
import anton25360.github.com.cascade2.MainActivity;
import anton25360.github.com.cascade2.R;


public class AlarmReciever extends BroadcastReceiver {

    NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(final Context context, Intent intent) {

        notificationManager = NotificationManagerCompat.from(context);

        Notification notification = new NotificationCompat.Builder(context, Cascade.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_chat_bubble_black) //todo change notification icon
                .setContentTitle(MainActivity.titleString) //todo title is invisible
                .setContentText("Here is your reminder.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();

        notificationManager.notify(1, notification);

    }
}