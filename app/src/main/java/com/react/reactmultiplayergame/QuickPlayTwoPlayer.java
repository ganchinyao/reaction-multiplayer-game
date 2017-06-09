package com.react.reactmultiplayergame;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.react.reactmultiplayergame.gamemode.AnimalKingdom;
import com.react.reactmultiplayergame.gamemode.CollidingCircle;
import com.react.reactmultiplayergame.gamemode.ColorMatchWords;
import com.react.reactmultiplayergame.gamemode.CountryAndContinent;
import com.react.reactmultiplayergame.gamemode.FiveColorsPanel;
import com.react.reactmultiplayergame.gamemode.FiveYellowCircle;
import com.react.reactmultiplayergame.gamemode.FoodParadise;
import com.react.reactmultiplayergame.gamemode.GameMode;
import com.react.reactmultiplayergame.gamemode.GameModeInstances;
import com.react.reactmultiplayergame.gamemode.IdentifyingLanguage;
import com.react.reactmultiplayergame.gamemode.IllusionCircle;
import com.react.reactmultiplayergame.gamemode.IllusionGear;
import com.react.reactmultiplayergame.gamemode.IllusionRectangle;
import com.react.reactmultiplayergame.gamemode.MandN;
import com.react.reactmultiplayergame.gamemode.MixingColors;
import com.react.reactmultiplayergame.gamemode.MultiColoredOrb;
import com.react.reactmultiplayergame.gamemode.NeonArrows;
import com.react.reactmultiplayergame.gamemode.NumberComparison;
import com.react.reactmultiplayergame.gamemode.OandX;
import com.react.reactmultiplayergame.gamemode.OddAndVowel;
import com.react.reactmultiplayergame.gamemode.PerfectMatch;
import com.react.reactmultiplayergame.gamemode.ScreenColor;
import com.react.reactmultiplayergame.gamemode.SmilingFace;
import com.react.reactmultiplayergame.gamemode.SolarEclipse;
import com.react.reactmultiplayergame.gamemode.TouchingSpam;
import com.react.reactmultiplayergame.gamemode.UnitConversion;
import com.react.reactmultiplayergame.helper.AutoResizeTextView;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.SoundPoolManager;
import com.react.reactmultiplayergame.helper.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/*
*  Each new gameMode instance will definitely call requestNewTimer();
*  In requestNewTimer(), condition is check if current gameMode require a default timer.
*  If required default timer -> call default timer of 3 sec e.g. timer used for NumberComparison.java
*  else, startCustomTimerCountdown will be called.
*  And in startCustomTimerCountdown, condition is First check if gameMode even require a timer.
*  If gameMode doesnt need timer, e.g. CollidingCircle.java, no timer is called at all.
*  If gameMode.requireATimer is true, a customTimer will start to countdown that correspond to gameMode.customTimerDuration,
*  and when customTimer is finished, delayMethodExecution(0) will be called.
*
*  Hence, delayMethodExecution() will either be called in customTimer, noTimer, or inside the player Button Clicks where gameMode.buttonClickedCustomExecute == false
*
*
*
 */

