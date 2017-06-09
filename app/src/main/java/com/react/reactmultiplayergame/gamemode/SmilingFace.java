package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * Created by gan on 16/4/17.
 * <p>
 * User tap when there is a smiling face in easy/medium, 2 smiling faces in hard, 4 smiling faces in insane
 *
 * Insane: 4 rotating faces compared to 2 in hard
 * 0-3 sec changing speed compared to 0-4 sec in hard
 *
 * Accounted for screensize and translation
 */

public class SmilingFace extends GameMode {
    private int[] imageArrayYes;
    private int[] imageArrayNo;
    private int randomTime;
    private int correctOrWrong; // 0 = correct
    private boolean isCurrentQuestionCorrect; // false if correctOrWrong != 0. Set this var so only need to calculate if(correctOrWrong==0) once
    private int levelDifficulty;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        switch (levelDifficulty){
            case Constants.MODE_EASY:
            case Constants.MODE_MEDIUM:
                player1Question.setText(R.string.smilingface_easymedium);
                player2Question.setText(R.string.smilingface_easymedium);
                break;
            case Constants.MODE_HARD:
                player1Question.setText(R.string.smilingface_hard);
                player2Question.setText(R.string.smilingface_hard);
                break;
            case Constants.MODE_INSANE:
                player1Question.setText(R.string.smilingface_Insane);
                player2Question.setText(R.string.smilingface_Insane);
                break;
        }
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.levelDifficulty = levelDifficulty;

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_smilingfaces);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        initializeArray();
        int _20dp = (int) context.getResources().getDimension(R.dimen._20sdp);
        int parentWidth = parentLayout.getWidth();
        int parentHeight = parentLayout.getHeight();

        // only initialize once here so randomTime is 1 constant number throughout the 3 calls to customTimerDuration in QuickPlay.java
        // if initialize randomTime inside customTimerDuration, it will result in 3 different randomTime
        // How this mode achieve random changing of the smiley face is that it uses a randomTime inside customTimerDuration
        // meaning it calls a new object to change the face. Hide the timerbar to not allow user to see when the face will change

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                randomTime = (int) ((randomGenerator.nextInt(2) + randomGenerator.nextDouble() + 1.5) * 1000); // 1.5-3.5 sec
                correctOrWrong = randomGenerator.nextInt(3); // 33% chance of correct
                break;
            case Constants.MODE_MEDIUM:
                randomTime = (int) ((randomGenerator.nextInt(2) + randomGenerator.nextDouble() + 1.5) * 1000); // 1.5-3.5 sec
                correctOrWrong = randomGenerator.nextInt(4); // 25% chance of correct
                break;
            case Constants.MODE_HARD:
                randomTime = (int) ((randomGenerator.nextInt(4) + randomGenerator.nextDouble() + 0.1) * 1000); // 0.1-4 sec, decimal included. + 0.1 to prevent a case where randomTime = 0, which will "freeze" and not call next instance
                correctOrWrong = randomGenerator.nextInt(4);
                break;
            case Constants.MODE_INSANE:
                randomTime = (int) ((randomGenerator.nextInt(3) + randomGenerator.nextDouble() + 0.1) * 1000); // 0.1-3 sec, decimal included
                correctOrWrong = randomGenerator.nextInt(5); // 20% chance correct. If change to more than 5, rmb to adjust the switch case in setGameContent below to account for more case
                break;
        }


        isCurrentQuestionCorrect = correctOrWrong == 0;

        if (levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            // easy / medium where user only have to guess 1 image

            // bottom View
            ImageView imageBottom = new ImageView(context);

            RelativeLayout.LayoutParams layoutParamsBottomImage = new RelativeLayout.LayoutParams(parentHeight / 2 - _20dp, parentHeight / 2 - _20dp);
            layoutParamsBottomImage.addRule(RelativeLayout.CENTER_IN_PARENT);

            RelativeLayout bottomContainer = new RelativeLayout(context);
            bottomContainer.addView(imageBottom, layoutParamsBottomImage);

            RelativeLayout.LayoutParams layoutParamsBottomContainer = new RelativeLayout.LayoutParams(parentWidth, parentHeight / 2);
            layoutParamsBottomContainer.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


            // Top View
            ImageView imageTop = new ImageView(context);
            imageTop.setRotation(180);

            RelativeLayout.LayoutParams layoutParamsTopImage = new RelativeLayout.LayoutParams(parentHeight / 2 - _20dp, parentHeight / 2 - _20dp);
            layoutParamsTopImage.addRule(RelativeLayout.CENTER_IN_PARENT);

            RelativeLayout topContainer = new RelativeLayout(context);
            topContainer.addView(imageTop, layoutParamsTopImage);

            RelativeLayout.LayoutParams layoutParamsTopContainer = new RelativeLayout.LayoutParams(parentWidth, parentHeight / 2);
            layoutParamsTopContainer.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            if (isCurrentQuestionCorrect) {
                // set up correct scenario
                int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                imageBottom.setBackgroundResource(currentImage_correct);
                imageTop.setBackgroundResource(currentImage_correct);
            } else {
                // set up wrong scenario
                int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                imageBottom.setBackgroundResource(currentImage_Wrong);
                imageTop.setBackgroundResource(currentImage_Wrong);
            }

            parentLayout.addView(topContainer, layoutParamsTopContainer);
            parentLayout.addView(bottomContainer, layoutParamsBottomContainer);

        } else if (levelDifficulty == Constants.MODE_HARD) {
            // set up hard mode with 2 rotating images. Both images have to be smiling for correct

            ImageView imageBottom = new ImageView(context);
            ImageView imageBottom2 = new ImageView(context);
            imageBottom.setRotation(randomGenerator.nextInt(360));
            imageBottom2.setRotation(randomGenerator.nextInt(360));

            RelativeLayout.LayoutParams layoutParamsBottomImage1 = new RelativeLayout.LayoutParams(parentHeight / 2 - _20dp, parentHeight / 2 - _20dp);
            RelativeLayout.LayoutParams layoutParamsBottomImage2 = new RelativeLayout.LayoutParams(parentHeight / 2 - _20dp, parentHeight / 2 - _20dp);
            layoutParamsBottomImage1.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParamsBottomImage2.addRule(RelativeLayout.CENTER_IN_PARENT);

            LinearLayout bottomContainer = new LinearLayout(context);
            bottomContainer.setOrientation(LinearLayout.HORIZONTAL);

            RelativeLayout bottomRelative1 = new RelativeLayout(context);
            bottomRelative1.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth / 2, parentHeight / 2));
            RelativeLayout bottomRelative2 = new RelativeLayout(context);
            bottomRelative2.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth / 2, parentHeight / 2));
            bottomRelative1.addView(imageBottom, layoutParamsBottomImage1);
            bottomRelative2.addView(imageBottom2, layoutParamsBottomImage2);

            bottomContainer.addView(bottomRelative1);
            bottomContainer.addView(bottomRelative2);

            RelativeLayout.LayoutParams layoutParamsBottomContainer = new RelativeLayout.LayoutParams(parentWidth, parentHeight / 2);
            layoutParamsBottomContainer.addRule(RelativeLayout.CENTER_IN_PARENT);

            int image1 = -1;
            int image2 = -1;
            switch (correctOrWrong) {
                case 0: {
                    // both left and right correct
                    int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    int currentImage_correct2 = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    image1 = currentImage_correct;
                    image2 = currentImage_correct2;
                    break;
                }
                case 1: {
                    // left correct right wrong
                    int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    image1 = currentImage_correct;
                    image2 = currentImage_Wrong;
                    break;
                }
                case 2: {
                    // left wrong right correct
                    int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    image1 = currentImage_Wrong;
                    image2 = currentImage_correct;
                    break;
                }
                case 3: {
                    // both wrong
                    int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)]; // initialize once to set same image for top and btm
                    int currentImage_Wrong2 = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    image1 = currentImage_Wrong;
                    image2 = currentImage_Wrong2;
                    break;
                }
            }

            imageBottom.setBackgroundResource(image1);
            imageBottom2.setBackgroundResource(image2);

            parentLayout.addView(bottomContainer, layoutParamsBottomContainer);
        } else {
            // set up Insane Mode where all 4 rotating images have to be smiling for correct scenario

            int _30dp = (int) context.getResources().getDimension(R.dimen._30sdp);

            // bottom View
            ImageView imageBottom = new ImageView(context);
            ImageView imageBottom2 = new ImageView(context);

            RelativeLayout.LayoutParams layoutParamsBottomImage1 = new RelativeLayout.LayoutParams(parentHeight / 2 - _30dp, parentHeight / 2 - _30dp);
            RelativeLayout.LayoutParams layoutParamsBottomImage2 = new RelativeLayout.LayoutParams(parentHeight / 2 - _30dp, parentHeight / 2 - _30dp);
            layoutParamsBottomImage1.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParamsBottomImage2.addRule(RelativeLayout.CENTER_IN_PARENT);

            LinearLayout bottomContainer = new LinearLayout(context);
            bottomContainer.setOrientation(LinearLayout.HORIZONTAL);

            RelativeLayout bottomRelative1 = new RelativeLayout(context);
            bottomRelative1.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth / 2, parentHeight / 2));
            RelativeLayout bottomRelative2 = new RelativeLayout(context);
            bottomRelative2.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth / 2, parentHeight / 2));
            bottomRelative1.addView(imageBottom, layoutParamsBottomImage1);
            bottomRelative2.addView(imageBottom2, layoutParamsBottomImage2);

            bottomContainer.addView(bottomRelative1);
            bottomContainer.addView(bottomRelative2);

            RelativeLayout.LayoutParams layoutParamsBottomContainer = new RelativeLayout.LayoutParams(parentWidth, parentHeight / 2);
            layoutParamsBottomContainer.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


            // Top View
            ImageView imageTop = new ImageView(context);
            ImageView imageTop2 = new ImageView(context);

            RelativeLayout.LayoutParams layoutParamsTopImage1 = new RelativeLayout.LayoutParams(parentHeight / 2 - _30dp, parentHeight / 2 - _30dp);
            RelativeLayout.LayoutParams layoutParamsTopImage2 = new RelativeLayout.LayoutParams(parentHeight / 2 - _30dp, parentHeight / 2 - _30dp);
            layoutParamsTopImage1.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParamsTopImage2.addRule(RelativeLayout.CENTER_IN_PARENT);

            LinearLayout topContainer = new LinearLayout(context);
            topContainer.setOrientation(LinearLayout.HORIZONTAL);

            RelativeLayout topRelative1 = new RelativeLayout(context);
            topRelative1.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth / 2, parentHeight / 2));
            RelativeLayout topRelative2 = new RelativeLayout(context);
            topRelative2.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth / 2, parentHeight / 2));
            topRelative1.addView(imageTop, layoutParamsTopImage1);
            topRelative2.addView(imageTop2, layoutParamsTopImage2);

            topContainer.addView(topRelative1);
            topContainer.addView(topRelative2);

            RelativeLayout.LayoutParams layoutParamsTopContainer = new RelativeLayout.LayoutParams(parentWidth, parentHeight / 2);
            layoutParamsTopContainer.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            int[] finalImageArray = new int[4]; // assuming only 4 images. If in the future add/reduce the smiling images, need to change this new int[], and all below that call finalImageArray[x]

            switch (correctOrWrong) {
                case 0: {
                    // all 4 correct
                    int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    int currentImage_correct2 = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)];
                    int currentImage_correct3 = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)];
                    int currentImage_correct4 = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)];
                    // no need randomize which correct becos all are correct anw
                    finalImageArray[0] = currentImage_correct;
                    finalImageArray[1] = currentImage_correct2;
                    finalImageArray[2] = currentImage_correct3;
                    finalImageArray[3] = currentImage_correct4;
                    break;
                }
                case 1: {
                    // 1 correct 3 wrong
                    int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    int currentImage_Wrong2 = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    int currentImage_Wrong3 = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];

                    finalImageArray[0] = currentImage_correct;
                    finalImageArray[1] = currentImage_Wrong;
                    finalImageArray[2] = currentImage_Wrong2;
                    finalImageArray[3] = currentImage_Wrong3;
                    // randomize which position is the correct or wrong
                    Utils.shuffleIntArray(finalImageArray);
                    break;
                }
                case 2: {
                    // 2 correct 2 wrong
                    int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    int currentImage_correct2 = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)];
                    int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    int currentImage_Wrong2 = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];

                    finalImageArray[0] = currentImage_correct;
                    finalImageArray[1] = currentImage_correct2;
                    finalImageArray[2] = currentImage_Wrong;
                    finalImageArray[3] = currentImage_Wrong2;
                    Utils.shuffleIntArray(finalImageArray);
                    break;
                }
                case 3: {
                    // 3 correct 1 wrong
                    int currentImage_correct = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)]; // initialize once to set same image for top and btm
                    int currentImage_correct2 = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)];
                    int currentImage_correct3 = imageArrayYes[randomGenerator.nextInt(imageArrayYes.length)];
                    int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];

                    finalImageArray[0] = currentImage_correct;
                    finalImageArray[1] = currentImage_correct2;
                    finalImageArray[2] = currentImage_correct3;
                    finalImageArray[3] = currentImage_Wrong;
                    Utils.shuffleIntArray(finalImageArray);
                    break;
                }
                case 4: {
                    // all 4 wrong
                    int currentImage_Wrong = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    int currentImage_Wrong2 = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    int currentImage_Wrong3 = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];
                    int currentImage_Wrong4 = imageArrayNo[randomGenerator.nextInt(imageArrayNo.length)];

                    finalImageArray[0] = currentImage_Wrong;
                    finalImageArray[1] = currentImage_Wrong2;
                    finalImageArray[2] = currentImage_Wrong3;
                    finalImageArray[3] = currentImage_Wrong4;
                    break;
                }
            }

            imageBottom.setBackgroundResource(finalImageArray[0]);
            imageBottom2.setBackgroundResource(finalImageArray[1]);
            imageTop.setBackgroundResource(finalImageArray[2]);
            imageTop2.setBackgroundResource(finalImageArray[3]);

            imageBottom.setRotation(randomGenerator.nextInt(360));
            imageBottom2.setRotation(randomGenerator.nextInt(360));
            imageTop.setRotation(randomGenerator.nextInt(360));
            imageTop2.setRotation(randomGenerator.nextInt(360));

            parentLayout.addView(bottomContainer, layoutParamsBottomContainer);
            parentLayout.addView(topContainer, layoutParamsTopContainer);

        }
    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_smilingface);
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
        return R.raw.gamemode_happyface;
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
        return randomTime;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return isCurrentQuestionCorrect;
    }

    private void initializeArray() {
        // both Yes and No can be of varying length. Code in setGameContent account for the length, hence no need to be of any fixed length
        int[] arrayYes = {
                R.drawable.y_yes_angel,
                R.drawable.y_yes_cute,
                R.drawable.y_yes_greed,
                R.drawable.y_yes_happy,
                R.drawable.y_yes_happy2,
                R.drawable.y_yes_happy3,
                R.drawable.y_yes_happy4,
                R.drawable.y_yes_happy5,
                R.drawable.y_yes_happy6,
                R.drawable.y_yes_happy7,
                R.drawable.y_yes_happy8,
                R.drawable.y_yes_laughing,
                R.drawable.y_yes_laughing2,
                R.drawable.y_yes_laughing3,
                R.drawable.y_yes_nerd,
                R.drawable.y_yes_smart,
                R.drawable.y_yes_tongue,
                R.drawable.y_yes_wink
        };

        int[] arrayNo = {
                R.drawable.y_no_angry,
                R.drawable.y_no_angry2,
                R.drawable.y_no_confused,
                R.drawable.y_no_crying,
                R.drawable.y_no_crying2,
                R.drawable.y_no_emoji,
                R.drawable.y_no_muted,
                R.drawable.y_no_sad,
                R.drawable.y_no_sad2,
                R.drawable.y_no_scare,
                R.drawable.y_no_shocked,
                R.drawable.y_no_sick,
                R.drawable.y_no_sleepy,
                R.drawable.y_no_surprised,
                R.drawable.y_no_surprised2,
                R.drawable.y_no_surprised3,
                R.drawable.y_no_surprised4,
                R.drawable.y_no_surprised5,
                R.drawable.y_no_suspicious,
        };

        imageArrayYes = arrayYes;
        imageArrayNo = arrayNo;
    }
}
