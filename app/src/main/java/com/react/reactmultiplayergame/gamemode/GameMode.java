package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by gan on 17/3/17.
 */

public abstract class GameMode {

    //one instance of random
    public final Random randomGenerator = new Random();

    // used only for illusionCircle gameMode whereby every new instance will toggle this from true to false and vice versa
    // so that every new instance will flip the case where there is 1 circle on top or 1 circle below
    public static boolean illusionCircletoggle = false;

    // the total duration for the dialog to appears, i.e. the 2 sec animation, + 300ms animating out
    public static int dialogDelay = 2300;
    // set to true when calling the alertdialog screen because alertdialog.ondismisslistener is set to call timer, and will be duplicated since another timer will be called
    // in setgamecontent method. Access this to determine if there is a dialog call ornot for various gameMode, and determine factors like timer duration to account for dialog.
    public static boolean callingNewDialog = false;

    public abstract void setCurrentQuestion(TextView player1Question, TextView player2Question);
    //rootView is the overall root parent of the xml file. Can be use to findviewbyid any child.
    //parentLayout refers to ONLY the gameContent Rectangle in the center
    public abstract void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers);
    public abstract String getDialogTitle(Context context);
    public abstract boolean player1ButtonClickedCustomExecute();
    public abstract boolean player2ButtonClickedCustomExecute();
    public abstract boolean player3ButtonClickedCustomExecute();
    public abstract boolean player4ButtonClickedCustomExecute();
    public abstract int getBackgroundMusic();
    //Default timer is the one that is set for user preference timing, panelNumber.e the one that NumberComparison is using
    public abstract boolean requireDefaultTimer();
    //set requireDefaultTimer to false to trigger requireAtimer, and set it to false also if doesnt require a timer
    public abstract boolean requireATimer();
    // used for the event where gameMode needs a timer, but want to hide the timerbar from user (e.g. IllusionGear), then set requireATimer to true, and wantToShowTimer to false
    // set requireATimer to true and wantToShowTimer to true if want to show timer and need a timer, e.g. animalKingdom
    public abstract boolean wantToShowTimer();
    public abstract int customTimerDuration();

    //override this method for custom timer that will end after only 1 iteration. e.g. TouchingSpam.java will always end after 1 iteration
    public boolean customTimerIsCalledOnce() {
        return false;
    }

    //override and set to true for gameMode that must press the player button multiple times, e.g. TouchingSpam.java,
    //so that button.onClickListener will not update score when pressed
    public boolean requireMultiTouch(){
        return false;
    }
    public abstract void removeDynamicallyAddedViewAfterQuestionEnds();

    //Override this to true if require a rectangular white stroke, instead of a rounded corner stroke.
    //By default it is false, meaning gameMode will call a rounded corner white stroke instaed
    public boolean gameContent_RequireRectangularWhiteStroke () {
        return false;
    }

    public int playerThatTouched() {
        return -1;
    }

    // true = correct answer
    public abstract boolean getCurrentQuestionAnswer();

}
