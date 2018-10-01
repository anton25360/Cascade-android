package anton25360.github.com.cascade2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import java.security.KeyStore;

import anton25360.github.com.cascade2.Login.LoginActivity;
import anton25360.github.com.cascade2.Popups.EditTask;

public class Cascade extends Application {

    public static final String CHANNEL_1_ID = "Notification channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //if Oreo or higher

            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, "Cascade Reminders", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Handles notifications for Cascade Reminders");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            }
    }
}
