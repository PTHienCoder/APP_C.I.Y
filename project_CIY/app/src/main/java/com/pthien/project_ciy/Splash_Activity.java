package com.pthien.project_ciy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Splash_Activity extends AppCompatActivity {

    ProgressBar splashProgress;
    int SPLASH_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_);
        splashProgress = findViewById(R.id.prgre);

        if (Load_Data.isNetWorkAcess(this)){
            playProgress();
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash_Activity.this,DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME);
        }else{
            Toast.makeText(Splash_Activity.this, "No internet" , Toast.LENGTH_SHORT).show();
        }

    }

    //Method to run progress bar for 5 seconds
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(5000)
                .start();
    }

//    @Override
//    public void initSplash(ConfigSplash configSplash) {
//        ActionBar actionBar = getSupportActionBar();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        configSplash.setBackgroundColor(R.color.colorAccent); //any color you want form colors.xml
//        configSplash.setAnimCircularRevealDuration(3000); //int ms
//        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
//        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP
//
//
//        configSplash.setTitleTextColor(R.color.whiteTextColor);
//        configSplash.setTitleTextSize(20f);
//        configSplash.setAnimTitleDuration(3000);
//        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
//
//
//    }
//    @Override
//    public void animationsFinished() {
//        checkUserStatus();
//    }

//    private void checkUserStatus(){
//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user != null){
//            startActivity(new Intent(Splash_Activity.this, DashboardActivity.class));
//
//        }else{
//            startActivity(new Intent(Splash_Activity.this, MainActivity.class));
//            finish();
//        }
//    }
}