package com.project.luckyducky.game;

import com.project.luckyducky.data.Models.Card;
import com.project.luckyducky.data.Models.Card.Rank;
import com.project.luckyducky.data.Models.Card.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CardManager {
    private List<Card> deck;
    private Random random;
    private List<Card> drawnCards;

    public CardManager() {
        this.random = new Random();
        this.drawnCards = new ArrayList<>();
        initializeDeck();
    }

    private void initializeDeck() {
        deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck, random);
    }

    public Card drawCard() {
        if (deck.isEmpty()) {
            initializeDeck();
            shuffle();
        }
        Card card = deck.remove(0);
        drawnCards.add(card);

        // for Q3
        if (drawnCards.size() > 2) {
            drawnCards.remove(0);
        }

        return card;
    }

    public Card drawnRandomCard() {
        shuffle();
        return drawCard();
    }

    public List<Card> getDrawnCards() {
        return new ArrayList<>(drawnCards);
    }

    public void reset() {
        initializeDeck();
        drawnCards.clear();
    }

    // helper methods
    public static boolean isRed(Card card) {
        return card.isRed();
    }

    public static boolean isHigherThan7(Card card) {
        return card.getRank().getValue() > 7;
    }

    public static boolean isInside(Card card, Card card1, Card card2) {
        int value = card.getRank().getValue();
        int min = Math.min(card1.getRank().getValue(), card2.getRank().getValue());
        int max = Math.max(card1.getRank().getValue(), card2.getRank().getValue());
        return value >= min && value <= max;
    }

    public static boolean isSuit(Card card, Suit suit) {
        return card.getSuit() == suit;
    }

    public static boolean isOdd(Card card) {
        return card.getRank().isOdd();
    }

    public static boolean isFaceCard(Card card) {
        return card.getRank().isFaceCard();
    }

    public static boolean isExactRank(Card card, Rank rank) {
        return card.getRank() == rank;
    }

    public static boolean isExactCard(Card card, Card card1) {
        return card.equals(card1);
    }

    // Convert String answer to boolean result
    public boolean validateAnswer(int questionNumber, String answer, Card card) {
        switch (questionNumber) {
            case 1: // Red or Black
                boolean isRed = answer.equals("Red");
                return isRed == card.isRed();

            case 2: // Higher or Lower than 7
                boolean wantHigher = answer.equals("Higher 7");
                boolean actualHigher = card.getRank().getValue() > 7;
                if (card.getRank().getValue() == 7) {
                    return false;
                }
                return wantHigher == actualHigher;

            case 3: // Inside or Outside
                if (drawnCards.size() < 2) {
                    return false;
                }
                boolean wantInside = answer.equals("Inside");
                boolean actualInside = isInside(card, drawnCards.get(0), drawnCards.get(1));
                return wantInside == actualInside;

            case 4: // Suit
                Suit selectedSuit = null;
                if (answer.contains("Heart")) selectedSuit = Suit.HEARTS;
                else if (answer.contains("Diamond")) selectedSuit = Suit.DIAMONDS;
                else if (answer.contains("Club")) selectedSuit = Suit.CLUBS;
                else if (answer.contains("Spade")) selectedSuit = Suit.SPADES;
                return selectedSuit == card.getSuit();

            case 5: // Odd or Even
                boolean wantOdd = answer.equals("Odd");
                return wantOdd == card.getRank().isOdd();

            case 6: // Face or Number
                boolean wantFace = answer.contains("Face card");
                return wantFace == card.getRank().isFaceCard();

            case 7: // Exact Rank
                return answer.equals(card.getRank().getDisplay());

            case 8: // Exact Rank + Suit
                String cardString = card.getRank().getDisplay() + card.getSuit().getSymbol();
                return answer.equals(cardString);

            default:
                return false;
        }
    }

    public String getCorrectAnswer(int questionNumber, Card card) {
        switch (questionNumber) {
            case 1:
                return card.isRed() ? "Red" : "Black";
            case 2:
                if (card.getRank().getValue() == 7) return "7 (Lose)";
                return card.getRank().getValue() > 7 ? "Higher than 7" : "Lower than 7";
            case 3:
                if (drawnCards.size() < 2) return "N/A";
                boolean inside = isInside(card, drawnCards.get(0), drawnCards.get(1));
                return inside ? "Inside" : "Outside";
            case 4:
                return card.getSuit().getSymbol() + " " + card.getSuit().getName();
            case 5:
                return card.getRank().isOdd() ? "Odd" : "Even";
            case 6:
                return card.getRank().isFaceCard() ? "Face card (J/Q/K)" : "Number (A-10)";
            case 7:
                return card.getRank().getDisplay();
            case 8:
                return card.getRank().getDisplay() + card.getSuit().getSymbol();
            default:
                return "Unknown";
        }
    }
}