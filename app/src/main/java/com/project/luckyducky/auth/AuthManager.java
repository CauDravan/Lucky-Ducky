package com.project.luckyducky.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.project.luckyducky.R;
import com.project.luckyducky.data.Models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static AuthManager instance;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;

    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }

    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    // Lấy Intent để mở Google Sign-In
    public Intent getGoogleSignInIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }

    // Xử lý kết quả từ Google Sign-In
    public void handleSignInResult(Intent data, AuthCallback callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account, callback);
            }
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            callback.onFailure("Google Sign-In failed: " + e.getMessage());
        }
    }

    // Sign Firebase với Google credential
    private void firebaseAuthWithGoogle(GoogleSignInAccount account, AuthCallback callback) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = createUserFromFirebase(firebaseUser);
                            callback.onSuccess(user);
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        callback.onFailure("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    // Check xem user đã sign chưa
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    // Lấy user hiện tại
    public User getCurrentUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            return createUserFromFirebase(firebaseUser);
        }
        return null;
    }

    // Log out
    public void signOut(Runnable onComplete) {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    // Convert FirebaseUser sang User model
    private User createUserFromFirebase(FirebaseUser firebaseUser) {
        String photoUrl = firebaseUser.getPhotoUrl() != null ?
                firebaseUser.getPhotoUrl().toString() : "";

        return new User(
            firebaseUser.getUid(),
            firebaseUser.getEmail(),
            firebaseUser.getDisplayName(),
            photoUrl
        );
    }

    // Lấy UID của user hiện tại
    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}