package com.react.reactmultiplayergame.gamemode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;

/**
 * Created by gan on 19/4/17.
 *
 * Tap when there is a multi colored orb
 *
 * Insane: 8 lanes of orbs instead of 6 for hard
 * faster orb moving speed than hard
 * and harder for correct ans than hard
 *
 * Accounted for screen size and translation
 */

public class MultiColoredOrb extends GameMode {
    private ObjectAnimator[] translateAnimationArray;
    private ImageView orbArray[];
    private int levelDifficulty;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.gradientorb);
        player2Question.setText(R.string.gradientorb);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        this.levelDifficulty = levelDifficulty;

        int numberOfLanes = 0; // the number of lanes occupying the orb.
        int parentHeight = parentLayout.getHeight();
        int parentWidth = parentLayout.getWidth();

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                numberOfLanes = 5; // feel free to change this var, it will not break other code
                break;
            case Constants.MODE_MEDIUM:
                numberOfLanes = 5;
                break;
            case Constants.MODE_HARD:
                numberOfLanes = 6;
                break;
            case Constants.MODE_INSANE:
                numberOfLanes = 8;
                break;
        }

        int orbSize = (int) (0.75 * parentHeight / numberOfLanes); // orb occupying 75% of lane space so there is 25% space for padding space top and btm
        final int[] colorArray = {
                // color of orbs
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

        RelativeLayout relativeLayoutArray[] = new RelativeLayout[numberOfLanes];
        orbArray = new ImageView[numberOfLanes];
        final GradientDrawable gradientDrawableArray[] = new GradientDrawable[numberOfLanes];
        translateAnimationArray = new ObjectAnimator[numberOfLanes];

        LinearLayout overallLinearLayout = new LinearLayout(context);
        overallLinearLayout.setOrientation(LinearLayout.VERTICAL);
        overallLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // initialize all the orbs into its lane
        for (int i = 0; i < numberOfLanes; i++) {
            // need to set up a final int that is used to reference inner anonymous class onAnimationRepeat.
            // cannot use a global var becos that value will be change, and then each call to onAnimationRepeat will Not be calling the correct index
            // don't change this final int index
            final int index = i;

            relativeLayoutArray[i] = new RelativeLayout(context);
            relativeLayoutArray[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentHeight / numberOfLanes));
            relativeLayoutArray[i].setGravity(Gravity.CENTER_VERTICAL);

            gradientDrawableArray[i] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{getRandomColorFromFullSpectrum(), getRandomColorFromFullSpectrum()});
            gradientDrawableArray[i].setSize(orbSize, orbSize);
            gradientDrawableArray[i].setShape(GradientDrawable.OVAL);

            orbArray[i] = new ImageView(context);
            orbArray[i].setImageDrawable(gradientDrawableArray[i]);
            orbArray[i].setColorFilter(colorArray[randomGenerator.nextInt(colorArray.length)]);
            orbArray[i].setTag("WrongAnswer"); // current orb is not a gradient but a solid color, hence set the tag as wrong answer. Case and word sensitive for tag!

            relativeLayoutArray[i].addView(orbArray[i]);
            overallLinearLayout.addView(relativeLayoutArray[i]);

            if (i % 2 == 0) {
                // start from left animate to right
                translateAnimationArray[index] = ObjectAnimator.ofFloat(orbArray[index], "x", -orbSize, parentWidth);
            } else {
                // start from right animate to left
                translateAnimationArray[index] = ObjectAnimator.ofFloat(orbArray[index], "x", parentWidth, -orbSize);
            }
            translateAnimationArray[index].setRepeatCount(Animation.INFINITE);
            translateAnimationArray[index].setDuration(getRandomAnimationTime());
            translateAnimationArray[index].setInterpolator(new LinearInterpolator());
            translateAnimationArray[index].addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    // change duration and color for each repeat

                    if (isGradientOrb()) {
                        orbArray[index].setColorFilter(0); // no color filter to reveal its original gradient color
                        orbArray[index].setTag("CorrectAnswer"); // current orb is a gradient orb, hence set the tag as correct ans. Case and word sensitive
                    } else {
                        orbArray[index].setColorFilter(colorArray[randomGenerator.nextInt(colorArray.length)]);
                        orbArray[index].setTag("WrongAnswer"); // current orb is not a gradient orb, hence set tag as wrong ans
                    }
                    translateAnimationArray[index].cancel(); // impt to cancel before we call start again
                    translateAnimationArray[index].setDuration(getRandomAnimationTime()).start();
                }
            });

            translateAnimationArray[i].start();
        }
        parentLayout.addView(overallLinearLayout);
    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_gradientorb);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        stopAnimationGetResult();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        stopAnimationGetResult();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        stopAnimationGetResult();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        stopAnimationGetResult();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_gradientorb;
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
        if(translateAnimationArray != null && orbArray != null) {
            stopAnimationGetResult();
        }
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        // the way we get answer is to setTag to the imageView which has a gradient orb
        // hence we iterate through all the imageView and see if there is any tag with CorrectAnswer.
        // If there is, that means there is a gradient orb currently, and return true. Else return false
        for (int i = 0; i < orbArray.length; i++) {
            if (orbArray[i].getTag().equals("CorrectAnswer")) return true;
        }
        return false;
    }

    @Override
    public boolean gameContent_RequireRectangularWhiteStroke() {
        return true;
    }

    private void stopAnimationGetResult() {
        boolean containsCorrectAnswer = false;
        for (int i = 0; i < translateAnimationArray.length; i++) {
            translateAnimationArray[i].cancel();
            if (orbArray[i].getTag().equals("CorrectAnswer")) {
                containsCorrectAnswer = true;
            }
        }
        // the reason for setting another for loop instead of setting the color filter in the for loop above
        // is to only set the gray filter to wrong orbs if there is a correct answer present.
        // If there is totally no gradient orb present, then don't set the gray filter, and let user see the original colored orb
        if (containsCorrectAnswer) {
            for (int i = 0; i < orbArray.length; i++) {
                if (!orbArray[i].getTag().equals("CorrectAnswer")) {
                    // darken all those that is not correct answer
                    orbArray[i].setColorFilter(Color.parseColor("#6a787f"));
                }
            }
        }

    }

    private int getRandomAnimationTime() {
        // use for randomly setting the translate animation with each pass of the orb
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                return (int) ((randomGenerator.nextDouble() * 1000 + 1500)); // 1500-2500 ms
            case Constants.MODE_MEDIUM:
                return (int) ((randomGenerator.nextDouble() * 1000 + 1000)); // 1000-2000 ms
            case Constants.MODE_HARD:
                return (int) ((randomGenerator.nextDouble() * 1000 + 1000)); // 1000-2000 ms
            case Constants.MODE_INSANE:
                return (int) ((randomGenerator.nextDouble() * 1000 + 500)); // 500-1500 ms
            default:
                return 1500;
        }
    }

    private boolean isGradientOrb() {
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                return randomGenerator.nextInt(15) == 0; // 1/15 = 6.6% chance of correct
            case Constants.MODE_MEDIUM:
                return randomGenerator.nextInt(17) == 0; // 1/17 = 5.8% chance of correct
            case Constants.MODE_HARD:
                return randomGenerator.nextInt(20) == 0; // 1/20 = 5% chance of correct
            case Constants.MODE_INSANE:
                return randomGenerator.nextInt(26) == 0; // 1/25 = 3.8% chance of correct
            default:
                return false;
        }
    }

    private int getRandomColorFromFullSpectrum() {
        int r = randomGenerator.nextInt(255);
        int g = randomGenerator.nextInt(255);
        int b = randomGenerator.nextInt(255);
        return Color.rgb(r,g,b);
    }

}
