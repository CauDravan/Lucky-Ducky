package com.project.luckyducky.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.luckyducky.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Install system splash screen (AndroidX)
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Check current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User already signed in -> go to Main
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Not signed in -> go to Login
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
