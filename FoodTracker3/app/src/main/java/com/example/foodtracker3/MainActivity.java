package com.example.foodtracker3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

import static com.example.foodtracker3.App.expDate_Warning;
import static com.example.foodtracker3.App.expFood;


public class MainActivity extends AppCompatActivity
{
    //private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notificationManager = NotificationManagerCompat.from(this);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch(menuItem.getItemId()){
                        case R.id.nav_list:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

                            break;
                        case R.id.nav_add:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddFragment()).commit();

                            break;
                    }
                    return true;
                }
            };

    /*public void expDateNotif()
    {
        //-----------------------------------------------------------------------------------

        Date today = new Date();
        if(today.after(currentProduct.getExpiration_date())) {     // if expires today, or already expired
            //holder.expiredImageView.setImageResource(R.drawable.ic_error_outline);
            sendOnChannel1();
        } else {

            if(currentProduct.getExpiration_date().getTime() - today.getTime() <= 345600000) {        // 4 days
                //holder.expiredImageView.setImageResource(R.drawable.ic_warning);
                sendOnChannel2();
            } else {
                //holder.expiredImageView.setImageResource(0);
            }
        }
        //------------------------------------------------------------------------------

    }//method expDateNotif

    public void sendOnChannel1() {
        //String title = editTextTitle.getText().toString();
        //String message = editTextMessage.getText().toString();

        String title = "Title #1";
        String message = "Message #1";

    Notification notification = new NotificationCompat.Builder(this, expFood)
                .setSmallIcon(R.drawable.ic_kitchen)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(1, notification);
    }
    public void sendOnChannel2() {
        //String title = editTextTitle.getText().toString();
        //String message = editTextMessage.getText().toString();
        String title = "Title #1";
        String message = "Message #1";

        Notification notification = new NotificationCompat.Builder(this, expDate_Warning)
                .setSmallIcon(R.drawable.ic_kitchen)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        notificationManager.notify(2, notification);
    }*/

}//End MainActivity
