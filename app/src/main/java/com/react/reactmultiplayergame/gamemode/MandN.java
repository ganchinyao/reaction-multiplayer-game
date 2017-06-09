package com.react.reactmultiplayergame.gamemode;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;

/**
 * Created by gan on 14/4/17.
 * <p>
 * Player tap when they see a letter N. When question is wrong, all letters are M and there is no N.
 * <p>
 * Insane:  grid of 6x8, vs hard grid of 5x6. Speed change is the same throughout all modes
 * <p>
 * Accounted for translation and screensize
 */

public class MandN extends GameMode {
    private int levelDifficulty;
    private TextView[] textViewArray;
    private int correctOrWrong;
    private boolean currentQuestionCorrect; // true when correctOrWrong = 0. Declare this variable so do not have to keep calculating if(correctOrWrong == 0)
    private int currentPositionToBe_N;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.mandn);
        player2Question.setText(R.string.mandn);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.levelDifficulty = levelDifficulty;

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_mn);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }


        int noOfRows = 0;
        int noOfColumns = 0;
        int[] rotationArray = {0, 90, 180, 270};
        int _25dp = (int) context.getResources().getDimension(R.dimen._25sdp);
        final int parentHeight = parentLayout.getHeight(); // initialize these 2 height and width to save resource from evaluating them constantly
        int parentWidth = parentLayout.getWidth();

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                noOfRows = 4;
                noOfColumns = 4;
                correctOrWrong = randomGenerator.nextInt(3);
                break;
            case Constants.MODE_MEDIUM:
                noOfRows = 4;
                noOfColumns = 5;
                correctOrWrong = randomGenerator.nextInt(3);
                break;
            case Constants.MODE_HARD:
                noOfRows = 5;
                noOfColumns = 6;
                correctOrWrong = randomGenerator.nextInt(4);
                break;
            case Constants.MODE_INSANE:
                noOfRows = 6;
                noOfColumns = 8;
                correctOrWrong = randomGenerator.nextInt(4);
                break;
        }


        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TableRow[] tableRowArray = new TableRow[noOfRows];

        textViewArray = new TextView[noOfRows * noOfColumns];

        int textViewLayoutSize = parentHeight / noOfRows;
        while (textViewLayoutSize * noOfColumns >= parentWidth - _25dp * 2) {
            // _50dp leeway for both left and right side
            textViewLayoutSize -= 4;
            // while loop to prevent textViewLayoutSize from exceeding width boundary
        }

        int[] colorArray = {
                Color.parseColor("#FFFF40"), // yellow
                Color.parseColor("#C0FF00"), // green
                Color.parseColor("#00FFC0"), // blue-green
                Color.parseColor("#40FFFF"), // light-blue
                Color.parseColor("#FFC0FF"), // light-pink
                Color.parseColor("#E0E0E0"), // light-gray
                Color.parseColor("#FF8000"), // orange
                Color.parseColor("#FF40FF"), // bright-purple
                Color.parseColor("#FF0000"), // red
        };


        if (correctOrWrong == 0) {
            // correct scenario
            currentQuestionCorrect = true;
            // the position to set to N
            currentPositionToBe_N = randomGenerator.nextInt(noOfRows * noOfColumns);
        } else {
            currentQuestionCorrect = false;
        }

        for (int i = 0; i < noOfRows * noOfColumns; i++) {
            textViewArray[i] = new TextView(context);
            if (currentQuestionCorrect) {
                if (i == currentPositionToBe_N) {
                    textViewArray[i].setText("N"); // set N to one of the position, and M to all the rest, for a correct scenario.
                } else {
                    textViewArray[i].setText("M");
                }
            } else {
                // set up wrong scenario where there are no N, and all text are M
                textViewArray[i].setText("M");
            }
            textViewArray[i].setRotation(rotationArray[randomGenerator.nextInt(rotationArray.length)]);
            textViewArray[i].setLayoutParams(new TableRow.LayoutParams(textViewLayoutSize, textViewLayoutSize));
            textViewArray[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewLayoutSize / 2); // size to be the layout bound /2, will gurantee a safe size that is slightly smaller than half of the layout bound. It will adjust and becomes larger when layoutbound is larger as well, so it suit all size
            textViewArray[i].setGravity(Gravity.CENTER);
            textViewArray[i].setTextColor(colorArray[randomGenerator.nextInt(colorArray.length)]);
        }

        int textViewIndexCounter = 0;
        for (int i = 0; i < noOfRows; i++) {
            //initialize the tablelayout
            tableRowArray[i] = new TableRow(context);
            for (int j = 0; j < noOfColumns; j++) {
                // add the textview to the tableRow
                tableRowArray[i].addView(textViewArray[textViewIndexCounter]);
                textViewIndexCounter++; // a counter to iterate from 0 to total number of textView
            }
            tableLayout.addView(tableRowArray[i]);
        }


        RelativeLayout.LayoutParams overallLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        overallLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        parentLayout.addView(tableLayout, overallLayoutParams);

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_mandn);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        getAnswerHighlight();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        getAnswerHighlight();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        getAnswerHighlight();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        getAnswerHighlight();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_mn;
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
        return false;
    }

    @Override
    public int customTimerDuration() {
        return 2000;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return currentQuestionCorrect;
    }

    // dim all M text to show user where the N is
    private void getAnswerHighlight() {
        for (int i = 0; i < textViewArray.length; i++) {
            if (currentQuestionCorrect) {
                // N not set to gray, the rest all set to gray
                if (i != currentPositionToBe_N) {
                    textViewArray[i].setTextColor(Color.parseColor("#515050")); // dark gray
                }
            } else {
                // current qn is wrong, all set to dark gray since there is no N, and only M
                textViewArray[i].setTextColor(Color.parseColor("#515050"));
            }
        }
    }

}
