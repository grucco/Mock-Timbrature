package it.reply.timbrature.timbrature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
//  SplashScreen
//  Timbrature
//
//  Created by Raffaele Forgione @ Syskoplan Reply.
//  Copyright © 2017 Acea. All rights reserved.
*/

public class SplashScreen extends AppCompatActivity {

    private LinearLayout l;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //configureTutorial();
        StartAnimations();
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                Animation newAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.invalpha);
                newAnim.reset();
                l.clearAnimation();
                l.startAnimation(newAnim);
            }
        });

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    startIntent();
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }

            }
        };

        splashTread.start();

    }

    private void configureTutorial(){
        // variabile tuto
        SharedPreferences settings = getApplicationContext().getSharedPreferences("Tutorial", 0);
        String tuto = settings.getString("tutorial", null);
        if(tuto == null || tuto.length() == 0) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("tutorial", "show");
            editor.apply();
        }
    }

    private String parseURLSchema() {
        Intent intent = getIntent();
        String response = "";
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String path = uri.getPath();
            if(path.contains("verify")) {
                response = uri.getQueryParameter("verificationCode");
                // doppio controllo sugli apici da rimuovere
                response = response.replaceAll("’", "");
                response = response.replaceAll("'", "");
            }
            else if(path.contains("swfm")){
                response = path;
            }
        }
        return response;
    }

    private void startIntent(){
        Intent intent;
        String response = parseURLSchema();
        if(response.contains("swfm")){
            intent = new Intent(SplashScreen.this, LoginActivity.class);
        }
        // CASO - VALIDATION CODE
        else if(response.length() > 0){
            intent = new Intent(SplashScreen.this, LoginActivity.class);
            intent.putExtra("verificationCode", response);
        }
        else{
            intent = new Intent(SplashScreen.this,
                    MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
