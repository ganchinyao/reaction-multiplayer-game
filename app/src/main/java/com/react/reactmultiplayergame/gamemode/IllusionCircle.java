package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.Utils;

/**
 * Created by gan on 23/4/17.
 *
 * Where the 3 circles are all equal in size
 *
 * Insane mode: only timer 1s vs 1.5s for hard, and circle wrong difference slighly lesser. The rest game play no change
 *
 * Accounted for screensize and language
 */

public class IllusionCircle extends GameMode {
    private ImageView circle1, circle2, circle3;
    private int parentHeight, parentWidth; // to be used in animation to translate
    private Context context;
    private int correctOrWrong; // 0 = correct, all other numbers = wrong scenario
    private int levelDifficulty;
    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.illusioncircle);
        player2Question.setText(R.string.illusioncircle);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.context = context;
        this.levelDifficulty = levelDifficulty;
        // every new instance will toggle this true false, so that every new instance will flip the 1 circle top to become 1 circle bottom and vice versa
        GameMode.illusionCircletoggle = !GameMode.illusionCircletoggle;

        // anchor_orbSize is the size that is used for other orbSize to calculate from, i.e. 80% of anchor_orbSize for example
        int anchor_orbSize = 0;
        // rangeMin and rangeMax are used to generate a random double within the range,
        // for use during wrong scenario, where we want a random percent of another orb size, e.g. 0.8 * anchor_orbSize
        // so two orbs have different size. Here we don't use minus or plus to make a wrong orb size, but a factor of anchor_orbSize, e.g. 0.9 * anchor_orbSize
        double rangeMin = 0;
        double rangeMax = 0;
        parentHeight = parentLayout.getHeight();
        parentWidth = parentLayout.getWidth();

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
                anchor_orbSize = parentHeight/3;
                correctOrWrong = randomGenerator.nextInt(3); // 33% chance correct
                rangeMax = 0.9; // the closer it is to 1, the more obvious is the wrong size, i.e. 0.5size is more obvious than 0.9size when compared to size
                rangeMin = 0.75;
                break;
            case Constants.MODE_MEDIUM:
                anchor_orbSize = parentHeight/3;
                correctOrWrong = randomGenerator.nextInt(3);
                rangeMax = 0.9;
                rangeMin = 0.8;
                break;
            case Constants.MODE_HARD:
                anchor_orbSize = parentHeight/3;
                correctOrWrong = randomGenerator.nextInt(4); // 25% chance correct
                rangeMax = 0.92;
                rangeMin = 0.82;
                break;
            case Constants.MODE_INSANE:
                anchor_orbSize = parentHeight/3;
                correctOrWrong = randomGenerator.nextInt(4); // can go more than 4 without breaking code
                rangeMax = 0.92;
                rangeMin = 0.87;
                break;
        }

        // assume 3 orbs only.
        // used to store 3 different orbSize, and later on shuffle this array after initialize and set
        // circle1 to be orbSize[0], circle2 to be orbSize[1] and circle3 to be orbSize[2]
        // using an array here so that later we can shuffle to ensure there will be different combination as to which orb is correct or wrong
        int [] orbSizeArray = new int[3];

        if(correctOrWrong == 0){
            // all orb same size
            orbSizeArray[0] = orbSizeArray[1] = orbSizeArray[2] = anchor_orbSize;
        } else {
            switch (correctOrWrong){
                case 1:
                    // 1 orb wrong size, 2 orbs correct size
                    // later on orbSizeArray will be shuffled, so now just focus on filling all the elements in the array
                    orbSizeArray[0] = orbSizeArray[1] = anchor_orbSize;
                    // a percent within rangeMin to rangeMax of anchor_orbSize
                    orbSizeArray[2] = (int) ((rangeMin + (rangeMax - rangeMin) * randomGenerator.nextDouble()) * anchor_orbSize);
                    break;
                case 2:
                    // 2 orbs wrong size, 1 orb correct size
                    orbSizeArray[0] = anchor_orbSize;
                    // dont need to worry if random generates same number, becos orbSizeArray[1] and [2] are different size than orbSizeArray[0] anw, so cfm will wrong ans
                    orbSizeArray[1] = (int) ((rangeMin + (rangeMax - rangeMin) * randomGenerator.nextDouble()) * anchor_orbSize);
                    orbSizeArray[2] = (int) ((rangeMin + (rangeMax - rangeMin) * randomGenerator.nextDouble()) * anchor_orbSize);
                    break;
                default:
                    // will only trigger during hard/insane mode for case 3.
                    // Reason to put it as default instead of case 3: is also in the future if we add a higher number for int correctOrWrong,
                    // the code will not break as default will get triggered for any other number

                    // same as case 2, just need a duplicate for a higher chance of wrong scenario in hard/insane mode
                    // 2 orbs wrong size, 1 orb correct size
                    orbSizeArray[0] = anchor_orbSize;
                    // dont need to worry if random generates same number, becos orbSizeArray[1] and [2] are different size than orbSizeArray[0] anw, so cfm will wrong ans
                    orbSizeArray[1] = (int) ((rangeMin + (rangeMax - rangeMin) * randomGenerator.nextDouble()) * anchor_orbSize);
                    orbSizeArray[2] = (int) ((rangeMin + (rangeMax - rangeMin) * randomGenerator.nextDouble()) * anchor_orbSize);
                    break;
            }
        }

        // shuffle the size array so we can achieve all combination as to which orb is correct or wrong
        Utils.shuffleIntArray(orbSizeArray);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        // set the size accordingly, circle1 = [0] , circle2 = [1], circle3 = [2]. No need randomize here as the orbSizeArray is alr randomize above under Utils.shuffleIntArray
        gradientDrawable1.setSize(orbSizeArray[0], orbSizeArray[0]);
        gradientDrawable1.setShape(GradientDrawable.OVAL);
        gradientDrawable1.setColor(Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]));

        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setSize(orbSizeArray[1], orbSizeArray[1]);
        gradientDrawable2.setShape(GradientDrawable.OVAL);
        gradientDrawable2.setColor(Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]));

        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setSize(orbSizeArray[2], orbSizeArray[2]);
        gradientDrawable3.setShape(GradientDrawable.OVAL);
        gradientDrawable3.setColor(Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]));

        // assuming top is 1 circle and bottom is 2 circles
        LinearLayout topLinearLayout = new LinearLayout(context);
        topLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, parentHeight/2));
        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLinearLayout.setGravity(Gravity.CENTER);
        topLinearLayout.setClipChildren(false); // allow circle to animate out of layout bound


        LinearLayout bottomLinearLayout = new LinearLayout(context);
        bottomLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, parentHeight/2));
        bottomLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLinearLayout.setClipChildren(false); // allow circle to animate out of layout bound


        circle1 = new ImageView(context);
        circle2 = new ImageView(context);
        circle3 = new ImageView(context);

        circle1.setImageDrawable(gradientDrawable1);
        circle2.setImageDrawable(gradientDrawable2);
        circle3.setImageDrawable(gradientDrawable3);

        RelativeLayout circleEnclosure2 = new RelativeLayout(context);
        circleEnclosure2.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth/2, parentHeight/2));

        RelativeLayout circleEnclosure3 = new RelativeLayout(context);
        circleEnclosure3.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth/2, parentHeight/2));

        RelativeLayout.LayoutParams circle1_Param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams circle2_Param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        circle2_Param.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout.LayoutParams circle3_Param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        circle3_Param.addRule(RelativeLayout.CENTER_IN_PARENT);

        circleEnclosure2.addView(circle2, circle2_Param);
        circleEnclosure3.addView(circle3, circle3_Param);

        topLinearLayout.addView(circle1, circle1_Param);
        bottomLinearLayout.addView(circleEnclosure2);
        bottomLinearLayout.addView(circleEnclosure3);

        LinearLayout overallLinearLayout = new LinearLayout(context);
        overallLinearLayout.setOrientation(LinearLayout.VERTICAL);
        overallLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, parentHeight));
        overallLinearLayout.setClipChildren(false); // allow circle to animate out of layout bound

        if(GameMode.illusionCircletoggle) {
            // every new instance will toggle between 1 circle top to 1 circle bottom and vice versa
            overallLinearLayout.addView(topLinearLayout);
            overallLinearLayout.addView(bottomLinearLayout);
        } else {
            overallLinearLayout.addView(bottomLinearLayout);
            overallLinearLayout.addView(topLinearLayout);

        }

        parentLayout.addView(overallLinearLayout);

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_illusioncircle);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        getAnimationOnAnswer();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        getAnimationOnAnswer();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        getAnimationOnAnswer();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        getAnimationOnAnswer();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_illusioncircle;
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
                return 2000;
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

    private void getAnimationOnAnswer() {
        if(GameMode.illusionCircletoggle){
            //animate top to bottom
            // animate towards teh center. divide by 4 becoz each circle occupies half of height, and they are center in parent.
            // hence /2 /2 = /4 = results in the circle center animates towards the center of parentlayout
            // set alpha to 0.5f so that we can see through which circle is bigger or smaller, i.e. no big circle cover all the view of smaller circle behind
            circle1.animate().yBy(parentHeight/4).alpha(0.5f).setDuration(Utils.getAnswerDelayPreference(context)/2).start();
            circle2.animate().xBy(parentWidth/4).yBy(-parentHeight/4).alpha(0.5f).setDuration(Utils.getAnswerDelayPreference(context)/2).start();
            circle3.animate().xBy(-parentWidth/4).yBy(-parentHeight/4).alpha(0.5f).setDuration(Utils.getAnswerDelayPreference(context)/2).start();
        } else {
            //animate bottom to top
            circle1.animate().yBy(-parentHeight/4).alpha(0.5f).setDuration(Utils.getAnswerDelayPreference(context)/2).start();
            circle2.animate().xBy(parentWidth/4).yBy(parentHeight/4).alpha(0.5f).setDuration(Utils.getAnswerDelayPreference(context)/2).start();
            circle3.animate().xBy(-parentWidth/4).yBy(parentHeight/4).alpha(0.5f).setDuration(Utils.getAnswerDelayPreference(context)/2).start();
        }
    }
}
