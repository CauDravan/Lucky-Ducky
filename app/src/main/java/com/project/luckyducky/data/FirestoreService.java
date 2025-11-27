package com.project.luckyducky.data;

import com.project.luckyducky.data.Models.GameHistory;
import com.project.luckyducky.data.Models.Stats;
import com.project.luckyducky.data.Models.User;
import com.project.luckyducky.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FirestoreService {
    private static FirestoreService instance;
    private FirebaseFirestore db;

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnDataLoadListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    private FirestoreService() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreService getInstance() {
        if (instance == null) {
            instance = new FirestoreService();
        }
        return instance;
    }

    // ==================== USER OPERATIONS ====================

    public void saveUser(User user, OnCompleteListener listener) {
        db.collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getUser(String userId, OnDataLoadListener<User> listener) {
        db.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        listener.onSuccess(user);
                    } else {
                        listener.onFailure("User not found");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ==================== STATS OPERATIONS ====================

    public void saveStats(Stats stats, OnCompleteListener listener) {
        db.collection(Constants.COLLECTION_STATS)
                .document(stats.getUserId())
                .set(stats)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getStats(String userId, OnDataLoadListener<Stats> listener) {
        db.collection(Constants.COLLECTION_STATS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Stats stats = documentSnapshot.toObject(Stats.class);
                    if (stats != null) {
                        listener.onSuccess(stats);
                    } else {
                        // Nếu chưa có stats, tạo mới
                        Stats newStats = new Stats(userId);
                        listener.onSuccess(newStats);
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void updateStats(String userId, Stats stats, OnCompleteListener listener) {
        db.collection(Constants.COLLECTION_STATS)
                .document(userId)
                .set(stats)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ==================== GAME HISTORY OPERATIONS ====================

    public void saveGameHistory(GameHistory history, OnCompleteListener listener) {
        db.collection(Constants.COLLECTION_HISTORY)
                .add(history)
                .addOnSuccessListener(documentReference -> {
                    history.setId(documentReference.getId());
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getGameHistory(String userId, OnDataLoadListener<List<GameHistory>> listener) {
        db.collection(Constants.COLLECTION_HISTORY)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50) // Lấy 50 game gần nhất
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GameHistory> historyList = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        GameHistory history = doc.toObject(GameHistory.class);
                        history.setId(doc.getId());
                        historyList.add(history);
                    });
                    listener.onSuccess(historyList);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void deleteGameHistory(String historyId, OnCompleteListener listener) {
        db.collection(Constants.COLLECTION_HISTORY)
                .document(historyId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void clearAllHistory(String userId, OnCompleteListener listener) {
        db.collection(Constants.COLLECTION_HISTORY)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}