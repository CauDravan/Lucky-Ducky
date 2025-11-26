package com.project.luckyducky.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.project.luckyducky.R;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.User;
import com.project.luckyducky.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private AuthManager authManager;
    private FirestoreService firestoreService;

    private Button btnGoogleSignIn;
    private ProgressBar progressBar;

    // ActivityResultLauncher để handle Google Sign-In
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize managers
        authManager = AuthManager.getInstance(this);
        firestoreService = FirestoreService.getInstance();

        // Initialize views
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressBar = findViewById(R.id.progressBar);

        // Setup Google Sign-In launcher
        setupGoogleSignInLauncher();

        // Click listener
        btnGoogleSignIn.setOnClickListener(v -> startGoogleSignIn());
    }

    private void setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        handleSignInResult(data);
                    } else {
                        hideLoading();
                        Toast.makeText(this, "Đăng nhập bị hủy", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void startGoogleSignIn() {
        showLoading();
        Intent signInIntent = authManager.getGoogleSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Intent data) {
        authManager.handleSignInResult(data, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Sign in success: " + user.getDisplayName());

                // Lưu user vào Firestore
                saveUserToFirestore(user);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Sign in failed: " + error);
                hideLoading();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserToFirestore(User user) {
        firestoreService.saveUser(user, new FirestoreService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "User saved to Firestore");
                hideLoading();
                navigateToMain();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to save user: " + error);
                // Vẫn cho vào app dù lưu Firestore thất bại
                hideLoading();
                navigateToMain();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnGoogleSignIn.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnGoogleSignIn.setEnabled(true);
    }
}