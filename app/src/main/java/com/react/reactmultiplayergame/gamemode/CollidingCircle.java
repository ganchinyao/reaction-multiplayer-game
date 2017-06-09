package com.react.reactmultiplayergame.gamemode;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gan on 27/3/17.
 * Colliding circle is when 2 circle move and collide onto each other
 */

public class CollidingCircle extends GameMode {
    private float ball1_x;
    private float ball1_y;
    private float ball2_x;
    private float ball2_y;

    private ImageView ball1, ball2;
    private int ball1_radius, ball2_radius;
    private boolean currentQuestionAnswer = false;
    private int playerThatTouched;
    private RelativeLayout.LayoutParams layoutParamsb1;
    private RelativeLayout.LayoutParams layoutParamsb2;
    // randomize ball1 and ball2 initial location
    private int random1 = randomGenerator.nextInt(360);
    private int random2 = randomGenerator.nextInt(360);
    private RelativeLayout parentLayout;
    private float radiusToTravel;
    private ValueAnimator animatorball1;
    private ValueAnimator animatorball2;
    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.circlecollide);
        player2Question.setText(R.string.circlecollide);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        this.parentLayout = parentLayout;

        // set transparent background
        parentLayout.setBackgroundResource(0);

        //Did not shuffle this array, hence call colorArray.get(random)
        ArrayList<Integer> colorArray = new ArrayList<>();
        colorArray.add(Color.parseColor("#FF1744")); //red
        colorArray.add(Color.parseColor("#FF5252")); //red
        colorArray.add(Color.parseColor("#ff80ab")); //pink
        colorArray.add(Color.parseColor("#E040FB")); //purple
        colorArray.add(Color.parseColor("#EA80FC")); //purple
        colorArray.add(Color.parseColor("#7E57C2")); // deep purple
        colorArray.add(Color.parseColor("#90CAF9")); // light blue
        colorArray.add(Color.parseColor("#18FFFF")); // cyan
        colorArray.add(Color.parseColor("#64FFDA")); // blue-green
        colorArray.add(Color.parseColor("#1DE9B6")); // blue-green
        colorArray.add(Color.parseColor("#CCFF90")); // yellow-green
        colorArray.add(Color.parseColor("#B2FF59")); // yellow-green
        colorArray.add(Color.parseColor("#76FF03")); // green
        colorArray.add(Color.parseColor("#CDDC39")); // lime
        colorArray.add(Color.parseColor("#FFEB3B")); // yellow
        colorArray.add(Color.parseColor("#F9A825")); // orange
        colorArray.add(Color.parseColor("#FF6F00")); // dark orange
        colorArray.add(Color.parseColor("#FFC107")); // amber
        colorArray.add(Color.parseColor("#FF3D00")); // deep orange
        colorArray.add(Color.parseColor("#8D6E63")); // brown
        colorArray.add(Color.parseColor("#6D4C41")); // dark brown
        colorArray.add(Color.parseColor("#757575")); // dark gray
        colorArray.add(Color.parseColor("#607D8B")); // blue gray
        colorArray.add(Color.parseColor("#000000")); // black
        colorArray.add(Color.parseColor("#FFFFFF")); // white

        ball1 = new ImageView(context);
        Drawable background = ContextCompat.getDrawable(context, R.drawable.b1);
        background.mutate();
        background.setColorFilter(new PorterDuffColorFilter(colorArray.get(randomGenerator.nextInt(colorArray.size())), PorterDuff.Mode.SRC_ATOP));
        ball1.setBackground(background);

        ball2 = new ImageView(context);
        Drawable background2 = ContextCompat.getDrawable(context, R.drawable.b1);
        background2.mutate();
        background2.setColorFilter(new PorterDuffColorFilter(colorArray.get(randomGenerator.nextInt(colorArray.size())), PorterDuff.Mode.SRC_ATOP));
        ball2.setBackground(background2);

        animatorball1 = ValueAnimator.ofFloat(0, 1);
        animatorball2 = ValueAnimator.ofFloat(0, 1);

        switch (levelDifficulty){
            case Constants.MODE_EASY:
                layoutParamsb1 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/4, parentLayout.getHeight()/4);
                layoutParamsb2 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/4, parentLayout.getHeight()/4);
                radiusToTravel = parentLayout.getHeight()/3;
                animatorball1.setDuration(2000);
                animatorball2.setDuration(1700);
                break;
            case Constants.MODE_MEDIUM:
                layoutParamsb1 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/5, parentLayout.getHeight()/5);
                layoutParamsb2 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/5, parentLayout.getHeight()/5);
                radiusToTravel = parentLayout.getHeight()/100 * 38;
                animatorball1.setDuration(2000);
                animatorball2.setDuration(1700);
                break;
            case Constants.MODE_HARD:
                layoutParamsb1 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/6, parentLayout.getHeight()/6);
                layoutParamsb2 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/6, parentLayout.getHeight()/6);
                radiusToTravel = parentLayout.getHeight()/5 *2;
                animatorball1.setDuration(1500);
                animatorball2.setDuration(1200);
                break;
            case Constants.MODE_INSANE:
                layoutParamsb1 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/7, parentLayout.getHeight()/7);
                layoutParamsb2 = new RelativeLayout.LayoutParams(parentLayout.getHeight()/7, parentLayout.getHeight()/7);
                radiusToTravel = parentLayout.getHeight()/5 *2;
                animatorball1.setDuration(1100);
                animatorball2.setDuration(900);
                break;
        }

        animatorball1.setRepeatCount(ValueAnimator.INFINITE);
        animatorball1.setInterpolator(new LinearInterpolator());
        animatorball2.setRepeatCount(ValueAnimator.INFINITE);
        animatorball2.setInterpolator(new LinearInterpolator());

        animatorball1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                ball1.setTranslationX((float) (radiusToTravel * Math.sin(Math.toRadians((value * 360f + random1) % 360))));
                ball1.setTranslationY((float) (radiusToTravel * Math.cos(Math.toRadians((value * 360f + random1) % 360))));
                setBall_xy(1, ball1.getX(), ball1.getY());
            }
        });

        animatorball2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                ball2.setTranslationX((float) (radiusToTravel * Math.cos(Math.toRadians((value * 360f + random2) % 360))));
                ball2.setTranslationY((float) (radiusToTravel * Math.sin(Math.toRadians((value * 360f + random2) % 360))));
                setBall_xy(2, ball2.getX(), ball2.getY());
            }
        });

        layoutParamsb1.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParamsb2.addRule(RelativeLayout.CENTER_IN_PARENT);

        ball1.post(new Runnable() {
            @Override
            public void run() {
                ball1_radius = ball1.getHeight()/2;
            }
        });

        ball2.post(new Runnable() {
            @Override
            public void run() {
                ball2_radius = ball2.getHeight()/2;
            }
        });
        parentLayout.addView(ball1, layoutParamsb1);
        parentLayout.addView(ball2, layoutParamsb2);

        animatorball1.start();
        animatorball2.start();

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getResources().getString(R.string.dialog_circlecollide);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER1;
        terminateAnimation();
        calculateCollide();
        return true;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER2;
        terminateAnimation();
        calculateCollide();
        return true;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER3;
        terminateAnimation();
        calculateCollide();
        return true;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        playerThatTouched = Constants.PLAYER4;
        terminateAnimation();
        calculateCollide();
        return true;
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        return currentQuestionAnswer;
    }

    @Override
    public int playerThatTouched() {
        return playerThatTouched;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_circlecollide;
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
        terminateAnimation();
        // set back the default blue background
        parentLayout.setBackgroundResource(R.drawable.gamemode_gamcontent_background);
    }

    private void terminateAnimation() {
        if(animatorball1 != null && animatorball2 != null) {
            animatorball1.cancel();
            animatorball1.removeAllListeners();
            animatorball1.removeAllUpdateListeners();

            animatorball2.cancel();
            animatorball2.removeAllListeners();
            animatorball2.removeAllUpdateListeners();
        }
    }

    private void setBall_xy (int ball, float x, float y) {
        switch (ball) {
            case 1:
                ball1_x = x;
                ball1_y = y;
                break;
            case 2:
                ball2_x = x;
                ball2_y = y;
                break;
        }
    }


    private void calculateCollide(){
        double distance = Math.sqrt(((ball1_x - ball2_x) * (ball1_x - ball2_x)) + ((ball1_y - ball2_y) * (ball1_y - ball2_y)));
        if (distance < ball1_radius + ball2_radius) {
            // ball collided
            currentQuestionAnswer = true;
        }
    }

}
