package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        Runnable r = () -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startChooseActivity();
            } else {
                startOnBoardingActivity();
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 1500); // Delay for 1.5 seconds
    }

    void startChooseActivity() {
        startActivity(new Intent(MainActivity.this, chose.class));
        finish();
    }

    void startOnBoardingActivity() {
        startActivity(new Intent(MainActivity.this, onBoarding.class));
        finish();
    }
}
