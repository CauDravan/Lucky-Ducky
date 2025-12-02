package com.project.luckyducky.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.project.luckyducky.data.Models.Card;

public class CardDrawableGenerator extends Drawable {

    private final Paint backgroundPaint;
    private final Paint borderPaint;
    private final Paint textPaint;
    private final Paint suitPaint;
    private final String rank;
    private final String suit;
    private final int suitColor;

    public CardDrawableGenerator(Card card) {
        this.rank = card.getRank();
        this.suit = card.getSuit();
        this.suitColor = getSuitColor(card.getSuit());

        // Background paint (white)
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        // Border paint
        borderPaint = new Paint();
        borderPaint.setColor(Color.LTGRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        borderPaint.setAntiAlias(true);

        // Rank text paint
        textPaint = new Paint();
        textPaint.setColor(suitColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(60);
        textPaint.setFakeBoldText(true);

        // Suit symbol paint
        suitPaint = new Paint();
        suitPaint.setColor(suitColor);
        suitPaint.setTextAlign(Paint.Align.CENTER);
        suitPaint.setAntiAlias(true);
        suitPaint.setTextSize(80);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        // Draw rounded rectangle background
        RectF rectF = new RectF(bounds);
        canvas.drawRoundRect(rectF, 20, 20, backgroundPaint);
        canvas.drawRoundRect(rectF, 20, 20, borderPaint);

        // Calculate sizes based on card dimensions
        float cardHeight = bounds.height();
        float cardWidth = bounds.width();

        // Adjust text sizes for smaller cards
        float rankTextSize = Math.min(cardHeight * 0.15f, 60);
        float smallSuitSize = Math.min(cardHeight * 0.1f, 40);
        float centerSuitSize = Math.min(cardHeight * 0.35f, 100);

        textPaint.setTextSize(rankTextSize);

        // Draw rank in top left
        float topRankX = bounds.left + cardWidth * 0.2f;
        float topRankY = bounds.top + cardHeight * 0.15f;
        canvas.drawText(rank, topRankX, topRankY, textPaint);

        // Draw rank in bottom right (upside down)
        canvas.save();
        canvas.rotate(180, bounds.centerX(), bounds.centerY());
        float bottomRankX = bounds.left + cardWidth * 0.2f;
        float bottomRankY = bounds.top + cardHeight * 0.15f;
        canvas.drawText(rank, bottomRankX, bottomRankY, textPaint);
        canvas.restore();

        // Draw single suit symbol in center
        String suitSymbol = getSuitSymbol(suit);
        Paint centerPaint = new Paint(suitPaint);
        centerPaint.setTextSize(centerSuitSize);

        float centerX = bounds.centerX();
        float centerY = bounds.centerY() + (centerSuitSize * 0.35f);
        canvas.drawText(suitSymbol, centerX, centerY, centerPaint);

        // Draw small suit symbol next to top rank
        Paint smallSuitPaint = new Paint(suitPaint);
        smallSuitPaint.setTextSize(smallSuitSize);
        canvas.drawText(suitSymbol, topRankX, topRankY + smallSuitSize, smallSuitPaint);

        // Draw small suit symbol next to bottom rank
        canvas.save();
        canvas.rotate(180, bounds.centerX(), bounds.centerY());
        canvas.drawText(suitSymbol, bottomRankX, bottomRankY + smallSuitSize, smallSuitPaint);
        canvas.restore();
    }

    private void drawCenterSuits(Canvas canvas, Rect bounds, String suitSymbol) {
        float centerX = bounds.centerX();
        float centerY = bounds.centerY() + 30;

        // Get numeric value for positioning
        int value = getNumericValue(rank);

        // Draw different patterns based on card value
        if (value >= 11 || rank.equals("A")) {
            // Face cards and Ace: single large symbol
            canvas.drawText(suitSymbol, centerX, centerY, suitPaint);
        } else if (value <= 3) {
            // 1-3: vertical line
            for (int i = 0; i < value; i++) {
                float y = centerY + (i - value/2f) * 60;
                canvas.drawText(suitSymbol, centerX, y, suitPaint);
            }
        } else {
            // 4-10: draw in grid pattern
            drawGridPattern(canvas, bounds, suitSymbol, value);
        }
    }

    private void drawGridPattern(Canvas canvas, Rect bounds, String suitSymbol, int count) {
        float startY = bounds.top + bounds.height() * 0.3f;
        float endY = bounds.bottom - bounds.height() * 0.3f;
        float leftX = bounds.centerX() - 40;
        float rightX = bounds.centerX() + 40;

        Paint gridPaint = new Paint(suitPaint);
        gridPaint.setTextSize(50);

        // Simple grid for 4-10
        int rows = (count + 1) / 2;
        float spacing = (endY - startY) / (rows + 1);

        for (int i = 0; i < count; i++) {
            float x = (i % 2 == 0) ? leftX : rightX;
            float y = startY + ((i / 2) + 1) * spacing;
            canvas.drawText(suitSymbol, x, y, gridPaint);
        }
    }

    private int getNumericValue(String rank) {
        switch (rank) {
            case "A": return 1;
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            default:
                try {
                    return Integer.parseInt(rank);
                } catch (NumberFormatException e) {
                    return 0;
                }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        backgroundPaint.setAlpha(alpha);
        borderPaint.setAlpha(alpha);
        textPaint.setAlpha(alpha);
        suitPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        backgroundPaint.setColorFilter(colorFilter);
        borderPaint.setColorFilter(colorFilter);
        textPaint.setColorFilter(colorFilter);
        suitPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private int getSuitColor(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts":
            case "diamonds":
                return Color.RED;
            case "clubs":
            case "spades":
                return Color.BLACK;
            default:
                return Color.BLACK;
        }
    }

    private String getSuitSymbol(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts":
                return "♥";
            case "diamonds":
                return "♦";
            case "clubs":
                return "♣";
            case "spades":
                return "♠";
            default:
                return "?";
        }
    }

    public static Drawable createCardDrawable(Card card) {
        return new CardDrawableGenerator(card);
    }
}