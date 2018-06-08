package com.nghianguyen.intheneighborhood;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class InTheNeightborhoodApp extends Application{

    public static final String CHANNEL_NEARBY_ALERT = "NEARBY_ALERTS";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_NEARBY_ALERT, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
