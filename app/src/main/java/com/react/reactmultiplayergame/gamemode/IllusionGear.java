package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;

/**
 * Created by gan on 23/4/17.
 * Tap when all gears turn in same direction
 *
 * Insane mode is faster than hard, and have 2 more gears
 *
 * Accounted for screensize and translation
 */

public class IllusionGear extends GameMode {
    private ImageView[] gearImageArray;
    private int correctOrWrong;
    private int customTimerDuration;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.illusiongear);
        player2Question.setText(R.string.illusiongear);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        // Correct scenario occurs when all gears turn in same direction. 50% chance of turning clockwise and 50% chance of turning anticlockwise
        // The way we set up wrong scenario is to first determine which gear is turning wrongly by the int gearWhichTurnWrongDirection.
        // randomly select a gear to turn wrong, then during for loop, if the current gear is the gearWhichTurnWrongDirection,
        // then we set its rotation to be rotationNumber_wrongDirection. Otherwise, we set its rotation to be rotationNumber.
        // We will also determine if there are 1 or 2 gears that is turning wrongly, and set them accordingly

        int numberOfGears = 0;
        int parentWidth = parentLayout.getWidth();
        int parentHeight = parentLayout.getHeight();
        int rotationNumber = 0; // used to control the speed of the gear rotating
        int rotationNumber_wrongDirection = 0; // used for wrong situation to cause a different direction from the rest
        // here it is important that first and secondGearWhichTurnWrongDirection is not >= 0, but negative.
        // This is becoz the for loop below will not check if correctOrWrong == 0; since it is check before for loop.
        // During the check, if current qn is wrong, first and second gear will be initialize to 0 to numberOfGears-1.
        // Hence during the for loop check, if qn is correct, the if statement will be used against a value of -1 for first and second gear, which will nv match, and the gear will all be set in correct direction
        // During a wrong scenario, during the if check inside for loop to tell if current gear is first or secondGearWrongDirection, first and secondGearWrongDirection would have been initialized to match
        // the respective gears turning wrong direction
        int firstGearWhichTurnWrongDirection = -1; // used to tell which gear, from 0 to numberOfGears-1, that is turning wrong direction during a wrong scenario
        int secondGearWhichTurnWrongDirection = -1;

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_illusiongear);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        // currently setting all timer to be 2sec. Hence adjust the speed by rotationNumber. The higher the number, the faster the speed
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                numberOfGears = 4;
                correctOrWrong = randomGenerator.nextInt(3); // 33% chance of correct
                customTimerDuration = 2000;
                rotationNumber = 540; // 1 and half round
                break;
            case Constants.MODE_MEDIUM:
                numberOfGears = 4;
                correctOrWrong = randomGenerator.nextInt(3);
                customTimerDuration = 2000;
                rotationNumber = 720;
                break;
            case Constants.MODE_HARD:
                numberOfGears = 6;
                correctOrWrong = randomGenerator.nextInt(4); // 25% chance of correct
                customTimerDuration = 2000;
                rotationNumber = 800;
                break;
            case Constants.MODE_INSANE:
                numberOfGears = 8;
                correctOrWrong = randomGenerator.nextInt(4);
                customTimerDuration = 2000;
                rotationNumber = 1000; // 2 additional gears, and slightly faster than hard
                break;
        }

        int gearSize = parentHeight / 3; // no matter which mode, there will only be 2 rows. Hence divide by 3 so that size is slightly smaller than half of height
        while (gearSize * numberOfGears / 2 > (0.8 * parentWidth)) {
            // this 80% is inline with the relativelayout that contains the imageView below 80%. If want change, make sure change both
            // numberOfGears/2 represents how many columns is needed for each mode.
            // if more than 80% of width, shrink down gearSize. Leave 20% parentWidth for padding so gear not all the way to the edge
            gearSize -= 10;
        }

        if (correctOrWrong == 0) {
            // set up correct situation, all gears turn in same direction
            // 50% chance turn clockwise, 50% chance turn anticlockwise
            if (randomGenerator.nextInt(2) == 0) {
                // anticlockwise. Dont need an else clause becoz rotationNumber is positive to begin with, hence implicitly causing a clockwise turn
                rotationNumber = -1 * rotationNumber;
            }
        } else {
            // intialize wrong scenario
            // 1 gear turn wrongly, the rest all turn correctly;
            if (randomGenerator.nextInt(2) == 0) {
                // equal chance of turning clockwise or anticlockwise
                rotationNumber = -1 * rotationNumber;
            }
            // regardless of rotationNumber turning clockwise or anticlockwise, the wrongDirection will be opposite of it
            rotationNumber_wrongDirection = -1 * rotationNumber;

            // implicitly imples that if correctOrWrong == 1, we only get 1 gear turning wrong direction.
            // Otherwise if correctOrWrong == 2 or 3, we initialize 2 gears to turn wrong direction
            firstGearWhichTurnWrongDirection = randomGenerator.nextInt(numberOfGears); // randomly choose 1 gear that is turning wrong direction

            if(correctOrWrong==2 || correctOrWrong ==3) {
                // initialize 1 more gear to turn wrong direction
                secondGearWhichTurnWrongDirection = randomGenerator.nextInt(numberOfGears);
                while(secondGearWhichTurnWrongDirection == firstGearWhichTurnWrongDirection){
                    // keep looping until secondGearWhichTurnWrongDirection is different from firstGear.
                    // We want it to be 2 separate gear, but randomGenerator may generate the same number, hence we keep looping to ensure it will be different
                    secondGearWhichTurnWrongDirection = randomGenerator.nextInt(numberOfGears);
                }
            }
        }


        gearImageArray = new ImageView[numberOfGears];
        RelativeLayout [] relativeLayoutArray = new RelativeLayout[numberOfGears];
        LinearLayout topLinearLayout = new LinearLayout(context);
        topLinearLayout.setLayoutParams(new LinearLayout.LayoutParams((int) (0.8 * parentWidth), parentHeight/2)); // leave 10% margin space at left and another 10% at right
        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout bottomLinearLayout = new LinearLayout(context);
        bottomLinearLayout.setLayoutParams(new LinearLayout.LayoutParams((int) (0.8 * parentWidth), parentHeight/2)); // leave 10% margin space at left and another 10% at right
        bottomLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0, half = numberOfGears / 2; i < numberOfGears; i++) {
            gearImageArray[i] = new ImageView(context);
            gearImageArray[i].setImageResource(R.drawable.gear);
            gearImageArray[i].setLayoutParams(new TableRow.LayoutParams(gearSize, gearSize));
            //gearImageArray[i].setPadding(marginSize,marginSize,marginSize,marginSize);

            relativeLayoutArray[i] = new RelativeLayout(context);
            // assign a width according to how many gears are there in a row, while taking into account maximum space is 0.8 * parentWidth
            relativeLayoutArray[i].setLayoutParams(new RelativeLayout.LayoutParams( (int) (0.8 * parentWidth / half )  , parentHeight/2));
            relativeLayoutArray[i].addView(gearImageArray[i]);
            relativeLayoutArray[i].setGravity(Gravity.CENTER);

            // add all the imageview into 2 rows
            if (i < half) {
                topLinearLayout.addView(relativeLayoutArray[i]);
            } else {
                bottomLinearLayout.addView(relativeLayoutArray[i]);
            }

            // randomize the starting rotation of the gear so that the stripe of the gearIcon will not be the same, hence more difficult to tell if theres a wrong turning gear
            gearImageArray[i].setRotation(randomGenerator.nextInt(360));

            // rotationBy means from the starting point, how many degrees to turn. If set 720, this means turn 2 complete round.
            // set 3600 means turns 10 complete round. Hence, by combining how many rounds to turn and setting speed by Duration,
            // we do not need animate.repeat, since we can set more rounds to turn than before timer change.
            // therefore we can use ViewPropertyAnimator and do not need the repeat function of ObjectAnimator
            // Just add 3 more sec to account for the case where there is dialog. Even if there is no dialog, 3 additional sec also nvm
            // Since duration is more or less fix (if customTimerDuration) is same, we set the speed by telling it how many rounds to turn

            if(i == firstGearWhichTurnWrongDirection || i == secondGearWhichTurnWrongDirection) {
                // no need check if correctOrWrong == 0, becoz if it is, then first and secondGearWhichTurnWrongDirection will be the initial value of -1, and will nv match i
                gearImageArray[i].animate().rotationBy(rotationNumber_wrongDirection).setDuration(customTimerDuration + 3000).setInterpolator(new LinearInterpolator()).start();
                if(rotationNumber_wrongDirection <0) {
                    // set tag to determine direction, which will be used later to show which direction it is turning
                    gearImageArray[i].setTag("AntiClockwise");
                } else {
                    gearImageArray[i].setTag("Clockwise");
                }
            } else {
                gearImageArray[i].animate().rotationBy(rotationNumber).setDuration(customTimerDuration + 3000).setInterpolator(new LinearInterpolator()).start();
                if(rotationNumber <0) {
                    gearImageArray[i].setTag("AntiClockwise");
                } else {
                    gearImageArray[i].setTag("Clockwise");
                }
            }
        }

        LinearLayout overallLinearLayout = new LinearLayout(context);
        overallLinearLayout.setOrientation(LinearLayout.VERTICAL);
        overallLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overallLinearLayout.addView(topLinearLayout);
        overallLinearLayout.addView(bottomLinearLayout);


        RelativeLayout.LayoutParams overallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        overallParam.addRule(RelativeLayout.CENTER_IN_PARENT);

        parentLayout.addView(overallLinearLayout, overallParam);

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_illusiongear);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        getTurningDirectionAfterButtonClicked();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        getTurningDirectionAfterButtonClicked();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        getTurningDirectionAfterButtonClicked();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        getTurningDirectionAfterButtonClicked();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_illusiongear;
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
        return customTimerDuration;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return correctOrWrong == 0;
    }

    private void getTurningDirectionAfterButtonClicked() {
        for(int i =0; i< gearImageArray.length; i++) {
            // set rotation to be 0 first so all will be at same position, since in setGameContent, each gearImage is assign a different starting setRotation
            gearImageArray[i].setRotation(0);
            gearImageArray[i].animate().cancel();

            if(gearImageArray[i].getTag().equals("Clockwise")){
                gearImageArray[i].setImageResource(R.drawable.gearclockwise);
            } else {
                gearImageArray[i].setImageResource(R.drawable.gearanticlockwise);
            }
        }
    }
}
