package com.project.luckyducky.data;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.luckyducky.data.Models.GameHistory;
import com.project.luckyducky.data.Models.Stats;
import com.project.luckyducky.data.Models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreService {

    private final FirebaseFirestore db;

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_GAME_HISTORY = "game_history";
    private static final String COLLECTION_STATS = "stats";

    public FirestoreService() {
        this.db = FirebaseFirestore.getInstance();
    }

    // ==================== User Management ====================

    public void createUser(User user, OnCompleteListener listener) {
        db.collection(COLLECTION_USERS)
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Also create initial stats
                    createInitialStats(user.getUserId());
                    listener.onSuccess();
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getUser(String userId, OnUserLoadListener listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onUserLoaded(user);
                    } else {
                        listener.onUserLoaded(null);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void updateUser(User user, OnCompleteListener listener) {
        db.collection(COLLECTION_USERS)
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // ==================== Game History ====================

    public void saveGameHistory(GameHistory history, OnCompleteListener listener) {
        db.collection(COLLECTION_GAME_HISTORY)
                .add(history)
                .addOnSuccessListener(documentReference -> {
                    history.setId(documentReference.getId());
                    listener.onSuccess();
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getGameHistory(String userId, int limit, OnHistoryLoadListener listener) {
        db.collection(COLLECTION_GAME_HISTORY)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GameHistory> historyList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        GameHistory history = doc.toObject(GameHistory.class);
                        if (history != null) {
                            history.setId(doc.getId());
                            historyList.add(history);
                        }
                    }
                    listener.onHistoryLoaded(historyList);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllGameHistory(String userId, OnHistoryLoadListener listener) {
        db.collection(COLLECTION_GAME_HISTORY)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GameHistory> historyList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        GameHistory history = doc.toObject(GameHistory.class);
                        if (history != null) {
                            history.setId(doc.getId());
                            historyList.add(history);
                        }
                    }
                    listener.onHistoryLoaded(historyList);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteGameHistory(String historyId, OnCompleteListener listener) {
        db.collection(COLLECTION_GAME_HISTORY)
                .document(historyId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // ==================== Stats Management ====================

    private void createInitialStats(String userId) {
        Stats stats = new Stats(userId);
        db.collection(COLLECTION_STATS)
                .document(userId)
                .set(stats);
    }

    public void getStats(String userId, OnStatsLoadListener listener) {
        db.collection(COLLECTION_STATS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Stats stats = documentSnapshot.toObject(Stats.class);
                        listener.onStatsLoaded(stats);
                    } else {
                        // Create initial stats if not exists
                        Stats newStats = new Stats(userId);
                        createInitialStats(userId);
                        listener.onStatsLoaded(newStats);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void updateStats(String userId, List<GameHistory.QuestionResult> questionResults) {
        db.collection(COLLECTION_STATS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Stats stats;
                    if (documentSnapshot.exists()) {
                        stats = documentSnapshot.toObject(Stats.class);
                    } else {
                        stats = new Stats(userId);
                    }

                    if (stats != null) {
                        // Update overall stats
                        stats.setTotalGamesPlayed(stats.getTotalGamesPlayed() + 1);
                        stats.setTotalQuestionsAnswered(stats.getTotalQuestionsAnswered() + questionResults.size());

                        int correctInThisGame = 0;
                        int wrongInThisGame = 0;

                        // Update per-question stats
                        Map<String, Stats.QuestionStats> questionStatsMap = stats.getQuestionStats();

                        for (GameHistory.QuestionResult result : questionResults) {
                            String key = getQuestionKey(result.getQuestionNumber());
                            Stats.QuestionStats qStats = questionStatsMap.get(key);

                            if (qStats == null) {
                                qStats = new Stats.QuestionStats(getQuestionName(result.getQuestionNumber()));
                                questionStatsMap.put(key, qStats);
                            }

                            qStats.setTimesAnswered(qStats.getTimesAnswered() + 1);

                            if (result.isCorrect()) {
                                qStats.setTimesCorrect(qStats.getTimesCorrect() + 1);
                                correctInThisGame++;
                            } else {
                                qStats.setTimesWrong(qStats.getTimesWrong() + 1);
                                wrongInThisGame++;
                            }
                        }

                        stats.setTotalCorrectAnswers(stats.getTotalCorrectAnswers() + correctInThisGame);
                        stats.setTotalWrongAnswers(stats.getTotalWrongAnswers() + wrongInThisGame);
                        stats.setQuestionStats(questionStatsMap);

                        // Save updated stats
                        db.collection(COLLECTION_STATS)
                                .document(userId)
                                .set(stats);
                    }
                });
    }

    private String getQuestionKey(int questionNumber) {
        switch (questionNumber) {
            case 1: return "q1_color";
            case 2: return "q2_higher_lower";
            case 3: return "q3_inside_outside";
            case 4: return "q4_suit";
            case 5: return "q5_odd_even";
            case 6: return "q6_face_number";
            case 7: return "q7_rank_prediction";
            default: return "q" + questionNumber;
        }
    }

    private String getQuestionName(int questionNumber) {
        switch (questionNumber) {
            case 1: return "Color";
            case 2: return "Higher/Lower";
            case 3: return "Inside/Outside";
            case 4: return "Suit";
            case 5: return "Odd/Even";
            case 6: return "Face/Number";
            case 7: return "Rank Prediction";
            default: return "Question " + questionNumber;
        }
    }

    public void resetStats(String userId, OnCompleteListener listener) {
        Stats newStats = new Stats(userId);
        db.collection(COLLECTION_STATS)
                .document(userId)
                .set(newStats)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // ==================== Listeners ====================

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnUserLoadListener {
        void onUserLoaded(User user);
        void onFailure(Exception e);
    }

    public interface OnHistoryLoadListener {
        void onHistoryLoaded(List<GameHistory> historyList);
        void onFailure(Exception e);
    }

    public interface OnStatsLoadListener {
        void onStatsLoaded(Stats stats);
        void onFailure(Exception e);
    }
}