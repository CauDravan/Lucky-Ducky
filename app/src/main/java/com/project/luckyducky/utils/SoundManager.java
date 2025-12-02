package com.project.luckyducky.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import com.project.luckyducky.R;

public class SoundManager {

    private static SoundManager instance;
    private SoundPool soundPool;
    private int correctSound;
    private int wrongSound;
    private int cardFlipSound;
    private int buttonClickSound;
    private boolean soundEnabled = true;

    private SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        // Load sounds
        correctSound = soundPool.load(context, R.raw.sound_correct, 1);
        wrongSound = soundPool.load(context, R.raw.sound_wrong, 1);
        cardFlipSound = soundPool.load(context, R.raw.sound_card_flip, 1);
        buttonClickSound = soundPool.load(context, R.raw.sound_button_click, 1);
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context.getApplicationContext());
        }
        return instance;
    }

    public void playCorrectSound() {
        if (soundEnabled) {
            soundPool.play(correctSound, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    public void playWrongSound() {
        if (soundEnabled) {
            soundPool.play(wrongSound, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    public void playCardFlipSound() {
        if (soundEnabled) {
            soundPool.play(cardFlipSound, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    public void playButtonClickSound() {
        if (soundEnabled) {
            soundPool.play(buttonClickSound, 0.5f, 0.5f, 0, 0, 1.0f);
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}