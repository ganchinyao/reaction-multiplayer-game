package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
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
import com.react.reactmultiplayergame.helper.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gan on 12/4/17.
 * <p>
 * how this class work: int correctOrWrong = 0 means current qn is correct
 * int correctOrWrong != 0 means a wrong scenario, and getWrongArrowPositionArray will be called to purposely create an arrow position array that contains minimum 2 arrow in same direction -> imageView set the same direction arrow as background
 * <p>
 * <p>
 * Insane = additional color of white, pink and dark blue. 8 arrow instead of 6 for hard, and 1000ms timer instead of 1500ms for hard
 *
 * ** Accounted for translation and screensize
 */

public class NeonArrows extends GameMode {
    private ImageView[] imageViewArray;
    // noOfArrows is tied to arrowDirection_easy and arrowDirection_notEasy array. Make sure the array contains equal, or more elements than noOfArrows to prevent arrayoutofbound
    private int noOfArrows;
    private ArrayList<Integer> colorArray = new ArrayList<>();
    private int levelDifficulty;
    // correctOrWrong 0 = current question is correct
    private int correctOrWrong;
    private int[] wrongArrowDirectionArray;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.neonarrows);
        player2Question.setText(R.string.neonarrows);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.levelDifficulty = levelDifficulty;
        int imageSize = (parentLayout.getHeight() / 2) - (int) context.getResources().getDimension(R.dimen._25sdp);
        while (imageSize * 4 >= parentLayout.getWidth() - (int )context.getResources().getDimension(R.dimen._20sdp)/* - a leeway of 20dp so images does not stretch all the way to full width*/) {
            // to ensure 4 of such imageSize will not exceed width, since Mode.Insane consist of 2 rows of 4 images
            imageSize -= 10;

        }

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_neonarrow);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }


        initializeColorArray();

        // the number for setRotation to enable arrow to be in different position. Total 8 different position
        // Do not set arrowDirection be less than 2 element, because getWrongArrowPositionArray requires minimum 2 elements
        int[] arrowDirection_easyMediumMode = {0, 90, 180, 270};
        // randomize the array because imageView will call the arrowDirection sequentially, i.e. imageView1 call index[0], imageView2 call index[1] etc.
        Utils.shuffleIntArray(arrowDirection_easyMediumMode);

        int[] arrowDirection_InsaneMode = {0, 45, 90, 135, 180, 225, 270, 315};
        Utils.shuffleIntArray(arrowDirection_InsaneMode);

        int[] arrowDirection_HardMode = new int[6];
        if (levelDifficulty == Constants.MODE_HARD) {
            // set up an if sentence to not waste cpu instantiating if mode is not hard. Above easy Medium Insane get instantiated everytime because I am lazy to set up the various case, and it is easier to instantiate them in one sentence like that
            for (int i = 0; i < 6; i++) {
                // populating hard mode with only 6 elements. Cannot use 8 elements for hard mode becos hard mode only show 6 arrow.
                // If use 8 element for showing 6 arrow, there may be a chance that after calling getWrongArray, the first 6 index are correct whereas last 2 index are wrong,
                // but when setting imageView sequentially, all the first 6 imageView set the correct scenario instead of wrong scenaio

                // set the first 6 elements of insaneMode to hardMode. As shuffleIntArray has been called for insaneMode, content in hardMode will also be different each time. No need call shuffle again.
                arrowDirection_HardMode[i] = arrowDirection_InsaneMode[i];
            }
        }

        switch (levelDifficulty) {
            case Constants.MODE_EASY: // easy medium same, only timer difference
            case Constants.MODE_MEDIUM:
                // dont change easy mode arrow=4 without changing arrowDirection_easyMediumMode []. 4 elements there correspond to noOfArrows = 4. Increasing noOfArrows without increasing the [] will result in arrayindexoutofbound
                noOfArrows = 4;
                // 33% chance of correct
                correctOrWrong = randomGenerator.nextInt(3);
                if (correctOrWrong != 0) {
                    wrongArrowDirectionArray = getWrongArrowPositionArray(arrowDirection_easyMediumMode);
                }
                break;
            case Constants.MODE_HARD:
                noOfArrows = 6; // only for Hard case, if you change this noOfArrows, manually change the for loop i<6, and array new int[6] above to correspond to the new noOfArrows.
                // 25% chance of correct
                correctOrWrong = randomGenerator.nextInt(4);
                if (correctOrWrong != 0) {
                    wrongArrowDirectionArray = getWrongArrowPositionArray(arrowDirection_HardMode);
                }
                break;
            case Constants.MODE_INSANE:
                noOfArrows = 8; // do not go more than 8, unless arrowDirection_InsaneMode increase to more than 8 elements.
                correctOrWrong = randomGenerator.nextInt(4);
                if (correctOrWrong != 0) {
                    wrongArrowDirectionArray = getWrongArrowPositionArray(arrowDirection_InsaneMode);
                }
                break;
        }


        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TableRow tableRow = new TableRow(context);
        TableRow tableRow2 = new TableRow(context);

        // initialize the arrows
        Drawable[] arrowArray = new Drawable[noOfArrows];
        imageViewArray = new ImageView[noOfArrows];
        for (int i = 0, half = noOfArrows / 2; i < noOfArrows; i++) {
            arrowArray[i] = ResourcesCompat.getDrawable(context.getResources(), R.drawable.arrow, null);
            arrowArray[i].mutate();
            arrowArray[i].setColorFilter(new PorterDuffColorFilter(colorArray.get(randomGenerator.nextInt(colorArray.size())), PorterDuff.Mode.SRC_ATOP));
            imageViewArray[i] = new ImageView(context);
            imageViewArray[i].setImageDrawable(arrowArray[i]);
            imageViewArray[i].setLayoutParams(new TableRow.LayoutParams(imageSize, imageSize));

            switch (levelDifficulty) {
                case Constants.MODE_EASY:
                case Constants.MODE_MEDIUM:
                    if (correctOrWrong == 0) {
                        // set up correct scenario
                        imageViewArray[i].setRotation(arrowDirection_easyMediumMode[i]); // all arrow point to different direction
                    } else {
                        // set up wrong scenario
                        imageViewArray[i].setRotation(wrongArrowDirectionArray[i]);
                    }
                    break;
                case Constants.MODE_HARD:
                    if (correctOrWrong == 0) {
                        imageViewArray[i].setRotation(arrowDirection_HardMode[i]);
                    } else {
                        imageViewArray[i].setRotation(wrongArrowDirectionArray[i]);
                    }
                    break;
                case Constants.MODE_INSANE:
                    if (correctOrWrong == 0) {
                        imageViewArray[i].setRotation(arrowDirection_InsaneMode[i]);
                    } else {
                        imageViewArray[i].setRotation(wrongArrowDirectionArray[i]);
                    }
                    break;
            }

            // add all the imageview into 2 rows
            if (i < half) {
                tableRow.addView(imageViewArray[i]);
            } else {
                tableRow2.addView(imageViewArray[i]);
            }
        }

        tableLayout.addView(tableRow);
        tableLayout.addView(tableRow2);

        RelativeLayout.LayoutParams overallLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        overallLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        parentLayout.addView(tableLayout, overallLayoutParams);

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_neonarrows);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        getAnswerHighlightAnimation();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        getAnswerHighlightAnimation();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        getAnswerHighlightAnimation();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        getAnswerHighlightAnimation();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_neonarrows;
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
        switch (levelDifficulty){
            case Constants.MODE_EASY:
                return 2500;
            case Constants.MODE_MEDIUM:
                return 2000;
            case Constants.MODE_HARD:
                return 1500;
            case Constants.MODE_INSANE:
                return 1000;
            default: return 2000;
        }
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return correctOrWrong == 0;
    }

    private void initializeColorArray() {
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                colorArray.add(Color.parseColor("#FFFF00")); //yellow
                colorArray.add(Color.parseColor("#FF0000")); //red
                colorArray.add(Color.parseColor("#00FFFF")); //cyan
                colorArray.add(Color.parseColor("#00FF00")); //green
                break;
            case Constants.MODE_MEDIUM:
                colorArray.add(Color.parseColor("#FFFF00")); //yellow
                colorArray.add(Color.parseColor("#FF0000")); //red
                colorArray.add(Color.parseColor("#00FFFF")); //cyan
                colorArray.add(Color.parseColor("#00FF00")); //green
                colorArray.add(Color.parseColor("#CC00FF")); //purple
                colorArray.add(Color.parseColor("#FF9933")); //orange
                break;
            case Constants.MODE_HARD:
                colorArray.add(Color.parseColor("#FFFF00")); //yellow
                colorArray.add(Color.parseColor("#FF0000")); //red
                colorArray.add(Color.parseColor("#00FFFF")); //cyan
                colorArray.add(Color.parseColor("#00FF00")); //green
                colorArray.add(Color.parseColor("#CC00FF")); //purple
                colorArray.add(Color.parseColor("#FF9933")); //orange
                break;
            case Constants.MODE_INSANE:
                colorArray.add(Color.parseColor("#FFFF00")); //yellow
                colorArray.add(Color.parseColor("#FF0000")); //red
                colorArray.add(Color.parseColor("#00FFFF")); //cyan
                colorArray.add(Color.parseColor("#00FF00")); //green
                colorArray.add(Color.parseColor("#CC00FF")); //purple
                colorArray.add(Color.parseColor("#FF9933")); //orange
                colorArray.add(Color.parseColor("#FFFFFF")); //white
                colorArray.add(Color.parseColor("#F535AA")); //pink
                colorArray.add(Color.parseColor("#203CFD")); //dark blue
                break;
        }
    }

    // use to generate a wrong array whereby there is exactly 2 repeating arrow direction from the array argument passed in.
    // Exactly 2 wrong instead of minimum 2 wrong results in a more challenging situation because it is harder to spot
    // This results in a false scenario, whereby not all arrows point in different direction
    // argument requires minimum 2 elements to be passed in
    private int[] getWrongArrowPositionArray(int[] originalCorrectArray) {
        int[] result = new int[originalCorrectArray.length];
        int repeatedNumber = originalCorrectArray[randomGenerator.nextInt(originalCorrectArray.length)]; // first select which element in the array you want to repeat
        int repeatCount = 2; // the number of times to repeat the repeatedNumber. Set it to 2 and not so high so that there are not so many repeated elements -> not so obvious it is wrong

        for (int i = 0; i < repeatCount; i++) {
            result[i] = repeatedNumber; // set repeated number to the array from the first position until repeat count. The remaining elements to be filled with the for loop below
        }

        for (int i = repeatCount; i < originalCorrectArray.length; i++) {
            // for loop will skip if repeatCount = originalCorrectArray.length, i.e. all elements in array is 1 same number
            // fill the rest of the array randomly from elements of originalCorrectArray.
            // This can result in another repeatNumber (which doesnt matter becos minimum number of repeating words (2) is already achieve in the for loop above)
            result[i] = originalCorrectArray[randomGenerator.nextInt(originalCorrectArray.length)];
        }
        Utils.shuffleIntArray(result); // randomise the wrong array
        return result;
    }

    // darken the non-repeating arrows, and show user which arrow is repeated. No animation here. Animation name just for the name sake
    private void getAnswerHighlightAnimation() {
        if (correctOrWrong == 0)
            return; // no need for any highlight animation. If qn is wrong, then continue down the code

        int repeatedNumber = -1; // initialize for the sake of initializing. Value will change at for loop below anyway
        Set<Integer> set = new HashSet<>(); // using set ensures no duplicated elements allowed
        for (int number : wrongArrowDirectionArray) {
            if (!set.add(number)) {
                // found a duplicate element because set cannot add duplicate element
                // set repeatedNumber to be that number, and break out of the for loop. Do not need to care about subsequent repeatedNumber
                // All I need is to just find the first repeated Number, because I only want to show 1 kind of repeated element to user.
                // Showing all the repeat arrows is too overwhelming and hard to see. Hence, just show 1 kind will do
                repeatedNumber = number;
                break;
            }
        }

        // iterate through all the imageview
        for (ImageView currentImageView : imageViewArray) {
            if ((int) currentImageView.getRotation() != repeatedNumber) {
                // current element is not repeated, therefore set it to black to dim it away
                currentImageView.setColorFilter(new PorterDuffColorFilter(0x7f000000, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

}
