package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;
import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;
import com.react.reactmultiplayergame.helper.Utils;

import java.util.List;
import java.util.Random;

/**
 * Created by gan on 17/4/17.
 * <p>
 * Perfect Match is whereby the "wall" from left and right can matched each other like a jigsaw puzzle
 */

public class PerfectMatch extends GameMode {
    // Use for animation to translate the entire jigsaw to show user if it fits ornot upon user tapped
    private LinearLayout leftLinearLayout, rightLinearLayout;
    int parentWidth;
    private Context context;
    // used for animation
    private float percentWidth;
    private boolean isCurrentQuestionCorrect; // true if current scenario is correct
    private ConfettiManager confettiManager;
    private int customTimerDuration;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.perfectmatch);
        player2Question.setText(R.string.perfectmatch);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.context = context;
        int parentHeight = parentLayout.getHeight();
        parentWidth = parentLayout.getWidth(); // parentWidth is global var as animation need to use parentWidth value

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pattern_perfectmatch);
        if (bitmap != null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        int objectSize = 0;
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                // can change percentWidth freely without needing to change other code,
                // as animation math accounts for the percentWidth here
                // template for nextFloat range: rand.nextFloat() * (maxX - minX) + minX;
                percentWidth = randomGenerator.nextFloat() * (0.14f - 0.09f) + 0.09f; // 0.09 to 0.14
                objectSize = (int) (percentWidth * parentWidth); // the size of the wallBase and the jigsaw objects (circle/square/triangle etc). 10% parentWidth
                isCurrentQuestionCorrect = randomGenerator.nextInt(3) == 0; // 33% chance of correct
                customTimerDuration = 2000; // set the timer here instead of customTimerDuration because of shooting star accounting for the countdown
                break;
            case Constants.MODE_MEDIUM:
                percentWidth = randomGenerator.nextFloat() * (0.1f - 0.08f) + 0.08f; // 0.08 to 0.10
                objectSize = (int) (percentWidth * parentWidth); // 8% parentWidth, i.e. total size shown = 7x4 = 28%
                isCurrentQuestionCorrect = randomGenerator.nextInt(3) == 0;
                customTimerDuration = 2000;
                break;
            case Constants.MODE_HARD:
                percentWidth = randomGenerator.nextFloat() * (0.09f - 0.06f) + 0.06f; // 0.06 to 0.09
                objectSize = (int) (percentWidth * parentWidth);
                isCurrentQuestionCorrect = randomGenerator.nextInt(4) == 0; // 25% chance of correct
                customTimerDuration = 1500;
                break;
            case Constants.MODE_INSANE:
                percentWidth = randomGenerator.nextFloat() * (0.09f - 0.05f) + 0.05f; // 0.05 to 0.09
                objectSize = (int) (percentWidth * parentWidth);
                isCurrentQuestionCorrect = randomGenerator.nextInt(4) == 0;
                customTimerDuration = 1000;
                break;
        }


        // used for view.SetY(x) to determine the current y position of the jigsaw object
        // first half of y
        int itemPosition_Y1_Correct = randomGenerator.nextInt(parentHeight / 2 - objectSize); // position up till half of height
        // 2nd half of y. Split them into 2 halves to ensure doesn't collide with itemPosition_Y1_Correct
        int itemPosition_Y2_Correct = randomGenerator.nextInt(parentHeight / 2 - objectSize) + parentHeight / 2; // position from 2nd half of height to end of height

        int currentShapeToShow = randomGenerator.nextInt(4); // choose randomly between 4 shapes
        String[] colorArray = {
                "#FFEE58", // yellow
                "#66BB6A", // green
                "#00BFA5", // blue-green
                "#42A5F5", // light-blue
                "#B2FF59", // light-green
                "#FF4081", // pink
                "#FF9800", // orange
                "#7C4DFF", // bright-purple
                "#00BFA5", // teal
                "#4FC3F7", // bright-blue
                "#F44336", // red
                "#795548", // brown
                "#18FFFF", // Cyan
                "#CDDC39", // Lime
                "#FFC107", // Amber
                "#CFD8DC", // Gray
                "#673AB7", // deep purple
                "#FF1744" // bright red
        };
        int jigsawColor = Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]);


        View wallBaseLeft = new View(context);
        wallBaseLeft.setBackgroundColor(jigsawColor);
        wallBaseLeft.setLayoutParams(new ViewGroup.LayoutParams(objectSize, parentHeight));

        ImageView jigsaw1 = new ImageView(context);
        ImageView jigsaw2 = new ImageView(context);

        if (isCurrentQuestionCorrect) {
            // show correct scenario
            jigsaw1.setY(itemPosition_Y1_Correct);
            jigsaw2.setY(itemPosition_Y2_Correct);
        } else {
            // show wrong scenario
            // The approach to show wrong scenario is to ONLY set wrong Y value to the LEFT side, as there are only 2 shapes on left sides
            // Setting wrong Y value on right side will be more troublesome since there are 5 shapes, with 3 rectangle blocks to cover the "empty" space.
            // Hence, for wrong scenario, the left side will intentionally be set wrong, and right side will always be correct
            float additionalPixelsToAddForWrong1 = 0;
            float additionalPixelsToAddForWrong2 = 0;
            switch (levelDifficulty) {
                case Constants.MODE_EASY:
                    // the larger the number for additionalPixels, the easier it is to see the current qn is wrong
                    additionalPixelsToAddForWrong1 = randomGenerator.nextInt((int) (0.04f * parentHeight)) + 0.03f * parentHeight; // move by 3% to 7% of parentHeight
                    additionalPixelsToAddForWrong2 = randomGenerator.nextInt((int) (0.04f * parentHeight)) + 0.03f * parentHeight; // move by 3% to 7% of parentHeight
                    break;
                case Constants.MODE_MEDIUM:
                    additionalPixelsToAddForWrong1 = randomGenerator.nextInt((int) (0.02f * parentHeight)) + 0.03f * parentHeight; // move by 3% to 5% of parentHeight
                    additionalPixelsToAddForWrong2 = randomGenerator.nextInt((int) (0.02f * parentHeight)) + 0.03f * parentHeight; // move by 3% to 5% of parentHeight
                    break;
                case Constants.MODE_HARD:
                    additionalPixelsToAddForWrong1 = randomGenerator.nextInt((int) (0.01f * parentHeight)) + 0.02f * parentHeight; // move by 2% to 3%% of parentHeight
                    additionalPixelsToAddForWrong2 = randomGenerator.nextInt((int) (0.01f * parentHeight)) + 0.02f * parentHeight; // move by 2% to 3%% of parentHeight
                    break;
                case Constants.MODE_INSANE:
                    additionalPixelsToAddForWrong1 = randomGenerator.nextInt((int) (0.01f * parentHeight)) + 0.02f * parentHeight; // move by 2% to 3% of parentHeight
                    additionalPixelsToAddForWrong2 = randomGenerator.nextInt((int) (0.01f * parentHeight)) + 0.02f * parentHeight; // move by 2% to 3% of parentHeight
                    break;
            }

            // create a temp variable pos1Wrong and pos2Wrong instead of changing itemPosition_Y_Correct, becos the latter variable is used for right side setY(),
            // hence cannot change it to a wrong value
            float pos1Wrong, pos2Wrong;
            if (itemPosition_Y1_Correct + additionalPixelsToAddForWrong1 > (parentHeight / 2 - objectSize)) {
                // overshot half the height, hence need to minus
                pos1Wrong = itemPosition_Y1_Correct - additionalPixelsToAddForWrong1;
            } else {
                // did not overshoot half of height, hence safe to add
                pos1Wrong = itemPosition_Y1_Correct + additionalPixelsToAddForWrong1;
            }

            if (itemPosition_Y2_Correct + additionalPixelsToAddForWrong2 > (parentHeight - objectSize)) {
                // overshot the entire height, hence need to minus
                pos2Wrong = itemPosition_Y2_Correct - additionalPixelsToAddForWrong2;
            } else {
                // did not overshoot the entire height, hence safe to add
                pos2Wrong = itemPosition_Y2_Correct + additionalPixelsToAddForWrong2;
            }
            jigsaw1.setY(pos1Wrong);
            jigsaw2.setY(pos2Wrong);
        }

        if (currentShapeToShow == 0) {
            // show square shape
            GradientDrawable squareDrawable = new GradientDrawable();
            squareDrawable.setSize(objectSize, objectSize);
            squareDrawable.setColor(jigsawColor);

            jigsaw1.setImageDrawable(squareDrawable);
            jigsaw2.setImageDrawable(squareDrawable);
        } else {
            int shapeResourceId = 0;
            switch (currentShapeToShow) {
                case 1: // triangle
                    shapeResourceId = R.drawable.jigsawleft_triangle;
                    break;
                case 2: // double triangle
                    shapeResourceId = R.drawable.jigsawleft_twotriangle;
                    break;
                case 3: // circle shape
                    shapeResourceId = R.drawable.jigsawleft_circle;
                    break;
            }
            jigsaw1.setImageResource(shapeResourceId);
            jigsaw2.setImageResource(shapeResourceId);
        }

        jigsaw1.setColorFilter(new PorterDuffColorFilter(jigsawColor, PorterDuff.Mode.SRC_ATOP));
        jigsaw2.setColorFilter(new PorterDuffColorFilter(jigsawColor, PorterDuff.Mode.SRC_ATOP));

        RelativeLayout.LayoutParams left_jigsaw1Param = new RelativeLayout.LayoutParams(objectSize, objectSize);
        RelativeLayout.LayoutParams left_jigsaw2Param = new RelativeLayout.LayoutParams(objectSize, objectSize);

        RelativeLayout jigsawObjectsLayout_Left = new RelativeLayout(context);
        jigsawObjectsLayout_Left.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        jigsawObjectsLayout_Left.addView(jigsaw1, left_jigsaw1Param);
        jigsawObjectsLayout_Left.addView(jigsaw2, left_jigsaw2Param);

        leftLinearLayout = new LinearLayout(context);
        leftLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftLinearLayout.addView(wallBaseLeft);
        leftLinearLayout.addView(jigsawObjectsLayout_Left);
        leftLinearLayout.setGravity(Gravity.CENTER);


        // Right side of jigsaw

        View wallBaseRight = new View(context);
        wallBaseRight.setBackgroundColor(jigsawColor);
        wallBaseRight.setLayoutParams(new ViewGroup.LayoutParams(objectSize, parentHeight));

        ImageView rightJigsaw1 = new ImageView(context);
        ImageView rightJigsaw2 = new ImageView(context);
        ImageView rightJigsaw3 = new ImageView(context);
        ImageView rightJigsaw4 = new ImageView(context);
        ImageView rightJigsaw5 = new ImageView(context);
        // no need setY() for rightJigsaw1 as it is position from the top, i.e. setY(0)
        rightJigsaw2.setY(itemPosition_Y1_Correct);
        rightJigsaw3.setY(itemPosition_Y1_Correct + objectSize);
        rightJigsaw4.setY(itemPosition_Y2_Correct);
        rightJigsaw5.setY(itemPosition_Y2_Correct + objectSize);

        if (currentShapeToShow == 0) {
            // show square shape, i.e. right side no need show anything, just a blank space,
            // hence no code require
        } else {
            int shapeResourceId = 0;
            switch (currentShapeToShow) {
                case 1: // triangle
                    shapeResourceId = R.drawable.jigsawright_triangle;
                    break;
                case 2: // double triangle
                    shapeResourceId = R.drawable.jigsawright_twotriangle;
                    break;
                case 3: // circle shape
                    shapeResourceId = R.drawable.jigsawright_circle;
                    break;
            }
            rightJigsaw2.setImageResource(shapeResourceId);
            rightJigsaw4.setImageResource(shapeResourceId);
        }

        GradientDrawable rightDrawableTop = new GradientDrawable();
        rightDrawableTop.setSize(objectSize, itemPosition_Y1_Correct /* height of top jigsaw */);
        rightDrawableTop.setColor(jigsawColor);

        GradientDrawable rightDrawableCenter = new GradientDrawable();
        rightDrawableCenter.setSize(objectSize, itemPosition_Y2_Correct - itemPosition_Y1_Correct - objectSize /* the height of middle jigsaw*/);
        rightDrawableCenter.setColor(jigsawColor);

        GradientDrawable rightDrawableBottom = new GradientDrawable();
        rightDrawableBottom.setSize(objectSize, parentHeight - objectSize - itemPosition_Y2_Correct /* height of bottom jigsaw */);
        rightDrawableBottom.setColor(jigsawColor);

        // 1,3,5 is the rectangle block for the empty spaces, not the shape space
        rightJigsaw1.setImageDrawable(rightDrawableTop);
        rightJigsaw3.setImageDrawable(rightDrawableCenter);
        rightJigsaw5.setImageDrawable(rightDrawableBottom);

        // 2,4 is the shape space whereby the custom shape is fitted
        rightJigsaw2.setColorFilter(new PorterDuffColorFilter(jigsawColor, PorterDuff.Mode.SRC_ATOP));
        rightJigsaw4.setColorFilter(new PorterDuffColorFilter(jigsawColor, PorterDuff.Mode.SRC_ATOP));

        RelativeLayout.LayoutParams right_jigsaw2Param = new RelativeLayout.LayoutParams(objectSize, objectSize);
        RelativeLayout.LayoutParams right_jigsaw4Param = new RelativeLayout.LayoutParams(objectSize, objectSize);

        RelativeLayout jigsawObjectsLayout_Right = new RelativeLayout(context);
        jigsawObjectsLayout_Right.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout.LayoutParams right_jigsaw1Param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams right_jigsaw3Param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams right_jigsaw5Param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        jigsawObjectsLayout_Right.addView(rightJigsaw1, right_jigsaw1Param);
        jigsawObjectsLayout_Right.addView(rightJigsaw2, right_jigsaw2Param);
        jigsawObjectsLayout_Right.addView(rightJigsaw3, right_jigsaw3Param);
        jigsawObjectsLayout_Right.addView(rightJigsaw4, right_jigsaw4Param);
        jigsawObjectsLayout_Right.addView(rightJigsaw5, right_jigsaw5Param);

        rightLinearLayout = new LinearLayout(context);
        rightLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        rightLinearLayout.addView(jigsawObjectsLayout_Right);
        rightLinearLayout.addView(wallBaseRight);
        rightLinearLayout.setGravity(Gravity.CENTER);


        // left has 60% of parentWidth space as there is a timerbar, hence give it abit more space
        // If change this parentWidth/5*3, or the parentWidth/5*2, need to recalculate animation translateX_value as the Math now is based on this 60% width size
        RelativeLayout.LayoutParams overallLeftLinearLayoutParam = new RelativeLayout.LayoutParams(parentWidth / 5 * 3, ViewGroup.LayoutParams.MATCH_PARENT);
        overallLeftLinearLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        RelativeLayout.LayoutParams overallRightLinearLayoutParam = new RelativeLayout.LayoutParams(parentWidth / 5 * 2, ViewGroup.LayoutParams.MATCH_PARENT);
        overallRightLinearLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        parentLayout.addView(leftLinearLayout, overallLeftLinearLayoutParam);
        parentLayout.addView(rightLinearLayout, overallRightLinearLayoutParam);


        int confettiSize = context.getResources().getDimensionPixelOffset(R.dimen._2sdp);
        int defaultVelocityFast = context.getResources().getDimensionPixelOffset(R.dimen._120sdp);
        int emissionDuration;
        if (GameMode.callingNewDialog) {
            // accounting for dialog delay
            emissionDuration = customTimerDuration + GameMode.dialogDelay;
        } else {
            emissionDuration = customTimerDuration;
        }

        // setting own confetti up, because default implementation results in an exploding radius that is not accounted for tablet size since it only contains one value
        final List<Bitmap> allPossibleConfetti = com.github.jinatonic.confetti.Utils.generateConfettiBitmaps(new int[]{Color.parseColor("#64FFFFFF"), Color.parseColor("#96F5F5F5"),
                Color.parseColor("#96FFFF40"), Color.parseColor("#9640FFFF")}, confettiSize);

        final int numConfetti = allPossibleConfetti.size();
        ConfettoGenerator confettoGenerator = new ConfettoGenerator() {
            @Override
            public Confetto generateConfetto(Random random) {
                final Bitmap bitmap = allPossibleConfetti.get(random.nextInt(numConfetti));
                return new BitmapConfetto(bitmap);
            }
        };

        ConfettiSource confettiSource = new ConfettiSource(parentWidth / 2, parentHeight / 2);

        confettiManager = new ConfettiManager(context, confettoGenerator, confettiSource, parentLayout)
                .setTTL(1000)
                .setBound(new Rect(0, 0, parentWidth, parentHeight))
                .setVelocityX(0, defaultVelocityFast)
                .setVelocityY(0, defaultVelocityFast)
                .enableFadeOut(com.github.jinatonic.confetti.Utils.getDefaultAlphaInterpolator())
                .setInitialRotation(180, 180)
                .setRotationalAcceleration(360, 180)
                .setTargetRotationalVelocity(360)
                .setEmissionRate(10)
                .setEmissionDuration(emissionDuration);
        confettiManager.animate();

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_perfectmatch);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        animateToGetAnswer();
        terminateShootingStars();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        animateToGetAnswer();
        terminateShootingStars();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        animateToGetAnswer();
        terminateShootingStars();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        animateToGetAnswer();
        terminateShootingStars();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_perfectmatch;
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
        return isCurrentQuestionCorrect;
    }

    private void animateToGetAnswer() {
        // translateX_value took into account objectSize, so objectSize can change freely without needing to change the math here
        // BUT if change the 60% left container and 40% right container, then need to recalculate the translateX_value here since it is based on 60% and 40% math

        // +2.5f to valueLeft, move additional pixel just in case the casting to (int) of objectSize in setGameContent were to round down the value,
        // + 2.5f impt!! +1.5f is still not enough after testing
        // and the exact calculation of translateX value were to be underestimated. Don't +2 as it is too much and will lead to a correct depiction of picture, but the answer is wrong

        float translateX_valueLeft = (0.3f - percentWidth) * parentWidth + 2.5f; // math is calculated based on left container takes 60% width and right container takes 40% width
        float translateX_valueRight = -0.2f * parentWidth;

        leftLinearLayout.animate().translationX(translateX_valueLeft).setDuration(Utils.getAnswerDelayPreference(context) / 2).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        rightLinearLayout.animate().translationX(translateX_valueRight).setDuration(Utils.getAnswerDelayPreference(context) / 2).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    private void terminateShootingStars() {
        confettiManager.setEmissionRate(0);
        confettiManager.terminate();
        confettiManager = null;
    }
}
