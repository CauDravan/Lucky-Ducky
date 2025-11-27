package com.project.luckyducky.data.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private long timestamp;
    private Card drawnCard;
    private List<QuestionResult> results;

    public static class QuestionResult implements Serializable {
        private int questionNumber;
        private String question;
        private String userAnswer;
        private String correctAnswer;
        private boolean isCorrect;

        public QuestionResult() {}

        public QuestionResult(int questionNumber, String question, String userAnswer, String correctAnswer, boolean isCorrect) {
            this.questionNumber = questionNumber;
            this.question = question;
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
            this.isCorrect = isCorrect;
        }

        // get-set
        public int getQuestionNumber() {return questionNumber;}
        public void setQuestionNumber(int questionNumber) {this.questionNumber = questionNumber;}

        public String getQuestion() {return question;}
        public void setQuestion(String question) {this.question = question;}

        public String getUserAnswer() {return userAnswer;}
        public void setUserAnswer(String userAnswer) {this.userAnswer = userAnswer;}

        public String getCorrectAnswer() {return correctAnswer;}
        public void setCorrectAnswer(String correctAnswer) {this.correctAnswer = correctAnswer;}

        public boolean isCorrect() {return isCorrect;}
        public void setCorrect(boolean correct) {isCorrect = correct;}
    }

    // constructor firestore
    public GameHistory() {
        this.results = new ArrayList<>();
    }

    public GameHistory(String userId, Card drawnCard) {
        this.userId = userId;
        this.drawnCard = drawnCard;
        this.timestamp = System.currentTimeMillis();
        this.results = new ArrayList<>();
    }

    public void addResult(QuestionResult result) {
        results.add(result);
    }

    public int getCorrectCount() {
        int count = 0;
        for (QuestionResult result : results) {
            if (result.isCorrect()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalQuestions() {
        return results.size();
    }

    // get-set
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public long getTimestamp() {return timestamp;}
    public void setTimestamp(long timestamp) {this.timestamp = timestamp;}

    public Card getDrawnCard() {return drawnCard;}
    public void setDrawnCard(Card drawnCard) {this.drawnCard = drawnCard;}

    public List<QuestionResult> getResults() {return results;}
    public void setResults(List<QuestionResult> results) {this.results = results;}
}
