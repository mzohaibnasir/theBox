package com.madein.thebox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class launch extends AppCompatActivity {

    private ConstraintLayout startScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);


        startScreen=findViewById(R.id.startScreen);
        Toast.makeText(launch.this,"Tap Anywhere to Continue!",Toast.LENGTH_LONG).show();
        startScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(launch.this,MainActivity.class));
            }
        });
    }
}