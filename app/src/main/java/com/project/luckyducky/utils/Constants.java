package com.project.luckyducky.utils;

public class Constants {
    // firebase collection
    public static final String COLLECTION_USERS = "user";
    public static final String COLLECTION_STATS = "stats";
    public static final String COLLECTION_HISTORY = "game_history";

    // shared preferences keys
    public static final String PREFS_NAME = "LuckyDuckyPrefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FIRST_TIME = "first_time";

    // game constants
    public static final int TOTAL_QUESTIONS = 8;
    public static final int TOTAL_CARDS = 52;

    // question numbers
    public static final int Q1_RED_OR_BLACK = 1;
    public static final int Q2_HIGHER_OR_LOWER = 2;
    public static final int Q3_INSIDE_OR_OUTSIDE = 3;
    public static final int Q4_SUIT = 4;
    public static final int Q5_ODD_OR_EVEN = 5;
    public static final int Q6_FACE_OR_NUMBER = 6;
    public static final int Q7_EXACT_RANK = 7;
    public static final int Q8_EXACT_CARD = 8;

    // question texts
    public static final String[] QUESTIONS = {
        "Red or Black?",
        "Higher or Lower?",
        "Inside or Outside?",
        "Suit?",
        "Odd or Even?",
        "Face or Number?",
        "Rank?",
        "Card?"
    };

    // answer options
    public static final String[] Q1_OPTION = {"Red", "Black"};
    public static final String[] Q2_OPTION = {"Higher", "Lower"};
    public static final String[] Q3_OPTION = {"Inside", "Outside"};
    public static final String[] Q4_OPTION = {"Hearts", "Diamonds", "Clubs", "Spades"};
    public static final String[] Q5_OPTION = {"Odd", "Even"};
    public static final String[] Q6_OPTION = {"Face", "Number"};
    public static final String[] Q7_OPTION = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    // intent keys
    public static final String EXTRA_GAME_RESULT = "game_result";
    public static final String EXTRA_DRAWN_CARD = "drawn_card";

    // ui constants
    public static final int ANIMATION_DURATION = 300;
    public static final int CARD_FLIP_DURATION = 500;
}
