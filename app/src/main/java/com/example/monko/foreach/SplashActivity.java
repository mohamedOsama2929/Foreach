package com.example.monko.foreach;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        i=new Intent(this,MainActivity.class);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Thread.sleep(3000);

                    startActivity(i);





                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                finish();

            }
        }).start();
    }
}
