package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.CountDownTimer;
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

import java.text.DecimalFormat;

import me.grantland.widget.AutofitHelper;

/**
 * Created by gan on 17/3/17.
 *
 * NumberComparison is where player compare Math Equation
 *  int count_Easy/Medium/Hard/Insane is use to cycle through question and answer
 *  Always update int count_Easy/Medium/Hard/Insane, boolean currentQuestionAnswer() to return new case,
 *  and @Override setCurrentQuestion to extend new case. THESE 3 ARE INTERLINK
 */

public class NumberComparison extends GameMode {

    private final boolean CORRECT = true;
    private final boolean WRONG = false;
    private int PLUS_MINUS_MIN_VALUE;
    private int PLUS_MINUS_MAX_VALUE;
    private int TIMES_MIN_VALUE;
    private int TIMES_MAX_VALUE;
    private int DIVIDE_MIN_VALUE;
    private int DIVIDE_MAX_VALUE;
    private final String PLUS = "+";
    private final String MINUS = "-";
    private final String TIMES = "x";
    private final String DIVIDE = "/";
    private final String MORE_THAN = ">";
    private final String LESS_THAN = "<";

    // plusMinus_Number1 and 2 are also used for comparison qn
    private int plusMinus_Number1;
    private int plusMinus_Number2;
    private int timesNumber1;
    private int timesNumber2;
    private double divideNumber1;
    private int divideNumber2;

    private String [] operatorArray = {PLUS,MINUS, TIMES, DIVIDE};
    //randomize operator
    private String currentOperator = operatorArray[randomGenerator.nextInt(operatorArray.length)];

    private int levelDifficulty;
    private int count_Easy = randomGenerator.nextInt(3);
    private int count_Medium = randomGenerator.nextInt(3);
    private int count_Hard = randomGenerator.nextInt(2);
    private int count_Insane = randomGenerator.nextInt(2);
    private int count_ComparisonQn = randomGenerator.nextInt(2);

    private boolean currentQuestionAnswer(){
        switch(levelDifficulty){
            case Constants.MODE_EASY:
                switch (count_Easy) {
                    case 0: return true;
                    case 1: return false;
                    case 2: return count_ComparisonQn == 0 ? plusMinus_Number1 > plusMinus_Number2 : plusMinus_Number1 < plusMinus_Number2;
                }
            case Constants.MODE_MEDIUM:
                switch (count_Medium) {
                    case 0: return true;
                    case 1: return false;
                    case 2: return count_ComparisonQn == 0 ? plusMinus_Number1 > plusMinus_Number2 : plusMinus_Number1 < plusMinus_Number2;
                }
            case Constants.MODE_HARD:
                switch (count_Hard) {
                    case 0: return true;
                    case 1: return false;
                }
            case Constants.MODE_INSANE:
                switch (count_Insane) {
                    case 0: return true;
                    case 1: return false;
                }
            default: return false;
        }
    }

        //getArithemeticQn are used only for +, -, * and / operator. Parameter 'correct' is use to account for 2 case of correct/wrong answer
        private String getArithmeticQn(boolean correct){
            if(correct) {
                switch(currentOperator){
                    case TIMES:
                        return "" + timesNumber1 + " " + currentOperator + " " + timesNumber2 + " = " + getArithmeticAnswer(CORRECT);
                    case PLUS:
                    case MINUS:
                        return "" + plusMinus_Number1 + " " + currentOperator + " " + plusMinus_Number2 + " = " + getArithmeticAnswer(CORRECT);
                    case DIVIDE:
                        return "" + (int)divideNumber1 + " " + currentOperator + " " + divideNumber2 + " = " + getDivideAnswer(CORRECT);
                    default: return "";
                }
            }
            else {
                switch(currentOperator){
                    case TIMES:
                        return "" + timesNumber1 + " " + currentOperator + " " + timesNumber2 + " = " + getArithmeticAnswer(WRONG);
                    case PLUS:
                    case MINUS:
                        return "" + plusMinus_Number1 + " " + currentOperator + " " + plusMinus_Number2 + " = " + getArithmeticAnswer(WRONG);
                    case DIVIDE:
                        return "" + (int)divideNumber1 + " " + currentOperator + " " + divideNumber2 + " = " + getDivideAnswer(WRONG);
                    default: return "";
                }
            }
        }

