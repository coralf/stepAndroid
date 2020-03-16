package com.android.step;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
            startActivity(intent);
        });


        vRight.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, RightActivity.class);
            startActivity(intent);
        });


    }

}
