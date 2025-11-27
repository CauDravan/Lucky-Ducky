package com.project.luckyducky.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.luckyducky.R;
import com.project.luckyducky.auth.AuthManager;
import com.project.luckyducky.data.Adapter.HistoryAdapter;
import com.project.luckyducky.data.FirestoreService;
import com.project.luckyducky.data.Models.GameHistory;
import com.project.luckyducky.game.ResultActivity;
import com.project.luckyducky.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryClickListener {

    private AuthManager authManager;
    private FirestoreService firestoreService;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private HistoryAdapter adapter;
    private List<GameHistory> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize managers
        authManager = AuthManager.getInstance(this);
        firestoreService = FirestoreService.getInstance();

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load history
        loadHistory();

        // Setup back press
        setupBackPressHandler();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadHistory() {
        showLoading(true);

        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Not found user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestoreService.getGameHistory(userId, new FirestoreService.OnDataLoadListener<List<GameHistory>>() {
            @Override
            public void onSuccess(List<GameHistory> data) {
                historyList = data;
                adapter.updateData(historyList);
                showLoading(false);
                updateEmptyState();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(HistoryActivity.this,
                        "Error load history: " + error,
                        Toast.LENGTH_SHORT).show();
                showLoading(false);
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (historyList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void onHistoryClick(GameHistory history) {
        // Navigate to ResultActivity to show details
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.EXTRA_GAME_RESULT, history);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear_history) {
            showClearHistoryDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showClearHistoryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete History")
                .setMessage("Are you sure want to delete history?\nThis action can't be undo")
                .setPositiveButton("Delete", (dialog, which) -> clearHistory())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearHistory() {
        showLoading(true);

        String userId = authManager.getCurrentUserId();
        if (userId == null) return;

        firestoreService.clearAllHistory(userId, new FirestoreService.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(HistoryActivity.this,
                        "Deleted history",
                        Toast.LENGTH_SHORT).show();
                historyList.clear();
                adapter.updateData(historyList);
                showLoading(false);
                updateEmptyState();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(HistoryActivity.this,
                        "Error delete history: " + error,
                        Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}