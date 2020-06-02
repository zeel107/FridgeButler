package com.example.foodtracker3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000) //3 seconds timeout
                .withBackgroundColor(Color.parseColor("#46403D"))
                //.withHeaderText("Header")
                //.withFooterText("Footer")
                .withBeforeLogoText("WELCOME")
                .withAfterLogoText("Food inventorying made easy!")
                .withLogo(R.mipmap.ic_launcher_round);

        //config.getHeaderTextView().setTextColor(Color.WHITE);
       // config.getFooterTextView().setTextColor(Color.YELLOW);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }//method OnCreate
}//end SplashScreenActivity
