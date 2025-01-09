package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Button loginButton = findViewById(R.id.startQuizButton);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSubscription.class);
            startActivity(intent);
        });
    }
}

