package com.project.luckyducky.data.Models;

import java.util.HashMap;
import java.util.Map;

public class Stats {
    private String userId;
    private int totalGames;
    private Map<Integer, QuestionStats> questionStats;

    public static class QuestionStats {
        private int questionNumber;
        private int correctCount;
        private int incorrectCount;

        public QuestionStats() {}

        public QuestionStats(int questionNumber) {
            this.questionNumber = questionNumber;
            this.correctCount = 0;
            this.incorrectCount = 0;
        }

        public void addCorrect() {correctCount++;}
        public void addIncorrect() {incorrectCount++;}

        public double getWinRate() {
            int total = correctCount + incorrectCount;
            return total == 0 ? 0 : (double) correctCount / total * 100;
        }

        // get-set QuestionStats
        public int getQuestionNumber() {return questionNumber;}
        public void setQuestionNumber(int questionNumber) {this.questionNumber = questionNumber;}

        public int getCorrectCount() {return correctCount;}
        public void setCorrectCount(int correctCount) {this.correctCount = correctCount;}

        public int getIncorrectCount() {return incorrectCount;}
        public void setIncorrectCount(int incorrectCount) {this.incorrectCount = incorrectCount;}
    }

    // constructor firestore
    public Stats() {
        this.questionStats = new HashMap<>();
    }

    public Stats(String userId) {
        this.userId = userId;
        this.totalGames = 0;
        this.questionStats = new HashMap<>();

        for (int i = 1; i <= 8; i++) {
            questionStats.put(i, new QuestionStats(i));
        }
    }

    public void recordAnswer(int questionNumber, boolean isCorrect) {
        QuestionStats qStats = questionStats.get(questionNumber);
        if (qStats != null) {
            if (isCorrect) {
                qStats.addCorrect();
            } else {
                qStats.addIncorrect();
            }
        }
    }

    public void incrementTotalGames() {
        totalGames++;
    }

    // get-set Stats
    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public int getTotalGames() {return totalGames;}
    public void setTotalGames(int totalGames) {this.totalGames = totalGames;}

    public Map<Integer, QuestionStats> getQuestionStats() {return questionStats;}
    public void setQuestionStats(Map<Integer, QuestionStats> questionStats) {
        this.questionStats = questionStats;
    }
    public QuestionStats getQuestionStats(int questionNumber) {
        return questionStats.get(questionNumber);
    }
}
