package com.polen.receipt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.polen.receipt.R;


public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startAnimations();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        }, 3000);

    }


    Animation anim;
    private void startAnimations() {
        anim = AnimationUtils.loadAnimation(this, R.anim.alpha1);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.layout);
        l.clearAnimation();
        l.startAnimation(anim);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                anim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alpha2);
                LinearLayout l = (LinearLayout) findViewById(R.id.layout);
                anim.reset();
                l.clearAnimation();
                l.startAnimation(anim);
            }
        }, 1000);
    }

    @Override
    protected int getStyleRes() {
        return R.style.Theme_Opengur_Dark_Main_Dark;
    }
}
