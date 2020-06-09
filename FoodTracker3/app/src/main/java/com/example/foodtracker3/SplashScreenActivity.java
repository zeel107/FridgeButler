package com.example.foodtracker3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * This class creates a splash screen and applies the given setting
 *
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 */
public class SplashScreenActivity extends AppCompatActivity
{
    /**
     * method creates a splash screen with the given settings applied
     * @param savedInstanceState hold the saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000) //3 seconds timeout
                .withBackgroundColor(Color.parseColor("#ACB0BA"))
                .withBeforeLogoText("WELCOME")
                .withAfterLogoText("Food inventorying made easy!")
                .withLogo(R.mipmap.ic_launcher_round);

        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }//method OnCreate
}//end SplashScreenActivity
