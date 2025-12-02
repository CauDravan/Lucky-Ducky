package com.project.luckyducky.auth;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
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
import com.project.luckyducky.R;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.User;

public class AuthManager {

    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;
    private final FirestoreService firestoreService;
    private final Activity activity;

    public static final int RC_SIGN_IN = 9001; // Deprecated - use ActivityResultLauncher instead

    public AuthManager(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestoreService = new FirestoreService();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    // Get current user
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Check if user is logged in
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    // Start Google Sign In
    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    // Handle sign in result
    public void handleSignInResult(Intent data, OnAuthCompleteListener listener) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account, listener);
            }
        } catch (ApiException e) {
            listener.onFailure(e);
        }
    }

    // Firebase auth with Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount account, OnAuthCompleteListener listener) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Check if user exists in Firestore
                            checkAndCreateUser(firebaseUser, listener);
                        }
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    // Check if user exists, create if not
    private void checkAndCreateUser(FirebaseUser firebaseUser, OnAuthCompleteListener listener) {
        firestoreService.getUser(firebaseUser.getUid(), new FirestoreService.OnUserLoadListener() {
            @Override
            public void onUserLoaded(User user) {
                if (user == null) {
                    // User doesn't exist, create new
                    createNewUser(firebaseUser, listener);
                } else {
                    // User exists, update last login
                    user.updateLastLogin();
                    firestoreService.updateUser(user, new FirestoreService.OnCompleteListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess(user);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Still allow login even if update fails
                            listener.onSuccess(user);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                // If can't check, try to create new user anyway
                createNewUser(firebaseUser, listener);
            }
        });
    }

    // Create new user in Firestore
    private void createNewUser(FirebaseUser firebaseUser, OnAuthCompleteListener listener) {
        User newUser = new User(
                firebaseUser.getUid(),
                firebaseUser.getEmail(),
                firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null
        );

        firestoreService.createUser(newUser, new FirestoreService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(newUser);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    // Update user info
    public void updateUserInfo(User user, OnAuthCompleteListener listener) {
        firestoreService.updateUser(user, new FirestoreService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(user);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    // Sign out
    public void signOut(OnSignOutListener listener) {
        firebaseAuth.signOut();
        googleSignInClient.signOut()
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        listener.onSignOutSuccess();
                    } else {
                        listener.onSignOutFailure(task.getException());
                    }
                });
    }

    // Listeners
    public interface OnAuthCompleteListener {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface OnSignOutListener {
        void onSignOutSuccess();
        void onSignOutFailure(Exception e);
    }
}