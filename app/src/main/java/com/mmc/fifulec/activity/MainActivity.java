package com.mmc.fifulec.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mmc.fifulec.R;
import com.mmc.fifulec.broadcastreciver.Alarm;
import com.mmc.fifulec.broadcastreciver.RemoveUnAcceptedChallenges;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Alarm(this).on(RemoveUnAcceptedChallenges.class, 1000L * 60 * 60);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
