package anton25360.github.com.cascade2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

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
            assert manager != null;
            manager.createNotificationChannel(channel);
            }
    }
}
