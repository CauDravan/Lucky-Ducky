package com.project.luckyducky.data.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class User {
    private String userId;
    private String email;
    private String displayName;
    private String photoUrl;

    @ServerTimestamp
    private Timestamp createdAt;

    @ServerTimestamp
    private Timestamp lastLogin;

    private int totalGamesPlayed;
    private int bestScore;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String userId, String email, String displayName, String photoUrl) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        // timestamps will be set by @ServerTimestamp
        this.totalGamesPlayed = 0;
        this.bestScore = 0;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public void updateLastLogin() {
        this.lastLogin = Timestamp.now();
    }

    public void incrementGamesPlayed() {
        this.totalGamesPlayed++;
    }

    public void updateBestScore(int newScore) {
        if (newScore > this.bestScore) {
            this.bestScore = newScore;
        }
    }
}