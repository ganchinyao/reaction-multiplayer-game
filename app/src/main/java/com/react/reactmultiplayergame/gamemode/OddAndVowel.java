package com.react.reactmultiplayergame.gamemode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.react.reactmultiplayergame.helper.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gan on 31/3/17.
 */

public class OddAndVowel extends GameMode {
    private TextView numberBottom, numberTop, vowelBottom, vowelTop;
    private String[] letterArray = {"a", "e", "i", "o", "u", "a", "b", "c", "d", "e", "f", "g", "a", "e", "i", "o", "u", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "a", "e", "i", "o", "u", "u", "v", "w", "x", "y", "z", "a", "e", "i", "o", "u"};
    private int currentNumber;
    private String currentString;
    private int marginSize = Utils.dpToPx(12);
    private int marginSize1 = Utils.dpToPx(randomGenerator.nextInt(20)) + 20;
    private int marginSize2 = Utils.dpToPx(randomGenerator.nextInt(30)) + 20;
    private int marginSizeBig = Utils.dpToPx(randomGenerator.nextInt(140) + 60);
    private float currentTextSize;
    private float moveXby_numberBottom, moveYby_numberBottom, moveX_vowelBottom, moveY_vowelBottom, moveX_numberTop, moveY_numberTop, moveX_vowelTop, moveY_vowelTop;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private Context context;
    private RandomTimerTask randomTimerTask1, randomTimerTask2;
    private RelativeLayout.LayoutParams numberTopLayout, vowelTopLayout, topwrapperLayout;
    private LinearLayout topLayoutWrapper;
    private int levelDifficulty;
    private float moveXArray [] = new float [2];
    private float moveYArray [] = new float [2];
    private int allCaps = randomGenerator.nextInt(2);
    private String [] colorArray = {};

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.oddandvowel);
        player2Question.setText(R.string.oddandvowel);
    }

    @Override
    public void setGameContent(LinearLayout rootView, final RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.context = context;
        this.levelDifficulty = levelDifficulty;

        //note here the colorArray we used array, not arrayList
        colorArray = new String []{
                "#FF5252", //red
                "#F48FB1", //pink
                "#C51162", //dark pink
                "#FF69B4", //Hot pink
                "#CE93D8", //light purple
                "#EA80FC", //light purple
                "#D1C4E9", //light violet
                "#B388FF", //light violet
                "#1A237E", //dark blue
                "#81D4FA", //light blue
                "#18FFFF", //cyan
                "#004D40", //dark green blue
                "#80CBC4", //light green blue
                "#00E676", //bright green
                "#76FF03", //bright green
                "#B2FF59", //bright yellow green
                "#CDDC39", //lime
                "#EEFF41", //bright lime
                "#FFEB3B", //bright yellow
                "#F9A825", //light orange
                "#FF6F00", //dark orange
                "#FF9800", //orange
                "#FF6E40", //light orange
                "#BCAAA4", //light brown
                "#CFD8DC", //light blue gray
                "#90A4AE", //dark blue gray
                "#000000", //white
                "#FFFFFF", //black
                "#FFE4C4", //bisque aka beige
                "#DEB887", //burlywood aka beige
                "#DAA520", //goldenrod
                "#9400D3", //dark violet
                "#B22222", //firebrick
                "#800000", //maroon
                "#40E0D0", //Turquiose
                "#F0E68C" //khaki
        };

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                currentNumber = randomGenerator.nextInt(21);
                currentString = letterArray[randomGenerator.nextInt(letterArray.length)];
                currentTextSize = context.getResources().getDimension(R.dimen._42ssp);
                break;
            case Constants.MODE_MEDIUM:
                currentNumber = randomGenerator.nextInt(51) + 10;
                currentString = letterArray[randomGenerator.nextInt(letterArray.length)];
                currentTextSize = context.getResources().getDimension(R.dimen._42ssp);
                break;
            case Constants.MODE_HARD:
                currentNumber = randomGenerator.nextInt(221) + 50;
                currentString = letterArray[randomGenerator.nextInt(letterArray.length)];
                currentTextSize = context.getResources().getDimension(R.dimen._36ssp);
                break;
            case Constants.MODE_INSANE:
                currentNumber = randomGenerator.nextInt(500) + 500;
                currentString = letterArray[randomGenerator.nextInt(letterArray.length)];
                currentTextSize = context.getResources().getDimension(R.dimen._36ssp);
                break;

        }
        numberBottom = new TextView(context);
        numberBottom.setText("" + currentNumber);
        numberBottom.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);

        vowelBottom = new TextView(context);
        vowelBottom.setText("" + currentString);
        vowelBottom.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);

        RelativeLayout.LayoutParams wrapperLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
        wrapperLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        RelativeLayout.LayoutParams numberBottomLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams vowelBottomLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout bottomLayoutWrapper = new LinearLayout(context);

        if (levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            bottomLayoutWrapper.setGravity(Gravity.CENTER);
            bottomLayoutWrapper.setOrientation(LinearLayout.HORIZONTAL);

            numberBottomLayout.setMargins(0, 0, marginSize, 0);

            vowelBottomLayout.setMargins(marginSize, 0, 0, 0);

            wrapperLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        ///// Top copy /////
        // Do not need 2 copy if Mode is insane as only 1 copy is needed to move and rotate for both users
        if (levelDifficulty != Constants.MODE_INSANE) {
            numberTop = new TextView(context);
            numberTop.setText("" + currentNumber);
            numberTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
            numberTop.setRotation(180);

            vowelTop = new TextView(context);
            vowelTop.setText("" + currentString);
            vowelTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
            vowelTop.setRotation(180);

            topLayoutWrapper = new LinearLayout(context);
            numberTopLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            vowelTopLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            topwrapperLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
            topwrapperLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            if(levelDifficulty != Constants.MODE_HARD) {
                topLayoutWrapper.setGravity(Gravity.CENTER);
                topLayoutWrapper.setOrientation(LinearLayout.HORIZONTAL);
                numberTopLayout.setMargins(marginSize, 0, 0, 0);
                vowelTopLayout.setMargins(0, 0, marginSize, 0);
                topwrapperLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
            }

            if(levelDifficulty == Constants.MODE_HARD) {
                // center line
                View centerLine = new View(context);
                centerLine.setBackgroundColor(Color.LTGRAY);
                RelativeLayout.LayoutParams layoutParamsCenterLine = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(1));
                layoutParamsCenterLine.addRule(RelativeLayout.CENTER_IN_PARENT);
                parentLayout.addView(centerLine, layoutParamsCenterLine);
            }

        }

        if(levelDifficulty == Constants.MODE_EASY) {
            // easy no change color, i.e. one fix color. Only medium/hard/insane then change text color
            int textColor = Color.parseColor("#fafafa");
            numberTop.setTextColor(textColor);
            numberBottom.setTextColor(textColor);
            vowelTop.setTextColor(textColor);
            vowelBottom.setTextColor(textColor);
        }
        if(levelDifficulty == Constants.MODE_MEDIUM) {
            // here we allocate a var for randomNumber1&2 to ensure that it is a same number, and randomColor will be the same for top and bottom
            int randomNumber1 = randomGenerator.nextInt(colorArray.length);
            int randomNumber2 = randomGenerator.nextInt(colorArray.length);
            int randomColor1 = Color.parseColor(colorArray[randomNumber1]);
            int randomColor2 = Color.parseColor(colorArray[randomNumber2]);
            numberTop.setTextColor(randomColor1);
            numberBottom.setTextColor(randomColor1);
            vowelTop.setTextColor(randomColor2);
            vowelBottom.setTextColor(randomColor2);
            if(allCaps==0) {
                vowelTop.setAllCaps(true);
                vowelBottom.setAllCaps(true);
            }
            else {
                vowelTop.setAllCaps(false);
                vowelBottom.setAllCaps(false);
            }
        }


        //  animate_numberBottom(numberBottom,parentLayout,Utils.dpToPx(20),Utils.dpToPx(15),0);

        if(levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            bottomLayoutWrapper.addView(numberBottom, numberBottomLayout);
            bottomLayoutWrapper.addView(vowelBottom, vowelBottomLayout);
            parentLayout.addView(bottomLayoutWrapper, wrapperLayout);
            topLayoutWrapper.addView(vowelTop, vowelTopLayout);
            topLayoutWrapper.addView(numberTop, numberTopLayout);
            parentLayout.addView(topLayoutWrapper, topwrapperLayout);
        }

        else if(levelDifficulty == Constants.MODE_HARD) {
            parentLayout.addView(numberBottom, numberBottomLayout);
            parentLayout.addView(vowelBottom, vowelBottomLayout);
            parentLayout.addView(vowelTop, vowelTopLayout);
            parentLayout.addView(numberTop,numberTopLayout);
        }

        else {
            parentLayout.addView(numberBottom, numberBottomLayout);
            parentLayout.addView(vowelBottom, vowelBottomLayout);
        }

        switch (levelDifficulty) {
            case Constants.MODE_HARD: {
                float _15dp = context.getResources().getDimension(R.dimen._18sdp);
                float _20dp = context.getResources().getDimension(R.dimen._22sdp);
                float _25dp = context.getResources().getDimension(R.dimen._26sdp);
                numberTopLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                numberTopLayout.setMargins(0, marginSize1, marginSizeBig, 0);
                vowelTopLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                vowelTopLayout.setMargins(marginSize2, marginSize2, 0, 0);
                numberBottomLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                numberBottomLayout.setMargins(marginSizeBig, 0, 0, marginSize1);
                vowelBottomLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                vowelBottomLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                vowelBottomLayout.setMargins(marginSize2, 0, 0, marginSize2);
                Utils.canAnimate = true;
                animate_numberBottom(numberBottom, parentLayout, _20dp, _15dp, 0);
                animate_vowelBottom(vowelBottom, parentLayout, -_25dp, _15dp, 0);
                animate_numberTop(numberTop, parentLayout, _20dp, _15dp, 0);
                animate_vowelTop(vowelTop, parentLayout, -_25dp, _15dp, 0);

                randomTimerTask1 = new RandomTimerTask(1);
                randomTimerTask2 = new RandomTimerTask(2);
                randomTimerTask1.run();
                randomTimerTask2.run();
                break;
            }
            case Constants.MODE_INSANE: {
                float _35dp = context.getResources().getDimension(R.dimen._28sdp);
                float _25dp = context.getResources().getDimension(R.dimen._22sdp);
                int _5dp = (int) context.getResources().getDimension(R.dimen._5sdp);
                int [] paramPosition = {RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_IN_PARENT, RelativeLayout.CENTER_HORIZONTAL
                , RelativeLayout.CENTER_VERTICAL, RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_TOP};
                numberBottomLayout.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                numberBottomLayout.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                vowelBottomLayout.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                vowelBottomLayout.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                Utils.canAnimate = true;

                animate_InsaneMode(0,vowelBottom, parentLayout, -_35dp, _25dp, _5dp);
                animate_InsaneMode(1,numberBottom, parentLayout, -_25dp + 11, _25dp, _5dp);
                randomTimerTask1 = new RandomTimerTask(1);
                randomTimerTask2 = new RandomTimerTask(2);
                randomTimerTask1.run();
                randomTimerTask2.run();
                break;
            }


        }

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_oddandvowel);
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
        return R.raw.gamemode_oddandvowel;
    }

    @Override
    public boolean requireDefaultTimer() {
        if(levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            return true;
        }
        else return false;
    }

    @Override
    public boolean requireATimer() {
        if(levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            return true;
        }
        else return false;
    }

    @Override
    public boolean wantToShowTimer() {
        if(levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            return true;
        }
        else return false;
    }

    @Override
    public int customTimerDuration() {
        return 0;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {
        Utils.canAnimate = false;
        if(timer1 != null && timer2!= null) {
            timer1.cancel();
            timer2.cancel();
            timer1 = null;
            timer2 = null;
        }
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        timer1.cancel();
        timer2.cancel();
        Utils.canAnimate = false;
        if(currentNumber %2 != 0 && ( currentString.equalsIgnoreCase("a") || currentString.equalsIgnoreCase("e") || currentString.equalsIgnoreCase("i")
                                    || currentString.equalsIgnoreCase("o") || currentString.equalsIgnoreCase("u"))) {
            return true;
        }
        else return false;
    }

    @Override
    public boolean gameContent_RequireRectangularWhiteStroke() {
        return true;
    }

    private void animate_numberBottom(final TextView numberBottom, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate) {
        moveXby_numberBottom = move_X_by;
        moveYby_numberBottom = move_Y_by;
        // add + Utils.dpToPx(1) so that it will turn to the other direction 1dp earlier, to prevent the case whereby it got "stuck",
        // since the textView is constantly rotating and the point it hits the edge might not be its top left position
        if (numberBottom.getX() > buttonContainer.getWidth() - numberBottom.getWidth()) {
            if(moveXby_numberBottom >0) {
                moveXby_numberBottom = -moveXby_numberBottom;
            }
        } else if(numberBottom.getX() < 0) {
            moveXby_numberBottom = Math.abs(moveXby_numberBottom);
        }

        if (((numberBottom.getY() - buttonContainer.getHeight()/2) + numberBottom.getHeight()) > buttonContainer.getHeight() / 2) {
            if(moveYby_numberBottom >0) {
                moveYby_numberBottom = -moveYby_numberBottom;
            }
        } else if(numberBottom.getY() < buttonContainer.getHeight()/2) {
            moveYby_numberBottom = Math.abs(moveYby_numberBottom);
        }

        numberBottom.animate().translationXBy(moveXby_numberBottom).translationYBy(moveYby_numberBottom).setDuration(200).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                animate_numberBottom(numberBottom, buttonContainer, moveXby_numberBottom, moveYby_numberBottom, rotate);
            }
        }).start();
    }

    private void animate_vowelBottom(final TextView vowelBottom, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate) {
        moveX_vowelBottom = move_X_by;
        moveY_vowelBottom = move_Y_by;
        if (vowelBottom.getX() > buttonContainer.getWidth() - vowelBottom.getWidth()) {
            if(moveX_vowelBottom >0) {
                moveX_vowelBottom = -moveX_vowelBottom;
            }
        } else if(vowelBottom.getX() < 0) {
            moveX_vowelBottom = Math.abs(moveX_vowelBottom);
        }

        if ((vowelBottom.getY() - buttonContainer.getHeight()/2) +vowelBottom.getHeight() > buttonContainer.getHeight() / 2) {
            if(moveY_vowelBottom >0){
                moveY_vowelBottom = -moveY_vowelBottom;
            }
        } else if(vowelBottom.getY() < buttonContainer.getHeight()/2) {
            moveY_vowelBottom = Math.abs(moveY_vowelBottom );
        }

        vowelBottom.animate().translationXBy(moveX_vowelBottom).translationYBy(moveY_vowelBottom).setDuration(200).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                animate_vowelBottom(vowelBottom, buttonContainer, moveX_vowelBottom, moveY_vowelBottom, rotate);
            }
        }).start();
    }

    private void animate_numberTop(final TextView numberTop, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate) {
        moveX_numberTop = move_X_by;
        moveY_numberTop = move_Y_by;
        if (numberTop.getX() > buttonContainer.getWidth() - numberTop.getWidth()) {
            if(moveX_numberTop >0) {
                moveX_numberTop = - moveX_numberTop;
            }
        } else if(numberTop.getX() < 0){
            moveX_numberTop = Math.abs(moveX_numberTop);
        }

        if ((numberTop.getY() + numberTop.getHeight()) > buttonContainer.getHeight() / 2 ) {
            if(moveY_numberTop >0){
                moveY_numberTop = -moveY_numberTop;
            }
        } else if(numberTop.getY() < 0) {
            moveY_numberTop = Math.abs(moveY_numberTop);
        }

        numberTop.animate().translationXBy( moveX_numberTop).translationYBy(moveY_numberTop).setDuration(200).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                animate_numberTop(numberTop, buttonContainer,  moveX_numberTop, moveY_numberTop, rotate);
            }
        }).start();
    }

    private void animate_vowelTop(final TextView vowelTop, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate) {
        moveX_vowelTop = move_X_by;
        moveY_vowelTop = move_Y_by;
        if (vowelTop.getX() > buttonContainer.getWidth() - vowelTop.getWidth()) {
            if(moveX_vowelTop >0) {
                moveX_vowelTop = - moveX_vowelTop;
            }
        } else if ( vowelTop.getX() < 0) {
            moveX_vowelTop = Math.abs(moveX_vowelTop);
        }

        if ((vowelTop.getY() + vowelTop.getHeight()) > buttonContainer.getHeight() / 2) {
            if(moveY_vowelTop >0) {
                moveY_vowelTop = -moveY_vowelTop;
            }
        } else if(vowelTop.getY() < 0) {
            moveY_vowelTop = Math.abs(moveY_vowelTop);
        }

        vowelTop.animate().translationXBy( moveX_vowelTop).translationYBy(moveY_vowelTop).setDuration(200).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                animate_vowelTop(vowelTop, buttonContainer, moveX_vowelTop, moveY_vowelTop, rotate);
            }
        }).start();
    }

    private void animate_InsaneMode(final int number, final TextView vowelBottom, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate) {
        moveXArray[number] = move_X_by;
        moveYArray[number] = move_Y_by;
        if (vowelBottom.getX() > buttonContainer.getWidth() - vowelBottom.getWidth()) {
            // want the vowel to move left, aka moveX be negative, hence we first check if it is positive, then turn it to negative.
            // We CANNOT use moveXArray[number] = - moveXArray[number], becos moveXArray[number] may be negative in the first place, and this will result in the "stuck"
            // situation whereby it will keep bounce left and right at the same position
            if(moveXArray[number] >0) {
                // positive, hence return negative
                moveXArray[number] = - moveXArray[number];
            }
            // no need else, as else means it is negative, and we do nth, since we want negative X to move left
        } else if(vowelBottom.getX() <0) {
            // want it to move in right direction, aka x be positive
            moveXArray[number] = Math.abs(moveXArray[number]);
        }

        if ((vowelBottom.getY()+vowelBottom.getHeight()) > buttonContainer.getHeight()) {
            // want it to move up, aka negative Y
            if(moveYArray[number] >0) {
                moveYArray[number] = - moveYArray[number];
            } // no need else, since else means moveYArray[number] is negative, and no need do anything
        } else if (vowelBottom.getY() <0) {
            // want it to move down, aka positive y
            moveYArray[number] = Math.abs(moveYArray[number]);
        }

        vowelBottom.animate().translationXBy(moveXArray[number]).translationYBy(moveYArray[number]).setDuration(100).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                    animate_InsaneMode(number, vowelBottom, buttonContainer, moveXArray[number], moveYArray[number], rotate);
            }
        }).start();
    }



    private class RandomTimerTask extends TimerTask {
        private int timerNumber;
        private int caps;

        RandomTimerTask(int timerNumber) {
            this.timerNumber = timerNumber;
        }
        @Override
        public void run() {

            switch(timerNumber){
                case 1: {
                    final int randomColor = Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]);
                    int delay = ((randomGenerator.nextInt(4))) * 1000;
                    timer1.schedule(new RandomTimerTask(timerNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch(levelDifficulty) {
                                case Constants.MODE_MEDIUM:
                                    currentNumber = randomGenerator.nextInt(51) + 10;
                                    numberTop.setText("" + currentNumber);
                                    numberTop.setTextColor(randomColor);
                                    break;
                                case Constants.MODE_HARD:
                                    currentNumber = randomGenerator.nextInt(221) + 50;
                                    numberTop.setText("" + currentNumber);
                                    numberTop.setTextColor(randomColor);
                                    break;
                                case Constants.MODE_INSANE:
                                    currentNumber = randomGenerator.nextInt(500) + 500;
                                    break;
                            }
                            numberBottom.setText("" + currentNumber);
                            numberBottom.setTextColor(randomColor);
                        }
                    });
                    break;
                }
                case 2: {
                    final int randomColor = Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]);
                    caps = randomGenerator.nextInt(2);
                    int delay = ((randomGenerator.nextInt(4))) * 1000;
                    timer2.schedule(new RandomTimerTask(timerNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentString = letterArray[randomGenerator.nextInt(letterArray.length)];
                            if (levelDifficulty != Constants.MODE_INSANE) {
                                vowelTop.setText(""+currentString);
                                vowelTop.setTextColor(randomColor);
                                if(caps == 0) vowelTop.setAllCaps(true);
                                else vowelTop.setAllCaps(false);
                            }
                            vowelBottom.setText(""+currentString);
                            vowelBottom.setTextColor(randomColor);
                            if(caps == 0) vowelBottom.setAllCaps(true);
                            else vowelBottom.setAllCaps(false);
                        }
                    });
                    break;
                }

            }
        }
    }

}
