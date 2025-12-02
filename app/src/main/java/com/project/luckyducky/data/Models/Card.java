package com.project.luckyducky.data.Models;

public class Card {
    private String suit; // "hearts", "diamonds", "clubs", "spades"
    private String rank; // "A", "2", "3", ..., "J", "Q", "K"
    private int value; // 1-13

    public Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return (suit.equals("hearts") || suit.equals("diamonds")) ? "red" : "black";
    }

    public boolean isOdd() {
        return value % 2 != 0;
    }

    public boolean isEven() {
        return value % 2 == 0;
    }

    public String getCardImageName() {
        return rank.toLowerCase() + "_of_" + suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}