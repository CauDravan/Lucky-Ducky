package com.project.luckyducky;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.project.luckyducky.utils.SoundManager;

public class BaseActivity extends AppCompatActivity {

    protected SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        soundManager = SoundManager.getInstance(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        applyButtonSounds();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        applyButtonSounds();
    }

    private void applyButtonSounds() {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            addButtonSoundRecursively(rootView);
        }
    }

    private void addButtonSoundRecursively(View view) {
        if (view instanceof Button) {
            final View.OnClickListener originalListener = getOnClickListener(view);
            view.setOnClickListener(v -> {
                soundManager.playButtonClickSound();
                if (originalListener != null) {
                    originalListener.onClick(v);
                }
            });
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                addButtonSoundRecursively(viewGroup.getChildAt(i));
            }
        }
    }

    private View.OnClickListener getOnClickListener(View view) {
        try {
            java.lang.reflect.Field field = View.class.getDeclaredField("mListenerInfo");
            field.setAccessible(true);
            Object listenerInfo = field.get(view);

            if (listenerInfo != null) {
                java.lang.reflect.Field clickListenerField =
                        listenerInfo.getClass().getDeclaredField("mOnClickListener");
                clickListenerField.setAccessible(true);
                return (View.OnClickListener) clickListenerField.get(listenerInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void playButtonSound() {
        soundManager.playButtonClickSound();
    }
}