package com.project.luckyducky.game;

import com.project.luckyducky.data.Models.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionManager {
    private List<Card> deck;
    private Random random;
    private Card previousCard;
    private Card currentCard;
    private int currentQuestionNumber;

    public QuestionManager() {
        this.random = new Random();
        this.currentQuestionNumber = 1;
        initializeDeck();
    }

    private void initializeDeck() {
        deck = new ArrayList<>();
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String suit : suits) {
            for (int i = 0; i < ranks.length; i++) {
                deck.add(new Card(suit, ranks[i], i + 1));
            }
        }

        Collections.shuffle(deck);
    }

    public Card drawCard() {
        if (deck.isEmpty()) {
            initializeDeck();
        }
        previousCard = currentCard;
        currentCard = deck.remove(0);
        return currentCard;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public Card getPreviousCard() {
        return previousCard;
    }

    public int getRemainingCards() {
        return deck.size();
    }

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    public void nextQuestion() {
        currentQuestionNumber++;
    }

    public void reset() {
        currentQuestionNumber = 1;
        previousCard = null;
        currentCard = null;
        initializeDeck();
    }

    // Question 1: Red or Black
    public boolean checkColorAnswer(String answer) {
        return currentCard.getColor().equalsIgnoreCase(answer);
    }

    // Question 2: Higher or Lower than previous card
    public boolean checkHigherLowerAnswer(String answer) {
        if (previousCard == null) return false;

        if (answer.equalsIgnoreCase("higher")) {
            return currentCard.getValue() > previousCard.getValue();
        } else if (answer.equalsIgnoreCase("lower")) {
            return currentCard.getValue() < previousCard.getValue();
        }
        return false;
    }

    // Question 3: Inside or Outside the range of previous two cards
    public boolean checkInsideOutsideAnswer(String answer, Card firstCard) {
        if (firstCard == null || previousCard == null) return false;

        int min = Math.min(firstCard.getValue(), previousCard.getValue());
        int max = Math.max(firstCard.getValue(), previousCard.getValue());
        int current = currentCard.getValue();

        if (answer.equalsIgnoreCase("inside")) {
            return current > min && current < max;
        } else if (answer.equalsIgnoreCase("outside")) {
            return current <= min || current >= max;
        }
        return false;
    }

    // Question 4: Suit guess
    public boolean checkSuitAnswer(String answer) {
        return currentCard.getSuit().equalsIgnoreCase(answer);
    }

    // Question 5: Odd or Even
    public boolean checkOddEvenAnswer(String answer) {
        if (answer.equalsIgnoreCase("odd")) {
            return currentCard.isOdd();
        } else if (answer.equalsIgnoreCase("even")) {
            return currentCard.isEven();
        }
        return false;
    }

    // Question 6: Face card (J, Q, K) or Number card
    public boolean checkFaceNumberAnswer(String answer) {
        boolean isFaceCard = currentCard.getValue() >= 11; // J=11, Q=12, K=13

        if (answer.equalsIgnoreCase("face")) {
            return isFaceCard;
        } else if (answer.equalsIgnoreCase("number")) {
            return !isFaceCard;
        }
        return false;
    }

    // Question 7: Rank prediction (A, 2-10, J, Q, K)
    public boolean checkRankPredictionAnswer(String answer) {
        return currentCard.getRank().equalsIgnoreCase(answer);
    }

    public String getQuestionType(int questionNumber) {
        switch (questionNumber) {
            case 1: return "color";
            case 2: return "higher_lower";
            case 3: return "inside_outside";
            case 4: return "suit";
            case 5: return "odd_even";
            case 6: return "face_number";
            case 7: return "rank_prediction";
            default: return "unknown";
        }
    }

    public String getQuestionText(int questionNumber) {
        switch (questionNumber) {
            case 1: return "Will the card be Red or Black?";
            case 2: return "Will the next card be Higher or Lower?";
            case 3: return "Will the next card be Inside or Outside?";
            case 4: return "What suit will the card be?";
            case 5: return "Will the card value be Odd or Even?";
            case 6: return "Will it be a Face card or Number card?";
            case 7: return "What rank will the card be?";
            default: return "Unknown question";
        }
    }
}