package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gan on 14/5/17.
 *
 * Hard:  5x6 grid. Insane: 6x8 Grid. The rest like timer all same
 *
 * Accounted for translation and screen size
 *
 */

public class FiveYellowCircle extends GameMode {
    private ImageView[] imageViewArray;
    private int correctOrWrong;
    private boolean currentQuestionCorrect; // true when correctOrWrong = 0. Declare this variable so do not have to keep calculating if(correctOrWrong == 0)
    private  int [] currentPositionToBeYellow;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.fiveyellowcircle);
        player2Question.setText(R.string.fiveyellowcircle);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

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
        int _25dp = (int) context.getResources().getDimension(R.dimen._25sdp);
        final int parentHeight = parentLayout.getHeight(); // initialize these 2 height and width to save resource from evaluating them constantly
        int parentWidth = parentLayout.getWidth();

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                // noOfRows * noOfColumns cannot be less than 10 as maximum numberOfYellowCircle to be shown is 10
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

        imageViewArray = new ImageView[noOfRows * noOfColumns];

        int imageViewLayoutSize = (parentHeight - (int) context.getResources().getDimension(R.dimen._10sdp)) / noOfRows; // 10dp for top and bottom leeway in total
        while (imageViewLayoutSize * noOfColumns >= parentWidth - _25dp * 2) {
            // _50dp leeway for both left and right side
            imageViewLayoutSize -= 4;
            // while loop to prevent imageViewLayoutSize from exceeding width boundary
        }

        int[] colorArray = {
                // Must not contain yellow becoz yellow is the correct ans
                Color.parseColor("#C0FF00"), // green
                Color.parseColor("#00FFC0"), // blue-green
                Color.parseColor("#40FFFF"), // light-blue
                Color.parseColor("#FFC0FF"), // light-pink
                Color.parseColor("#E0E0E0"), // light-gray
                Color.parseColor("#FF8000"), // orange
                Color.parseColor("#FF40FF"), // bright-purple
                Color.parseColor("#FF0000"), // red
        };

        int numberOfYellowCircle;
        ArrayList<Integer> totalNumberOfPositionsArrayList = new ArrayList<>();
        // fill the arrayList with all possible numbers
        for(int i =0; i<noOfColumns*noOfRows; i++) {
            totalNumberOfPositionsArrayList.add(i);
        }
        // impt to shuffle it to randomize it
        Collections.shuffle(totalNumberOfPositionsArrayList);

        GradientDrawable gradientDrawableArray[] = new GradientDrawable[noOfColumns * noOfRows];

        if (correctOrWrong == 0) {
            // correct scenario
            currentQuestionCorrect = true;
            numberOfYellowCircle = randomGenerator.nextInt(6) + 5; // from 5-10 number of yellows to be shown
        } else {
            currentQuestionCorrect = false;
            numberOfYellowCircle = randomGenerator.nextInt(5); // from 0-4 shown
        }

        // applies to both correct and wrong situation
        currentPositionToBeYellow = new int [numberOfYellowCircle];
        for(int i =0; i< numberOfYellowCircle; i++) {
            // get a random number from the totalNumber possible
            // this ensures we will definitely get unique number becoz we are getting it sequentially based off arrayList.
            // that is why it is impt to shuffle the arrayList before hand to randomize all possible number
            currentPositionToBeYellow[i] = totalNumberOfPositionsArrayList.get(i);
        }


        int _5sdp = (int) context.getResources().getDimension(R.dimen._5sdp);
        for (int i = 0; i < noOfRows * noOfColumns; i++) {
            gradientDrawableArray[i] = new GradientDrawable();
            gradientDrawableArray[i].setSize(imageViewLayoutSize, imageViewLayoutSize);
            gradientDrawableArray[i].setShape(GradientDrawable.OVAL);

            imageViewArray[i] = new ImageView(context);
            imageViewArray[i].setImageDrawable(gradientDrawableArray[i]);
            // we set all to be a color, and go through bottom for loop top change the color to yellow if it is supposed to be yellow
            imageViewArray[i].setColorFilter(colorArray[randomGenerator.nextInt(colorArray.length)]);
            imageViewArray[i].setPadding(_5sdp, _5sdp, _5sdp, _5sdp);

            // loop through all the positions to be yellow
            for(int j = 0; j< currentPositionToBeYellow.length; j++) {
                if(i == currentPositionToBeYellow[j]) {
                    // current position is supposed to be yellow, hence set it to yellow
                    imageViewArray[i].setColorFilter( Color.parseColor("#FFFF40")); // yellow
                    break; // break to prevent unnecessary loop through subsequently since we alr know it is yellow alr
                }
            }

            imageViewArray[i].setLayoutParams(new TableRow.LayoutParams(imageViewLayoutSize, imageViewLayoutSize));
        }

        int imageViewIndexCounter = 0;
        for (int i = 0; i < noOfRows; i++) {
            //initialize the tablelayout
            tableRowArray[i] = new TableRow(context);
            for (int j = 0; j < noOfColumns; j++) {
                // add the imageView to the tableRow
                tableRowArray[i].addView(imageViewArray[imageViewIndexCounter]);
                imageViewIndexCounter++; // a counter to iterate from 0 to total number of textView
            }
            tableLayout.addView(tableRowArray[i]);
        }

        RelativeLayout.LayoutParams overallLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        overallLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        parentLayout.addView(tableLayout, overallLayoutParams);

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_fiveyellowcircle);
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
        return R.raw.gamemode_fiveyellowcircle;
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
        // randomly from 1.5sec to 2.5sec
        return  (int) ((randomGenerator.nextFloat() ) * 1000 + 1500);
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
        outerloop:
        for (int i = 0; i < imageViewArray.length; i++) {

            // loop through all the possible position
            for(int j=0; j<currentPositionToBeYellow.length;j++) {
                if(i == currentPositionToBeYellow[j]) {
                    // current position is indeed a yellow
                    // we skip through this entire outer loop and go to next instance and donnid do anything, leave yellow as it is
                    continue  outerloop;
                }
            }
            // we have loop through the above inner for loop, and since it does not execute the continue, here below getting executed
            // means that the current position is not yellow
            // therefore we set it to gray
            imageViewArray[i].setColorFilter( Color.parseColor("#515050")); // gray
        }
    }
}
