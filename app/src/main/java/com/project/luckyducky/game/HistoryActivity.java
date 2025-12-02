package com.project.luckyducky.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.luckyducky.R;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.GameHistory;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends com.project.luckyducky.BaseActivity {

    private static final int MAX_HISTORY_ITEMS = 20;

    private FirestoreService firestoreService;

    private LinearLayout llHistoryList;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();
        firestoreService = new FirestoreService();
        loadGameHistory();
    }

    private void initializeViews() {
        llHistoryList = findViewById(R.id.llHistoryList);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadGameHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        llHistoryList.setVisibility(View.GONE);
        tvEmptyMessage.setVisibility(View.GONE);

        // Load only last 20 games
        firestoreService.getGameHistory(user.getUid(), MAX_HISTORY_ITEMS,
                new FirestoreService.OnHistoryLoadListener() {
                    @Override
                    public void onHistoryLoaded(List<GameHistory> historyList) {
                        progressBar.setVisibility(View.GONE);

                        if (historyList.isEmpty()) {
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                        } else {
                            llHistoryList.setVisibility(View.VISIBLE);
                            displayHistory(historyList);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HistoryActivity.this,
                                "Failed to load history: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayHistory(List<GameHistory> historyList) {
        llHistoryList.removeAllViews();

        for (GameHistory history : historyList) {
            addHistoryItemView(history);
        }
    }

    private void addHistoryItemView(GameHistory history) {
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_game_history, llHistoryList, false);

        TextView tvDate = itemView.findViewById(R.id.tvDate);
        TextView tvScore = itemView.findViewById(R.id.tvScore);
        TextView tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
        TextView tvCorrectWrong = itemView.findViewById(R.id.tvCorrectWrong);
        CardView cardView = itemView.findViewById(R.id.cardHistory);

        // Format date - handle null timestamp
        String dateStr;
        if (history.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            dateStr = sdf.format(history.getTimestamp().toDate());
        } else {
            dateStr = "Unknown date";
        }
        tvDate.setText(dateStr);

        // Display score
        tvScore.setText(history.getCorrectAnswers() + "/" + history.getTotalQuestions());

        // Calculate and display accuracy
        double accuracy = history.getTotalQuestions() > 0 ?
                (double) history.getCorrectAnswers() / history.getTotalQuestions() * 100 : 0;
        DecimalFormat df = new DecimalFormat("#.#");
        tvAccuracy.setText(df.format(accuracy) + "%");

        // Display correct/wrong
        tvCorrectWrong.setText("✓ " + history.getCorrectAnswers() +
                "  ✗ " + history.getWrongAnswers());

        // Set card background based on accuracy
        int colorResId;
        if (accuracy >= 75) {
            colorResId = R.color.excellent_stat;
        } else if (accuracy >= 50) {
            colorResId = R.color.good_stat;
        } else {
            colorResId = R.color.poor_stat;
        }
        cardView.setCardBackgroundColor(getResources().getColor(colorResId));

        // Click to view details
        itemView.setOnClickListener(v -> showHistoryDetails(history));

        llHistoryList.addView(itemView);
    }

    private void showHistoryDetails(GameHistory history) {
        // Create dialog showing detailed results
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_history_details, null);

        LinearLayout llDetails = dialogView.findViewById(R.id.llQuestionDetails);

        List<GameHistory.QuestionResult> results = history.getQuestionResults();
        if (results != null) {
            for (GameHistory.QuestionResult result : results) {
                addQuestionDetailView(llDetails, result);
            }
        }

        builder.setView(dialogView)
                .setTitle("Game Details")
                .setPositiveButton("Close", null)
                .show();
    }

    private void addQuestionDetailView(LinearLayout container,
                                       GameHistory.QuestionResult result) {
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_history_detail, container, false);

        TextView tvQuestion = itemView.findViewById(R.id.tvQuestionDetail);
        TextView tvAnswer = itemView.findViewById(R.id.tvAnswerDetail);
        TextView tvCard = itemView.findViewById(R.id.tvCardDetail);
        TextView tvResult = itemView.findViewById(R.id.tvResultDetail);

        tvQuestion.setText("Q" + result.getQuestionNumber() + ": " +
                formatQuestionType(result.getQuestionType()));
        tvAnswer.setText("Your answer: " + result.getUserAnswer());
        tvCard.setText("Card drawn: " + result.getCardDrawn());

        String resultText = result.isCorrect() ? "✓ Correct" : "✗ Wrong";
        int colorResId = result.isCorrect() ?
                R.color.correct_answer : R.color.wrong_answer;

        tvResult.setText(resultText);
        tvResult.setTextColor(getResources().getColor(colorResId));

        container.addView(itemView);
    }

    private String formatQuestionType(String type) {
        switch (type) {
            case "color": return "Red or Black";
            case "higher_lower": return "Higher or Lower";
            case "inside_outside": return "Inside or Outside";
            case "suit": return "Suit";
            case "odd_even": return "Odd or Even";
            case "face_number": return "Face or Number";
            case "rank_prediction": return "Rank Prediction";
            default: return type;
        }
    }
}