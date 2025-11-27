package com.project.luckyducky.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.project.luckyducky.R;
import com.project.luckyducky.auth.AuthManager;
import com.project.luckyducky.auth.LoginActivity;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.Stats;
import com.project.luckyducky.data.Models.User;
import com.project.luckyducky.game.GameActivity;

public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;
    private FirestoreService firestoreService;

    private ImageView imgUserAvatar;
    private TextView tvUserName;
    private TextView tvTotalGames;
    private Button btnStartGame;
    private Button btnViewStats;
    private Button btnViewHistory;

    private User currentUser;
    private Stats userStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize managers
        authManager = AuthManager.getInstance(this);
        firestoreService = FirestoreService.getInstance();

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lucky Ducky");
            getSupportActionBar().setElevation(4);
        }

        // Initialize views
        initViews();

        // Load user data
        loadUserData();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvTotalGames = findViewById(R.id.tvTotalGames);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnViewStats = findViewById(R.id.btnViewStats);
        btnViewHistory = findViewById(R.id.btnViewHistory);
    }

    private void loadUserData() {
        currentUser = authManager.getCurrentUser();

        if (currentUser != null) {
            // Display user info
            tvUserName.setText(currentUser.getDisplayName());

            // Load avatar
            if (currentUser.getPhotoUrl() != null && !currentUser.getPhotoUrl().isEmpty()) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_lucky_ducky)
                        .into(imgUserAvatar);
            }

            // Load stats
            loadStats();
        } else {
            // No user -> back to login
            navigateToLogin();
        }
    }

    private void loadStats() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            firestoreService.getStats(userId, new FirestoreService.OnDataLoadListener<Stats>() {
                @Override
                public void onSuccess(Stats stats) {
                    userStats = stats;
                    tvTotalGames.setText("Total played: " + stats.getTotalGames());
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(MainActivity.this,
                            "Error load stats: " + error,
                            Toast.LENGTH_SHORT).show();
                    // Create new stats if error
                    userStats = new Stats(userId);
                    tvTotalGames.setText("Total played: 0");
                }
            });
        }
    }

    private void setupClickListeners() {
        btnStartGame.setOnClickListener(v -> startGame());

        btnViewStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.project.luckyducky.game.StatsActivity.class);
            startActivity(intent);
        });

        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.project.luckyducky.game.HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        authManager.signOut(() -> {
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload stats when come back to GameActivity
        if (authManager.getCurrentUserId() != null) {
            loadStats();
        }
    }
}