package com.project.luckyducky.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.project.luckyducky.R;
import com.project.luckyducky.auth.AuthManager;
import com.project.luckyducky.auth.LoginActivity;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.Stats;
import com.project.luckyducky.data.Models.User;
import com.project.luckyducky.game.GameActivity;
import com.project.luckyducky.game.HistoryActivity;
import com.project.luckyducky.game.StatsActivity;

public class MainActivity extends com.project.luckyducky.BaseActivity {

    private AuthManager authManager;
    private FirestoreService firestoreService;
    private User currentUser;

    private ImageView ivProfilePicture;
    private TextView tvWelcome, tvUserName, tvTotalGames, tvBestScore, tvAccuracy;
    private Button btnPlayGame, btnViewStats, btnViewHistory, btnLogout;
    private ProgressBar progressBar;
    private View layoutUserInfo, layoutQuickStats;

    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        authManager = new AuthManager(this);
        firestoreService = new FirestoreService();

        checkUserAuthentication();
    }

    private void initializeViews() {
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserName = findViewById(R.id.tvUserName);
        tvTotalGames = findViewById(R.id.tvTotalGames);
        tvBestScore = findViewById(R.id.tvBestScore);
        tvAccuracy = findViewById(R.id.tvAccuracy);

        btnPlayGame = findViewById(R.id.btnPlayGame);
        btnViewStats = findViewById(R.id.btnViewStats);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnLogout = findViewById(R.id.btnLogout);

        progressBar = findViewById(R.id.progressBar);
        layoutUserInfo = findViewById(R.id.layoutUserInfo);
        layoutQuickStats = findViewById(R.id.layoutQuickStats);

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnPlayGame.setOnClickListener(v -> startGame());
        btnViewStats.setOnClickListener(v -> viewStats());
        btnViewHistory.setOnClickListener(v -> viewHistory());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void checkUserAuthentication() {
        FirebaseUser firebaseUser = authManager.getCurrentUser();

        if (firebaseUser == null) {
            // User not logged in, go to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Load user data
        loadUserData(firebaseUser.getUid());
    }

    private void loadUserData(String userId) {
        showLoading(true);

        firestoreService.getUser(userId, new FirestoreService.OnUserLoadListener() {
            @Override
            public void onUserLoaded(User user) {
                currentUser = user;
                displayUserInfo(user);
                loadQuickStats(userId);
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(MainActivity.this,
                        "Failed to load user data: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserInfo(User user) {
        if (isFinishing() || isDestroyed()) {
            // Nếu Activity đã bị hủy, không làm gì cả và thoát khỏi hàm
            return;
        }

        if (user == null) return;

        tvWelcome.setText("Welcome back!");
        tvUserName.setText(user.getDisplayName() != null ?
                user.getDisplayName() : "Player");

        // Load profile picture
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfilePicture);
        }

        layoutUserInfo.setVisibility(View.VISIBLE);
    }

    private void loadQuickStats(String userId) {
        firestoreService.getStats(userId, new FirestoreService.OnStatsLoadListener() {
            @Override
            public void onStatsLoaded(Stats stats) {
                showLoading(false);
                displayQuickStats(stats);
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                // Show default stats on failure
                layoutQuickStats.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayQuickStats(Stats stats) {
        if (stats == null) {
            layoutQuickStats.setVisibility(View.VISIBLE);
            return;
        }

        tvTotalGames.setText(String.valueOf(stats.getTotalGamesPlayed()));

        // Calculate best score from current user
        if (currentUser != null) {
            tvBestScore.setText(currentUser.getBestScore() + "/7");
        } else {
            tvBestScore.setText("0/7");
        }

        // Display accuracy
        double accuracy = stats.getAccuracyRate();
        tvAccuracy.setText(String.format("%.1f%%", accuracy));

        layoutQuickStats.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutUserInfo.setVisibility(show ? View.GONE : View.VISIBLE);
        layoutQuickStats.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void viewStats() {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    private void viewHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        progressBar.setVisibility(View.VISIBLE);

        authManager.signOut(new AuthManager.OnSignOutListener() {
            @Override
            public void onSignOutSuccess() {
                Toast.makeText(MainActivity.this,
                        "Logged out successfully",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSignOutFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        "Logout failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to MainActivity
        FirebaseUser firebaseUser = authManager.getCurrentUser();
        if (firebaseUser != null) {
            loadUserData(firebaseUser.getUid());
        }
    }
}