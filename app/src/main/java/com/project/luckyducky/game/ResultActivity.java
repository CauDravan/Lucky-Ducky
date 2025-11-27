package com.project.luckyducky.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.project.luckyducky.R;
import com.project.luckyducky.data.Models.Card;
import com.project.luckyducky.data.Models.GameHistory;
import com.project.luckyducky.main.MainActivity;
import com.project.luckyducky.utils.Constants;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResultTitle;
    private TextView tvScore;
    private TextView tvCardInfo;
    private LinearLayout llQuestionResults;
    private Button btnPlayAgain;
    private Button btnBackToMenu;

    private GameHistory gameHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Result");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get game history from intent
        gameHistory = (GameHistory) getIntent().getSerializableExtra(Constants.EXTRA_GAME_RESULT);

        // Initialize views
        initViews();

        // Display results
        displayResults();

        // Setup click listeners
        setupClickListeners();

        // Setup back press handler
        setupBackPressHandler();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMenu();
            }
        });
    }

    private void initViews() {
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvScore = findViewById(R.id.tvScore);
        tvCardInfo = findViewById(R.id.tvCardInfo);
        llQuestionResults = findViewById(R.id.llQuestionResults);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
    }

    private void displayResults() {
        if (gameHistory == null) {
            tvResultTitle.setText("No data");
            return;
        }

        // Calculate score
        int correct = gameHistory.getCorrectCount();
        int total = gameHistory.getTotalQuestions();
        double percentage = (correct * 100.0) / total;

        // Display title based on score
        if (percentage >= 80) {
            tvResultTitle.setText("🎉 Excellent!");
        } else if (percentage >= 60) {
            tvResultTitle.setText("👏 Nice!");
        } else if (percentage >= 40) {
            tvResultTitle.setText("👍 Good!");
        } else {
            tvResultTitle.setText("💪 Okay!");
        }

        // Display score
        tvScore.setText(String.format("Correct %d/%d câu (%.0f%%)", correct, total, percentage));

        // Display card info
        Card card = gameHistory.getDrawnCard();
        if (card != null) {
            tvCardInfo.setText("Card: " + card.toString());
        }

        // Display question results
        displayQuestionResults();
    }

    private void displayQuestionResults() {
        llQuestionResults.removeAllViews();

        for (GameHistory.QuestionResult result : gameHistory.getResults()) {
            CardView cardView = createResultCard(result);
            llQuestionResults.addView(cardView);
        }
    }

    private CardView createResultCard(GameHistory.QuestionResult result) {
        // Create CardView programmatically
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12);
        cardView.setCardElevation(4);
        cardView.setContentPadding(16, 16, 16, 16);

        // Create content layout
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        // Question number and status
        TextView tvHeader = new TextView(this);
        String status = result.isCorrect() ? "✓" : "✗";
        String statusColor = result.isCorrect() ? "#4CAF50" : "#F44336";
        tvHeader.setText(String.format("%s Question %d", status, result.getQuestionNumber()));
        tvHeader.setTextSize(16);
        tvHeader.setTextColor(android.graphics.Color.parseColor(statusColor));
        tvHeader.setPadding(0, 0, 0, 8);
        contentLayout.addView(tvHeader);

        // Question text
        TextView tvQuestion = new TextView(this);
        tvQuestion.setText(result.getQuestion());
        tvQuestion.setTextSize(14);
        tvQuestion.setTextColor(getResources().getColor(R.color.text_primary));
        tvQuestion.setPadding(0, 0, 0, 4);
        contentLayout.addView(tvQuestion);

        // User answer
        TextView tvUserAnswer = new TextView(this);
        tvUserAnswer.setText("You choose: " + result.getUserAnswer());
        tvUserAnswer.setTextSize(12);
        tvUserAnswer.setTextColor(getResources().getColor(R.color.text_secondary));
        contentLayout.addView(tvUserAnswer);

        // Correct answer (if wrong)
        if (!result.isCorrect()) {
            TextView tvCorrectAnswer = new TextView(this);
            tvCorrectAnswer.setText("Correct answer: " + result.getCorrectAnswer());
            tvCorrectAnswer.setTextSize(12);
            tvCorrectAnswer.setTextColor(getResources().getColor(R.color.success));
            tvCorrectAnswer.setPadding(0, 4, 0, 0);
            contentLayout.addView(tvCorrectAnswer);
        }

        cardView.addView(contentLayout);
        return cardView;
    }

    private void setupClickListeners() {
        btnPlayAgain.setOnClickListener(v -> playAgain());
        btnBackToMenu.setOnClickListener(v -> backToMenu());
    }

    private void playAgain() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void backToMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        backToMenu();
        return true;
    }
}