package com.project.luckyducky.data.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.luckyducky.R;
import com.project.luckyducky.data.Models.Card;
import com.project.luckyducky.data.Models.GameHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<GameHistory> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClick(GameHistory history);
    }

    public HistoryAdapter(List<GameHistory> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        GameHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<GameHistory> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvCard;
        private TextView tvScore;
        private TextView tvPercentage;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCard = itemView.findViewById(R.id.tvCard);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvPercentage = itemView.findViewById(R.id.tvPercentage);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onHistoryClick(historyList.get(position));
                }
            });
        }

        public void bind(GameHistory history) {
            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateStr = dateFormat.format(new Date(history.getTimestamp()));
            tvDate.setText(dateStr);

            // Display card
            Card card = history.getDrawnCard();
            if (card != null) {
                tvCard.setText("Card: " + card.toString());

                // Set card color
                if (card.isRed()) {
                    tvCard.setTextColor(itemView.getContext().getResources().getColor(R.color.card_red));
                } else {
                    tvCard.setTextColor(itemView.getContext().getResources().getColor(R.color.card_black));
                }
            }

            // Display score
            int correct = history.getCorrectCount();
            int total = history.getTotalQuestions();
            tvScore.setText(String.format("Result: %d/%d correct", correct, total));

            // Display percentage with color
            double percentage = (correct * 100.0) / total;
            tvPercentage.setText(String.format("%.0f%%", percentage));

            int percentageColor;
            if (percentage >= 70) {
                percentageColor = R.color.success;
            } else if (percentage >= 50) {
                percentageColor = R.color.warning;
            } else {
                percentageColor = R.color.error;
            }
            tvPercentage.setTextColor(itemView.getContext().getResources().getColor(percentageColor));
        }
    }
}