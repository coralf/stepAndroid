package com.android.step;

import android.content.Intent;
import android.os.Bundle;

import com.android.step.client.LeftActivity;
import com.android.step.server.RightActivity;
import com.android.step.utils.Config;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        View vLeft = findViewById(R.id.l_left);
        View vRight = findViewById(R.id.l_right);

        vLeft.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LeftActivity.class);
            intent.putExtra("from", Config.CLIENT);
            startActivity(intent);
        });


        vRight.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, RightActivity.class);
            intent.putExtra("from", Config.SERVER);
            startActivity(intent);
        });


    }

}
