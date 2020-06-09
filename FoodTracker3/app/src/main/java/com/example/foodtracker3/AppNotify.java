package com.example.foodtracker3;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * This class creates notification channels, to be used in the ExpJobService class at the beginning
 * of the application
 *
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 */
public class AppNotify extends Application
{
    public static final String expFood = "channel1";
    public static final String expDate_Warning = "channel2";

    /**
     * Method creates calls createNotificationChannels and creates 2 notification channels
     * a the start of the application
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannels();
    }//method onCreate

    /**
     * Method creates 2 customized notification channels
     */
    public void createNotificationChannels()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(
                    expFood,
                    "Food_Expired!",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This pushes a notification on the expiration date and after");

            NotificationChannel channel2 = new NotificationChannel(
                    expDate_Warning,
                    "Food_about_to_Expired!",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription("This pushes a notification a few days before the expiration date");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }

    }//method createNotificationChannels

}//End App
