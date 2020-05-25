package com.example.foodtracker3;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Date;

import static com.example.foodtracker3.App.expDate_Warning;
import static com.example.foodtracker3.App.expFood;

public class ExpJobService extends JobService
{
    //***************************************************************************************************
    private static NotificationManagerCompat notificationManager;
    //***************************************************************************************************
    //tag for log messages
    private static final String TAG = "ExpDateJobService";
    private boolean jobCancelled = false;
    private DatabaseHelper db = new DatabaseHelper(this);

    @Override
    public  boolean onStartJob(JobParameters params)
    {
        //***************************************************************************************************
        notificationManager = NotificationManagerCompat.from(this);
        //***************************************************************************************************
        Log.d(TAG, "job started");
        doBackgroundWork(params);

        return true;
    }//method onStartJob

    private void doBackgroundWork(final JobParameters params)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                for (int i = 0; i < 10; i++)
                {
                    Log.d(TAG, "run: " + i);
                    if (jobCancelled)
                    {
                        return;
                    }

                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
//***************************************************************************************************
                if (jobCancelled)
                {
                    return;
                }
                ArrayList<Product> productList = db.getAllProducts();
                expDateChecker(productList);

//***************************************************************************************************
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params)
    {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }//method onStopJob

    public void expDateChecker(ArrayList<Product> pList)
    {
        Date today = new Date();

        for(int i = 0; i < pList.size(); i++)
        {
            if(today.after(pList.get(i).getExpiration_date()))
            {     // if expires today, or already expired
                sendOnChannel1(pList.get(i));
            }
            else if(pList.get(i).getExpiration_date().getTime() - today.getTime() <= 345600000) // 4 days
            {
                sendOnChannel2(pList.get(i));
            }
        }

    }//method expDateChecker

//***************************************************************************************************
    //Notifications methods
    public void sendOnChannel1(Product currProduct)
    {
        String title = currProduct.getName() +  " EXPIRED!";
        String message = currProduct.getName() + " has expired as of " + Product.date_toAppStr(currProduct.getExpiration_date()) + "!";

        //defines action when clicked on
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, expFood)
                .setSmallIcon(R.drawable.ic_error_outline)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setAutoCancel(true) //dismiss notification after tap
                .setContentIntent(contentIntent)
                .setGroup("expired_foods")
                .build();

        Notification summaryNotification = new NotificationCompat.Builder(this,expFood)
                .setSmallIcon(R.drawable.ic_error)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(title + " " + message)
                        .setBigContentTitle("Expired Warning")
                        .setSummaryText("Expired Foods"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setGroup("expired_foods")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
                .build();
        notificationManager.notify((int) currProduct.getId(), notification); //NEED FIX FOR LONG AS INT
        notificationManager.notify(-1, summaryNotification);
    }//method SendOnChannel1

    public void sendOnChannel2(Product currProduct)
    {
        String title = currProduct.getName() +  " expiring!";
        String message = currProduct.getName() + " will expire on " + Product.date_toAppStr(currProduct.getExpiration_date()) + "!";

        //defines action when clicked on
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, expDate_Warning)
                .setSmallIcon(R.drawable.ic_watch)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.YELLOW)
                .setAutoCancel(true) //dismiss notification after tap
                .setContentIntent(contentIntent)
                .setGroup("about_to_expire")
                .build();

        Notification summaryNotification = new NotificationCompat.Builder(this, expDate_Warning)
                .setSmallIcon(R.drawable.ic_warning)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(title + " " + message)
                        .setBigContentTitle("Expiration Warning")
                        .setSummaryText("Foods expiration approaching "))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(Color.YELLOW)
                .setGroup("about_to_expire")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
                .build();

        notificationManager.notify((int) currProduct.getId(), notification); //NEED FIX FOR LONG AS INT
        notificationManager.notify(0, summaryNotification);

    }//method sendOnChannel2
//***************************************************************************************************
}//end ExpJobService