public class QuickPlayTwoPlayer extends AppCompatActivity {
    private int totalRounds;
    private int currentRound = 1;
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean getNewMusic = true;
    private int numberOfQuestionCorrectToSwitchNewMode = 0;
    TextView player1Question, player2Question, player1ScoreTextField, player2ScoreTextField, dialogQuestionTop, dialogQuestionBottom, dialogQuestionTop2, dialogQuestionBottom2,
            player1Comment, player2Comment, player1Name, player2Name;
    int levelDifficulty = 2;
    RelativeLayout player1Button, player2Button, gameContent, timerbar, gameContentWhiteStroke;
    LinearLayout rootView;
    GameMode gameMode;
    ArrayList<GameModeInstances> mode = new ArrayList<GameModeInstances>();
    Animation timerAnimation;
    private CountDownTimer defaultTimer, customTimer, delayExecutionTimer, timerForDialog;
    private AlertDialog dialog;
    private View dialogView;
    private String[] commentsArray_Win;
    private String[] commentsArray_Lose;
    private final Random randomGenerator = new Random();
    private MediaPlayer mediaPlayer;
    private View exit_customDialogView;
    private NativeExpressAdView mNativeExpressAdView;
    private boolean onResumeCalledBefore = false;
    private boolean isAppRunning = false;
    private int onAdCloseMethodToCall;
    private boolean playCloseMenuSound = true;
    // a flag to set to true on clicking return to main menu
    // this flag is used for exitdialog.onDismissListener whether ornot to call initializeCustomDialog again
    // if we not returning, i.e. we are just closing dialog instead of pressing return to main menu, then we call the initializeCustomDialog again
    private boolean goingToExitActivity = false;
    private int isitQuickPlayOrCustomGame;
    // set to true when user do action like go to home during game play. We then cancel timer, and onresume we will start a new timer. This is to prevent onResume for calling timer
    // at the start when using just arrived at this activity, but only called the new timer when user came back from background
    private boolean isReturnedfromBackgroundToForeGroundButNotOnStart = false;
    private boolean buttonCanClick = true; // used as the flag to prevent 2 button from clicking at the same time
    private ValueAnimator winnerpage_zoominzoomoutanimator;
    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String admobNativeExpress_ID = Constants.admob_NativeExpress_Id; // the reason we create a global var here so that during decompilation they will see this id here instead of inside method
    private String admob_Interstitial_ID = Constants.admob_Interstitial_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.react.reactmultiplayergame.R.layout.quickplay_twoplayer);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Intent intent = getIntent();
        isitQuickPlayOrCustomGame = intent.getIntExtra("isitQuickPlayOrCustomGame", Constants.GAMEMODE_QUICKPLAY);
        levelDifficulty = Utils.getDifficultyPreference(this);
        onResumeCalledBefore = false;
        isAppRunning = true;
        rootView = (LinearLayout) findViewById(com.react.reactmultiplayergame.R.id.quickplay_twoplayer);
        player1Question = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player1Question);
        player2Question = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player2Question);
        player1Button = (RelativeLayout) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player1Button);
        player2Button = (RelativeLayout) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player2Button);
        player1ScoreTextField = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player1Score);
        player2ScoreTextField = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player2Score);
        gameContent = (RelativeLayout) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_gamecontent);
        timerbar = (RelativeLayout) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_timerbar);
        player1Comment = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player1Comment);
        player2Comment = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player2Comment);
        commentsArray_Win = getResources().getStringArray(com.react.reactmultiplayergame.R.array.comments_win);
        commentsArray_Lose = getResources().getStringArray(com.react.reactmultiplayergame.R.array.comments_lose);
        player1Name = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player1Name);
        player2Name = (TextView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player2Name);
        // set the player name according to user preference in editplayerprofile
        player1Name.setText(Utils.getPlayerName(this, Constants.PLAYER1));
        player2Name.setText(Utils.getPlayerName(this, Constants.PLAYER2));
        gameContentWhiteStroke = (RelativeLayout) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_gameContentWhiteStroke);

        if(isitQuickPlayOrCustomGame == Constants.GAMEMODE_CUSTOMGAME) {
            // set up custom game mode
            Bundle bundle = intent.getExtras();
            mode = (ArrayList<GameModeInstances>) bundle.getSerializable("customGameModeToPlay");
            if(Utils.getCustomGameRandomizeOrderOn(this)) {
                // shuffle array
                Collections.shuffle(mode);
            }
            // set total round according to number of custom games
            totalRounds = mode.size();

        } else {
            // set total round according to settings preference
            totalRounds = Utils.getQuickPlayTotalRoundsToPlay(this);

            // set up quick play mode
            //An array list to instantiate the class. Add more elements to mode.add(xxx.class) to instantiate new game mode
            //TODO when new mode is added, update the following: - Two/Three/FourPlayer.java, - MainActivity customPopUp myList, - and method customGame_checkGameMode in MainActivity
            //TODO and also in arrays.xml under numberOfRoundsToPlay update the total number of gameModes
            //TODO also add new credits to credits page for the music used (and images or what if needed)
            //TODO also add new case in findoutmore portion (including adding new screenshot). Use the template.psd to create the new screenshot, and use png if possible
            // TODO Inside shop_findoutmore.xml, DONNID to rearrange all the id in order.
            //TODO just add on to it, like item 25, item 26. Their order jumble up nvm, so long as u place them at the correct alphabetical order position in layout, and edit their content.
            //TODO Then in ShopFindOutMore.java, add the new item into the 4 different arrays and we are done
            //TODO side note: Take note that when u create a new gamemode, in removedynamically method, must check for null before removing any view. This is becos
            //TODO removedynamically method may be called twice, in the event before going to winnerpage, and exit_returnToMainMenu in winnerpage, which may result in NPE
            mode.add(new GameModeInstances(AnimalKingdom.class));
            mode.add(new GameModeInstances(CollidingCircle.class));
            mode.add(new GameModeInstances(MixingColors.class));
            mode.add(new GameModeInstances(ScreenColor.class));
            mode.add(new GameModeInstances(FiveColorsPanel.class));
            mode.add(new GameModeInstances(CountryAndContinent.class));
            mode.add(new GameModeInstances(SolarEclipse.class));
            mode.add(new GameModeInstances(NumberComparison.class));
            mode.add(new GameModeInstances(FiveYellowCircle.class));
            mode.add(new GameModeInstances(FoodParadise.class));
            mode.add(new GameModeInstances(IdentifyingLanguage.class));
            mode.add(new GameModeInstances(IllusionCircle.class));
            mode.add(new GameModeInstances(IllusionGear.class));
            mode.add(new GameModeInstances(IllusionRectangle.class));
            mode.add(new GameModeInstances(MandN.class));
            mode.add(new GameModeInstances(PerfectMatch.class));
            mode.add(new GameModeInstances(ColorMatchWords.class));
            mode.add(new GameModeInstances(MultiColoredOrb.class));
            mode.add(new GameModeInstances(NeonArrows.class));
            mode.add(new GameModeInstances(OandX.class));
            mode.add(new GameModeInstances(OddAndVowel.class));
            mode.add(new GameModeInstances(SmilingFace.class));
            mode.add(new GameModeInstances(TouchingSpam.class));
            mode.add(new GameModeInstances(UnitConversion.class));
            Collections.shuffle(mode);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Only bold if it is api >=21
            // api is 21 and above, hence casual font is available. Therefore we set it as casual font
            // hence here we bold the casual font. Prior to 21, system will auto fall back to default font,
            // hence we do not bold the default font, otherwise it looks ugly.
            TextViewCompat.setTextAppearance(player1Question, R.style.casualAndBold);
            TextViewCompat.setTextAppearance(player1Name, R.style.casualAndBold);
            TextViewCompat.setTextAppearance(player1ScoreTextField, R.style.casualAndBold);
            TextViewCompat.setTextAppearance(player2Question, R.style.casualAndBold);
            TextViewCompat.setTextAppearance(player2Name, R.style.casualAndBold);
            TextViewCompat.setTextAppearance(player2ScoreTextField, R.style.casualAndBold);
        }

        ImageView player1Glow = (ImageView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player1Glow);
        ImageView player2Glow = (ImageView) findViewById(com.react.reactmultiplayergame.R.id.twoplayer_player2Glow);

        // setColorFilter can only be applied to the drawable, not to the imageview directly. Hence we set up and mutate 2 drawable using the same background,
        // and set color filter to the glow according to the user preference color tag
        Drawable p1glow = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.playerglow, null);
        if (p1glow != null) {
            p1glow.mutate();
            p1glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER1), PorterDuff.Mode.SRC_ATOP));
            player1Glow.setBackground(p1glow);
        }

        Drawable p2glow = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.playerglow, null);
        if (p2glow != null) {
            p2glow.mutate();
            p2glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER2), PorterDuff.Mode.SRC_ATOP));
            player2Glow.setBackground(p2glow);
        }


        initializeExitCustomDialog();

        // here we put the same click listener. To prevent them from clicking tgt, we put one more flag buttonCanClick in addition to setClickable.
        // This will ensure a high chance of not able to click tgt
        View.OnClickListener buttonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // for multitouch like touching spam, we dont set buttonCanClick to false, hence we can keep clicking
                if(!gameMode.requireMultiTouch()) {
                    if (!buttonCanClick) {
                        return;
                    }

                    buttonCanClick = false;
                }

                switch (v.getId()) {

                    case R.id.twoplayer_player1Button:
                        if (gameMode.player1ButtonClickedCustomExecute()) {
                            //For gamemode that require more special treatment and need to customize, e.g. TouchingSpam.java
                            getCustomUpdateScoreSystem();
                        } else {
                            //With no custom execution, code here handle update of score, and start next question,
                            //as if the mode requirement is similar to NumberComparison
                            player1Button.setClickable(false);
                            player2Button.setClickable(false);
                            hideTimerCountdown();
                            if (gameMode.getCurrentQuestionAnswer()) {
                                player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                                updateScoreAndGetComments(Constants.PLAYER1, Constants.CORRECT);
                            } else {
                                player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_wrong);
                                updateScoreAndGetComments(Constants.PLAYER1, !Constants.CORRECT);
                            }
                            delayMethodExecution(1);

                        }
                        break;

                    case R.id.twoplayer_player2Button:
                        if (gameMode.player2ButtonClickedCustomExecute()) {
                            getCustomUpdateScoreSystem();

                            //handle everything in respective classes for game mode that does not want default treatment
                        } else {
                            //With no custom execution, code here handle update of score, and start next question,
                            //as if the mode requirement is similar to NumberComparison
                            player1Button.setClickable(false);
                            player2Button.setClickable(false);
                            hideTimerCountdown();
                            if (gameMode.getCurrentQuestionAnswer()) {
                                player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                                updateScoreAndGetComments(Constants.PLAYER2, Constants.CORRECT);
                            } else {
                                player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_wrong);
                                updateScoreAndGetComments(Constants.PLAYER2, !Constants.CORRECT);
                            }
                            delayMethodExecution(2);
                        }
                        break;
                }
            }
        };


        player1Button.setOnClickListener(buttonClickListener);

        player2Button.setOnClickListener(buttonClickListener);

        //Default timer of 2sec (or whatever user preference is)
        defaultTimer = new CountDownTimer(Utils.getQuestionDelayPreference(getApplicationContext()), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                callGameContentAndQn();
            }
        };


        // Show for 2 second, and upon finish call the animation to slide the dialog out for 300millisec, while pausing for 300millisec before dismissing dialog
        timerForDialog = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                RelativeLayout dialogBottomRelative = (RelativeLayout) dialogView.findViewById(com.react.reactmultiplayergame.R.id.dialogBottomRelative);
                Animation topToBottom = AnimationUtils.loadAnimation(getApplicationContext(), com.react.reactmultiplayergame.R.anim.dialoganimation_toptobottom);
                Animation bottomToTop = AnimationUtils.loadAnimation(getApplicationContext(), com.react.reactmultiplayergame.R.anim.dialoganimation_bottomtotop);
                bottomToTop.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                dialogBottomRelative.startAnimation(topToBottom);
                RelativeLayout dialogTopRelative = (RelativeLayout) dialogView.findViewById(com.react.reactmultiplayergame.R.id.dialogTopRelative);
                dialogTopRelative.startAnimation(bottomToTop);
            }
        };

        gameContent.post(new Runnable() {
            @Override
            public void run() {
                mFirebaseAnalytics.logEvent("Total_Rounds_Played", null); // first round
                playMediaPlayer(0); // play dialog sound
                GameMode.callingNewDialog = true;
                GameMode.dialogDelay = 2300;
                callGameContentAndQn();
                timerForDialog.start();
                // somehow there is an error crash here with NPE. So we try see if gameMode is null ornot
                if(gameMode != null) {
                    callDialog(getResources().getString(com.react.reactmultiplayergame.R.string.round) + " " + currentRound + " " + getResources().getString(com.react.reactmultiplayergame.R.string.of) + " " + totalRounds, gameMode.getDialogTitle(getApplicationContext()));
                } else {
                    // since gameMode is null we give it one more change and recall gamecontent again
                    callGameContentAndQn();
                    if(gameMode != null) {
                        callDialog(getResources().getString(com.react.reactmultiplayergame.R.string.round) + " " + currentRound + " " + getResources().getString(com.react.reactmultiplayergame.R.string.of) + " " + totalRounds, gameMode.getDialogTitle(getApplicationContext()));
                    }
                }
            }
        });


    }

    // to be call when you need a .postdelay execution of any method.
    // create a new case and pass in the int param of the new case that you have created to call the method
    // timer delayed is Utils.getAnswerDelayPreference , 2 sec by default
    // ** Assume that call to this method MUST always end up with an answer to which player correct, because
    // ** this is where player score get updated, and whether next gameMode is triggered, or still display current gameMode at next qn
    private void delayMethodExecution(final int methodToCall) {
        delayExecutionTimer = new CountDownTimer(Utils.getAnswerDelayPreference(getApplicationContext()), 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                switch (methodToCall) {
                    case 0:
                        //to be called in customTimer onFinished or require customTouch when touched
                        buttonCanClick = true;
                        player1Button.setClickable(true);
                        player2Button.setClickable(true);
                        player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design);
                        player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design);
                        break;
                    case 1:
                        //to be called in player1button.setonclicklistener
                        player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design);
                        buttonCanClick = true;
                        player1Button.setClickable(true);
                        player2Button.setClickable(true);
                        break;
                    case 2:
                        //to be called in player2button.setonclicklistener
                        player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design);
                        buttonCanClick = true;
                        player1Button.setClickable(true);
                        player2Button.setClickable(true);
                        break;
                }

                if (numberOfQuestionCorrectToSwitchNewMode != 0 && numberOfQuestionCorrectToSwitchNewMode >= Utils.getNumberOfCorrectToGoNextGameMode(getApplicationContext())) {
                   callNewRound();
                } else callGameContentAndQn();

            }
        }.start();

    }

    //Default timer of 2 sec (or whatever user preference is)
    private void startTimerCountdown() {

        if (gameMode.wantToShowTimer()) {
            // gameMode needs a timer and want to show timer, hence set visible
            timerbar.setVisibility(View.VISIBLE);
            timerAnimation = AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, com.react.reactmultiplayergame.R.anim.timeranimation);
            timerAnimation.setDuration(Utils.getQuestionDelayPreference(this));
            timerbar.startAnimation(timerAnimation);
        } else {
            // gameMode needs a timer but donwant to show timer, hence set it as gone
            timerbar.setVisibility(View.GONE);
        }

        defaultTimer.start();
    }

    // Can be used for either default timer or custom timer
    private void hideTimerCountdown() {
        if(gameMode != null ) { // impt to check becos somehow in the winner page gameMode may be null
            if (gameMode.requireDefaultTimer()) {
                defaultTimer.cancel();
            }
            if (gameMode.requireATimer() && gameMode.wantToShowTimer()) {
                // becoz only wantToShowTimer will the timerbar animation be called. Hence we only call timerbar.ClearAnimation on those that actually has animation
                timerbar.clearAnimation();

            }

            if (customTimer != null) {
                customTimer.cancel();
            }

            if (timerbar.getVisibility() == View.VISIBLE) {
                timerbar.setVisibility(View.GONE);
            }
        }
    }


    //Custom timer and default timer are mutually exclusive, i.e. only 1 will get executed depending on current gameMode
    private void startCustomTimerCountdown() {

        //only call timer if gameMode require a timer. gameMode like circleCollide does not require a timer
        if (gameMode.requireATimer()) {
            //Custom timer for some gameMode. Either customTimer or the one above (defaultTimer) will be called. Both are mutually exclusive.
            customTimer = new CountDownTimer(gameMode.customTimerDuration(), gameMode.customTimerDuration()) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    // stop and evaluate answer for gameMode that only need customTimer once, e.g. TouchingSpam.java.
                    // if we require custom timer to continuously loop, else clause for callGameContentAndQn() is called.
                    if (gameMode.customTimerIsCalledOnce()) {
                        hideTimerCountdown();
                        player1Button.setClickable(false);
                        player2Button.setClickable(false);
                        updateScoreAndGetComments(gameMode.playerThatTouched(), gameMode.getCurrentQuestionAnswer());
                        switch (gameMode.playerThatTouched()) {
                            case Constants.PLAYER1:
                                player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                                break;
                            case Constants.PLAYER2:
                                player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                                break;
                            case Constants.DRAW:
                                player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                                player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                                break;
                        }
                        delayMethodExecution(0);
                    } else callGameContentAndQn();
                }
            };

            if (gameMode.wantToShowTimer()) {
                // only start timerbar animation when we want to show timer
                timerbar.setVisibility(View.VISIBLE);
                Animation customTimeAnimation = AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, com.react.reactmultiplayergame.R.anim.timeranimation);
                // 6 second animation instead
                customTimeAnimation.setDuration(gameMode.customTimerDuration());
                timerbar.startAnimation(customTimeAnimation);
            } else {
                // impt else statement, otherwise the timerbar will not disappear if u call a mode that show timer bar, and next mode doesnt want timer bar
                timerbar.setVisibility(View.GONE);
            }
            customTimer.start();

        }

    }

    private void getCustomUpdateScoreSystem() {
        // method doesnt work on touchingspam since touchingspam touches button many times
        if (gameMode.requireMultiTouch()) return;
        player1Button.setClickable(false);
        player2Button.setClickable(false);
        if (gameMode.requireATimer()) {
            hideTimerCountdown();
        }
        updateScoreAndGetComments(gameMode.playerThatTouched(), gameMode.getCurrentQuestionAnswer());
        switch (gameMode.playerThatTouched()) {
            case Constants.PLAYER1:
                if (gameMode.getCurrentQuestionAnswer())
                    player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                else
                    player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_wrong);
                break;
            case Constants.PLAYER2:
                if (gameMode.getCurrentQuestionAnswer())
                    player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                else
                    player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_wrong);
                break;
            case Constants.DRAW:
                player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design_correct);
                break;
        }
        delayMethodExecution(0);
    }

    private void callDialog(String top, String bottom) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickPlayTwoPlayer.this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(com.react.reactmultiplayergame.R.layout.question_customdialog, null);
        builder.setView(dialogView);
        dialogQuestionTop = (TextView) dialogView.findViewById(com.react.reactmultiplayergame.R.id.dialogQuestionTop);
        dialogQuestionBottom = (TextView) dialogView.findViewById(com.react.reactmultiplayergame.R.id.dialogQuestionBottom);
        dialogQuestionTop2 = (TextView) dialogView.findViewById(com.react.reactmultiplayergame.R.id.dialogQuestionTop2);
        dialogQuestionBottom2 = (TextView) dialogView.findViewById(com.react.reactmultiplayergame.R.id.dialogQuestionBottom2);
        dialogQuestionTop.setText(top);
        dialogQuestionBottom.setText(bottom);
        dialogQuestionTop2.setText(top);
        dialogQuestionBottom2.setText(bottom);
        dialog = builder.create();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                requestNewTimer();
                GameMode.callingNewDialog = false;
                GameMode.dialogDelay = 0;
                numberOfQuestionCorrectToSwitchNewMode = 0;
                releaseMediaPlayer();
                playMediaPlayer(1); // play background music
                getNewMusic = false;
            }
        });

        dialogQuestionTop.startAnimation(AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, com.react.reactmultiplayergame.R.anim.dialoganimation_lefttoright));
        dialogQuestionBottom.startAnimation(AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, com.react.reactmultiplayergame.R.anim.dialoganimation_righttoleft));
        dialogQuestionTop2.startAnimation(AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, com.react.reactmultiplayergame.R.anim.dialoganimation_righttoleft));
        dialogQuestionBottom2.startAnimation(AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, com.react.reactmultiplayergame.R.anim.dialoganimation_lefttoright));
        dialog.setCancelable(true);
        dialog.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWindow.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    //decide if current gameMode require default, or custom timer
    private void requestNewTimer() {
        if (gameMode.requireDefaultTimer()) startTimerCountdown();
        else startCustomTimerCountdown();
    }

    //reinstantate a new instance of gameMode to get new question and content
    private void callGameContentAndQn() {
        try {
            gameContent.removeAllViews();
            //timerbar.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.timerbar, null)); //set timerbar back to default
            if (gameMode != null) gameMode.removeDynamicallyAddedViewAfterQuestionEnds();
            gameMode = null;
            gameMode = (GameMode) mode.get(currentRound - 1).getModeClass().newInstance();
            //Polymorphism at work. setCurrentQuestion will be calling the respective class overriden setQuestion(String x) etc, which in turn passed
            //the respective param to superclass GameMode.java to change the respective variable.
            gameMode.setGameContent(rootView, gameContent, this, levelDifficulty, Constants.MODE_TWOPLAYER);
            gameMode.setCurrentQuestion(player1Question, player2Question);
            player1Comment.setVisibility(View.GONE);
            player2Comment.setVisibility(View.GONE);
            if (!GameMode.callingNewDialog) {
                requestNewTimer();
                if (getNewMusic) {
                    releaseMediaPlayer();
                    playMediaPlayer(1); // play background music
                    getNewMusic = false;
                }
            }
            if (gameMode.gameContent_RequireRectangularWhiteStroke()) {
                // gameMode requires a rectangular stroke instead of rounded, hence set a rect stroke
                gameContentWhiteStroke.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.gamemode_gamecontent_whitestrokeonly_rect);
            } else {
                // else set a rounded rect stroke
                gameContentWhiteStroke.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.gamemode_gamecontent_whitestrokeonly_rounded);
            }
        } catch (Exception e2) {

        }
    }


    // One and ONLY place to update score.
    // Other places will all eventually call this method no matter if require customUpdateScore, or require only default update score
    private void updateScoreAndGetComments(int player, boolean correct) {
        getNewMusic = true;
        if (correct) {
            numberOfQuestionCorrectToSwitchNewMode++;
            releaseMediaPlayer();
            playMediaPlayer(2); // play correct ans sound
        } else {
            releaseMediaPlayer();
            playMediaPlayer(3); // play wrong ans sound
        }
        switch (player) {
            case Constants.PLAYER1:
                if (correct) {
                    player1Score += Utils.getScoreSystemPreference(getApplicationContext());
                    player1ScoreTextField.setText("" + player1Score);
                    player1Comment.setVisibility(View.VISIBLE);
                    player2Comment.setVisibility(View.VISIBLE);
                    player1Comment.setText(commentsArray_Win[randomGenerator.nextInt(commentsArray_Win.length)]);
                    player2Comment.setText(commentsArray_Lose[randomGenerator.nextInt(commentsArray_Lose.length)]);
                } else {
                    player1Score -= Utils.getScoreSystemPreference_Minus(getApplicationContext());
                    player1ScoreTextField.setText("" + player1Score);
                    player1Comment.setVisibility(View.VISIBLE);
                    player1Comment.setText(commentsArray_Lose[randomGenerator.nextInt(commentsArray_Lose.length)]);
                }
                break;
            case Constants.PLAYER2:
                if (correct) {
                    player2Score += Utils.getScoreSystemPreference(getApplicationContext());
                    player2ScoreTextField.setText("" + player2Score);
                    player1Comment.setVisibility(View.VISIBLE);
                    player2Comment.setVisibility(View.VISIBLE);
                    player2Comment.setText(commentsArray_Win[randomGenerator.nextInt(commentsArray_Win.length)]);
                    player1Comment.setText(commentsArray_Lose[randomGenerator.nextInt(commentsArray_Lose.length)]);
                } else {
                    player2Score -= Utils.getScoreSystemPreference_Minus(getApplicationContext());
                    player2ScoreTextField.setText("" + player2Score);
                    player2Comment.setVisibility(View.VISIBLE);
                    player2Comment.setText(commentsArray_Lose[randomGenerator.nextInt(commentsArray_Lose.length)]);
                }
                break;
            case Constants.DRAW:
                player1Comment.setVisibility(View.VISIBLE);
                player2Comment.setVisibility(View.VISIBLE);
                player2Comment.setText(com.react.reactmultiplayergame.R.string.gamedraw);
                player1Comment.setText(com.react.reactmultiplayergame.R.string.gamedraw);
                break;
        }
    }

    @Override
    protected void onPause() {
        // when we go to the game mode activity, current activity will be onPause. So we release soundpool resources since now we are at game mode activity
        // this also takes into account user press home button or take phone call e.g, transitting away from the app,
        // hence we stop all sound including background sound
        super.onPause();
        // set isAppRunning to false, so no subsequent mediaplayer sound will be called from playMediaPlayer(x)
        // this is impt, becos in the event user pause screen when dialog is calling, subsequent background music will still be called since releaseMediaPlayer is only releasing the dialog sound
        // hence we use this flag to prevent background music from even starting
        isAppRunning = false;
        releaseMediaPlayer();

        isReturnedfromBackgroundToForeGroundButNotOnStart = true;
        hideTimerCountdown();
    }

    @Override
    protected void onResume() {
        // when we press back from game mode, this activity will onResume from onPause, and we need to reinstate all soundPool resources again since we released onPause
        super.onResume();
        isAppRunning = true; // set back to true to allow playMediaPlayer to play sound

        if(!isSoundMuted()) {
            // unlike MainActivity, we can check if !isSoundMuted and do not instantiate if sound is muted.
            // But in MainActivity we just create anw even if sound is muted, becos there is the chance of user pressing the mute button again to toggle to sound unmute
            // However in QuickPlay, there is no chance for user to press the unmute sound, so its ok to check if !isSoundMuted()
        }

        if(onResumeCalledBefore) {
            // we only play background music onResume if onResume has alr been called out once, after onCreate -> onResume
            // this is not to overlap and call playMediaPlayer twice, since onCreate will call playMediaPlayer(1) itself too.
            // hence, this field is for when user go to background, and onResume again, play sound after
            playMediaPlayer(1);
        }
        // this call determines that onResume is called once before.
        onResumeCalledBefore = true;

        // this method is only called when onPause is called, i.e. player go to background. Otherwise, at the first onCreate, this will not be called.
        // This is to resume the timer since we cancel timer at onPause.
        if(isReturnedfromBackgroundToForeGroundButNotOnStart) {
            isReturnedfromBackgroundToForeGroundButNotOnStart = false; // set back to false
            requestNewTimer();
        }

    }

    private boolean isSoundMuted () {
        return Utils.getIfSoundIsMuted(QuickPlayTwoPlayer.this);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null && !isSoundMuted()) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startSound() {
        if(mediaPlayer != null)
            mediaPlayer.start();
    }

    // ONE and ONLY place to play sound. Play the sound based on the param passed. Add new case here to play new sound
    private void playMediaPlayer(int soundToPlay) {
        if(!isSoundMuted() && isAppRunning) {
            //sound is not muted in main page, hence play sound
            switch (soundToPlay){
                case 0:
                    // play dialog
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), com.react.reactmultiplayergame.R.raw.dialogsound);
                    startSound();
                    break;
                case 1:
                    // play gameMode background music
                    if(gameMode != null) {
                        // impt to check gameMode != null becos somehow there is a crash report for NPE associated with this line below
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), gameMode.getBackgroundMusic());
                        startSound();
                        mediaPlayer.setLooping(true);
                    }
                    break;
                case 2:
                    // play correct ans sound
                    switch (randomGenerator.nextInt(3)) {
                        // play a random corret sound out of 3
                        case 0:
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), com.react.reactmultiplayergame.R.raw.answer_correct1);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), com.react.reactmultiplayergame.R.raw.answer_correct2);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), com.react.reactmultiplayergame.R.raw.answer_correct3);
                            break;
                        default:
                            // should not be called, but just in case
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), com.react.reactmultiplayergame.R.raw.answer_correct3);
                            break;
                    }
                    startSound();
                    break;
                case 3:
                    // play wrong ans sound
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), com.react.reactmultiplayergame.R.raw.answer_wrong);
                    startSound();
                    break;
                case 4:
                    // play victory page short sound
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.winnerpage_victorysound);
                    startSound();
                    // play long victory sound after
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.reset();
                            playMediaPlayer(5);
                        }
                    });
                    break;
                case 5:
                    // play victory page long background sound
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.winnerpage_backgroundsound);
                    startSound();
                    mediaPlayer.setLooping(true);
                    break;
            }
        }
    }



    private void initializeExitCustomDialog() {
        if(!Utils.hasPurchasedRemoveAds(QuickPlayTwoPlayer.this)) {
            // user did not buy the iap, hence show ads
            // preload the ads first onCreate
            // initialize these layout once in onCreate, instead of keep reinitializing in onBackPress
            LayoutInflater inflater = this.getLayoutInflater();
            exit_customDialogView = inflater.inflate(com.react.reactmultiplayergame.R.layout.exit_customdialog, null);

            if(mNativeExpressAdView == null) {
                mNativeExpressAdView = new NativeExpressAdView(getApplicationContext()); // dont use 'this' in param, as it will cause memory leak
                // set width as total width - 48sdp, becoz the pop up background has 20sdp margin left and right, which totals 40 sdp. We give 8 more sdp allowance becoz
                // the popup background is not full width, and we dont want the ads to exceed the popup background
                // we have to change all to dp becoz admob based their width on dp
                int adWidth = Utils.pxToDp(Utils.getCurrentDeviceWidth(QuickPlayTwoPlayer.this)) - Utils.pxToDp((int) getResources().getDimension(R.dimen._48sdp));
                if (adWidth < 280) {
                    // Unlikely to happen, but
                    // in the event for very small device, 85% of screen width is less than 280dp, we ignore the 7.5% margin alignment and make width bigger
                    // this is becos for nativeadexpress, minimum width must be 280dp
                    adWidth = 280;

                    LinearLayout overallContainer = (LinearLayout) exit_customDialogView.findViewById(R.id.exitCustomDialog_OverallContainer);
                    ViewGroup.MarginLayoutParams containerParam = (ViewGroup.MarginLayoutParams) overallContainer.getLayoutParams();
                    int newLeftMargin = containerParam.leftMargin;
                    int newRightMargin;

                    // want to ensure popup background takes in 8 more dp than 280, so ads does not exceed popup background. More is ok
                    if (Utils.getCurrentDeviceWidth(QuickPlayTwoPlayer.this) - (newLeftMargin * 2) < Utils.dpToPx(288)) {
                        //totalWidth - 2*margin = the overallContainer width. Convenient way of not calling overallContainer.getWidth
                        // this means the overallContainer is less than 280dp, hence ads will not display.
                        // We need to reduce the marginLeft and marginRight which is set in the .xml file, to give the width more space up to minimum 280dp width

                        // set the new margin as the maximum margin allowed, which is total width - miminum280dp width, divide by 2
                        // minus another 4 to the back just incase the conversion of dp to px and vice versa returns truncated value
                        // we minus by 288 instead of 280 to give 8 more dp of allowance to both left and right so ads does not exceed popup background
                        newLeftMargin = (Utils.getCurrentDeviceWidth(QuickPlayTwoPlayer.this) - Utils.dpToPx(288)) / 2 - 4;
                        newRightMargin = (Utils.getCurrentDeviceWidth(QuickPlayTwoPlayer.this) - Utils.dpToPx(288)) / 2 - 4;

                        containerParam.leftMargin = newLeftMargin;
                        containerParam.rightMargin = newRightMargin;
                    }

                }
                // set height as 35% of screen height, and not too big, otherwise it looks ugly
                int adHeight = (int) (0.35 * Utils.pxToDp(Utils.getCurrentDeviceHeight(QuickPlayTwoPlayer.this)));
                if (adHeight < 250) {
                    // 250 is the minimum height dp for large nativeAdExpress according to admob
                    // here this will occur for very small device with low density.
                    // In that case, we cant set the height to be 35% of screenHeight.
                    // Hence we just set the adheight to be the minimum 250dp, and the result will be an ad that is much taller than 35% (uglier, but too bad).
                    adHeight = 250;
                }

                mNativeExpressAdView.setAdSize(new AdSize(adWidth, adHeight));
                mNativeExpressAdView.setAdUnitId(admobNativeExpress_ID.substring(0, 15) + "2232" + admobNativeExpress_ID.substring(19));
                AdRequest request = new AdRequest.Builder().addTestDevice("3AC3E5EAAFC94AE88EF23BEB39B117F0")
                        .addTestDevice("B15B760B2CF2D7F2C34F2EE31CC3B4D5")//TODO add device
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                mNativeExpressAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        mNativeExpressAdView.pause();
                        super.onAdLoaded();

                    }
                });
                mNativeExpressAdView.loadAd(request);
            } else {
                mNativeExpressAdView.pause();
            }

            RelativeLayout adContainer = (RelativeLayout) exit_customDialogView.findViewById(R.id.returnToMainMenu_AdRelativeLayout);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            adContainer.addView(mNativeExpressAdView, layoutParams);

        } else {
            // user bought iap, hence show no ads layout
            // initialize these layout once in onCreate, instead of keep reinitializing in onBackPress
            LayoutInflater inflater = this.getLayoutInflater();
            exit_customDialogView = inflater.inflate(R.layout.exit_customdialog_noads, null);
        }
    }

    private void callWinnerPage_withAds(){
        // Call the ending winner page, meaning game has ended
        View winnerpageView = this.getLayoutInflater().inflate(R.layout.winnerpage_2players, null);
        setContentView(winnerpageView);
        // put the sound in .post so that it sounds more smooth, i.e. doesnt play when layout is still setting and have a lag effect
        winnerpageView.post(new Runnable() {
            @Override
            public void run() {
                // play short victory sound then long background sound
                releaseMediaPlayer();
                playMediaPlayer(4);
            }
        });


        // admob interstitial ads
        mInterstitialAd = new InterstitialAd(getApplicationContext()); // dont use 'this' as it will cause a leak
        mInterstitialAd.setAdUnitId(admob_Interstitial_ID.substring(0,15) + "2232" + admob_Interstitial_ID.substring(19));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("B15B760B2CF2D7F2C34F2EE31CC3B4D5")
                .addTestDevice("3AC3E5EAAFC94AE88EF23BEB39B117F0")
                .build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // these 2 lines to prevent memory leak
                mInterstitialAd.setAdListener(null);
                mInterstitialAd = null;
                // determine whether it is return to main menu or restart quick play
                if(onAdCloseMethodToCall == 1) {
                    returnToMainMenu();
                } else {
                    // restart quick play
                    restartQuickPlay();
                }
            }
        });

        final AutoResizeTextView winnerName_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topName);
        final AutoResizeTextView winnerName_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomName);

        ImageView player1Glow = (ImageView) findViewById(R.id.winnerpage_player1Glow);
        ImageView player2Glow = (ImageView) findViewById(R.id.winnerpage_player2Glow);

        Drawable p1glow = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.playerglow, null);
        p1glow.mutate();
        Drawable p2glow = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.playerglow, null);
        p2glow.mutate();


        if(player1Score > player2Score) {
            // player 1 won
            winnerName_top.setText(Utils.getPlayerName(this, Constants.PLAYER1));
            winnerName_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER1));
            // keep the winner glow color, and set the loser glow color to gray
            p1glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER1), PorterDuff.Mode.SRC_ATOP));
            player1Glow.setBackground(p1glow);
            p2glow.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#90A4AE"), PorterDuff.Mode.SRC_ATOP));
            player2Glow.setBackground(p2glow);

        } else if(player2Score > player1Score) {
            // player 2 won
            winnerName_top.setText(Utils.getPlayerName(this, Constants.PLAYER2));
            winnerName_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER2));
            // keep the winner glow color, and set the loser glow color to gray
            p1glow.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#90A4AE"), PorterDuff.Mode.SRC_ATOP));
            player1Glow.setBackground(p1glow);
            p2glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER2), PorterDuff.Mode.SRC_ATOP));
            player2Glow.setBackground(p2glow);
        } else {
            // draw
            // keep both their glow color
            p1glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER1), PorterDuff.Mode.SRC_ATOP));
            player1Glow.setBackground(p1glow);
            p2glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER2), PorterDuff.Mode.SRC_ATOP));
            player2Glow.setBackground(p2glow);
            winnerName_top.setText(R.string.gamedraw);
            winnerName_bottom.setText(R.string.gamedraw);
        }

        Animation zoomIn = AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, R.anim.winnerpage_winnerzoomin);
        final Animation zoomOut = AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, R.anim.winnerpage_winnerzoomout);
        zoomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                winnerName_top.startAnimation(zoomOut);
                winnerName_bottom.startAnimation(zoomOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                winnerpage_zoominzoomoutanimator = ValueAnimator.ofFloat(0, 1);
                winnerpage_zoominzoomoutanimator.setDuration(3000);
                winnerpage_zoominzoomoutanimator.setRepeatCount(ValueAnimator.INFINITE);
                winnerpage_zoominzoomoutanimator.setRepeatMode(ValueAnimator.REVERSE);
                winnerpage_zoominzoomoutanimator.setInterpolator(new AccelerateDecelerateInterpolator());

                winnerpage_zoominzoomoutanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        winnerName_bottom.setScaleX(0.18f * value + 1);
                        winnerName_bottom.setScaleY(0.18f * value + 1);
                        winnerName_top.setScaleX(0.18f * value + 1);
                        winnerName_top.setScaleY(0.18f * value + 1);
                    }
                });

                winnerpage_zoominzoomoutanimator.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        winnerName_top.startAnimation(zoomIn);
        winnerName_bottom.startAnimation(zoomIn);


        AutoResizeTextView player1Name_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer1Name);
        AutoResizeTextView player2Name_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer2Name);
        AutoResizeTextView player1Name_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer1Name);
        AutoResizeTextView player2Name_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer2Name);

        player1Name_top.setText(Utils.getPlayerName(this, Constants.PLAYER1));
        player2Name_top.setText(Utils.getPlayerName(this, Constants.PLAYER2));
        player1Name_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER1));
        player2Name_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER2));

        AutoResizeTextView player1Score_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer1Score);
        AutoResizeTextView player2Score_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer2Score);
        AutoResizeTextView player1Score_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer1Score);
        AutoResizeTextView player2Score_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer2Score);
        player1Score_top.setText("" + player1Score);
        player2Score_top.setText("" + player2Score);
        player1Score_bottom.setText("" + player1Score);
        player2Score_bottom.setText("" + player2Score);

        RelativeLayout returnToMainMenu_top = (RelativeLayout) findViewById(R.id.winnerpage_TopReturnToMainMenuButton);
        RelativeLayout returnToMainMenu_bottom = (RelativeLayout) findViewById(R.id.winnerpage_BottomReturnToMainMenuButton);
        returnToMainMenu_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation(); // clear animation so that in the event user clicked before animation has finished, it will not hang
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0); // general click sound
                releaseMediaPlayer(); // stop all background music
                // show interstitial ads first. Return to main menu will be handled on interstitial dismiss
                if (mInterstitialAd.isLoaded()) {
                    onAdCloseMethodToCall = 1; // call returnToMainMenu()
                    mInterstitialAd.show();
                } else {
                    // ad is not loaded, therefore we cant rely onAdClosed method to return to main menu, hence, since no ads is loaded, no choice but to return to main menu
                    returnToMainMenu();
                }
            }
        });
        // same as returnToMainMenu_top
        returnToMainMenu_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation();
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0); // general click sound
                releaseMediaPlayer(); // stop all background music
                // show interstitial ads first. Return to main menu will be handled on interstitial dismiss
                if (mInterstitialAd.isLoaded()) {
                    onAdCloseMethodToCall = 1; // call returnToMainMenu()
                    mInterstitialAd.show();
                } else {
                    // ad is not loaded, therefore we cant rely onAdClosed method to return to main menu, hence, since no ads is loaded, no choice but to return to main menu
                    returnToMainMenu();
                }
            }
        });

        RelativeLayout restartQuickPlay_top = (RelativeLayout) findViewById(R.id.winnerpage_TopRestartQuickPlayButton);
        RelativeLayout restartQuickPlay_bottom = (RelativeLayout) findViewById(R.id.winnerpage_BottomRestartQuickPlayButton);

        if(isitQuickPlayOrCustomGame == Constants.GAMEMODE_CUSTOMGAME) {
            // if it is custom game then set as custom game. Otherwise no need do anything becos xml alr wrote as quick play
            AutoResizeTextView restartTextTop = (AutoResizeTextView) findViewById(R.id.winnerpage_restartbuttontextTop);
            AutoResizeTextView restartTextBottom = (AutoResizeTextView) findViewById(R.id.winnerpage_restartbuttontextBottom);
            restartTextTop.setText(R.string.restartcustomgame_winnerpage);
            restartTextBottom.setText(R.string.restartcustomgame_winnerpage);
        }

        restartQuickPlay_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation();
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0);
                releaseMediaPlayer();
                if (mInterstitialAd.isLoaded()) {
                    onAdCloseMethodToCall = 2; // call restartQuickPlay
                    mInterstitialAd.show();
                } else {
                    // ad is not loaded, therefore we cant rely onAdClosed method. Hence, manually recreate
                    restartQuickPlay();
                }
            }
        });

        restartQuickPlay_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation();
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0);
                releaseMediaPlayer();
                if (mInterstitialAd.isLoaded()) {
                    onAdCloseMethodToCall = 2; // call restartQuickPlay
                    mInterstitialAd.show();
                } else {
                    // ad is not loaded, therefore we cant rely onAdClosed method. Hence, manually recreate
                    restartQuickPlay();
                }
            }
        });



    }

    private void callWinnerPage_noAds(){
        // Call the ending winner page, meaning game has ended
        View winnerpageView = this.getLayoutInflater().inflate(R.layout.winnerpage_2players, null);
        setContentView(winnerpageView);
        // put the sound in .post so that it sounds more smooth, i.e. doesnt play when layout is still setting and have a lag effect
        winnerpageView.post(new Runnable() {
            @Override
            public void run() {
                // play short victory sound then long background sound
                releaseMediaPlayer();
                playMediaPlayer(4);
            }
        });


        final AutoResizeTextView winnerName_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topName);
        final AutoResizeTextView winnerName_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomName);

        ImageView player1Glow = (ImageView) findViewById(R.id.winnerpage_player1Glow);
        ImageView player2Glow = (ImageView) findViewById(R.id.winnerpage_player2Glow);

        Drawable p1glow = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.playerglow, null);
        p1glow.mutate();
        Drawable p2glow = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.playerglow, null);
        p2glow.mutate();


        if(player1Score > player2Score) {
            // player 1 won
            winnerName_top.setText(Utils.getPlayerName(this, Constants.PLAYER1));
            winnerName_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER1));
            // keep the winner glow color, and set the loser glow color to gray
            p1glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER1), PorterDuff.Mode.SRC_ATOP));
            player1Glow.setBackground(p1glow);
            p2glow.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#90A4AE"), PorterDuff.Mode.SRC_ATOP));
            player2Glow.setBackground(p2glow);

        } else if(player2Score > player1Score) {
            // player 2 won
            winnerName_top.setText(Utils.getPlayerName(this, Constants.PLAYER2));
            winnerName_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER2));
            // keep the winner glow color, and set the loser glow color to gray
            p1glow.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#90A4AE"), PorterDuff.Mode.SRC_ATOP));
            player1Glow.setBackground(p1glow);
            p2glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER2), PorterDuff.Mode.SRC_ATOP));
            player2Glow.setBackground(p2glow);
        } else {
            // draw
            // keep both their glow color
            p1glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER1), PorterDuff.Mode.SRC_ATOP));
            player1Glow.setBackground(p1glow);
            p2glow.setColorFilter(new PorterDuffColorFilter(Utils.getActualColorTag(this, Constants.PLAYER2), PorterDuff.Mode.SRC_ATOP));
            player2Glow.setBackground(p2glow);
            winnerName_top.setText(R.string.gamedraw);
            winnerName_bottom.setText(R.string.gamedraw);
        }

        Animation zoomIn = AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, R.anim.winnerpage_winnerzoomin);
        final Animation zoomOut = AnimationUtils.loadAnimation(QuickPlayTwoPlayer.this, R.anim.winnerpage_winnerzoomout);
        zoomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                winnerName_top.startAnimation(zoomOut);
                winnerName_bottom.startAnimation(zoomOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                winnerpage_zoominzoomoutanimator = ValueAnimator.ofFloat(0, 1);
                winnerpage_zoominzoomoutanimator.setDuration(3000);
                winnerpage_zoominzoomoutanimator.setRepeatCount(ValueAnimator.INFINITE);
                winnerpage_zoominzoomoutanimator.setRepeatMode(ValueAnimator.REVERSE);
                winnerpage_zoominzoomoutanimator.setInterpolator(new AccelerateDecelerateInterpolator());

                winnerpage_zoominzoomoutanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        winnerName_bottom.setScaleX(0.18f * value + 1);
                        winnerName_bottom.setScaleY(0.18f * value + 1);
                        winnerName_top.setScaleX(0.18f * value + 1);
                        winnerName_top.setScaleY(0.18f * value + 1);
                    }
                });

                winnerpage_zoominzoomoutanimator.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        winnerName_top.startAnimation(zoomIn);
        winnerName_bottom.startAnimation(zoomIn);

        AutoResizeTextView player1Name_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer1Name);
        AutoResizeTextView player2Name_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer2Name);
        AutoResizeTextView player1Name_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer1Name);
        AutoResizeTextView player2Name_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer2Name);

        player1Name_top.setText(Utils.getPlayerName(this, Constants.PLAYER1));
        player2Name_top.setText(Utils.getPlayerName(this, Constants.PLAYER2));
        player1Name_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER1));
        player2Name_bottom.setText(Utils.getPlayerName(this, Constants.PLAYER2));

        if(isitQuickPlayOrCustomGame == Constants.GAMEMODE_CUSTOMGAME) {
            // if it is custom game then set as custom game. Otherwise no need do anything becos xml alr wrote as quick play
            AutoResizeTextView restartTextTop = (AutoResizeTextView) findViewById(R.id.winnerpage_restartbuttontextTop);
            AutoResizeTextView restartTextBottom = (AutoResizeTextView) findViewById(R.id.winnerpage_restartbuttontextBottom);
            restartTextTop.setText(R.string.restartcustomgame_winnerpage);
            restartTextBottom.setText(R.string.restartcustomgame_winnerpage);
        }

        AutoResizeTextView player1Score_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer1Score);
        AutoResizeTextView player2Score_top = (AutoResizeTextView) findViewById(R.id.winnerpage_topPlayer2Score);
        AutoResizeTextView player1Score_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer1Score);
        AutoResizeTextView player2Score_bottom = (AutoResizeTextView) findViewById(R.id.winnerpage_bottomPlayer2Score);
        player1Score_top.setText("" + player1Score);
        player2Score_top.setText("" + player2Score);
        player1Score_bottom.setText("" + player1Score);
        player2Score_bottom.setText("" + player2Score);

        RelativeLayout returnToMainMenu_top = (RelativeLayout) findViewById(R.id.winnerpage_TopReturnToMainMenuButton);
        RelativeLayout returnToMainMenu_bottom = (RelativeLayout) findViewById(R.id.winnerpage_BottomReturnToMainMenuButton);
        returnToMainMenu_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation();
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0); // general click sound
                releaseMediaPlayer(); // stop all background music
                returnToMainMenu();

            }
        });
        // same as returnToMainMenu_top
        returnToMainMenu_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation();
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0); // general click sound
                releaseMediaPlayer(); // stop all background music
                returnToMainMenu();
            }
        });

        RelativeLayout restartQuickPlay_top = (RelativeLayout) findViewById(R.id.winnerpage_TopRestartQuickPlayButton);
        RelativeLayout restartQuickPlay_bottom = (RelativeLayout) findViewById(R.id.winnerpage_BottomRestartQuickPlayButton);

        restartQuickPlay_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation();
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0);
                releaseMediaPlayer();
                restartQuickPlay();

            }
        });

        restartQuickPlay_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winnerName_top.clearAnimation();
                winnerName_bottom.clearAnimation();
                SoundPoolManager.getInstance().playSound(0);
                releaseMediaPlayer();
                restartQuickPlay();
            }
        });

    }

    private void restartQuickPlay() {
        // to be called after game has ended, during winner page
        if (customTimer != null) customTimer.cancel();
        if (defaultTimer != null) defaultTimer.cancel();
        if (delayExecutionTimer != null) delayExecutionTimer.cancel();
        Utils.canAnimate = false;
        Intent intent = getIntent();
        finishAtWinnerPage();
        startActivity(intent);
    }

    private void returnToMainMenu() {
        // ONLY called during winner page, when user pressed return to main menu, i.e. after game has ended
        if (customTimer != null) customTimer.cancel();
        if (defaultTimer != null) defaultTimer.cancel();
        if (delayExecutionTimer != null) delayExecutionTimer.cancel();
        Utils.canAnimate = false;
        finishAtWinnerPage();
    }

    private void callNewRound() {
        // used in 2 places, one when users has click 3 correct (or whatever the preference), and the 2nd place is at backpressed "Go To Next Round"
        currentRound++; // increase round first
        if(currentRound <= totalRounds) {
            mFirebaseAnalytics.logEvent("Total_Rounds_Played", null);
            // call new game mode since game has not ended yet
            if(gameMode!= null && gameMode instanceof TouchingSpam ) {
                // in TouchingSpam if you go next round, delayMethodExecution will not be called hence the player button is not set back to border
                // therefore we set the border back here
                player1Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design);
                player2Button.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.button_design);
            }
            releaseMediaPlayer();
            playMediaPlayer(0); // play dialog sound
            GameMode.callingNewDialog = true;
            GameMode.dialogDelay = 2300;
            callGameContentAndQn();
            timerForDialog.start();
            callDialog(getResources().getString(com.react.reactmultiplayergame.R.string.round) + " " + currentRound + " " + getResources().getString(com.react.reactmultiplayergame.R.string.of) + " " + totalRounds, gameMode.getDialogTitle(getApplicationContext()));
        } else {
            // need this becos removeDynamically is not called at other places
            if(gameMode!=null) {
                gameMode.removeDynamicallyAddedViewAfterQuestionEnds();
            }
            // game has ended, call winner page
            if(Utils.hasPurchasedRemoveAds(QuickPlayTwoPlayer.this)) {
                callWinnerPage_noAds();
            } else {
                callWinnerPage_withAds();
            }
        }
    }

    @Override
    public void onBackPressed() {
        SoundPoolManager.getInstance().playSound(1); // popup appear sound
        if(mNativeExpressAdView != null)
        mNativeExpressAdView.resume();
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickPlayTwoPlayer.this);
        builder.setView(exit_customDialogView);

        final AlertDialog dialogExit = builder.create();
        Window dialogWindow = dialogExit.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));

        dialogExit.setCancelable(true);
        dialogExit.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWindow.getAttributes());
       //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);

        RelativeLayout exit_returnToMainMenuButton = (RelativeLayout) exit_customDialogView.findViewById(R.id.exit_returnToMainMenuButton);
        RelativeLayout exit_goToNextGameRoundButon = (RelativeLayout) exit_customDialogView.findViewById(R.id.exit_goToNextRoundButton);
        AutoResizeTextView exit_currentRoundTextView = (AutoResizeTextView) exit_customDialogView.findViewById(R.id.exit_currentRound);

        exit_returnToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goingToExitActivity = true;
                SoundPoolManager.getInstance().playSound(0); // general click sound. This will result in both general click sound and dismiss sound to play on "yes" clicked
                if(gameMode!=null) {
                    gameMode.removeDynamicallyAddedViewAfterQuestionEnds();
                }
                if (customTimer != null) customTimer.cancel();
                if (defaultTimer != null) defaultTimer.cancel();
                if (delayExecutionTimer != null) delayExecutionTimer.cancel();
                releaseMediaPlayer();
                Utils.canAnimate = false;
                dialogExit.dismiss();
                finishAtWinnerPage(); // may be triggered at both winnerpage/no winner page, but thats ok since we got a != null check at this method
            }
        });


        if(currentRound > totalRounds) {
            // meaning we are now at victory page. The game has ended. Note it must be > and not >=
            // gray out go goToNextGameRound button
            exit_goToNextGameRoundButon.setBackgroundResource(R.drawable.exit_gotonextroundgrayoutbutton);
            // set game completed at victory page
            exit_currentRoundTextView.setText(getString(R.string.gamecompleted));
        }
        else {
            // only set this round text when it is not at victory page
            exit_currentRoundTextView.setText(getString(R.string.round) + " " + currentRound + "/" + totalRounds);

            // only set the button to be clickable if it is not at victory page
            exit_goToNextGameRoundButon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundPoolManager.getInstance().playSound(0);
                    playCloseMenuSound = false;
                    hideTimerCountdown();
                    dialogExit.dismiss();
                    callNewRound();
                }
            });
        }


        dialogExit.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // reinitialize the view again, becos we cannot use back a new alertdialog on old inflated view
                if(!goingToExitActivity) {
                    // not calling this if we are going to exit activity, i.e. return to main menu
                    // otherwise, such as closing dialog, we initialize it again

                    RelativeLayout adContainer = (RelativeLayout) exit_customDialogView.findViewById(R.id.returnToMainMenu_AdRelativeLayout);
                    if(mNativeExpressAdView != null) {
                        // need to remove the view becos we will be calling a new adContainer with the same nativeExpressView
                        adContainer.removeView(mNativeExpressAdView);
                    }
                    initializeExitCustomDialog();
                }
                if(playCloseMenuSound) {
                    SoundPoolManager.getInstance().playSound(2); // popup dismiss sound
                }
                // reinitialize it back to true in case it was false when this method was called
                playCloseMenuSound = true;
            }
        });
    }


    @Override
    protected void onDestroy() {
        // check to prevent window leak/crash, esp when user press home button or unexpected behavior
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        if(mInterstitialAd != null ) {
            mInterstitialAd.setAdListener(null);
            mInterstitialAd = null;
        }

        if(mNativeExpressAdView !=null) {
            mNativeExpressAdView.destroy();
            mNativeExpressAdView = null;
        }
        super.onDestroy();
    }

    private void finishAtWinnerPage() {
        if(winnerpage_zoominzoomoutanimator != null) {
            winnerpage_zoominzoomoutanimator.cancel();
            winnerpage_zoominzoomoutanimator.removeAllListeners();
            winnerpage_zoominzoomoutanimator.removeAllUpdateListeners();
            winnerpage_zoominzoomoutanimator = null;
        }
        finish();
    }

}
