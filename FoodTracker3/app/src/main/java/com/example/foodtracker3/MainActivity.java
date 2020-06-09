package com.example.foodtracker3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainAct_Notifi_Job";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        scheduleJob();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
                {
                    switch(menuItem.getItemId())
                    {
                        case R.id.nav_list:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                            break;
                        case R.id.nav_add:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddFragment()).commit();

                            break;
                    }
                    return true;
                }//method onNavigationItemSelected
            };

    /**
     * Method schedules a customized job that will execute every 12 hours
     */
    public void scheduleJob()
    {
        ComponentName componentName = new ComponentName(this, ExpJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true) //keeps job alive even on reboot
                .setPeriodic(43200000) //43200000 = 12h    15 * 60 * 1000 = 15min
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);

        if (resultCode == JobScheduler.RESULT_SUCCESS)
        {
            Log.d(TAG, "Job scheduled");
        }
        else {
            Log.d(TAG, "Job scheduling failed");
        }
    }//method schedulejob

    /**
     * Method cancels the job using the JOB_SCHEDULER_SERVICE
     */
    public void cancelJob()
    {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Job cancelled");
    }//method cancelJob

}//end MainActivity
