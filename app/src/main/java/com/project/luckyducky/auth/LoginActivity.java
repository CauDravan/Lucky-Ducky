package com.project.luckyducky.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.project.luckyducky.R;
import com.project.luckyducky.data.Models.User;
import com.project.luckyducky.main.MainActivity;

public class LoginActivity extends com.project.luckyducky.BaseActivity {

    private AuthManager authManager;
    private Button btnGoogleSignIn;
    private ProgressBar progressBar;
    private TextView tvAppTitle, tvAppSubtitle;
    private View layoutLoginContent;

    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        authManager = new AuthManager(this);

        // Setup Activity Result Launcher for Google Sign In
        setupSignInLauncher();
        setupClickListeners();
    }

    private void initializeViews() {
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressBar = findViewById(R.id.progressBar);
        tvAppTitle = findViewById(R.id.tvAppTitle);
        tvAppSubtitle = findViewById(R.id.tvAppSubtitle);
        layoutLoginContent = findViewById(R.id.layoutLoginContent);
    }

    private void setupSignInLauncher() {
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        handleSignInResult(data);
                    } else {
                        showLoading(false);
                        Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupClickListeners() {
        btnGoogleSignIn.setOnClickListener(v -> startGoogleSignIn());
    }

    private void startGoogleSignIn() {
        showLoading(true);
        Intent signInIntent = authManager.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Intent data) {
        authManager.handleSignInResult(data, new AuthManager.OnAuthCompleteListener() {
            @Override
            public void onSuccess(User user) {
                showLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Welcome, " + user.getDisplayName() + "!",
                        Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Login failed: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutLoginContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}