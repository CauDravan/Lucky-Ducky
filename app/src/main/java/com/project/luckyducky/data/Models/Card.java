package com.project.luckyducky.data.Models;

public class Card {
    private static final long serialVersionUID = 1L;

    private Suit suit;
    private Rank rank;

    public enum Suit {
        HEARTS("♥", "Hearts", true),
        DIAMONDS("♦", "Diamonds", true),
        CLUBS("♣", "Clubs", false),
        SPADES("♠", "Spades", false);

        private final String symbol;
        private final String name;
        private final boolean isRed;

        Suit(String symbol, String name, boolean isRed) {
            this.symbol = symbol;
            this.name = name;
            this.isRed = isRed;
        }

        public String getSymbol() {return symbol;}
        public String getName() {return name;}
        public boolean isRed() {return isRed;}
    }

    public enum Rank {
        ACE("A", 1),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13);

        private final String display;
        private final int value;

        Rank(String display, int value) {
            this.display = display;
            this.value = value;
        }

        public String getDisplay() {return display;}
        public int getValue() {return value;}
        public boolean isFaceCard() {return value >= 11;}
        public boolean isOdd() {
            return value % 2 == 1;
        }
    }

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {return suit;}
    public Rank getRank() {return rank;}

    public boolean isRed() {return suit.isRed();}
    public boolean isBlack() {return !isRed();}

    @Override
    public String toString() {
        return rank.getDisplay() + suit.getSymbol();
    }
}
