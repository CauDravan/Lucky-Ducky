package com.project.luckyducky.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.luckyducky.R;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.Card;
import com.project.luckyducky.data.Models.GameHistory;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private QuestionManager questionManager;
    private CardManager cardManager;
    private FirestoreService firestoreService;
    private com.project.luckyducky.utils.SoundManager soundManager;

    private ImageView ivCurrentCard, ivPreviousCard, ivFirstCard;
    private TextView tvQuestion, tvCardsRemaining, tvQuestionNumber;
    private TextView tvScore, tvCorrect, tvWrong;
    private LinearLayout llAnswerButtons;

    private int correctCount = 0;
    private int wrongCount = 0;
    private Card firstCard; // For Q3
    private List<GameHistory.QuestionResult> questionResults;

    private static final int TOTAL_QUESTIONS = 7;
    private boolean isAnswering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeViews();
        initializeGame();
        startGame();
    }

    private void initializeViews() {
        ivCurrentCard = findViewById(R.id.ivCurrentCard);
        ivPreviousCard = findViewById(R.id.ivPreviousCard);
        ivFirstCard = findViewById(R.id.ivFirstCard);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvCardsRemaining = findViewById(R.id.tvCardsRemaining);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvScore = findViewById(R.id.tvScore);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);

        llAnswerButtons = findViewById(R.id.llAnswerButtons);
    }

    private void initializeGame() {
        questionManager = new QuestionManager();
        cardManager = new CardManager(this);
        firestoreService = new FirestoreService();
        soundManager = com.project.luckyducky.utils.SoundManager.getInstance(this);
        questionResults = new ArrayList<>();

        correctCount = 0;
        wrongCount = 0;
    }

    private void startGame() {
        updateUI();
        showQuestion();
    }

    private void showQuestion() {
        int questionNum = questionManager.getCurrentQuestionNumber();

        if (questionNum > TOTAL_QUESTIONS) {
            endGame();
            return;
        }

        tvQuestionNumber.setText("Question " + questionNum + "/" + TOTAL_QUESTIONS);
        tvQuestion.setText(questionManager.getQuestionText(questionNum));

        llAnswerButtons.removeAllViews();

        switch (questionNum) {
            case 1:
                showColorButtons();
                break;
            case 2:
                showHigherLowerButtons();
                break;
            case 3:
                showInsideOutsideButtons();
                break;
            case 4:
                showSuitButtons();
                break;
            case 5:
                showOddEvenButtons();
                break;
            case 6:
                showFaceNumberButtons();
                break;
            case 7:
                showRankButtons();
                break;
        }
    }

    private void showColorButtons() {
        addAnswerButton("Red", "red");
        addAnswerButton("Black", "black");
    }

    private void showHigherLowerButtons() {
        addAnswerButton("Higher", "higher");
        addAnswerButton("Lower", "lower");
    }

    private void showInsideOutsideButtons() {
        addAnswerButton("Inside", "inside");
        addAnswerButton("Outside", "outside");
    }

    private void showSuitButtons() {
        addAnswerButton("♥ Hearts", "hearts");
        addAnswerButton("♦ Diamonds", "diamonds");
        addAnswerButton("♣ Clubs", "clubs");
        addAnswerButton("♠ Spades", "spades");
    }

    private void showOddEvenButtons() {
        addAnswerButton("Odd", "odd");
        addAnswerButton("Even", "even");
    }

    private void showFaceNumberButtons() {
        addAnswerButton("Face Card", "face");
        addAnswerButton("Number Card", "number");
    }

    private void showRankButtons() {
        // Create 13 buttons for all ranks
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        // Create rows of buttons (4-4-5 layout)
        LinearLayout row1 = createButtonRow();
        LinearLayout row2 = createButtonRow();
        LinearLayout row3 = createButtonRow();

        for (int i = 0; i < ranks.length; i++) {
            Button button = createRankButton(ranks[i]);

            if (i < 4) {
                row1.addView(button);
            } else if (i < 8) {
                row2.addView(button);
            } else {
                row3.addView(button);
            }
        }

        llAnswerButtons.addView(row1);
        llAnswerButtons.addView(row2);
        llAnswerButtons.addView(row3);
    }

    private LinearLayout createButtonRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(params);
        return row;
    }

    private Button createRankButton(String rank) {
        Button button = new Button(this);
        button.setText(rank);
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(16);
        button.setBackground(getResources().getDrawable(R.drawable.btn_answer));
        button.setOnClickListener(v -> handleAnswer(rank));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        params.setMargins(4, 4, 4, 4);
        button.setLayoutParams(params);
        button.setPadding(8, 20, 8, 20);

        return button;
    }

    private void addAnswerButton(String displayText, String answer) {
        Button button = new Button(this);
        button.setText(displayText);
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(18);
        button.setBackground(getResources().getDrawable(R.drawable.btn_answer));
        button.setOnClickListener(v -> handleAnswer(answer));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        button.setLayoutParams(params);
        button.setPadding(32, 24, 32, 24);

        llAnswerButtons.addView(button);
    }

    private void handleAnswer(String answer) {
        if (isAnswering) return; // ⭐ chặn spam click trước khi animation xong
        isAnswering = true;

        llAnswerButtons.setEnabled(false);

        // Play button click sound
        soundManager.playButtonClickSound();

        Card drawnCard = questionManager.drawCard();

        boolean isCorrect = checkAnswer(answer);

        if (isCorrect) {
            correctCount++;
        } else {
            wrongCount++;
        }

        // Save question result
        questionResults.add(new GameHistory.QuestionResult(
                questionManager.getCurrentQuestionNumber(),
                questionManager.getQuestionType(questionManager.getCurrentQuestionNumber()),
                answer,
                isCorrect,
                drawnCard.toString()
        ));

        // Save first card for Q3
        if (questionManager.getCurrentQuestionNumber() == 1) {
            firstCard = drawnCard;
        }

        showCardWithAnimation(drawnCard, isCorrect);
    }

    private boolean checkAnswer(String answer) {
        int questionNum = questionManager.getCurrentQuestionNumber();

        switch (questionNum) {
            case 1:
                return questionManager.checkColorAnswer(answer);
            case 2:
                return questionManager.checkHigherLowerAnswer(answer);
            case 3:
                return questionManager.checkInsideOutsideAnswer(answer, firstCard);
            case 4:
                return questionManager.checkSuitAnswer(answer);
            case 5:
                return questionManager.checkOddEvenAnswer(answer);
            case 6:
                return questionManager.checkFaceNumberAnswer(answer);
            case 7:
                return questionManager.checkRankPredictionAnswer(answer);
            default:
                return false;
        }
    }

    private void showCardWithAnimation(Card card, boolean isCorrect) {
        if (isFinishing() || isDestroyed()) return; // ⭐ guard

        ivCurrentCard.setImageDrawable(cardManager.getCardBackDrawable());

        // Play card flip sound
        soundManager.playCardFlipSound();

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(ivCurrentCard, "rotationY", 0f, 90f);
        flipOut.setDuration(200);

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFinishing() || isDestroyed()) return; // ⭐ guard

                ivCurrentCard.setImageDrawable(cardManager.getCardDrawable(card));

                ObjectAnimator flipIn = ObjectAnimator.ofFloat(ivCurrentCard, "rotationY", -90f, 0f);
                flipIn.setDuration(200);
                flipIn.start();

                // Play result sound after card is revealed
                flipIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isFinishing() || isDestroyed()) return; // ⭐ guard

                        if (isCorrect) {
                            soundManager.playCorrectSound();
                        } else {
                            soundManager.playWrongSound();
                        }
                        showResult(isCorrect);
                    }
                });
            }
        });

        flipOut.start();
    }

    private void showResult(boolean isCorrect) {
        if (isFinishing() || isDestroyed()) return; // ⭐ guard

        String message = isCorrect ? "Correct! ✓" : "Wrong! ✗";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Wait before moving to next question
        ivCurrentCard.postDelayed(() -> {
            // Move current card to previous position
            if (isFinishing() || isDestroyed()) return; // ⭐ guard

            Card justDrawnCard = questionManager.getCurrentCard();

            // Update all card displays BEFORE moving to next question
            if (questionManager.getPreviousCard() != null) {
                ivFirstCard.setImageDrawable(
                        cardManager.getCardDrawable(questionManager.getPreviousCard())
                );
            }

            if (justDrawnCard != null) {
                ivPreviousCard.setImageDrawable(
                        cardManager.getCardDrawable(justDrawnCard)
                );
            }

            // Reset current card to back
            ivCurrentCard.setImageDrawable(cardManager.getCardBackDrawable());

            // Move to next question
            questionManager.nextQuestion();
            llAnswerButtons.setEnabled(true);
            isAnswering = false; // ⭐ cho phép user trả lời câu tiếp theo
            updateUI();
            showQuestion();
        }, 1500);
    }

    private void updateUI() {
        tvCardsRemaining.setText("Cards: " + questionManager.getRemainingCards());
        tvScore.setText("Score: " + correctCount + "/" +
                (correctCount + wrongCount));
        tvCorrect.setText("✓ " + correctCount);
        tvWrong.setText("✗ " + wrongCount);
    }

    private void endGame() {
        saveGameData();

        // Navigate to ResultActivity with actual results
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("correctCount", correctCount);
        intent.putExtra("wrongCount", wrongCount);
        intent.putExtra("totalQuestions", TOTAL_QUESTIONS);

        // Pass question results as ArrayList
        ArrayList<String> resultsData = new ArrayList<>();
        for (GameHistory.QuestionResult result : questionResults) {
            resultsData.add(result.getQuestionNumber() + ":" + result.isCorrect());
        }
        intent.putStringArrayListExtra("questionResults", resultsData);

        startActivity(intent);
        finish();
    }

    private void saveGameData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            GameHistory history = new GameHistory(
                    user.getUid(),
                    TOTAL_QUESTIONS,
                    correctCount,
                    wrongCount,
                    questionResults
            );

            firestoreService.saveGameHistory(history, new FirestoreService.OnCompleteListener() {
                @Override
                public void onSuccess() {
                    // Update stats
                    firestoreService.updateStats(user.getUid(), questionResults);

                    // Update user's best score and games played
                    updateUserProgress(user.getUid());
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(GameActivity.this,
                            "Failed to save game: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUserProgress(String userId) {
        firestoreService.getUser(userId, new FirestoreService.OnUserLoadListener() {
            @Override
            public void onUserLoaded(com.project.luckyducky.data.Models.User user) {
                if (user != null) {
                    user.incrementGamesPlayed();
                    user.updateBestScore(correctCount);
                    firestoreService.updateUser(user, new FirestoreService.OnCompleteListener() {
                        @Override
                        public void onSuccess() {
                            // User updated successfully
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Ignore failure
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Ignore failure
            }
        });
    }

    private void restartGame() {
        questionManager.reset();
        initializeGame();
        startGame();
    }

    private boolean isActivityDead() {
        return isFinishing() || isDestroyed();
    }

}