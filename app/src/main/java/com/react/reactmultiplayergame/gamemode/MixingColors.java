package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;
import com.react.reactmultiplayergame.helper.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by gan on 30/3/17.
 */

public class MixingColors extends GameMode {
    private ImageView color1, color2, colorMixed, correctColor, color1a, color2a, colorMixeda;
    private int resultColorCorrect, resultColorWrong;
    private int correctOrWrong = randomGenerator.nextInt(2);
    private int CORRECT = 0;
    private int WRONG = 1;
    private boolean currentQuestionAnswer;
    private int playerThatTouched;
    private Context context;
    private RelativeLayout parentLayout;
    private int customTimerDuration;
    private int _25dp;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.mixingcolors);
        player2Question.setText(R.string.mixingcolors);
    }

    @Override
    public int playerThatTouched() {
        return playerThatTouched;
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        this.context = context;
        this.parentLayout = parentLayout;
        _25dp = (int) context.getResources().getDimension(R.dimen._25sdp);
        String paletteTotal[] = {};

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pattern_mixingcolor);
        if (bitmap != null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                paletteTotal = new String[]{
                        "#FF0000",
                        "#000000",
                        "#0000FF",
                        "#00FF00",
                        "#18FFFF",
                        "#FFEB3B",
                        "#FFFFFF"
                };
                customTimerDuration = 3000;
                break;
            case Constants.MODE_MEDIUM:
                paletteTotal = new String[]{
                        "#00BFFF",
                        "#00EE76",
                        "#FF82AB",
                        "#8B2252",
                        "#CD00CD",
                        "#6959CD",
                        "#B9D3EE",
                        "#2E8B57",
                        "#CAFF70",
                        "#FFFF00",
                        "#EEC900",
                        "#FF9912",
                        "#CD3700",
                        "#8B1A1A",
                        "#C67171",
                        "#696969",
                        "#969696",
                        "#636363"
                };
                customTimerDuration = 3000;
                break;
            case Constants.MODE_HARD:
                paletteTotal = new String[]{
                        // if add more color palette to hard or insane, rmb to change strings findoutmore_colormixer lines to the appropriate number
                        "#00BFFF",
                        "#00EE76",
                        "#FF82AB",
                        "#8B2252",
                        "#CD00CD",
                        "#6959CD",
                        "#B9D3EE",
                        "#2E8B57",
                        "#CAFF70",
                        "#FFFF00",
                        "#EEC900",
                        "#FF9912",
                        "#CD3700",
                        "#8B1A1A",
                        "#C67171",
                        "#696969",
                        "#969696",
                        "#636363",
                        "#FF1744",
                        "#FF9100",
                        "#FFEB3B",
                        "#000000",
                        "#00796B",
                        "#00B0FF",
                        "#E040FB",
                        "#18FFFF",
                        "#EEFF41",
                        "#536DFE",
                        "#78909C",
                        "#9CCC65",
                        "#3F51B5",
                        "#A5D6A7",
                        "#80CBC4"
                };
                customTimerDuration = 2000;
                break;
            case Constants.MODE_INSANE:
                paletteTotal = new String[]{
                        "#C67171",
                        "#696969",
                        "#969696",
                        "#636363",
                        "#FF1744",
                        "#FF9100",
                        "#FFEB3B",
                        "#000000",
                        "#00796B",
                        "#00B0FF",
                        "#E040FB",
                        "#FF3D00",
                        "#FFEA00",
                        "#FFC400",
                        "#AEEA00",
                        "#76FF03",
                        "#AFB42B",
                        "#4CAF50",
                        "#00E5FF",
                        "#18FFFF",
                        "#01579B",
                        "#26A69A",
                        "#29B6F6",
                        "#009688",
                        "#2962FF",
                        "#B388FF",
                        "#B39DDB",
                        "#3F51B5",
                        "#2196F3",
                        "#E3F2FD",
                        "#C2185B",
                        "#BA68C8",
                        "#F06292",
                        "#EF9A9A",
                        "#F44336",
                        "#FFAB91",
                        "#6D4C41",
                        "#BCAAA4",
                        "#795548",
                        "#9E9E9E",
                        "#424242",
                        "#FAFAFA",
                        "#607D8B",
                        "#FFFFFF",
                        "#37474F"
                };
                customTimerDuration = 1000;
                break;
        }
        shuffleStringArray(paletteTotal);

        color1 = new ImageView(context);
        Drawable background = ContextCompat.getDrawable(context, R.drawable.b1);
        background.mutate();
        background.setColorFilter(new PorterDuffColorFilter(Color.parseColor(paletteTotal[0]), PorterDuff.Mode.SRC_ATOP));
        color1.setBackground(background);

        color2 = new ImageView(context);
        Drawable background2 = ContextCompat.getDrawable(context, R.drawable.b1);
        background2.mutate();
        background2.setColorFilter(new PorterDuffColorFilter(Color.parseColor(paletteTotal[1]), PorterDuff.Mode.SRC_ATOP));
        color2.setBackground(background2);

        resultColorCorrect = ColorUtils.blendARGB(Color.parseColor(paletteTotal[0]), Color.parseColor(paletteTotal[1]), 0.5F);
        resultColorWrong = ColorUtils.blendARGB(Color.parseColor(paletteTotal[2]), Color.parseColor(paletteTotal[3]), 0.5F);

        colorMixed = new ImageView(context);
        Drawable background3 = ContextCompat.getDrawable(context, R.drawable.b1);
        background3.mutate();
        if (correctOrWrong == CORRECT) {
            background3.setColorFilter(new PorterDuffColorFilter(resultColorCorrect, PorterDuff.Mode.SRC_ATOP));
        } else {
            background3.setColorFilter(new PorterDuffColorFilter(resultColorWrong, PorterDuff.Mode.SRC_ATOP));
        }
        colorMixed.setBackground(background3);

        LinearLayout linearLayoutBottom = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParamsBottomImage = new LinearLayout.LayoutParams(parentLayout.getHeight() / 3, parentLayout.getHeight() / 3, 1);
        layoutParamsBottomImage.setMargins(0, 0, 0, parentLayout.getHeight() / 16);

        TextView plus = new TextView(context);
        plus.setText("+");
        plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, _25dp);
        plus.setGravity(Gravity.CENTER);

        TextView equals = new TextView(context);
        equals.setText("=");
        equals.setTextSize(TypedValue.COMPLEX_UNIT_PX, _25dp);
        equals.setGravity(Gravity.CENTER);

        linearLayoutBottom.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutBottom.addView(color1, layoutParamsBottomImage);
        linearLayoutBottom.addView(plus, layoutParamsBottomImage);
        linearLayoutBottom.addView(color2, layoutParamsBottomImage);
        linearLayoutBottom.addView(equals, layoutParamsBottomImage);
        linearLayoutBottom.addView(colorMixed, layoutParamsBottomImage);

        RelativeLayout.LayoutParams layoutParamsBottomParent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsBottomParent.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParamsBottomParent.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsBottomParent.setMargins(parentLayout.getWidth() / 11, 0, parentLayout.getWidth() / 11, 0);
        parentLayout.addView(linearLayoutBottom, layoutParamsBottomParent);


        ///// Top View ////
        color1a = new ImageView(context);
        Drawable background1a = ContextCompat.getDrawable(context, R.drawable.b1);
        background1a.mutate();
        background1a.setColorFilter(new PorterDuffColorFilter(Color.parseColor(paletteTotal[0]), PorterDuff.Mode.SRC_ATOP));
        color1a.setBackground(background1a);

        color2a = new ImageView(context);
        Drawable background2a = ContextCompat.getDrawable(context, R.drawable.b1);
        background2a.mutate();
        background2a.setColorFilter(new PorterDuffColorFilter(Color.parseColor(paletteTotal[1]), PorterDuff.Mode.SRC_ATOP));
        color2a.setBackground(background2a);

        colorMixeda = new ImageView(context);
        Drawable background3a = ContextCompat.getDrawable(context, R.drawable.b1);
        background3a.mutate();
        if (correctOrWrong == CORRECT) {
            background3a.setColorFilter(new PorterDuffColorFilter(resultColorCorrect, PorterDuff.Mode.SRC_ATOP));
        } else {
            background3a.setColorFilter(new PorterDuffColorFilter(resultColorWrong, PorterDuff.Mode.SRC_ATOP));
        }
        colorMixeda.setBackground(background3a);

        LinearLayout linearLayoutTop = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParamsTopImage = new LinearLayout.LayoutParams(parentLayout.getHeight() / 3, parentLayout.getHeight() / 3, 1);
        layoutParamsTopImage.setMargins(0, parentLayout.getHeight() / 16, 0, 0);

        TextView plusTop = new TextView(context);
        plusTop.setText("+");
        plusTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, _25dp);
        plusTop.setGravity(Gravity.CENTER);
        plusTop.setRotation(180);

        TextView equalsTop = new TextView(context);
        equalsTop.setText("=");
        equalsTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, _25dp);
        equalsTop.setGravity(Gravity.CENTER);
        equalsTop.setRotation(180);

        linearLayoutTop.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutTop.addView(colorMixeda, layoutParamsTopImage);
        linearLayoutTop.addView(equalsTop, layoutParamsTopImage);
        linearLayoutTop.addView(color2a, layoutParamsTopImage);
        linearLayoutTop.addView(plusTop, layoutParamsTopImage);
        linearLayoutTop.addView(color1a, layoutParamsTopImage);

        RelativeLayout.LayoutParams layoutParamsTopParent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTopParent.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParamsTopParent.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsTopParent.setMargins(parentLayout.getWidth() / 11, 0, parentLayout.getWidth() / 11, 0);
        parentLayout.addView(linearLayoutTop, layoutParamsTopParent);


    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_mixingcolors);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER1;
        buttonTouchedAction();
        return true;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER2;
        buttonTouchedAction();
        return true;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER3;
        buttonTouchedAction();
        return true;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER4;
        buttonTouchedAction();
        return true;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_mixingcolor;
    }

    @Override
    public boolean requireDefaultTimer() {
        return false;
    }

    @Override
    public boolean requireATimer() {
        return true;
    }

    @Override
    public boolean wantToShowTimer() {
        return true;
    }

    @Override
    public int customTimerDuration() {
        return customTimerDuration;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return currentQuestionAnswer;
    }

    private void buttonTouchedAction() {
        // find out if current question is correct/not , and to display the correct answer if question answer is wrong
        if (correctOrWrong == CORRECT) {
            currentQuestionAnswer = true;
        } else {
            int setMargin = Utils.dpToPx(10);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gameContent_backgroundPopUpforMixingColors));

            correctColor = new ImageView(context);
            Drawable backgroundCorrect = ContextCompat.getDrawable(context, R.drawable.b8);
            backgroundCorrect.setColorFilter(new PorterDuffColorFilter(resultColorCorrect, PorterDuff.Mode.SRC_ATOP));
            correctColor.setBackground(backgroundCorrect);

            TextView textViewBottom = new TextView(context);
            textViewBottom.setText(R.string.mixingcolors_correctanswer);
            textViewBottom.setTextColor(resultColorCorrect);
            textViewBottom.setTextSize(TypedValue.COMPLEX_UNIT_PX, _25dp);
            textViewBottom.setShadowLayer(1.5f, -1, 1, Color.BLACK);

            TextView textViewTop = new TextView(context);
            textViewTop.setText(R.string.mixingcolors_correctanswer);
            textViewTop.setTextColor(resultColorCorrect);
            textViewTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, _25dp);
            textViewTop.setRotation(180);
            textViewTop.setShadowLayer(1.5f, -1, 1, Color.BLACK);


            RelativeLayout.LayoutParams layoutParamsForTextBottom = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsForTextBottom.setMargins(setMargin, 0, setMargin, setMargin);
            RelativeLayout.LayoutParams layoutParamsForTextTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsForTextTop.setMargins(setMargin, setMargin, setMargin, 0);

            LinearLayout.LayoutParams layoutParamsForCorrectColor = new LinearLayout.LayoutParams(parentLayout.getHeight() / 3, parentLayout.getHeight() / 3);
            layoutParamsForCorrectColor.gravity = Gravity.CENTER;


            linearLayout.addView(textViewTop, layoutParamsForTextTop);
            linearLayout.addView(correctColor, layoutParamsForCorrectColor);
            linearLayout.addView(textViewBottom, layoutParamsForTextBottom);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

            parentLayout.addView(linearLayout, layoutParams);
        }
    }

    private void shuffleStringArray(String[] array) {
        // same method as the shuffle int array in Utils, just that this one shuffle string array

        for (int i=0; i<array.length; i++) {
            int randomPosition = randomGenerator.nextInt(array.length);
            String temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }
    }
}
