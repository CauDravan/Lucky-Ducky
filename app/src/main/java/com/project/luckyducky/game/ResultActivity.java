package com.project.luckyducky.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.project.luckyducky.R;
import com.project.luckyducky.data.Models.GameHistory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView tvFinalScore, tvAccuracy, tvCorrect, tvWrong;
    private LinearLayout llQuestionResults;
    private Button btnPlayAgain, btnViewStats, btnBackHome;

    private int correctCount;
    private int wrongCount;
    private int totalQuestions;
    private boolean[] actualResults; // Store actual results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initializeViews();
        getDataFromIntent();
        displayResults();
        setupButtons();
    }

    private void initializeViews() {
        tvFinalScore = findViewById(R.id.tvFinalScore);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);
        llQuestionResults = findViewById(R.id.llQuestionResults);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnViewStats = findViewById(R.id.btnViewStats);
        btnBackHome = findViewById(R.id.btnBackHome);
    }

    private void getDataFromIntent() {
        correctCount = getIntent().getIntExtra("correctCount", 0);
        wrongCount = getIntent().getIntExtra("wrongCount", 0);
        totalQuestions = getIntent().getIntExtra("totalQuestions", 7);

        // Get actual question results
        ArrayList<String> resultsData = getIntent().getStringArrayListExtra("questionResults");
        if (resultsData != null && !resultsData.isEmpty()) {
            actualResults = new boolean[totalQuestions];
            for (String data : resultsData) {
                String[] parts = data.split(":");
                if (parts.length == 2) {
                    int questionNum = Integer.parseInt(parts[0]);
                    boolean isCorrect = Boolean.parseBoolean(parts[1]);
                    if (questionNum > 0 && questionNum <= totalQuestions) {
                        actualResults[questionNum - 1] = isCorrect;
                    }
                }
            }
        }
    }

    private void displayResults() {
        // Display overall results
        tvFinalScore.setText(correctCount + "/" + totalQuestions);

        double accuracy = totalQuestions > 0 ?
                (double) correctCount / totalQuestions * 100 : 0;
        DecimalFormat df = new DecimalFormat("#.#");
        tvAccuracy.setText(df.format(accuracy) + "%");

        tvCorrect.setText("✓ Correct: " + correctCount);
        tvWrong.setText("✗ Wrong: " + wrongCount);

        // Display performance message
        displayPerformanceMessage(accuracy);

        // Display detailed results per question
        displayQuestionResults();
    }

    private void displayPerformanceMessage(double accuracy) {
        TextView tvMessage = findViewById(R.id.tvPerformanceMessage);
        String message;

        if (accuracy == 100) {
            message = "🎉 Perfect! You're incredibly lucky!";
        } else if (accuracy >= 80) {
            message = "🌟 Excellent! Lady Luck is on your side!";
        } else if (accuracy >= 60) {
            message = "👍 Good job! Pretty lucky today!";
        } else if (accuracy >= 40) {
            message = "😊 Not bad! Keep trying!";
        } else {
            message = "💪 Better luck next time!";
        }

        tvMessage.setText(message);
    }

    private void displayQuestionResults() {
        llQuestionResults.removeAllViews();

        String[] questionNames = {
                "Q1: Red or Black",
                "Q2: Higher or Lower",
                "Q3: Inside or Outside",
                "Q4: Suit",
                "Q5: Odd or Even",
                "Q6: Face or Number",
                "Q7: Rank Prediction"
        };

        // Use actual results if available
        for (int i = 0; i < totalQuestions; i++) {
            boolean isCorrect = (actualResults != null && actualResults.length > i)
                    ? actualResults[i]
                    : (i < correctCount); // Fallback

            addQuestionResultView(questionNames[i], isCorrect);
        }
    }

    private void addQuestionResultView(String questionName, boolean isCorrect) {
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_question_result, llQuestionResults, false);

        TextView tvQuestionName = itemView.findViewById(R.id.tvQuestionName);
        TextView tvResult = itemView.findViewById(R.id.tvResult);
        CardView cardView = itemView.findViewById(R.id.cardResult);

        tvQuestionName.setText(questionName);
        tvResult.setText(isCorrect ? "✓ Correct" : "✗ Wrong");

        // Set background color based on result
        int colorResId = isCorrect ?
                R.color.correct_answer : R.color.wrong_answer;
        cardView.setCardBackgroundColor(getResources().getColor(colorResId));

        llQuestionResults.addView(itemView);
    }

    private void setupButtons() {
        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        btnViewStats.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        btnBackHome.setOnClickListener(v -> {
            finish();
        });
    }
}