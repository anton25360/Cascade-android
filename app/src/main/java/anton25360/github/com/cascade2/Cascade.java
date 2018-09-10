package anton25360.github.com.cascade2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import anton25360.github.com.cascade2.Login.LoginActivity;
import anton25360.github.com.cascade2.Popups.EditTask;
import io.armcha.debugBanner.DebugBanner;

public class Cascade extends Application {

    public static final String CHANNEL_1_ID = "channel1"; //todo change name

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //if Oreo or higher

            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_DEFAULT); //todo research notification importance
            channel1.setDescription("balls"); //todo change this

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
