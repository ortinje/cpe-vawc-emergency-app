package com.bscpe.vawcemergencyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Loader extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly after a 5-second delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is already signed in, proceed to MainPage
                    startActivity(new Intent(Loader.this, MainPage.class));
                } else {
                    // User is not signed in, proceed to LoginPage
                    startActivity(new Intent(Loader.this, LandingPage.class));
                }
                finish(); // Finish the current activity to prevent the user from coming back to it on back press
            }
        }, 2000); // 5-second delay in milliseconds (5000 ms)
    }
}