    // A separate method for divide answer becos divide return double instead of int
        private int getArithmeticAnswer(boolean correct){
            if(correct) {
                switch (currentOperator) {
                    case PLUS:
                        return plusMinus_Number1 + plusMinus_Number2;
                    case MINUS:
                        return plusMinus_Number1 - plusMinus_Number2;
                    case TIMES:
                        return timesNumber1 * timesNumber2;
                    default: return 0;
                }
            } else {
                switch(currentOperator){
                    // +1 to the end because nextInt(n) may return 0, hence add 1 to ensure answer is definitely wrong
                    case PLUS:
                        return plusMinus_Number1 + plusMinus_Number2 + randomGenerator.nextInt(11) + 1;
                    case MINUS:
                        return plusMinus_Number1 - plusMinus_Number2 - randomGenerator.nextInt(11) - 1;
                    case TIMES:
                        return timesNumber1 * timesNumber2 + randomGenerator.nextInt(21) + 1;
                    default: return 0;
                }
            }
        }

    private double getDivideAnswer(boolean correct){
        // ~ to 2dp
        DecimalFormat df = new DecimalFormat("0.00");
        if(correct) return Double.parseDouble(df.format(divideNumber1 / divideNumber2));
        else return Double.parseDouble(df.format(divideNumber1 / divideNumber2 + randomGenerator.nextInt(11) + 1));
    }


    // Hard and Insane does not have comparison qn
    private String getComparisonQuestion_EasyMedium(){
        switch (count_ComparisonQn) {
            case 0:
                return "" + plusMinus_Number1 + " " + MORE_THAN + " " + plusMinus_Number2;
            case 1:
                return "" + plusMinus_Number1 + " " + LESS_THAN + " " + plusMinus_Number2;
            default: return "";
        }
    }

