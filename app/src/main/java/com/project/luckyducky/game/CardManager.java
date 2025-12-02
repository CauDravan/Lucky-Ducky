package com.project.luckyducky.game;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.project.luckyducky.R;
import com.project.luckyducky.data.Models.Card;

public class CardManager {
    private Context context;

    public CardManager(Context context) {
        this.context = context;
    }

    // Get drawable for a card (dynamically generated)
    public Drawable getCardDrawable(Card card) {
        if (card == null) {
            return ContextCompat.getDrawable(context, R.drawable.ic_card_back);
        }
        return CardDrawableGenerator.createCardDrawable(card);
    }

    // Get drawable resource ID for a card (for ImageView)
    public int getCardDrawableId(Card card) {
        // Return a placeholder, will use getCardDrawable() instead
        return R.drawable.ic_card_back;
    }

    // Get card back drawable
    public Drawable getCardBackDrawable() {
        return ContextCompat.getDrawable(context, R.drawable.ic_card_back);
    }

    public int getCardBackDrawableId() {
        return R.drawable.ic_card_back;
    }

    // Get suit icon
    public int getSuitIcon(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts":
                return R.drawable.ic_heart;
            case "diamonds":
                return R.drawable.ic_diamond;
            case "clubs":
                return R.drawable.ic_clubs;
            case "spades":
                return R.drawable.ic_spade;
            default:
                return 0;
        }
    }

    // Get color for suit
    public int getSuitColor(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts":
            case "diamonds":
                return ContextCompat.getColor(context, android.R.color.holo_red_dark);
            case "clubs":
            case "spades":
                return ContextCompat.getColor(context, android.R.color.black);
            default:
                return ContextCompat.getColor(context, android.R.color.black);
        }
    }

    // Format card description
    public String getCardDescription(Card card) {
        if (card == null) {
            return "Unknown Card";
        }
        return card.getRank() + " of " + capitalizeFirst(card.getSuit());
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}