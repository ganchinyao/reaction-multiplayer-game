package com.react.reactmultiplayergame.gamemode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gan on 29/3/17.
 * Color Stripe game Mode
 */

public class FiveColorsPanel extends GameMode {
    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    private Timer timer3 = new Timer();
    private Timer timer4 = new Timer();
    private Timer timer5 = new Timer();
    private View view1, view2, view3, view4, view5, view6, view7, view8, view9, view10, view11;
    private View [] viewArray = {view1, view2, view3, view4, view5, view6, view7, view8, view9, view10, view11};
    private RandomTimerTask randomTimer1, randomTimer2, randomTimer3, randomTimer4, randomTimer5, randomTimer6, randomTimer7, randomTimer8, randomTimer9, randomTimer10, randomTimer11;
    private RandomTimerTask [] randomTimerArray = {randomTimer1, randomTimer2, randomTimer3, randomTimer4, randomTimer5, randomTimer6, randomTimer7, randomTimer8, randomTimer9, randomTimer10, randomTimer11};
    private ArrayList<Integer> colorArray = new ArrayList<>();
    private ArrayList<Integer> paletteTotal = new ArrayList<>();
    private ArrayList<Integer> calculateAnswerFinal = new ArrayList<>();
    private Context context;
    private LinearLayout panelContainer;
    private int levelDifficulty;
    private ArrayList<Integer> calculateAnswer1 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer2 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer3 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer4 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer5 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer6 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer7 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer8 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer9 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer10 = new ArrayList<>();
    private ArrayList<Integer> calculateAnswer11 = new ArrayList<>();
    private RelativeLayout parentLayout;
    private Set<Integer> setUniqueNumbers = new LinkedHashSet<Integer>();
    private boolean currentQuestionAnswer = false;
    // player need at least this number of color to win. Used in setCurrentQuestion and calculateNumberOfUniqueColors
    private int easy_numbertoWin = 3;
    private int medium_numbertoWin = 4;
    private int hard_numbertoWin = 6;
    private int insane_numbertoWin = 7;
    private int playerThatTouched;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                player1Question.setText(context.getString(R.string.fivecolorspanel1) + " " + easy_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                player2Question.setText(context.getString(R.string.fivecolorspanel1) + " " + easy_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                break;
            case Constants.MODE_MEDIUM:
                player1Question.setText(context.getString(R.string.fivecolorspanel1) + " " + medium_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                player2Question.setText(context.getString(R.string.fivecolorspanel1) + " " + medium_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                break;
            case Constants.MODE_HARD:
                player1Question.setText(context.getString(R.string.fivecolorspanel1) + " " + hard_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                player2Question.setText(context.getString(R.string.fivecolorspanel1) + " " + hard_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                break;
            case Constants.MODE_INSANE:
                player1Question.setText(context.getString(R.string.fivecolorspanel1) + " " + insane_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                player2Question.setText(context.getString(R.string.fivecolorspanel1) + " " + insane_numbertoWin + " " + context.getString(R.string.fivecolorspanel2));
                break;
        }
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.context = context;
        this.levelDifficulty = levelDifficulty;
        this.parentLayout = parentLayout;
        // all the available colors to choose, and shuffle it to add into colorArray which will be the color display
        paletteTotal.add(Color.parseColor("#FF1744"));
        paletteTotal.add(Color.parseColor("#FF9100"));
        paletteTotal.add(Color.parseColor("#FFEB3B"));
        paletteTotal.add(Color.parseColor("#000000"));
        paletteTotal.add(Color.parseColor("#00796B"));
        paletteTotal.add(Color.parseColor("#00B0FF"));
        paletteTotal.add(Color.parseColor("#E040FB"));
        paletteTotal.add(Color.parseColor("#18FFFF"));
        paletteTotal.add(Color.parseColor("#EEFF41"));
        paletteTotal.add(Color.parseColor("#536DFE"));
        paletteTotal.add(Color.parseColor("#78909C"));
        paletteTotal.add(Color.parseColor("#9CCC65"));
        paletteTotal.add(Color.parseColor("#3F51B5"));
        paletteTotal.add(Color.parseColor("#A5D6A7"));
        paletteTotal.add(Color.parseColor("#80CBC4"));
        Collections.shuffle(paletteTotal);

        panelContainer = new LinearLayout(context);
        panelContainer.setOrientation(LinearLayout.HORIZONTAL);

        switch(levelDifficulty){
            case Constants.MODE_EASY:
                // Can safely add more to colorArray.add or remove elements from it, as answer and animating colors make use of colorArray.size() to auto account for size
                colorArray.add(paletteTotal.get(0));
                colorArray.add(paletteTotal.get(1));
                colorArray.add(paletteTotal.get(2));
                // ** WHENEVER CHANING number of views , e.g 3 in this case, or in MEDIUM/HARD/INSANE,
                // If want to change number of views, change also:
                // ** Add more view & viewArray, add more randomTimer & randomTimerArray, add more ArrayList<>calculateAnswer,
                // ** Add more case to RandomTimerTask class below,
                // ** Edit calculateNumberOfUniqueColors method to correspond to number of views
                // ** change initialiseView param to match the new number of views
                initialiseView(4,parentLayout);
                break;
            case Constants.MODE_MEDIUM:
                colorArray.add(paletteTotal.get(0));
                colorArray.add(paletteTotal.get(1));
                colorArray.add(paletteTotal.get(2));
                colorArray.add(paletteTotal.get(3));
                initialiseView(6,parentLayout);

                break;
            case Constants.MODE_HARD:
                colorArray.add(paletteTotal.get(0));
                colorArray.add(paletteTotal.get(1));
                colorArray.add(paletteTotal.get(2));
                colorArray.add(paletteTotal.get(3));
                colorArray.add(paletteTotal.get(4));
                colorArray.add(paletteTotal.get(5));
                colorArray.add(paletteTotal.get(6));
                colorArray.add(paletteTotal.get(7));
                initialiseView(8,parentLayout);
                break;
            case Constants.MODE_INSANE:
                colorArray.add(paletteTotal.get(0));
                colorArray.add(paletteTotal.get(1));
                colorArray.add(paletteTotal.get(2));
                colorArray.add(paletteTotal.get(3));
                colorArray.add(paletteTotal.get(4));
                colorArray.add(paletteTotal.get(5));
                colorArray.add(paletteTotal.get(6));
                colorArray.add(paletteTotal.get(7));
                // do not initialise more than 11, as randomTimerArray and viewArray only contains 11 elements. Setting more than 10 will result in arrayIndexOutOfBoundsException
                // unless manually add more elements to these arrays above
                initialiseView(11,parentLayout);
                break;
        }

    }

    private void initialiseView(int numberOfViews, RelativeLayout parentLayout){
        for(int i=0; i<numberOfViews; i++){
            viewArray[i] = new View(context);
            // impt to add +1 to the layoutParam width, otherwise it will truncate down and not match the parent full width.
            // here +1 to all the views will result in a slight overshoot original width, but thats ok
            viewArray[i].setLayoutParams(new ViewGroup.LayoutParams(parentLayout.getWidth()/numberOfViews +1, ViewGroup.LayoutParams.MATCH_PARENT));
            panelContainer.addView(viewArray[i]);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parentLayout.addView(panelContainer, layoutParams);

        for(int i=0; i<numberOfViews; i++) {
            randomTimerArray[i] = new RandomTimerTask(i+1);
            randomTimerArray[i].run();
        }
    }

    private void createAnswerPopUp(){
        RelativeLayout.LayoutParams answerBottomParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        answerBottomParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        answerBottomParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        answerBottomParam.setMargins(0,0,0,parentLayout.getHeight()/16);

        RelativeLayout.LayoutParams answerTopParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        answerTopParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        answerTopParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        answerTopParam.setMargins(0,parentLayout.getHeight()/16,0,0);


        TextView answerBottomText = new TextView(context);
        answerBottomText.setText("" + setUniqueNumbers.size());
        answerBottomText.setTextColor(Color.parseColor("#FFFFFF"));
        answerBottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._48ssp));
        answerBottomText.setBackgroundColor(ContextCompat.getColor(context, R.color.gameContent_backgroundColorPopUpWindow));
        answerBottomText.setPadding(parentLayout.getWidth()/8,0,parentLayout.getWidth()/8,0);

        TextView answerTopText = new TextView(context);
        answerTopText.setText("" + setUniqueNumbers.size());
        answerTopText.setTextColor(Color.parseColor("#FFFFFF"));
        answerTopText.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._48ssp));
        answerTopText.setBackgroundColor(ContextCompat.getColor(context, R.color.gameContent_backgroundColorPopUpWindow));
        answerTopText.setPadding(parentLayout.getWidth()/8,0,parentLayout.getWidth()/8,0);
        answerTopText.setRotation(180);

        parentLayout.addView(answerBottomText, answerBottomParam);
        parentLayout.addView(answerTopText, answerTopParam);
    }
    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_fivecolorspanel);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER1;
        cancelAllTimer();
        calculateNumberOfUniqueColors();
        createAnswerPopUp();
        return true;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER2;
        cancelAllTimer();
        calculateNumberOfUniqueColors();
        createAnswerPopUp();
        return true;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER3;
        cancelAllTimer();
        calculateNumberOfUniqueColors();
        createAnswerPopUp();
        return true;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER4;
        cancelAllTimer();
        calculateNumberOfUniqueColors();
        createAnswerPopUp();
        return true;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_fivecolorspanel;
    }

