package com.example.foodtracker3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The main activity of the UI that runs on opening the app.  Has a navigation bar at the bottom.
 * The add and home fragments are contained inside of the main activity.
 *
 * @author Andrew Dineen
 * @author Aidan Fallstorm
 * @author Rick Patneaude
 * @author Eli Storlie
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 */

public class MainActivity extends AppCompatActivity
{
    private static final String TAG_MainActivity = "MainAct_Notifi_Job";
    private static final String TAG_AddFragment = "AddFragment";
    private static final String TAG_HomeFragment = "HomeFragment";

    /**
     * Runs on opening the app.  Opens home fragment
     * @param savedInstanceState Reload from saved instance
     */
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


    /**
     * Set up the navigation bar
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                /**
                 * Button listener for the navigation bar
                 * @param menuItem Used to determine which fragment to open
                 * @return true
                 */
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
                {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    switch(menuItem.getItemId())
                    {
                        case R.id.nav_list:
                            if (fragment instanceof HomeFragment)   break;     // don't create new one if we are already on Home fragment
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(), TAG_HomeFragment).commit();
                            break;
                        case R.id.nav_add:
                            if (fragment instanceof AddFragment)    break;       // don't create new one if we are already on Add fragment
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddFragment(),TAG_AddFragment).commit();
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
            Log.d(TAG_MainActivity, "Job scheduled");
        }
        else {
            Log.d(TAG_MainActivity, "Job scheduling failed");
        }
    }//method schedulejob

    /**
     * Method cancels the job using the JOB_SCHEDULER_SERVICE
     */
    public void cancelJob()
    {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG_MainActivity, "Job cancelled");
    }//method cancelJob

}//end MainActivity
