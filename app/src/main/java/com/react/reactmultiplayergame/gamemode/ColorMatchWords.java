package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;

import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

/**
 * Created by gan on 1/4/17.
 * Where user pressed when color and words matched each other
 *
 *
 */

public class ColorMatchWords extends GameMode {
    private int levelDifficulty;
    private TextView bottomText, topText, bottomText2, topText2;
    private float currentTextSize;
    private ArrayList<Integer> colorArray = new ArrayList<>();
    private ArrayList<String> colorTextArray = new ArrayList<>();
    // 2 random number correct and wrong to set 2 different colors in mode insane
    private int currentRandomSelection_Correct, currentRandomSelection2_Correct, currentRandomSelection_Wrong, currentRandomSelection2_Wrong;
    // correct = 0, wrong = 1 & 2 randomness for whether this question will be correct ornot. 3 Int so that chance of correct = 33.3%
    private int correct = randomGenerator.nextInt(3);
    // correct2 used for mode insane to cycle whether both words correct, both words wrong, or 1 word correct 1 wrong, hence needing 4 int
    // 0 == both correct, 1 = left correct right wrong, 2 = right correct left wrong, 3 = both wrong. 4 Int chance of correct = 25%
    private int correct2 = randomGenerator.nextInt(4);
    private int color1, color2;
    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        if(levelDifficulty == Constants.MODE_INSANE) {
            player1Question.setText(R.string.colormatchwords_insane);
            player2Question.setText(R.string.colormatchwords_insane);
        } else {
            player1Question.setText(R.string.colormatchwords);
            player2Question.setText(R.string.colormatchwords);
        }
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        this.levelDifficulty = levelDifficulty;

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_colormatchwords);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }


        int _20dp = (int) context.getResources().getDimension(R.dimen._20sdp);
        int _10dp = (int) context.getResources().getDimension(R.dimen._10sdp);
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                colorEasy();
                currentTextSize = context.getResources().getDimension(R.dimen._34ssp);
                break;
            case Constants.MODE_MEDIUM:
                colorMedium();
                currentTextSize = context.getResources().getDimension(R.dimen._34ssp);
                break;
            case Constants.MODE_HARD:
                colorHard();
                currentTextSize = context.getResources().getDimension(R.dimen._34ssp);
                break;
            case Constants.MODE_INSANE:
                colorInsane();
                currentTextSize = context.getResources().getDimension(R.dimen._24ssp);
                break;
        }

        currentRandomSelection_Correct = randomGenerator.nextInt(colorArray.size());

        // account for the case where the random number is the max value, hence -1 from it to ensure no ArrayIndexOutOfBound.
        // otherwise if random number is not the max value, just add 1 to it to ensure number is definitely wrong
        // no need to call another randomGenerator, as +1 or -1 away from it ensures definite 2 separate number
        // calling another randomGenerator may even result in generating same number for both correct and wrong cases
        if(currentRandomSelection_Correct == colorArray.size() -1) {
            currentRandomSelection_Wrong = currentRandomSelection_Correct -1;
        }
        else {
            currentRandomSelection_Wrong = currentRandomSelection_Correct + 1;
        }
        String correctText = colorTextArray.get(currentRandomSelection_Correct);

        bottomText = new TextView(context);
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
        bottomText.setText(correctText);
        bottomText.setGravity(Gravity.CENTER);
        bottomText.setTypeface(Typeface.DEFAULT_BOLD);
        bottomText.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
        if(levelDifficulty == Constants.MODE_INSANE) {
            bottomText.setPadding(_20dp,0,_10dp,0);
        } else {
            bottomText.setPadding(_20dp,0,_20dp,0);
        }
        bottomText.setSingleLine();
        AutofitHelper.create(bottomText);

        topText = new TextView(context);
        topText.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
        topText.setText(correctText);
        topText.setRotation(180);
        topText.setGravity(Gravity.CENTER);
        topText.setTypeface(Typeface.DEFAULT_BOLD);
        topText.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
        if(levelDifficulty == Constants.MODE_INSANE) {
            topText.setPadding(_20dp,0,_10dp,0);
        } else {
            topText.setPadding(_20dp,0,_20dp,0);
        }
        topText.setSingleLine();
        AutofitHelper.create(topText);

        if(levelDifficulty!= Constants.MODE_INSANE) {
            RelativeLayout.LayoutParams bottomTextLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
            bottomTextLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            RelativeLayout.LayoutParams topTextLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
            topTextLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            // correct = randomGenerator.nextInt(2) declared globally
            if(correct ==0) {
                color1 = colorArray.get(currentRandomSelection_Correct);
            }
            else {
                color1 = colorArray.get(currentRandomSelection_Wrong);
            }
            bottomText.setTextColor(color1);
            topText.setTextColor(color1);
            parentLayout.addView(bottomText,bottomTextLayout);
            parentLayout.addView(topText,topTextLayout);
        } else {

            currentRandomSelection2_Correct = randomGenerator.nextInt(colorArray.size());
            // account for the case where the random number is the max value, hence -1 from it to ensure no ArrayIndexOutOfBound.
            // otherwise if random number is not the max value, just add 1 to it to ensure number is definitely wrong
            // no need to call another randomGenerator, as +1 or -1 away from it ensures definite 2 separate number
            // calling another randomGenerator may even result in generating same number for both correct and wrong cases
            if(currentRandomSelection2_Correct == colorArray.size() -1) {
                currentRandomSelection2_Wrong = currentRandomSelection2_Correct -1;
            }
            else {
                currentRandomSelection2_Wrong = currentRandomSelection2_Correct + 1;
            }


            switch (correct2) {
                case 0:
                    //both correct
                    color1 = colorArray.get(currentRandomSelection_Correct);
                    color2 = colorArray.get(currentRandomSelection2_Correct);
                    break;
                case 1:
                    //left correct, right wrong
                    color1 = colorArray.get(currentRandomSelection_Correct);
                    color2 = colorArray.get(currentRandomSelection2_Wrong);
                    break;
                case 2:
                    //left wrong, right correct
                    color1 = colorArray.get(currentRandomSelection_Wrong);
                    color2 = colorArray.get(currentRandomSelection2_Correct);
                    break;
                case 3:
                    //both wrong
                    color1 = colorArray.get(currentRandomSelection_Wrong);
                    color2 = colorArray.get(currentRandomSelection2_Wrong);
                    break;
            }
            String correctText2 = colorTextArray.get(currentRandomSelection2_Correct);
            bottomText2 = new TextView(context);
            bottomText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
            bottomText2.setText(correctText2);
            bottomText2.setGravity(Gravity.CENTER);
            bottomText2.setTypeface(Typeface.DEFAULT_BOLD);
            bottomText2.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
            if(levelDifficulty == Constants.MODE_INSANE) {
                bottomText2.setPadding(_10dp,0,_20dp,0);
            } else {
                bottomText2.setPadding(_20dp,0,_20dp,0);
            }
            bottomText2.setSingleLine();
            AutofitHelper.create(bottomText2);

            topText2 = new TextView(context);
            topText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
            topText2.setText(correctText2);
            topText2.setRotation(180);
            topText2.setGravity(Gravity.CENTER);
            topText2.setTypeface(Typeface.DEFAULT_BOLD);
            topText2.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
            if(levelDifficulty == Constants.MODE_INSANE) {
                topText2.setPadding(_10dp,0,_20dp,0);
            } else {
                topText2.setPadding(_20dp,0,_20dp,0);
            }
            topText2.setSingleLine();
            AutofitHelper.create(topText2);

            RelativeLayout.LayoutParams bottomTextLayout1 = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() / 2);
            bottomTextLayout1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            RelativeLayout.LayoutParams bottomTextLayout2 = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() / 2);
            bottomTextLayout2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            bottomTextLayout2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            RelativeLayout.LayoutParams topTextLayout1 = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() / 2);
            topTextLayout1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            topTextLayout1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            RelativeLayout.LayoutParams topTextLayout2 = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() / 2);
            topTextLayout2.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            bottomText.setTextColor(color1);
            bottomText2.setTextColor(color2);
            topText.setTextColor(color1);
            topText2.setTextColor(color2);

            parentLayout.addView(bottomText,bottomTextLayout1);
            parentLayout.addView(bottomText2,bottomTextLayout2);
            parentLayout.addView(topText,topTextLayout1);
            parentLayout.addView(topText2,topTextLayout2);

        }


    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_colormatchwords);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_colormatchwords;
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
        if(levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            return 2000;
        }

        else return 1000;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }


    @Override
    public boolean getCurrentQuestionAnswer() {
        if(levelDifficulty != Constants.MODE_INSANE) {
            if(correct == 0) return true;
            else return false;
        } else {
            if(correct2 == 0) return true;
            else return false;
        }

    }

    private void colorEasy () {
        // ** Positioning the color and colorTextArray is VERY IMPORTANT.
        // ** Make sure if you add e.g. Color.YELLOW to the 5th element in colorArray.add, add colorTextArray.add("Yellow") to the 5th element AS WELL in colorTextArray.
        // ** This is because text will always be correct. Only the color state will toggle between true or false to the user
        colorArray.add(Color.YELLOW);
        colorArray.add(Color.CYAN);
        colorArray.add(Color.GREEN);
        colorArray.add(Color.BLUE);
        colorArray.add(Color.BLACK);
        colorArray.add(Color.GRAY);
        colorArray.add(Color.RED);
        colorArray.add(Color.parseColor("#FF69B4"));
        colorArray.add(Color.parseColor("#800000"));
        colorArray.add(Color.parseColor("#FFA500"));
        colorArray.add(Color.parseColor("#800080"));

        colorTextArray.add("Yellow");
        colorTextArray.add("Cyan");
        colorTextArray.add("Green");
        colorTextArray.add("Blue");
        colorTextArray.add("Black");
        colorTextArray.add("Gray");
        colorTextArray.add("Red");
        colorTextArray.add("Hot Pink");
        colorTextArray.add("Maroon");
        colorTextArray.add("Orange");
        colorTextArray.add("Purple");
    }

    private void colorMedium(){
        colorEasy();
        colorArray.add(Color.parseColor("#A52A2A"));
        colorArray.add(Color.parseColor("#FFD700"));
        colorArray.add(Color.parseColor("#4B0082"));
        colorArray.add(Color.parseColor("#F0E68C"));
        colorArray.add(Color.parseColor("#E6E6FA"));
        colorArray.add(Color.parseColor("#00FF00"));
        colorArray.add(Color.parseColor("#FF00FF"));
        colorArray.add(Color.parseColor("#808000"));
        colorArray.add(Color.parseColor("#FF6347"));
        colorArray.add(Color.parseColor("#40E0D0"));
        colorArray.add(Color.parseColor("#C0C0C0"));

        colorTextArray.add("Brown");
        colorTextArray.add("Gold");
        colorTextArray.add("Indigo");
        colorTextArray.add("Khaki");
        colorTextArray.add("Lavender");
        colorTextArray.add("Lime");
        colorTextArray.add("Magenta");
        colorTextArray.add("Olive");
        colorTextArray.add("Tomato");
        colorTextArray.add("Turquoise");
        colorTextArray.add("Silver");


    }

    private void colorHard(){
        colorMedium();
        colorArray.add(Color.parseColor("#F5F5DC"));
        colorArray.add(Color.parseColor("#D2691E"));
        colorArray.add(Color.parseColor("#DC143C"));
        colorArray.add(Color.parseColor("#B22222"));
        colorArray.add(Color.parseColor("#A0522D"));
        colorArray.add(Color.parseColor("#008080"));
        colorArray.add(Color.parseColor("#F5DEB3"));
        colorArray.add(Color.parseColor("#D2B48C"));
        colorArray.add(Color.parseColor("#663399"));

        colorTextArray.add("Beige");
        colorTextArray.add("Chocolate");
        colorTextArray.add("Crimson");
        colorTextArray.add("FireBrick");
        colorTextArray.add("Sienna");
        colorTextArray.add("Teal");
        colorTextArray.add("Wheat");
        colorTextArray.add("Tan");
        colorTextArray.add("Rebecca Purple");

    }

    private void colorInsane(){
        colorHard();
        colorArray.add(Color.parseColor("#FF00FF"));
        colorArray.add(Color.parseColor("#DEB887"));
        colorArray.add(Color.parseColor("#7FFF00"));
        colorArray.add(Color.parseColor("#FFF8DC"));
        colorArray.add(Color.parseColor("#D8BFD8"));
        colorArray.add(Color.parseColor("#9400D3"));
        colorArray.add(Color.parseColor("#DAA520"));
        colorArray.add(Color.parseColor("#FAF0E6"));
        colorArray.add(Color.parseColor("#191970"));
        colorArray.add(Color.parseColor("#FFDEAD"));
        colorArray.add(Color.parseColor("#FFDAB9"));
        colorArray.add(Color.parseColor("#CD853F"));
        colorArray.add(Color.parseColor("#DDA0DD"));
        colorArray.add(Color.parseColor("#4682B4"));
        colorArray.add(Color.parseColor("#FFE4E1"));
        colorArray.add(Color.parseColor("#7FFFD4"));
        colorArray.add(Color.parseColor("#5F9EA0"));
        colorArray.add(Color.parseColor("#DA70D6"));
        colorArray.add(Color.parseColor("#B0E0E6"));
        colorArray.add(Color.parseColor("#FFE4C4"));
        colorArray.add(Color.parseColor("#FF7F50"));

        colorTextArray.add("Fuchsia");
        colorTextArray.add("BurlyWood");
        colorTextArray.add("Chartreuse");
        colorTextArray.add("Cornsilk");
        colorTextArray.add("Thistle");
        colorTextArray.add("Dark Violet");
        colorTextArray.add("GoldenRod");
        colorTextArray.add("Linen");
        colorTextArray.add("Midnight Blue");
        colorTextArray.add("Navajo White");
        colorTextArray.add("PeachPuff");
        colorTextArray.add("Peru");
        colorTextArray.add("Plum");
        colorTextArray.add("SteelBlue");
        colorTextArray.add("Misty Rose");
        colorTextArray.add("Aquamarine");
        colorTextArray.add("CadetBlue");
        colorTextArray.add("Orchid");
        colorTextArray.add("Powder Blue");
        colorTextArray.add("Bisque");
        colorTextArray.add("Coral");
    }
}
