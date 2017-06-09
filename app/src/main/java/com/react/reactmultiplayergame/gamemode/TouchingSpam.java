package com.react.reactmultiplayergame.gamemode;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.react.reactmultiplayergame.helper.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by gan on 23/3/17.
 * TouchingSpam is where player spam the button on the screen for the most amount of touch per predefined time
 */

public class TouchingSpam extends GameMode {
    int player1Taps = 0;
    int player2Taps = 0;
    int player3Taps = 0;
    int player4Taps = 0;
    int colorIterator = 0;
    int numberOfColorCalled = 0;
    private RelativeLayout p1ButtonRelativeLayout, p2ButtonRelativeLayout, p3ButtonRelativeLayout, p4ButtonRelativeLayout;
    private RelativeLayout player1MovingButton, player2MovingButton, player3MovingButton, player4MovingButton;
    private TextView player1Tracker, player2Tracker, player3Tracker, player4Tracker;
    private int noOfPlayers;
    private int levelDifficulty;
    private float moveXby_p1, moveYby_p1, moveXby_p2, moveYby_p2, moveXby_p3, moveYby_p3, moveXby_p4, moveYby_p4;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.spamhit);
        player2Question.setText(R.string.spamhit);
    }


    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

        switch (noOfPlayers) {
            case Constants.MODE_TWOPLAYER:
                p1ButtonRelativeLayout.removeView(player1Tracker);
                p2ButtonRelativeLayout.removeView(player2Tracker);
                if(levelDifficulty==Constants.MODE_INSANE || levelDifficulty==Constants.MODE_HARD){
                    p1ButtonRelativeLayout.removeView(player1MovingButton);
                    p2ButtonRelativeLayout.removeView(player2MovingButton);
                }
                break;
            case Constants.MODE_THREEPLAYER:
                p1ButtonRelativeLayout.removeView(player1Tracker);
                p2ButtonRelativeLayout.removeView(player2Tracker);
                p3ButtonRelativeLayout.removeView(player3Tracker);
                if(levelDifficulty==Constants.MODE_INSANE || levelDifficulty==Constants.MODE_HARD){
                    p1ButtonRelativeLayout.removeView(player1MovingButton);
                    p2ButtonRelativeLayout.removeView(player2MovingButton);
                    p3ButtonRelativeLayout.removeView(player3MovingButton);
                }
                break;
            case Constants.MODE_FOURPLAYER:
                p1ButtonRelativeLayout.removeView(player1Tracker);
                p2ButtonRelativeLayout.removeView(player2Tracker);
                p3ButtonRelativeLayout.removeView(player3Tracker);
                p4ButtonRelativeLayout.removeView(player4Tracker);
                if(levelDifficulty==Constants.MODE_INSANE || levelDifficulty==Constants.MODE_HARD){
                    p1ButtonRelativeLayout.removeView(player1MovingButton);
                    p2ButtonRelativeLayout.removeView(player2MovingButton);
                    p3ButtonRelativeLayout.removeView(player3MovingButton);
                    p4ButtonRelativeLayout.removeView(player4MovingButton);
                }
                break;
        }

        Utils.canAnimate = false;
    }

    @Override
    public void setGameContent(LinearLayout rootView, final RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.noOfPlayers = noOfPlayers;
        this.levelDifficulty = levelDifficulty;

        float trackerSize = context.getResources().getDimension(R.dimen._20ssp);

        int _5dp = (int) context.getResources().getDimension(R.dimen._5sdp);
        int _10dp = (int) context.getResources().getDimension(R.dimen._10sdp);

        //Create a new relativelayout to host the changing color background instead of calling directly on parentLayout, so that parentLayout.clear will remove this child
        //when new instances of gameMode is called
        RelativeLayout backgroundChangingColor = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        parentLayout.addView(backgroundChangingColor, layoutParams);
        callColor(backgroundChangingColor);

        switch (noOfPlayers) {
            case Constants.MODE_TWOPLAYER: {
                p1ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.twoplayer_player1Button);
                p2ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.twoplayer_player2Button);
                player1Tracker = new TextView(context);
                player1Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player1Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER1));
                player1Tracker.setTypeface(null, Typeface.ITALIC);
                RelativeLayout.LayoutParams p1Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p1Tracker.setMargins(0, 0, _10dp, _5dp);
                p1Tracker.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                p1Tracker.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                player2Tracker = new TextView(context);
                player2Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player2Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER2));
                player2Tracker.setTypeface(null, Typeface.ITALIC);
                player2Tracker.setRotation(180);
                RelativeLayout.LayoutParams p2Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p2Tracker.setMargins(_10dp, _5dp, 0, 0);

                p1ButtonRelativeLayout.addView(player1Tracker, p1Tracker);
                p2ButtonRelativeLayout.addView(player2Tracker, p2Tracker);
                break;
            }
            case Constants.MODE_THREEPLAYER: {
                p1ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.threeplayer_player1Button);
                p2ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.threeplayer_player2Button);
                p3ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.threeplayer_player3Button);
                player1Tracker = new TextView(context);
                player1Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player1Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER1));
                player1Tracker.setTypeface(null, Typeface.ITALIC);
                RelativeLayout.LayoutParams p1Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p1Tracker.setMargins(0, 0, _10dp, _5dp);
                p1Tracker.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                p1Tracker.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                player2Tracker = new TextView(context);
                player2Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player2Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER2));
                player2Tracker.setTypeface(null, Typeface.ITALIC);
                player2Tracker.setRotation(180);
                RelativeLayout.LayoutParams p2Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p2Tracker.setMargins(_10dp, _5dp, 0, 0);

                player3Tracker = new TextView(context);
                player3Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player3Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER3));
                player3Tracker.setTypeface(null, Typeface.ITALIC);
                player3Tracker.setRotation(180);
                RelativeLayout.LayoutParams p3Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p3Tracker.setMargins(_10dp, _5dp, 0, 0);

                p1ButtonRelativeLayout.addView(player1Tracker, p1Tracker);
                p2ButtonRelativeLayout.addView(player2Tracker, p2Tracker);
                p3ButtonRelativeLayout.addView(player3Tracker, p3Tracker);

                break;
            }
            case Constants.MODE_FOURPLAYER: {
                p1ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.fourplayer_player1Button);
                p2ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.fourplayer_player2Button);
                p3ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.fourplayer_player3Button);
                p4ButtonRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.fourplayer_player4Button);
                player1Tracker = new TextView(context);
                player1Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player1Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER1));
                player1Tracker.setTypeface(null, Typeface.ITALIC);
                RelativeLayout.LayoutParams p1Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p1Tracker.setMargins(0, 0, _10dp, _5dp);
                p1Tracker.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                p1Tracker.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                player2Tracker = new TextView(context);
                player2Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player2Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER2));
                player2Tracker.setTypeface(null, Typeface.ITALIC);
                // even though p1tracker and p2tracker are the same, but dont use the same layout param for addview, as it will not work correctly,
                // hence creating the same new instance
                RelativeLayout.LayoutParams p2Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p2Tracker.setMargins(0, 0, _10dp, _5dp);
                p2Tracker.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                p2Tracker.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                player3Tracker = new TextView(context);
                player3Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player3Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER3));
                player3Tracker.setTypeface(null, Typeface.ITALIC);
                player3Tracker.setRotation(180);
                RelativeLayout.LayoutParams p3Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p3Tracker.setMargins(_10dp, _5dp, 0, 0);

                player4Tracker = new TextView(context);
                player4Tracker.setTextSize(TypedValue.COMPLEX_UNIT_PX, trackerSize);
                player4Tracker.setTextColor(Utils.getActualColorTag(context, Constants.PLAYER4));
                player4Tracker.setTypeface(null, Typeface.ITALIC);
                player4Tracker.setRotation(180);
                RelativeLayout.LayoutParams p4Tracker = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p4Tracker.setMargins(_10dp, _5dp, 0, 0);

                p1ButtonRelativeLayout.addView(player1Tracker, p1Tracker);
                p2ButtonRelativeLayout.addView(player2Tracker, p2Tracker);
                p3ButtonRelativeLayout.addView(player3Tracker, p3Tracker);
                p4ButtonRelativeLayout.addView(player4Tracker, p4Tracker);

                break;
            }
        }


        //Moving buttons for difficulty HARD and INSANE
        // Insane rotates, Hard only moves, and moves slower than Insane too
        if(levelDifficulty == Constants.MODE_INSANE || levelDifficulty == Constants.MODE_HARD) {
            float _20dp = context.getResources().getDimension(R.dimen._20sdp);
            float _25dp = context.getResources().getDimension(R.dimen._25sdp);
            float _50dp = context.getResources().getDimension(R.dimen._50sdp);
            float _100dp = context.getResources().getDimension(R.dimen._100sdp);
            // random starting position for moving buttons
            int [] paramPosition = {RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_IN_PARENT, RelativeLayout.CENTER_HORIZONTAL
                    , RelativeLayout.CENTER_VERTICAL, RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_TOP};

            switch(noOfPlayers) {
                case Constants.MODE_TWOPLAYER: {
                    int buttonWidth = p1ButtonRelativeLayout.getWidth() / 5 *2;
                    int buttonHeight = p1ButtonRelativeLayout.getHeight() /5 * 2;
                    player1MovingButton = new RelativeLayout(context);
                    player1MovingButton.setBackgroundResource(R.drawable.button_design);
                    player1MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    // we set the button background to be transparent. Do not have to set back to original at removedynamicalview, becoz inside QuickPlay.java,
                    // on timer end, it will auto call green/red correct/wrong button, follow which auto set back to original button
                    p1ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player1MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER1);
                        }
                    });

                    player2MovingButton = new RelativeLayout(context);
                    player2MovingButton.setBackgroundResource(R.drawable.button_design);
                    player2MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p2ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player2MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER2);
                        }
                    });

                    RelativeLayout.LayoutParams p1moving = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
                    p1moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p1moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    RelativeLayout.LayoutParams p2moving = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
                    p2moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p2moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    p1ButtonRelativeLayout.addView(player1MovingButton, p1moving);
                    p2ButtonRelativeLayout.addView(player2MovingButton, p2moving);

                    Utils.canAnimate = true;
                    if(levelDifficulty == Constants.MODE_HARD){
                        animateMovingButton_p1(player1MovingButton, p1ButtonRelativeLayout, _50dp, _20dp, 0);
                        animateMovingButton_p2(player2MovingButton, p2ButtonRelativeLayout, _50dp, _20dp, 0);
                    }
                    else {
                        animateMovingButton_p1(player1MovingButton, p1ButtonRelativeLayout, _100dp, _20dp, _10dp);
                        animateMovingButton_p2(player2MovingButton, p2ButtonRelativeLayout, _100dp, _20dp, _10dp);
                    }
                    break;
                }
                case Constants.MODE_THREEPLAYER: {
                    int smallButtonWidth = p2ButtonRelativeLayout.getWidth() / 5*2;
                    int smallButtonHeight = p2ButtonRelativeLayout.getHeight() / 3;

                    player1MovingButton = new RelativeLayout(context);
                    player1MovingButton.setBackgroundResource(R.drawable.button_design);
                    player1MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p1ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player1MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER1);
                        }
                    });

                    player2MovingButton = new RelativeLayout(context);
                    player2MovingButton.setBackgroundResource(R.drawable.button_design);
                    player2MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p2ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player2MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER2);
                        }
                    });

                    player3MovingButton = new RelativeLayout(context);
                    player3MovingButton.setBackgroundResource(R.drawable.button_design);
                    player3MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p3ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player3MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER3);
                        }
                    });

                    RelativeLayout.LayoutParams p1moving = new RelativeLayout.LayoutParams(p1ButtonRelativeLayout.getWidth()/5 *2, smallButtonHeight);
                    p1moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p1moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    RelativeLayout.LayoutParams p2moving = new RelativeLayout.LayoutParams(smallButtonWidth, smallButtonHeight);
                    p2moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p2moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    RelativeLayout.LayoutParams p3moving = new RelativeLayout.LayoutParams(smallButtonWidth, smallButtonHeight);
                    p3moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p3moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    p1ButtonRelativeLayout.addView(player1MovingButton, p1moving);
                    p2ButtonRelativeLayout.addView(player2MovingButton, p2moving);
                    p3ButtonRelativeLayout.addView(player3MovingButton, p3moving);

                    Utils.canAnimate = true;
                    if(levelDifficulty== Constants.MODE_HARD){
                        animateMovingButton_p1(player1MovingButton, p1ButtonRelativeLayout, _50dp, _20dp, 0);
                        animateMovingButton_p2(player2MovingButton, p2ButtonRelativeLayout, _25dp, _20dp, 0);
                        animateMovingButton_p3(player3MovingButton, p3ButtonRelativeLayout, _25dp, _20dp, 0);
                    }

                    else {
                        animateMovingButton_p1(player1MovingButton, p1ButtonRelativeLayout, _100dp, _20dp, _10dp);
                        animateMovingButton_p2(player2MovingButton, p2ButtonRelativeLayout, _50dp, _20dp, _10dp);
                        animateMovingButton_p3(player3MovingButton, p3ButtonRelativeLayout, _50dp, _20dp, _10dp);
                    }
                    break;
                }
                case Constants.MODE_FOURPLAYER: {
                    int buttonWidth = p1ButtonRelativeLayout.getWidth() / 5 * 2;
                    int buttonHeight = p1ButtonRelativeLayout.getHeight() / 3;

                    player1MovingButton = new RelativeLayout(context);;
                    player1MovingButton.setBackgroundResource(R.drawable.button_design);
                    player1MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p1ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player1MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER1);
                        }
                    });


                    player2MovingButton = new RelativeLayout(context);
                    player2MovingButton.setBackgroundResource(R.drawable.button_design);
                    player2MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p2ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player2MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER2);
                        }
                    });

                    player3MovingButton = new RelativeLayout(context);
                    player3MovingButton.setBackgroundResource(R.drawable.button_design);
                    player3MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p3ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player3MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER3);
                        }
                    });

                    player4MovingButton = new RelativeLayout(context);
                    player4MovingButton.setBackgroundResource(R.drawable.button_design);
                    player4MovingButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    p4ButtonRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                    player4MovingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTrackerScore(Constants.PLAYER4);
                        }
                    });

                    RelativeLayout.LayoutParams p1moving = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
                    p1moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p1moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    RelativeLayout.LayoutParams p2moving = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
                    p2moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p2moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    RelativeLayout.LayoutParams p3moving = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
                    p3moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p3moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    RelativeLayout.LayoutParams p4moving = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
                    p4moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);
                    p4moving.addRule(paramPosition[randomGenerator.nextInt(paramPosition.length)]);

                    p1ButtonRelativeLayout.addView(player1MovingButton, p1moving);
                    p2ButtonRelativeLayout.addView(player2MovingButton, p2moving);
                    p3ButtonRelativeLayout.addView(player3MovingButton, p3moving);
                    p4ButtonRelativeLayout.addView(player4MovingButton, p4moving);

                    Utils.canAnimate = true;
                    if(levelDifficulty == Constants.MODE_HARD){
                        animateMovingButton_p1(player1MovingButton, p1ButtonRelativeLayout, _25dp, _20dp, 0);
                        animateMovingButton_p2(player2MovingButton, p2ButtonRelativeLayout, _25dp, _20dp, 0);
                        animateMovingButton_p3(player3MovingButton, p3ButtonRelativeLayout, _25dp, _20dp, 0);
                        animateMovingButton_p4(player4MovingButton, p4ButtonRelativeLayout, _25dp, _20dp, 0);
                    }
                    else {
                        animateMovingButton_p1(player1MovingButton, p1ButtonRelativeLayout, _50dp, _20dp, _10dp);
                        animateMovingButton_p2(player2MovingButton, p2ButtonRelativeLayout, _50dp, _20dp, _10dp);
                        animateMovingButton_p3(player3MovingButton, p3ButtonRelativeLayout, _50dp, _20dp, _10dp);
                        animateMovingButton_p4(player4MovingButton, p4ButtonRelativeLayout, _50dp, _20dp, _10dp);
                    }
                    break;
                }
            }
        }

        int parentWidth = parentLayout.getWidth();
        int parentHeight = parentLayout.getHeight();
        int confettiSize = context.getResources().getDimensionPixelOffset(R.dimen._5sdp);
        int defaultVelocityFast = context.getResources().getDimensionPixelOffset(R.dimen._120sdp);

        // setting own confetti up, because default implementation results in an exploding radius that is not accounted for tablet size since it only contains one value
        final List<Bitmap> allPossibleConfetti = com.github.jinatonic.confetti.Utils.generateConfettiBitmaps(new int[] { Color.parseColor("#64FF9E80"), Color.RED, Color.parseColor("#18FFFF"), Color.parseColor("#64FFDA"), Color.parseColor("#6200EA"), Color.parseColor("#37474F")
                , Color.parseColor("#64FFFF00"), Color.parseColor("#FFAB00"), Color.parseColor("#64FFEB3B"), Color.parseColor("#FF1744"), Color.parseColor("#64FFFFFF")}, confettiSize);

        final int numConfetti = allPossibleConfetti.size();
        ConfettoGenerator confettoGenerator = new ConfettoGenerator() {
            @Override
            public Confetto generateConfetto(Random random) {
                final Bitmap bitmap = allPossibleConfetti.get(random.nextInt(numConfetti));
                return new BitmapConfetto(bitmap);
            }
        };

        ConfettiSource confettiSource = new ConfettiSource(parentWidth/2, parentHeight/2);

       final ConfettiManager confettiManager = new ConfettiManager(context, confettoGenerator, confettiSource, parentLayout)
                .setTTL(1000)
                .setBound(new Rect(0, 0, parentWidth, parentHeight))
                .setVelocityX(0, defaultVelocityFast)
                .setVelocityY(0, defaultVelocityFast)
                .enableFadeOut(com.github.jinatonic.confetti.Utils.getDefaultAlphaInterpolator())
                .setInitialRotation(180, 180)
                .setRotationalAcceleration(360, 180)
                .setTargetRotationalVelocity(360)
                .setEmissionRate(150)
                .setEmissionDuration(10000);
        confettiManager.animate();



        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                confettiManager.setEmissionRate(0);
                if(player1MovingButton!=null)
                    player1MovingButton.setClickable(false);
                if(player2MovingButton!=null)
                    player2MovingButton.setClickable(false);
                if(player3MovingButton!=null)
                    player3MovingButton.setClickable(false);
                if(player4MovingButton!=null)
                    player4MovingButton.setClickable(false);
            }
        }, customTimerDuration() + GameMode.dialogDelay - 300);




    }


    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_touchingspam;
    }

    // Set the gameContent background changing color
    private void callColor(final RelativeLayout parentLayout) {

        ArrayList<Integer> colorArray = new ArrayList<>();
        colorArray.add(Color.parseColor("#80B0BEC5"));
        colorArray.add(Color.parseColor("#80FFAB91"));
        colorArray.add(Color.parseColor("#80EA80FC"));
        colorArray.add(Color.parseColor("#8080DEEA"));
        colorArray.add(Color.parseColor("#80FFE082"));
        colorArray.add(Color.parseColor("#80BCAAA4"));
        colorArray.add(Color.parseColor("#80CE93D8"));
        colorArray.add(Color.parseColor("#80A5D6A7"));
        colorArray.add(Color.parseColor("#80F9A825"));
        colorArray.add(Color.parseColor("#80F5F5F5"));
        colorArray.add(Color.parseColor("#80FF8A80"));
        Collections.shuffle(colorArray);
        final float[] from = new float[3],
                to = new float[3];

        if (colorIterator >= colorArray.size() - 1) return;
        numberOfColorCalled++;
        Color.colorToHSV(colorArray.get(colorIterator), from);   // from a color
        Color.colorToHSV(colorArray.get(colorIterator + 1), to);     // to another color

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(1500);                              // for 1500 ms

        final float[] hsv = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                parentLayout.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.start();
        colorIterator++;

        //recusive this method after 1500seconds again
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (numberOfColorCalled >= 5) return;
                callColor(parentLayout);
            }
        }, 1500);
    }


    private void animateMovingButton_p1(final RelativeLayout button, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate){
        moveXby_p1 = move_X_by;
        moveYby_p1 = move_Y_by;
        if(button.getX() > buttonContainer.getWidth() -button.getWidth()) {
            if(moveXby_p1 >0) {
                // for explaination of how these if else if works, see OddAndVowel animation comment
                moveXby_p1 = -moveXby_p1;
            }
        } else if(button.getX() <0) {
            moveXby_p1 = Math.abs(moveXby_p1);
        }

        if((button.getY()+button.getHeight()) > buttonContainer.getHeight()) {
            if(moveYby_p1 > 0) {
                moveYby_p1 = -moveYby_p1;
            }
        } else if(button.getY() <0) {
            moveYby_p1 = Math.abs(moveYby_p1);
        }

        button.animate().translationXBy(moveXby_p1).translationYBy(moveYby_p1).setDuration(300).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                    animateMovingButton_p1(button,  buttonContainer, moveXby_p1, moveYby_p1, rotate);
            }
        }) .start();
    }

    private void animateMovingButton_p2(final RelativeLayout button, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate){
        moveXby_p2 = move_X_by;
        moveYby_p2 = move_Y_by;
        if(button.getX() > buttonContainer.getWidth() -button.getWidth()) {
            if(moveXby_p2 >0) {
                moveXby_p2 = -moveXby_p2;
            }
        } else if(button.getX() <0) {
            moveXby_p2 = Math.abs(moveXby_p2);
        }

        if((button.getY()+button.getHeight()) > buttonContainer.getHeight()) {
            if(moveYby_p2 >0) {
                moveYby_p2 = -moveYby_p2;
            }
        } else if(button.getY() <0){
            moveYby_p2 = Math.abs(moveYby_p2);
        }

        button.animate().translationXBy(moveXby_p2).translationYBy(moveYby_p2).setDuration(300).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                    animateMovingButton_p2(button,  buttonContainer, moveXby_p2, moveYby_p2, rotate);
            }
        }) .start();
    }

    private void animateMovingButton_p3(final RelativeLayout button, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate){
        moveXby_p3 = move_X_by;
        moveYby_p3 = move_Y_by;
        if((button.getX() > buttonContainer.getWidth() -button.getWidth() )) {
            if(moveXby_p3 >0) {
                moveXby_p3 = -moveXby_p3;
            }
        } else if(button.getX() <0) {
            moveXby_p3 = Math.abs(moveXby_p3);
        }

        if((button.getY()+button.getHeight()) > buttonContainer.getHeight()) {
            if(moveYby_p3 >0) {
                moveYby_p3 = -moveYby_p3;
            }
        } else if(button.getY() <0) {
            moveYby_p3 = Math.abs(moveYby_p3);
        }

        button.animate().translationXBy(moveXby_p3).translationYBy(moveYby_p3).setDuration(300).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                    animateMovingButton_p3(button,  buttonContainer, moveXby_p3, moveYby_p3, rotate);
            }
        }) .start();
    }

    private void animateMovingButton_p4(final RelativeLayout button, final RelativeLayout buttonContainer, float move_X_by, float move_Y_by, final int rotate){
        moveXby_p4 = move_X_by;
        moveYby_p4 = move_Y_by;
        if(button.getX() > buttonContainer.getWidth() -button.getWidth()) {
            if(moveXby_p4 >0) {
                moveXby_p4 = -moveXby_p4;
            }
        } else if(button.getX() <0) {
            moveXby_p4 = Math.abs(moveXby_p4);
        }

        if((button.getY()+button.getHeight()) > buttonContainer.getHeight()) {
            if(moveYby_p4 >0) {
                moveYby_p4 = -moveYby_p4;
            }
        } else if(button.getY() <0) {
            moveYby_p4 = Math.abs(moveYby_p4);
        }

        button.animate().translationXBy(moveXby_p4).translationYBy(moveYby_p4).setDuration(300).rotationBy(rotate).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(Utils.canAnimate)
                    animateMovingButton_p4(button,  buttonContainer, moveXby_p4, moveYby_p4, rotate);
            }
        }) .start();
    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getResources().getString(R.string.dialog_spam);
    }

    @Override
    public boolean requireATimer() {
        return true;
    }

    @Override
    public boolean wantToShowTimer() {
        return true;
    }

    @Override
    public boolean requireDefaultTimer() {
        return false;
    }

    @Override
    public int customTimerDuration() {
        return 6000; // 6 seconds custom timer
    }

    private void updateTrackerScore(int player) {
        switch (player){
            case Constants.PLAYER1:
                if (player1Tracker != null) {
                    player1Taps++;
                    player1Tracker.setText("" + player1Taps);
                }
                break;
            case Constants.PLAYER2:
                if (player2Tracker != null) {
                    player2Taps++;
                    player2Tracker.setText("" + player2Taps);
                }
                break;
            case Constants.PLAYER3:
                if (player3Tracker != null) {
                    player3Taps++;
                    player3Tracker.setText("" + player3Taps);
                }
                break;
            case Constants.PLAYER4:
                if (player4Tracker != null) {
                    player4Taps++;
                    player4Tracker.setText("" + player4Taps);
                }

                break;
        }
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        if(levelDifficulty!= Constants.MODE_HARD && levelDifficulty!= Constants.MODE_INSANE) updateTrackerScore(Constants.PLAYER1);
        return true;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        if(levelDifficulty!= Constants.MODE_HARD && levelDifficulty!= Constants.MODE_INSANE) updateTrackerScore(Constants.PLAYER2);
        return true;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        if(levelDifficulty!= Constants.MODE_HARD && levelDifficulty!= Constants.MODE_INSANE) updateTrackerScore(Constants.PLAYER3);
        return true;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        if(levelDifficulty!= Constants.MODE_HARD && levelDifficulty!= Constants.MODE_INSANE) updateTrackerScore(Constants.PLAYER4);
        return true;
    }


    @Override
    public boolean getCurrentQuestionAnswer() {
        // for updating positive score on the one that taps the most
        return true;
    }

    @Override
    public boolean requireMultiTouch() {
        // set to true so button.setOnClickListener will not update score when touched
        return true;
    }

    @Override
    public boolean customTimerIsCalledOnce() {
        // TouchingSpam custom timer will always called once
        return true;
    }

    @Override
    public boolean gameContent_RequireRectangularWhiteStroke() {
        return true;
    }

    @Override
    public int playerThatTouched() {
        // logic to see who tap the most, and return that as the one who "touched"
        if (player1Taps > player2Taps && player1Taps > player3Taps && player1Taps > player4Taps)
            return Constants.PLAYER1;
        else if (player2Taps > player1Taps && player2Taps > player3Taps && player2Taps > player4Taps)
            return Constants.PLAYER2;
        else if (player3Taps > player1Taps && player3Taps > player2Taps && player3Taps > player4Taps)
            return Constants.PLAYER3;
        else if (player4Taps > player1Taps && player4Taps > player2Taps && player4Taps > player3Taps)
            return Constants.PLAYER4;
        else return Constants.DRAW;
    }

}
