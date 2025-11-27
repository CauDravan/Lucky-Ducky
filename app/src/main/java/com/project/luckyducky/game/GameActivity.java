package com.project.luckyducky.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.luckyducky.R;
import com.project.luckyducky.auth.AuthManager;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.Card;
import com.project.luckyducky.data.Models.GameHistory;
import com.project.luckyducky.data.Models.Stats;
import com.project.luckyducky.utils.Constants;

public class GameActivity extends AppCompatActivity {

    private CardManager cardManager;
    private QuestionManager questionManager;
    private AuthManager authManager;
    private FirestoreService firestoreService;

    // UI Components
    private ImageView imgCard;
    private TextView tvQuestionNumber;
    private TextView tvQuestionText;
    private TextView tvHint;
    private RadioGroup rgOptions;
    private Button btnSubmit;
    private ProgressBar progressBar;

    // Game State
    private Card drawnCard;
    private GameHistory gameHistory;
    private Stats userStats;
    private boolean isAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lucky Ducky Game");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize managers
        cardManager = new CardManager();
        questionManager = new QuestionManager(cardManager);
        authManager = AuthManager.getInstance(this);
        firestoreService = FirestoreService.getInstance();

        // Initialize views
        initViews();

        // Load user stats
        loadUserStats();

        // Start game
        startNewGame();
    }

    private void initViews() {
        imgCard = findViewById(R.id.imgCard);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        tvHint = findViewById(R.id.tvHint);
        rgOptions = findViewById(R.id.rgOptions);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        btnSubmit.setOnClickListener(v -> handleSubmit());

        // Setup back press handler
        setupBackPressHandler();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure? Progress will not save")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setNegativeButton("Continue playing", null)
                .show();
    }

    private void loadUserStats() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            firestoreService.getStats(userId, new FirestoreService.OnDataLoadListener<Stats>() {
                @Override
                public void onSuccess(Stats stats) {
                    userStats = stats;
                }

                @Override
                public void onFailure(String error) {
                    userStats = new Stats(userId);
                }
            });
        }
    }

    private void startNewGame() {
        // Draw a random card
        drawnCard = cardManager.drawCard();

        // Initialize game history
        String userId = authManager.getCurrentUserId();
        gameHistory = new GameHistory(userId, drawnCard);

        // Show card back
        imgCard.setImageResource(R.drawable.ic_card_back);

        // Display first question
        displayCurrentQuestion();
    }

    private void displayCurrentQuestion() {
        isAnswered = false;

        // Update question number
        int current = questionManager.getCurrentQuestionNumber();
        int total = questionManager.getTotalQuestions();
        tvQuestionNumber.setText(String.format("Questions %d/%d", current, total));

        // Update question text
        tvQuestionText.setText(questionManager.getCurrentQuestionText());

        // Update hint
        tvHint.setText(questionManager.getQuestionHint());

        // Setup options
        setupOptions();

        // Update button
        if (questionManager.isLastQuestion()) {
            btnSubmit.setText("Finish");
        } else {
            btnSubmit.setText("Next");
        }

        btnSubmit.setEnabled(false);
    }

    private void setupOptions() {
        rgOptions.removeAllViews();
        rgOptions.clearCheck();

        String[] options = questionManager.getCurrentQuestionOptions();

        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTextSize(16);
            radioButton.setPadding(16, 16, 16, 16);
            rgOptions.addView(radioButton);
        }

        // Enable submit when option is selected
        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            btnSubmit.setEnabled(true);
        });
    }

    private void handleSubmit() {
        if (!isAnswered) {
            // Get selected answer
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please choose the answer", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedId);
            String userAnswer = selectedRadioButton.getText().toString();

            // Validate answer
            boolean isCorrect = questionManager.validateAnswer(userAnswer, drawnCard);
            String correctAnswer = questionManager.getCorrectAnswer(drawnCard);

            // Record result
            GameHistory.QuestionResult result = new GameHistory.QuestionResult(
                    questionManager.getCurrentQuestionNumber(),
                    questionManager.getCurrentQuestionText(),
                    userAnswer,
                    correctAnswer,
                    isCorrect
            );
            gameHistory.addResult(result);

            // Update stats
            if (userStats != null) {
                userStats.recordAnswer(questionManager.getCurrentQuestionNumber(), isCorrect);
            }

            // Show feedback
            showAnswerFeedback(isCorrect, correctAnswer);

            isAnswered = true;

        } else {
            // Move to next question or finish
            if (questionManager.hasMoreQuestions()) {
                questionManager.nextQuestion();
                displayCurrentQuestion();
            } else {
                finishGame();
            }
        }
    }

    private void showAnswerFeedback(boolean isCorrect, String correctAnswer) {
        if (isCorrect) {
            Toast.makeText(this, "✓ Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "✗ Wrong! Answer: " + correctAnswer, Toast.LENGTH_LONG).show();
        }
    }

    private void finishGame() {
        showLoading(true);

        // Save game history
        firestoreService.saveGameHistory(gameHistory, new FirestoreService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                // Update stats
                if (userStats != null) {
                    userStats.incrementTotalGames();
                    saveStats();
                } else {
                    navigateToResult();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(GameActivity.this,
                        "Error save history: " + error,
                        Toast.LENGTH_SHORT).show();
                navigateToResult();
            }
        });
    }

    private void saveStats() {
        String userId = authManager.getCurrentUserId();
        if (userId != null && userStats != null) {
            firestoreService.updateStats(userId, userStats, new FirestoreService.OnCompleteListener() {
                @Override
                public void onSuccess() {
                    navigateToResult();
                }

                @Override
                public void onFailure(String error) {
                    navigateToResult();
                }
            });
        } else {
            navigateToResult();
        }
    }

    private void navigateToResult() {
        showLoading(false);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.EXTRA_GAME_RESULT, gameHistory);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        showExitDialog();
        return true;
    }
}