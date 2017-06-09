package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;
import com.react.reactmultiplayergame.helper.Utils;

/**
 * Created by gan on 9/4/17.
 *
 * User sees two length and pressed if both rectangles are of same length
 *
 * Easy = rightImage vertical, left image horiztonal, static in position
 * Medium = rightImage vertical, left image static in position between 0-360 degree angle
 * Hard = rightImage static in position between -20 to 20 degree, leftImage static in position between 0-360 degree
 * Insane = rightImage and RightImage both constantly rotating around
 *
 * Accounted for translation and screensize
 */

public class IllusionRectangle extends GameMode {
    private ImageView rightImage, leftImage;
    // for correctOrWrong, 0 = correct. Use to determine current qn should be correct or not
    private int correctOrWrong;
    private int currentLength2, currentBreath2, currentLength, currentBreath;
    private RelativeLayout parentLayout;
    private int leftRotionDegree, rightRotationDegree;
    private int levelDifficulty;
    private boolean userPressed = false;
    private Context context;
    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.illusionrectangle);
        player2Question.setText(R.string.illusionrectangle);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.parentLayout = parentLayout;
        this.levelDifficulty = levelDifficulty;
        this.context = context;

        //note here the colorArray we used array, not arrayList
        String colorArray [] = {
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
                //33% chance correct
                correctOrWrong = randomGenerator.nextInt(3);
                rightRotationDegree = 0;
                leftRotionDegree = 90;
                break;
            case Constants.MODE_MEDIUM:
                correctOrWrong = randomGenerator.nextInt(3);
                rightRotationDegree = 0;
                leftRotionDegree = randomGenerator.nextInt(340) + 20; // minimum 20. Can go as much as you want all the way to 360 without fearing image exceed bound, because image has 60% parentLayout.getWidth, which is greater than the height available.
                break;
            case Constants.MODE_HARD:
                // 25% chance correct
                rightRotationDegree = randomGenerator.nextInt(41) - 20; // -20 to 20 degree. ** CANNOT go too much, because left image only has 40% of width. If rotate too much, height of the image will exceed width of the image.
                leftRotionDegree = randomGenerator.nextInt(340) + 20;
                correctOrWrong = randomGenerator.nextInt(4);
                break;
            case Constants.MODE_INSANE:
                rightRotationDegree = 0; // not rotating, becos animation will handle the rotating job
                leftRotionDegree = 0;
                correctOrWrong = randomGenerator.nextInt(4);
                break;
        }

        int MAX_LENGTH, MAX_BREATH;

        // addDimension_toWrongAns (in px) is used to add to currentLength2/Breath for correctOrWrong = wrong situation, to intentionally generate a wrong question
        int addDimension_toWrongAns = randomGenerator.nextInt((int) context.getResources().getDimension(R.dimen.gameContent_illusionlength_additionalLength)) +5; //+5px in case random returns 0

        // algorithm to calculate maximum length and breath of rectangle to ensure no rectangle exceeds the width and height which may lead to inaccurate depicition of length
        // Assumes that one rectangle occupies 40% of width, and the other rectangle occupies 60%
        if(parentLayout.getHeight() > parentLayout.getWidth()/5 *3) {
            MAX_LENGTH = parentLayout.getWidth()/5 *3 - addDimension_toWrongAns; // - addDimension_toWrongAns so that in the event correctOrWrong = wrong, adding this dimension to generate wrong question would not overshoot bound
        } else {
            MAX_LENGTH = parentLayout.getHeight() - addDimension_toWrongAns;
        }
        if(parentLayout.getWidth()/5 *2 > parentLayout.getHeight()) {
            MAX_BREATH = parentLayout.getHeight() - addDimension_toWrongAns;
        }
        else {
            MAX_BREATH = parentLayout.getWidth() /5 *2 - addDimension_toWrongAns;
        }


        // additionalLength/Breath is used so that if random.nextInt generate 0, at least there is a minimum length/breath
        int additionalLength = (int) context.getResources().getDimension(R.dimen.gameContent_illusionlength_additionalLength);
        int additionalBreath = (int) context.getResources().getDimension(R.dimen.gameContent_illusionlength_additionalBreath);
        // abitralily get a currentLength2 and Breath to set for the rectangle
        currentLength2 = randomGenerator.nextInt(parentLayout.getHeight()/10 *7 /* up to 70% of height */) + additionalLength;
        currentBreath2 = randomGenerator.nextInt((parentLayout.getWidth()/5) /* up to 20% of width, i.e. half of the left 40% container */) +additionalBreath;


        while(currentLength2 > MAX_LENGTH) {
            // keep looping to ensure currentLength2 will not be > Max_Length to ensure no rectangle is out of bound
            // - 30 so that the loop doesnt have to go through so many times to save resources
            currentLength2 -= 30;
        }
        while(currentBreath2 >MAX_BREATH) {
            // keep looping to ensure currentBreath2 will not be > Max_Breath to ensure no rectangle is out of bound
            currentBreath2 -=30;
        }

        if(correctOrWrong == 0) {
            // setup for correct scenario
            currentLength = currentLength2;
            currentBreath = currentBreath2;
        } else {
            // setup for wrong scenario
            // randomly decide to just set wrong length, or wrong breath
            if(randomGenerator.nextInt(2) == 0) {
                currentLength = currentLength2 - addDimension_toWrongAns;
                currentBreath = currentBreath2;
                if(currentLength <= 0) currentLength = 5; // length must be positive. Just set it as 5. Do not have to worry currentLength == currentLength2, since currentLength2 has +additionalLength which gurantees it to be bigger than 5.
            }
            else {
                currentLength = currentLength2;
                currentBreath = currentBreath2 - addDimension_toWrongAns;
                if(currentBreath <= 0) currentBreath =5; // breath must be positive. Just set as 5. No need worry currentBreath == currentBreath2 since +additionaBreath in currentBreath2 gurantees it to be more than 5.
            }
        }


        int randomColor1 = Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]);
        int randomColor2 = Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]);


        GradientDrawable rightDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {randomColor1,randomColor2});;
        rightDrawable.setSize(currentBreath2, currentLength2);
        rightDrawable.setDither(true);

        GradientDrawable leftDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {randomColor1,randomColor2});
        leftDrawable.setSize(currentBreath, currentLength);
        leftDrawable.setDither(true);

        rightImage = new ImageView(context);
        rightImage.setImageDrawable(rightDrawable);
        rightImage.setRotation(rightRotationDegree);

        leftImage = new ImageView(context);
        leftImage.setImageDrawable(leftDrawable);
        leftImage.setRotation(leftRotionDegree);

        LinearLayout overallContainer = new LinearLayout(context);
        overallContainer.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams rectRight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rectRight.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout.LayoutParams rectLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rectLeft.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout rightContainer = new RelativeLayout(context);
        rightContainer.setLayoutParams(new ViewGroup.LayoutParams(parentLayout.getWidth()/5*2, ViewGroup.LayoutParams.MATCH_PARENT));
        rightContainer.addView(rightImage, rectRight);

        RelativeLayout leftContainer = new RelativeLayout(context);
        // 60% width for left image so as to have more space to display horizontal length
        leftContainer.setLayoutParams(new ViewGroup.LayoutParams(parentLayout.getWidth()/5*3, ViewGroup.LayoutParams.MATCH_PARENT));
        leftContainer.addView(leftImage, rectLeft);


        overallContainer.addView(leftContainer);
        overallContainer.addView(rightContainer);

        parentLayout.addView(overallContainer);

        if(levelDifficulty == Constants.MODE_INSANE) {
            // rotate all rect for insane mode
            rotateContinuously();
        }
    }



    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_illusionrectangle);
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
        return R.raw.gamemode_illusionlength;
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
        if(levelDifficulty == Constants.MODE_INSANE && !userPressed) {
            // user did not pressed, hence clearAnimation is not invoked in getAnswerAnimation. Therefore call clearAnimation manually.
            // this means timer has ended and new object is called, hence stop animating old objects
            if(rightImage != null && leftImage!= null) {
                rightImage.clearAnimation();
                leftImage.clearAnimation();
            }
        }
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        userPressed = true;
        getAnswerAnimation();
        if(correctOrWrong == 0) return true;
        else return false;
    }

    // triggered when any player clicked, and call an animation to see if both rectangle are equal
    private void getAnswerAnimation() {

        if(levelDifficulty == Constants.MODE_INSANE) {
            // stop the rotating animation
            rightImage.clearAnimation();
            leftImage.clearAnimation();
        }

        boolean rightRotated = false;
        boolean leftRotated = false;

        if(currentLength != currentLength2) {
            // compare length, set both to vertical
            rightRotated = true;
        }
        else if(currentBreath != currentBreath2) {
            // compare breath, set both to horizontal
            leftRotated = true;
        } else {
            // both rectangles are equal. set both to vertical
            rightRotated = true;
        }

        // algorithm to calculate how much to animate to if there is a rotation or no rotation. If no rotation, animation is straight forward
        // if there is a rotation, need to account for how much more/less to animate in order to reach desired position.
        // ** Dont change algorithm. Carefully thought out
        if(leftRotated) {
            if (currentLength > currentBreath) {
                leftImage.animate().x((parentLayout.getWidth() / 5 * 3 - leftImage.getWidth()) - ((currentLength - currentBreath) / 2)).rotation(90)
                        .setDuration((long)(0.4 * Utils.getAnswerDelayPreference(context))).start();
            } else {
                leftImage.animate().x((parentLayout.getWidth() / 5 * 3 - leftImage.getWidth()) + ((currentBreath - currentLength) / 2)).rotation(90).setDuration((long)(0.4 * Utils.getAnswerDelayPreference(context))).start();
            }
        } else {
            leftImage.animate().x(parentLayout.getWidth() / 5 * 3 - leftImage.getWidth()).rotation(0).setDuration((long)(0.4 * Utils.getAnswerDelayPreference(context))).start();
        }

        if(rightRotated) {
            rightImage.animate().x(0).rotation(0).setDuration((long)(0.4 * Utils.getAnswerDelayPreference(context))).start();
        }
        else {
            rightImage.animate().x((currentLength2 - currentBreath2)/2).rotation(90).setDuration((long)(0.4 * Utils.getAnswerDelayPreference(context))).start();
        }
    }

    private void rotateContinuously() {
        // use for insane mode where rect keeps on rotating
        int leftRotate = randomGenerator.nextInt(360); // ramdomise starting point
        int one;
        if(randomGenerator.nextInt(2) == 0) one = 1; // set whether rotation is clockwise or anticlockwise
        else one = -1;
        RotateAnimation rotateAnimation_leftRect_360 = new RotateAnimation(leftRotate, one *(360f + leftRotate),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation_leftRect_360.setInterpolator(new LinearInterpolator());
        rotateAnimation_leftRect_360.setDuration(2000);
        rotateAnimation_leftRect_360.setRepeatCount(Animation.INFINITE);

        if(currentLength2 >= (parentLayout.getWidth()/5 *2) - 30 /* -30 leeway as diagonal length when rotating can be larger than horizontal length*/) {
            // only rotate -30 to 30 degree to prevent exceeding bound since length of image is too huge

            RotateAnimation rotateAnimation_rightRect_30 = new RotateAnimation(30f * -(one), -30f * (-one), /* negative one to turn in opp direction from leftRect */
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation_rightRect_30.setInterpolator(new LinearInterpolator());
            rotateAnimation_rightRect_30.setDuration(1000);
            rotateAnimation_rightRect_30.setRepeatMode(Animation.REVERSE);
            rotateAnimation_rightRect_30.setRepeatCount(Animation.INFINITE);
            rightImage.startAnimation(rotateAnimation_rightRect_30);

        } else {
            // free to rotate 360 since layout bound will not be exceeded. Go in opposite direction to leftImage
            RotateAnimation rotateAnimation_rightRect_360 = new RotateAnimation(0, 360f * (-one),
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation_rightRect_360.setInterpolator(new LinearInterpolator());
            rotateAnimation_rightRect_360.setDuration(2000);
            rotateAnimation_rightRect_360.setRepeatCount(Animation.INFINITE);
            rightImage.startAnimation(rotateAnimation_rightRect_360);
        }

        leftImage.startAnimation(rotateAnimation_leftRect_360);

    }
}
