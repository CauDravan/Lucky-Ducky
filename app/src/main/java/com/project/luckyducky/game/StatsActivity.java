package com.project.luckyducky.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.luckyducky.R;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.Stats;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StatsActivity extends com.project.luckyducky.BaseActivity {

    private FirestoreService firestoreService;
    private Stats userStats;

    private TextView tvTotalGames, tvTotalQuestions, tvOverallAccuracy;
    private TextView tvTotalCorrect, tvTotalWrong;
    private LinearLayout llQuestionStats;
    private ProgressBar progressBar;
    private CardView cardOverallStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initializeViews();
        firestoreService = new FirestoreService();
        loadUserStats();
    }

    private void initializeViews() {
        tvTotalGames = findViewById(R.id.tvTotalGames);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvOverallAccuracy = findViewById(R.id.tvOverallAccuracy);
        tvTotalCorrect = findViewById(R.id.tvTotalCorrect);
        tvTotalWrong = findViewById(R.id.tvTotalWrong);
        llQuestionStats = findViewById(R.id.llQuestionStats);
        progressBar = findViewById(R.id.progressBar);
        cardOverallStats = findViewById(R.id.cardOverallStats);

        findViewById(R.id.btnResetStats).setOnClickListener(v -> showResetDialog());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadUserStats() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        cardOverallStats.setVisibility(View.GONE);

        firestoreService.getStats(user.getUid(), new FirestoreService.OnStatsLoadListener() {
            @Override
            public void onStatsLoaded(Stats stats) {
                progressBar.setVisibility(View.GONE);
                cardOverallStats.setVisibility(View.VISIBLE);
                userStats = stats;
                displayStats();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StatsActivity.this,
                        "Failed to load stats: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStats() {
        if (userStats == null) return;

        DecimalFormat df = new DecimalFormat("#.#");

        // Display overall stats
        tvTotalGames.setText(String.valueOf(userStats.getTotalGamesPlayed()));
        tvTotalQuestions.setText(String.valueOf(userStats.getTotalQuestionsAnswered()));
        tvOverallAccuracy.setText(df.format(userStats.getAccuracyRate()) + "%");
        tvTotalCorrect.setText("✓ " + userStats.getTotalCorrectAnswers());
        tvTotalWrong.setText("✗ " + userStats.getTotalWrongAnswers());

        // Display per-question stats
        displayQuestionStats();
    }

    private void displayQuestionStats() {
        llQuestionStats.removeAllViews();

        Map<String, Stats.QuestionStats> questionStatsMap = userStats.getQuestionStats();

        // Sort questions by key (q1, q2, q3, etc.)
        List<String> sortedKeys = new ArrayList<>(questionStatsMap.keySet());
        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {
            Stats.QuestionStats qStats = questionStatsMap.get(key);
            addQuestionStatView(qStats);
        }
    }

    private void addQuestionStatView(Stats.QuestionStats qStats) {
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_question_stat, llQuestionStats, false);

        TextView tvQuestionName = itemView.findViewById(R.id.tvQuestionName);
        TextView tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
        TextView tvCorrectWrong = itemView.findViewById(R.id.tvCorrectWrong);
        ProgressBar progressBar = itemView.findViewById(R.id.progressAccuracy);

        tvQuestionName.setText(qStats.getQuestionName());

        DecimalFormat df = new DecimalFormat("#.#");
        double accuracy = qStats.getAccuracy();
        tvAccuracy.setText(df.format(accuracy) + "%");

        tvCorrectWrong.setText("✓ " + qStats.getTimesCorrect() +
                "  ✗ " + qStats.getTimesWrong());

        progressBar.setProgress((int) accuracy);

        // Set color based on accuracy
        int color;
        if (accuracy >= 75) {
            color = getResources().getColor(R.color.excellent_stat);
        } else if (accuracy >= 50) {
            color = getResources().getColor(R.color.good_stat);
        } else {
            color = getResources().getColor(R.color.poor_stat);
        }
        progressBar.getProgressDrawable().setColorFilter(
                color, android.graphics.PorterDuff.Mode.SRC_IN);

        llQuestionStats.addView(itemView);
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Statistics")
                .setMessage("Are you sure you want to reset all your statistics? This action cannot be undone.")
                .setPositiveButton("Reset", (dialog, which) -> resetStats())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetStats() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);

        firestoreService.resetStats(user.getUid(), new FirestoreService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(StatsActivity.this,
                        "Statistics reset successfully",
                        Toast.LENGTH_SHORT).show();
                loadUserStats();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StatsActivity.this,
                        "Failed to reset stats: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}