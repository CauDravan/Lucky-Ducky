package com.project.luckyducky.game;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.project.luckyducky.R;
import com.project.luckyducky.auth.AuthManager;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.Stats;
import com.project.luckyducky.utils.Constants;

public class StatsActivity extends AppCompatActivity {

    private AuthManager authManager;
    private FirestoreService firestoreService;

    private ProgressBar progressBar;
    private TextView tvTotalGames;
    private LinearLayout llStatsContainer;

    private Stats userStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Stats");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize managers
        authManager = AuthManager.getInstance(this);
        firestoreService = FirestoreService.getInstance();

        // Initialize views
        initViews();

        // Load stats
        loadStats();

        // Setup back press
        setupBackPressHandler();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvTotalGames = findViewById(R.id.tvTotalGames);
        llStatsContainer = findViewById(R.id.llStatsContainer);
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void loadStats() {
        showLoading(true);

        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Not found user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestoreService.getStats(userId, new FirestoreService.OnDataLoadListener<Stats>() {
            @Override
            public void onSuccess(Stats stats) {
                userStats = stats;
                displayStats();
                showLoading(false);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(StatsActivity.this,
                        "Error load stats: " + error,
                        Toast.LENGTH_SHORT).show();
                showLoading(false);
                finish();
            }
        });
    }

    private void displayStats() {
        if (userStats == null) return;

        // Display total games
        tvTotalGames.setText(String.format("Total played: %d", userStats.getTotalGames()));

        // Clear previous stats
        llStatsContainer.removeAllViews();

        // Display stats for each question
        for (int i = 1; i <= Constants.TOTAL_QUESTIONS; i++) {
            Stats.QuestionStats qStats = userStats.getQuestionStats(i);
            if (qStats != null) {
                CardView statCard = createStatCard(qStats);
                llStatsContainer.addView(statCard);
            }
        }
    }

    private CardView createStatCard(Stats.QuestionStats qStats) {
        // Create CardView
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 24);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(16);
        cardView.setCardElevation(4);
        cardView.setContentPadding(24, 24, 24, 24);

        // Create content layout
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        // Question title
        TextView tvTitle = new TextView(this);
        tvTitle.setText(String.format("Question %d: %s",
                qStats.getQuestionNumber(),
                Constants.QUESTIONS[qStats.getQuestionNumber() - 1]));
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(getResources().getColor(R.color.text_primary));
        tvTitle.getPaint().setFakeBoldText(true);
        tvTitle.setPadding(0, 0, 0, 16);
        contentLayout.addView(tvTitle);

        // Stats row layout
        LinearLayout statsRow = new LinearLayout(this);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setWeightSum(3);

        // Correct count
        LinearLayout correctLayout = createStatItem(
                "Correct",
                String.valueOf(qStats.getCorrectCount()),
                R.color.success
        );
        statsRow.addView(correctLayout);

        // Incorrect count
        LinearLayout incorrectLayout = createStatItem(
                "Wrong",
                String.valueOf(qStats.getIncorrectCount()),
                R.color.error
        );
        statsRow.addView(incorrectLayout);

        // Win rate
        LinearLayout winRateLayout = createStatItem(
                "Ratio",
                String.format("%.0f%%", qStats.getWinRate()),
                R.color.primary
        );
        statsRow.addView(winRateLayout);

        contentLayout.addView(statsRow);

        // Progress bar
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                24
        );
        progressParams.setMargins(0, 16, 0, 0);
        progressBar.setLayoutParams(progressParams);
        progressBar.setMax(100);
        progressBar.setProgress((int) qStats.getWinRate());
        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(
                getResources().getColor(getProgressColor(qStats.getWinRate()))));
        contentLayout.addView(progressBar);

        cardView.addView(contentLayout);
        return cardView;
    }

    private LinearLayout createStatItem(String label, String value, int colorRes) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        layout.setLayoutParams(params);

        // Value
        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextSize(24);
        tvValue.setTextColor(getResources().getColor(colorRes));
        tvValue.getPaint().setFakeBoldText(true);
        tvValue.setGravity(android.view.Gravity.CENTER);
        layout.addView(tvValue);

        // Label
        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextSize(12);
        tvLabel.setTextColor(getResources().getColor(R.color.text_secondary));
        tvLabel.setGravity(android.view.Gravity.CENTER);
        tvLabel.setPadding(0, 4, 0, 0);
        layout.addView(tvLabel);

        return layout;
    }

    private int getProgressColor(double winRate) {
        if (winRate >= 70) {
            return R.color.success;
        } else if (winRate >= 50) {
            return R.color.warning;
        } else {
            return R.color.error;
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        llStatsContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}