    private void setNumber(int levelDifficulty) {

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                this.PLUS_MINUS_MIN_VALUE = 0;
                this.PLUS_MINUS_MAX_VALUE = 30;
                this.TIMES_MIN_VALUE = 0;
                this.TIMES_MAX_VALUE = 15;
                this.DIVIDE_MIN_VALUE = 1;
                this.DIVIDE_MAX_VALUE = 10;
                initializePlusMinusTimesDivide();
                break;
            case Constants.MODE_MEDIUM:
                this.PLUS_MINUS_MIN_VALUE = 5;
                this.PLUS_MINUS_MAX_VALUE = 50;
                this.TIMES_MIN_VALUE = 0;
                this.TIMES_MAX_VALUE = 30;
                this.DIVIDE_MIN_VALUE = 1;
                this.DIVIDE_MAX_VALUE = 20;
                initializePlusMinusTimesDivide();
                break;
            case Constants.MODE_HARD:
                this.PLUS_MINUS_MIN_VALUE = 50;
                this.PLUS_MINUS_MAX_VALUE = 300;
                this.TIMES_MIN_VALUE = 10;
                this.TIMES_MAX_VALUE = 50;
                this.DIVIDE_MIN_VALUE = 10;
                this.DIVIDE_MAX_VALUE = 40;
                initializePlusMinusTimesDivide();
                break;
            case Constants.MODE_INSANE:
                this.PLUS_MINUS_MIN_VALUE = 500;
                this.PLUS_MINUS_MAX_VALUE = 1000;
                this.TIMES_MIN_VALUE = 50;
                this.TIMES_MAX_VALUE = 100;
                this.DIVIDE_MIN_VALUE = 50;
                this.DIVIDE_MAX_VALUE = 100;
                initializePlusMinusTimesDivide();
                break;
        }
    }

    private void initializePlusMinusTimesDivide(){
        plusMinus_Number1 = randomGenerator.nextInt(PLUS_MINUS_MAX_VALUE - PLUS_MINUS_MIN_VALUE + 1) + PLUS_MINUS_MIN_VALUE;
        plusMinus_Number2 = randomGenerator.nextInt(PLUS_MINUS_MAX_VALUE - PLUS_MINUS_MIN_VALUE + 1) + PLUS_MINUS_MIN_VALUE;
        timesNumber1 = randomGenerator.nextInt(TIMES_MAX_VALUE - TIMES_MIN_VALUE + 1) + TIMES_MIN_VALUE;
        timesNumber2 = randomGenerator.nextInt(TIMES_MAX_VALUE - TIMES_MIN_VALUE + 1) + TIMES_MIN_VALUE;
        divideNumber1 = randomGenerator.nextInt(DIVIDE_MAX_VALUE - DIVIDE_MIN_VALUE + 1) + DIVIDE_MIN_VALUE;
        divideNumber2 = randomGenerator.nextInt(DIVIDE_MAX_VALUE - DIVIDE_MIN_VALUE + 1) + DIVIDE_MIN_VALUE;
    }


    public String setContentQuestion() {
        setNumber(levelDifficulty);
        String currentQuestion = "";
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
            switch (count_Easy) {
                case 0:
                    currentQuestion = getArithmeticQn(CORRECT);
                    break;
                case 1:
                    currentQuestion = getArithmeticQn(WRONG);
                    break;
                case 2:
                    currentQuestion = getComparisonQuestion_EasyMedium();
                    break;
            }
                break;
            case Constants.MODE_MEDIUM:
                switch (count_Medium) {
                    case 0:
                        currentQuestion = getArithmeticQn(CORRECT);
                        break;
                    case 1:
                        currentQuestion = getArithmeticQn(WRONG);
                        break;
                    case 2:
                        currentQuestion = getComparisonQuestion_EasyMedium();
                        break;
                }
                break;
            case Constants.MODE_HARD:
                switch (count_Hard) {
                    case 0:
                        currentQuestion = getArithmeticQn(CORRECT);
                        break;
                    case 1:
                        currentQuestion = getArithmeticQn(WRONG);
                        break;
                }
                break;
            case Constants.MODE_INSANE:
                switch (count_Insane) {
                    case 0:
                        currentQuestion = getArithmeticQn(CORRECT);
                        break;
                    case 1:
                        currentQuestion = getArithmeticQn(WRONG);
                        break;
                }
                break;
        }
        return currentQuestion;
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return currentQuestionAnswer();
    }

    @Override
    public boolean requireATimer() {
        return true;
    }

    @Override
    public boolean requireDefaultTimer() {
        return true;
    }

    @Override
    public boolean wantToShowTimer() {
        return true;
    }

    @Override
    public int customTimerDuration() {
        return 0; //doesnt use customTimer, panelNumber.e. using default timer
    }


    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_numbercomparison;
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers){

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_numbercomparison);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);
            drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#00611C"), PorterDuff.Mode.MULTIPLY));

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        this.levelDifficulty = levelDifficulty;
        TextView question1 = new TextView(context);
        question1.setGravity(Gravity.CENTER);
        TextView question2 = new TextView(context);
        question2.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, parentLayout.getHeight()/2);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, parentLayout.getHeight()/2);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        String question = setContentQuestion();
        question1.setText(question);
        question2.setText(question);
        question1.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._22ssp));
        question2.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._22ssp));
        question2.setRotation(180);
        question1.setSingleLine();
        question2.setSingleLine();
        question1.setTextColor(Color.parseColor("#FaFaFa"));
        question2.setTextColor(Color.parseColor("#FaFaFa"));
        AutofitHelper.create(question1);
        AutofitHelper.create(question2);
        parentLayout.addView(question1, layoutParams);
        parentLayout.addView(question2, layoutParams2);
    }

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question){
        player1Question.setText(R.string.hitwhencorrect);
        player2Question.setText(R.string.hitwhencorrect);
    }

    @Override
    public String getDialogTitle(Context context){
        return context.getResources().getString(R.string.dialog_mathequation);
    }

    // NOT customizing the buttonclickevent, hence using the default click in the QuickPlay.java
    @Override
    public boolean player4ButtonClickedCustomExecute() {
        return false;
    }
    @Override
    public boolean player3ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player1ButtonClickedCustomExecute(){
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute(){
        return false;
    }
}


