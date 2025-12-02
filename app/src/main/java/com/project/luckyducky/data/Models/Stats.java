package com.project.luckyducky.data.Models;

import java.util.HashMap;
import java.util.Map;

public class Stats {
    private String userId;
    private int totalGamesPlayed;
    private int totalQuestionsAnswered;
    private int totalCorrectAnswers;
    private int totalWrongAnswers;
    private Map<String, QuestionStats> questionStats;

    public Stats() {
        // Required empty constructor for Firestore
        this.questionStats = new HashMap<>();
    }

    public Stats(String userId) {
        this.userId = userId;
        this.totalGamesPlayed = 0;
        this.totalQuestionsAnswered = 0;
        this.totalCorrectAnswers = 0;
        this.totalWrongAnswers = 0;
        this.questionStats = new HashMap<>();

        // Initialize stats for each question type
        initializeQuestionStats();
    }

    private void initializeQuestionStats() {
        questionStats.put("q1_color", new QuestionStats("Color"));
        questionStats.put("q2_higher_lower", new QuestionStats("Higher/Lower"));
        questionStats.put("q3_inside_outside", new QuestionStats("Inside/Outside"));
        questionStats.put("q4_suit", new QuestionStats("Suit"));
        questionStats.put("q5_odd_even", new QuestionStats("Odd/Even"));
        questionStats.put("q6_face_number", new QuestionStats("Face/Number"));
        questionStats.put("q7_rank_prediction", new QuestionStats("Rank Prediction"));
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public int getTotalQuestionsAnswered() {
        return totalQuestionsAnswered;
    }

    public void setTotalQuestionsAnswered(int totalQuestionsAnswered) {
        this.totalQuestionsAnswered = totalQuestionsAnswered;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public int getTotalWrongAnswers() {
        return totalWrongAnswers;
    }

    public void setTotalWrongAnswers(int totalWrongAnswers) {
        this.totalWrongAnswers = totalWrongAnswers;
    }

    public Map<String, QuestionStats> getQuestionStats() {
        return questionStats;
    }

    public void setQuestionStats(Map<String, QuestionStats> questionStats) {
        this.questionStats = questionStats;
    }

    public double getAccuracyRate() {
        if (totalQuestionsAnswered == 0) return 0.0;
        return (double) totalCorrectAnswers / totalQuestionsAnswered * 100;
    }

    public static class QuestionStats {
        private String questionName;
        private int timesAnswered;
        private int timesCorrect;
        private int timesWrong;

        public QuestionStats() {
            // Required empty constructor
        }

        public QuestionStats(String questionName) {
            this.questionName = questionName;
            this.timesAnswered = 0;
            this.timesCorrect = 0;
            this.timesWrong = 0;
        }

        // Getters and Setters
        public String getQuestionName() {
            return questionName;
        }

        public void setQuestionName(String questionName) {
            this.questionName = questionName;
        }

        public int getTimesAnswered() {
            return timesAnswered;
        }

        public void setTimesAnswered(int timesAnswered) {
            this.timesAnswered = timesAnswered;
        }

        public int getTimesCorrect() {
            return timesCorrect;
        }

        public void setTimesCorrect(int timesCorrect) {
            this.timesCorrect = timesCorrect;
        }

        public int getTimesWrong() {
            return timesWrong;
        }

        public void setTimesWrong(int timesWrong) {
            this.timesWrong = timesWrong;
        }

        public double getAccuracy() {
            if (timesAnswered == 0) return 0.0;
            return (double) timesCorrect / timesAnswered * 100;
        }
    }
}