    @Override
    public boolean requireDefaultTimer() {
        return false;
    }

    @Override
    public boolean requireATimer() {
        return false;
    }

    @Override
    public boolean wantToShowTimer() {
        return false;
    }

    @Override
    public int customTimerDuration() {
        return 0;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {
        cancelAllTimer();
    }

    @Override
    public boolean gameContent_RequireRectangularWhiteStroke() {
        return true;
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return currentQuestionAnswer;
    }

    @Override
    public int playerThatTouched() {
        return playerThatTouched;
    }

    private void cancelAllTimer(){
        if(timer != null && timer2!= null && timer3!= null && timer4!= null && timer5!= null) {
            timer.cancel();
            timer2.cancel();
            timer3.cancel();
            timer4.cancel();
            timer5.cancel();
            timer = null;
            timer2= null;
            timer3= null;
            timer4= null;
            timer5= null;
        }
    }

    private void calculateNumberOfUniqueColors(){

        // *** UPDATE calculateAnswerFinal.add(x) according to numberOfViews of each game mode.
        // ** e.g. Mode.Medium has 5 color stripes, hence add 5 elements to them.
        // ** failure to compute correctly will result in ArrayIndexOutOfBounds since calling calculateAnswerX.get(0) will be invalid
        calculateAnswerFinal.add(calculateAnswer1.get(0));
        calculateAnswerFinal.add(calculateAnswer2.get(0));
        calculateAnswerFinal.add(calculateAnswer3.get(0));
        calculateAnswerFinal.add(calculateAnswer4.get(0));

        switch (levelDifficulty){
            case Constants.MODE_EASY:
                setUniqueNumbers.addAll(calculateAnswerFinal);
                if(setUniqueNumbers.size() >= easy_numbertoWin) currentQuestionAnswer = true;
                break;
            case Constants.MODE_MEDIUM:
                calculateAnswerFinal.add(calculateAnswer5.get(0));
                calculateAnswerFinal.add(calculateAnswer6.get(0));
                setUniqueNumbers.addAll(calculateAnswerFinal);
                if(setUniqueNumbers.size() >= medium_numbertoWin) currentQuestionAnswer = true;
                break;
            case Constants.MODE_HARD:
                calculateAnswerFinal.add(calculateAnswer5.get(0));
                calculateAnswerFinal.add(calculateAnswer6.get(0));
                calculateAnswerFinal.add(calculateAnswer7.get(0));
                calculateAnswerFinal.add(calculateAnswer8.get(0));
                setUniqueNumbers.addAll(calculateAnswerFinal);
                if(setUniqueNumbers.size() >= hard_numbertoWin) currentQuestionAnswer = true;
                break;
            case Constants.MODE_INSANE:
                calculateAnswerFinal.add(calculateAnswer5.get(0));
                calculateAnswerFinal.add(calculateAnswer6.get(0));
                calculateAnswerFinal.add(calculateAnswer7.get(0));
                calculateAnswerFinal.add(calculateAnswer8.get(0));
                calculateAnswerFinal.add(calculateAnswer9.get(0));
                calculateAnswerFinal.add(calculateAnswer10.get(0));
                calculateAnswerFinal.add(calculateAnswer11.get(0));
                setUniqueNumbers.addAll(calculateAnswerFinal);
                if(setUniqueNumbers.size() >= insane_numbertoWin) currentQuestionAnswer = true;
                break;
        }

    }

    private class RandomTimerTask extends TimerTask {
        private int panelNumber;

        RandomTimerTask(int panelNumber) {
            this.panelNumber = panelNumber;
        }
        @Override
        public void run() {

            switch(panelNumber){
                case 1: {
                    int delay = ((randomGenerator.nextInt(4)) + 2) * 1000;
                    timer.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[0] != null)
                                viewArray[0].setBackgroundColor(colorArray.get(i));
                            calculateAnswer1.clear();
                            calculateAnswer1.add(i);
                        }
                    });
                    break;
                }
                case 2: {
                    int delay = ((randomGenerator.nextInt(4)) + 2) * 1000;
                    timer2.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[1] != null)
                                viewArray[1].setBackgroundColor(colorArray.get(i));
                            calculateAnswer2.clear();
                            calculateAnswer2.add(i);
                        }
                    });
                    break;
                }

                case 3: {
                    int delay = ((randomGenerator.nextInt(4)) + 2) * 1000;
                    timer3.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[2] != null)
                                viewArray[2].setBackgroundColor(colorArray.get(i));
                            calculateAnswer3.clear();
                            calculateAnswer3.add(i);
                        }
                    });
                    break;
                }

                case 4: {
                    int delay = ((randomGenerator.nextInt(4)) + 2) * 1000;
                    timer4.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[3] != null)
                                viewArray[3].setBackgroundColor(colorArray.get(i));
                            calculateAnswer4.clear();
                            calculateAnswer4.add(i);
                        }
                    });
                    break;
                }
                case 5: {
                    int delay = ((randomGenerator.nextInt(4)) + 2) * 1000;
                    timer5.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[4] != null)
                                viewArray[4].setBackgroundColor(colorArray.get(i));
                            calculateAnswer5.clear();
                            calculateAnswer5.add(i);
                        }
                    });
                    break;
                }
                case 6: {
                    int delay = ((randomGenerator.nextInt(4))) * 1000;
                    timer.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[5] != null)
                                viewArray[5].setBackgroundColor(colorArray.get(i));
                            calculateAnswer6.clear();
                            calculateAnswer6.add(i);
                        }
                    });
                    break;
                }
                case 7: {
                    int delay = ((randomGenerator.nextInt(4))) * 1000;
                    timer2.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[6] != null)
                                viewArray[6].setBackgroundColor(colorArray.get(i));
                            calculateAnswer7.clear();
                            calculateAnswer7.add(i);
                        }
                    });
                    break;
                }
                case 8: {
                    int delay = ((randomGenerator.nextInt(4))) * 1000;
                    timer3.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[7] != null)
                                viewArray[7].setBackgroundColor(colorArray.get(i));
                            calculateAnswer8.clear();
                            calculateAnswer8.add(i);
                        }
                    });
                    break;
                }
                case 9: {
                    int delay = ((randomGenerator.nextInt(3))) * 1000;
                    timer4.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[8] != null)
                                viewArray[8].setBackgroundColor(colorArray.get(i));
                            calculateAnswer9.clear();
                            calculateAnswer9.add(i);
                        }
                    });
                    break;
                }
                case 10: {
                    int delay = ((randomGenerator.nextInt(3))) * 1000;
                    timer5.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[9] != null)
                                viewArray[9].setBackgroundColor(colorArray.get(i));
                            calculateAnswer10.clear();
                            calculateAnswer10.add(i);
                        }
                    });
                    break;
                }

                case 11: {
                    int delay = ((randomGenerator.nextInt(3))) * 1000;
                    timer.schedule(new RandomTimerTask(panelNumber), delay);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = randomGenerator.nextInt(colorArray.size());
                            if (viewArray[10] != null)
                                viewArray[10].setBackgroundColor(colorArray.get(i));
                            calculateAnswer11.clear();
                            calculateAnswer11.add(i);
                        }
                    });
                    break;
                }
            }
        }

    }
}
