package com.example.foodtracker3;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Date;

import static com.example.foodtracker3.AppNotify.expDate_Warning;
import static com.example.foodtracker3.AppNotify.expFood;

/**
 *Class creates a job to run in the backgroung as a background service. It will check all
 * the expiration dates of each products in the database and push notifications out depending on
 * the expiration date
 *
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 */
public class ExpJobService extends JobService
{
    //fields
    private static NotificationManagerCompat notificationManager;
    private static final String TAG = "ExpDateJobService";//tag for log messages
    private boolean jobCancelled = false;
    private DatabaseHelper db;
    Context context;

    /**
     * Method creates a database object and context object with the current context
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this;
        db = new DatabaseHelper(context);
    }//method onCreate

    /**
     *Methods starts a background thread to do long running operations
     *
     * @param params Contains the parameters used to configure/identify your job.
     *               You do not create this object yourself, instead it is handed in to
     *               your application by the System.
     *
     * @return true to tell the system to keep the device awake so the service can finish its work
     */
    @Override
    public  boolean onStartJob(JobParameters params)
    {
        notificationManager = NotificationManagerCompat.from(this);
        Log.d(TAG, "##-JOB STARTED-##");
        doBackgroundWork(params);
        return true;
    }//method onStartJob

    /**
     *Method will start a thread and check all the expiration dates of each products in the
     * database and push notifications out depending on the expiration date
     *
     * @param params Contains the parameters used to configure/identify your job.
     *               You do not create this object yourself, instead it is handed in to
     *               your application by the System.
     */
    private void doBackgroundWork(final JobParameters params)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                if (jobCancelled)
                {
                    return;
                }
                ArrayList<Product> productList = db.getAllProducts();
                expDateChecker(productList);

                Log.d(TAG, "##-JOB FINISHED-##");
                jobFinished(params, false);
            }//method run
        }).start();
    }//method doBackgroundWork

    /**
     * Method will cancel the job and releases the wakelock when the criteria of the job is met
     *
     * @param params Contains the parameters used to configure/identify your job.
     *               You do not create this object yourself, instead it is handed in to
     *               your application by the System.
     * @return true to tell the system to cancel the job and stop background services
     */
    @Override
    public boolean onStopJob(JobParameters params)
    {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }//method onStopJob

    /**
     * Method will check each expiration_date of each product in the arraylist and
     * determine if it is expired, with in 4 days of expiration, or not expired.
     * If expired, it will send a notification on notification channel 1 using  the sendOnChannel1 method.
     * If not expired or with in 4 days of expiration it will send a notification on
     * notification channel 2 using  the sendOnChannel2 method.
     *
     * @param pList arraylist of all the products in the database.
     */
    public void expDateChecker(ArrayList<Product> pList)
    {
        Date today = new Date();

        for(int i = 0; i < pList.size(); i++)
        {
            boolean hasExpiration = (pList.get(i).getExpiration_date() != null);
            if(hasExpiration && today.after(pList.get(i).getExpiration_date())) // if expires today, or already expired
            {
                sendOnChannel1(pList.get(i));
            }
            else if(hasExpiration && pList.get(i).getExpiration_date().getTime() - today.getTime() <= 345600000) // 4 days
            {
                sendOnChannel2(pList.get(i));
            }
        }
    }//method expDateChecker


    //Notifications methods

    /**
     * Method creates a customized notification and notification group for expired products.
     * Defines a action (navigating to the application home screen) to perform when
     * notification is clicked on.
     *
     * @param currProduct the current Product object that has a expiration date that is
     *                    expired or is about to expire
     */
    public void sendOnChannel1(Product currProduct)
    {
        String title = currProduct.getName() +  " EXPIRED!";
        String message = "Expired as of " + DatabaseHelper.date_toAppStr(currProduct.getExpiration_date()) + "!";

        //defines action when clicked on
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, expFood)
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

        Notification summaryNotification = new NotificationCompat.Builder(context,expFood)
                .setSmallIcon(R.drawable.ic_fridge)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(title + " " + message)
                        .setBigContentTitle("Expired Warning")
                        .setSummaryText("Expired Foods"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setGroup("expired_foods")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroupSummary(true)
                .build();
        notificationManager.notify((int) currProduct.getId(), notification); //NEED FIX FOR LONG AS INT
        notificationManager.notify(-1, summaryNotification);
    }//method SendOnChannel1

    /**
     * Method creates a customized notification and notification group for expired products.
     * Defines a action (navigating to the application home screen) to perform when
     * notification is clicked on.
     *
     * @param currProduct the current Product object that has a expiration date that is
     *                    expired or is about to expire
     */
    public void sendOnChannel2(Product currProduct)
    {
        String title = currProduct.getName() +  " Expiring!";
        String message = "Expires on " + DatabaseHelper.date_toAppStr(currProduct.getExpiration_date()) + "!";

        //defines action when clicked on
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, expDate_Warning)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.YELLOW)
                .setAutoCancel(true) //dismiss notification after tap
                .setContentIntent(contentIntent)
                .setGroup("about_to_expire")
                .build();

        Notification summaryNotification = new NotificationCompat.Builder(context, expDate_Warning)
                .setSmallIcon(R.drawable.ic_fridge)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(title + " " + message)
                        .setBigContentTitle("Expiration Warning")
                        .setSummaryText("Foods expiration approaching "))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(Color.YELLOW)
                .setGroup("about_to_expire")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroupSummary(true)
                .build();

        notificationManager.notify((int) currProduct.getId(), notification); //NEED FIX FOR LONG AS INT
        notificationManager.notify(0, summaryNotification);

    }//method sendOnChannel2
//***************************************************************************************************
}//end ExpJobService
