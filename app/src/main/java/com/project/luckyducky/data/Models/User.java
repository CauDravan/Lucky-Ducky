package com.project.luckyducky.data.Models;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;
    private long createdAt;
    private long lastLoginAt;

    // constructor
    public User() {}

    public User(String uid, String email, String displayName, String photoUrl) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.createdAt = System.currentTimeMillis();
        this.lastLoginAt = System.currentTimeMillis();
    }

    // get-set
    public String getUid() {return uid;}
    public void setUid(String uid) {this.uid = uid;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getDisplayName() {return displayName;}
    public void setDisplayName(String displayName) {this.displayName = displayName;}

    public String getPhotoUrl() {return photoUrl;}
    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}

    public long getCreatedAt() {return createdAt;}
    public void setCreatedAt(long createdAt) {this.createdAt = createdAt;}

    public long getLastLoginAt() {return lastLoginAt;}
    public void setLastLoginAt(long lastLoginAt) {this.lastLoginAt = lastLoginAt;}

    public void updateLastLogin() {
        this.lastLoginAt = System.currentTimeMillis();
    }
}
