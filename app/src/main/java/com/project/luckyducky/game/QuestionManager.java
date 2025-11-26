package com.project.luckyducky.game;

import com.project.luckyducky.data.Models.Card;
import com.project.luckyducky.utils.Constants;

public class QuestionManager {
    private int currentQuestion;
    private CardManager cardManager;

    public QuestionManager(CardManager cardManager) {
        this.cardManager = cardManager;
        this.currentQuestion = 1;
    }

    public String getCurrentQuestionText() {
        if (currentQuestion < 1 || currentQuestion > Constants.TOTAL_QUESTIONS) {
            return "";
        }
        return Constants.QUESTIONS[currentQuestion - 1];
    }

    public String[] getCurrentQuestionOptions() {
        switch (currentQuestion) {
            case Constants.Q1_RED_OR_BLACK:
                return Constants.Q1_OPTION;
            case Constants.Q2_HIGHER_OR_LOWER:
                return Constants.Q2_OPTION;
            case Constants.Q3_INSIDE_OR_OUTSIDE:
                return Constants.Q3_OPTION;
            case Constants.Q4_SUIT:
                return Constants.Q4_OPTION;
            case Constants.Q5_ODD_OR_EVEN:
                return Constants.Q5_OPTION;
            case Constants.Q6_FACE_OR_NUMBER:
                return Constants.Q6_OPTION;
            case Constants.Q7_EXACT_RANK:
                return Constants.Q7_OPTION;
            case Constants.Q8_EXACT_CARD:
                return getQ8Options(); // generate 52
            default:
                return new String[0];
        }
    }

    // only for Q8
    private String[] getQ8Options() {
        String[] options = new String[52];
        String[] suits = {"♥", "♦", "♣", "♠"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        int index = 0;
        for (String suit : suits) {
            for (String rank : ranks) {
                options[index++] = rank + suit;
            }
        }
        return options;
    }

    public boolean validateAnswer(String answer, Card card) {
        return cardManager.validateAnswer(currentQuestion, answer, card);
    }

    public String getCorrectAnswer(Card card) {
        return cardManager.getCorrectAnswer(currentQuestion, card);
    }

    public void nextQuestion() {
        if (currentQuestion < Constants.TOTAL_QUESTIONS) {
            currentQuestion++;
        }
    }

    public boolean isLastQuestion() {
        return currentQuestion == Constants.TOTAL_QUESTIONS;
    }

    public boolean hasMoreQuestions() {
        return currentQuestion < Constants.TOTAL_QUESTIONS;
    }

    public int getCurrentQuestionNumber() {
        return currentQuestion;
    }

    public void reset() {
        currentQuestion = 1;
    }

    public int getTotalQuestions() {
        return Constants.TOTAL_QUESTIONS;
    }

    public String getQuestionHint() {
        switch (currentQuestion) {
            case 1:
                return "1 in 2 chance";
            case 2:
                return "Get 7 doesn't count";
            case 3:
                return "Base on 2 previous question";
            case 4:
                return "1 in 4 chance!";
            case 5:
                return "Odd: A,3,5,7,9,J,K | Even: 2,4,6,8,10,Q";
            case 6:
                return "Face card: J,Q,K | Number: A-10";
            case 7:
                return "1 in 13 chance!!";
            case 8:
                return "1 in 52 chance!!!";
            default:
                return "";
        }
    }
}
