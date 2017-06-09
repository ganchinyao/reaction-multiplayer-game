package com.react.reactmultiplayergame.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by gan on 10/5/17.
 */

// here we customize the listpreference to play sound. So we use MyListPreference throughout instead of ListPreference. Rmb xml we use <xxx.MyListPreference instead of <ListPreference from android
public class MyListPreference extends ListPreference {
    public MyListPreference(Context context) {
        super(context);
    }

    public MyListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        SoundPoolManager.getInstance().playSound(2);
        super.onDialogClosed(positiveResult);
    }

    @Override
    protected void onClick() {
        SoundPoolManager.getInstance().playSound(0);
        super.onClick();
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        if(super.getDialog() != null ) {

            if (Build.VERSION.SDK_INT < 21) {
                // before that there will be a white background at alertdialog so we needa get rid of
                // but we cannot set it transparent also for after this api, becos then entire thing will be transparent
                super.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                try {
                    // after lolippop there is no this title divider so we only check before api 21
                    int titleDividerId = super.getDialog().getContext().getResources().getIdentifier("titleDivider", "id", "android");
                    View titleDivider = super.getDialog().getWindow().getDecorView().findViewById(titleDividerId);
                    titleDivider.setBackgroundColor(Color.parseColor("#FF4081")); // change title divider color
                } catch (Exception ex) {

                }
            }

        }
    }
}
