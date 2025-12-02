package com.project.luckyducky.data.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.List;

public class GameHistory {
    private String id;
    private String userId;

    @ServerTimestamp
    private Timestamp timestamp;

    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private List<QuestionResult> questionResults;

    public GameHistory() {
        // Required empty constructor for Firestore
    }

    public GameHistory(String userId, int totalQuestions, int correctAnswers,
                       int wrongAnswers, List<QuestionResult> questionResults) {
        this.userId = userId;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.questionResults = questionResults;
        // timestamp will be set by @ServerTimestamp
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }

    public static class QuestionResult {
        private int questionNumber;
        private String questionType; // "color", "higher_lower", "inside_outside", "suit", "odd_even"
        private String userAnswer;
        private boolean correct;
        private String cardDrawn; // e.g., "3 of hearts"

        public QuestionResult() {
            // Required empty constructor
        }

        public QuestionResult(int questionNumber, String questionType,
                              String userAnswer, boolean correct, String cardDrawn) {
            this.questionNumber = questionNumber;
            this.questionType = questionType;
            this.userAnswer = userAnswer;
            this.correct = correct;
            this.cardDrawn = cardDrawn;
        }

        // Getters and Setters
        public int getQuestionNumber() {
            return questionNumber;
        }

        public void setQuestionNumber(int questionNumber) {
            this.questionNumber = questionNumber;
        }

        public String getQuestionType() {
            return questionType;
        }

        public void setQuestionType(String questionType) {
            this.questionType = questionType;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }

        public String getCardDrawn() {
            return cardDrawn;
        }

        public void setCardDrawn(String cardDrawn) {
            this.cardDrawn = cardDrawn;
        }
    }
}