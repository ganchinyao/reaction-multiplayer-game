package com.react.reactmultiplayergame.gamemode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gan on 9/4/17.
 * <p>
 * User tap when screen turns to a particular color
 * easy = wait for screen to turn red
 * medium = wait for screen to turn to a particular color
 * hard = screen constantly changing to various color. Wait for screen to change to a particular color
 * insane = same as hard, but more color options and faster changing speed
 *
 * Accounted for translation *
 */

public class ScreenColor extends GameMode {
    private int requiredColor;
    private ArrayList<Integer> colorArray = new ArrayList<>();
    private int levelDifficulty;
    private Context context;
    private Timer timer = new Timer();
    private View changeColorView;
    // use to retain background color after user click, and prevent timer from switching color away, so user can see the current color
    private boolean userPressed = false;
    private RelativeLayout parentLayout;
    @Override
    public int customTimerDuration() {
        return 0;
    }

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        // using CharSequence and context.getText so that we do not have to use html.fromhtml and still able to set <b> tag in strings.xml
        CharSequence questionToSet = "";
        switch (requiredColor) {
            case Color.YELLOW:
                questionToSet = context.getText(R.string.colorscreen_yellow);
                break;
            case Color.CYAN:
                questionToSet = context.getText(R.string.colorscreen_cyan);
                break;
            case Color.GREEN:
                questionToSet = context.getText(R.string.colorscreen_green);
                break;
            case Color.BLUE:
                questionToSet = context.getText(R.string.colorscreen_blue);
                break;
            case Color.BLACK:
                questionToSet = context.getText(R.string.colorscreen_black);
                break;
            case Color.RED:
                questionToSet = context.getText(R.string.colorscreen_red);
                break;
            case Color.WHITE:
                questionToSet = context.getText(R.string.colorscreen_white);
                break;
            case Color.GRAY:
                questionToSet = context.getText(R.string.colorscreen_gray);
                break;
            default:
                // using if-else because cant use case since Color.parseColor() is not a constant at compile time
                if(requiredColor == Color.parseColor("#800080")) questionToSet = context.getText(R.string.colorscreen_purple);
                else if(requiredColor == Color.parseColor("#FF69B4")) questionToSet = context.getText(R.string.colorscreen_pink);
                else if(requiredColor == Color.parseColor("#FFA500")) questionToSet = context.getText(R.string.colorscreen_orange);
                else if(requiredColor == Color.parseColor("#800000")) questionToSet = context.getText(R.string.colorscreen_maroon);
                break;
        }
       player1Question.setText(questionToSet);
       player2Question.setText(questionToSet);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, final int levelDifficulty, int noOfPlayers) {
        this.levelDifficulty = levelDifficulty;
        this.context = context;
        initializeColor();
        this.parentLayout = parentLayout;

        // remove the blue background for this gameMode, since user is waiting for color to appear, and may thought background blue to be actual blue
        // we will set it back on dynamicallyRemoveView
        parentLayout.setBackgroundResource(0);

        // the color to set as the correct answer
        if(levelDifficulty == Constants.MODE_EASY) {
            requiredColor = Color.RED;
        }
        else {
            requiredColor = colorArray.get(randomGenerator.nextInt(colorArray.size()));
        }

        // Add a new view that changes color ontop of parentLayout instead of directly changing parentLayout color
        // So that parentLayout.clearAllView will auto helps to erase the displayed color
        changeColorView = new View(context);
        changeColorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parentLayout.addView(changeColorView);


        // wait for changecolorview to initialize first to avoid null pointer
        changeColorView.post(new Runnable() {
            @Override
            public void run() {
                if(levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
                    // wait for at least 3 sec to account for the dialog opening so that screen doesnt change color when dialog hasn't close
                    // easy/medium mode change color only once and reset to default color.
                    int delay = ((randomGenerator.nextInt(5)) + 3) * 1000;
                    timer.schedule(new RandomTimerTask(1), delay); // RandomTimerTask(1) to call special method that handles easy/medium mode
                } else {
                    // doesnt matter if screen turned color when dialog hasn't close, since hard/insane is toggling between various color, and not waiting for just 1 color to appear
                    // screen will immediaitely changed to a new color, unlike easy/medium which waits for 3 sec
                    RandomTimerTask randomTimerTask = new RandomTimerTask(5); // RandomTimerTask(5) trigger the default method in the switch method in RandomTimerTask run()
                    randomTimerTask.run();
                }
            }
        });


    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_screencolor);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        userPressed = true;
        timer.cancel();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        userPressed = true;
        timer.cancel();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        userPressed = true;
        timer.cancel();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        userPressed = true;
        timer.cancel();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_screencolor;
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
    public void removeDynamicallyAddedViewAfterQuestionEnds() {
        if(timer!= null) {
            timer.cancel();
            timer = null;
        }
        // set back the default blue background
        parentLayout.setBackgroundResource(R.drawable.gamemode_gamcontent_background);

    }

    @Override
    public boolean gameContent_RequireRectangularWhiteStroke() {
        return true;
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        if(changeColorView!= null) {
            ColorDrawable backgroundColor = (ColorDrawable) changeColorView.getBackground();
            // calculate the current background color, and if it the color required, return true
            if (backgroundColor!=null && backgroundColor.getColor()== requiredColor) return true;
            else return false;
        } else return false;
    }

    private void initializeColor() {
        // colorArray order and size doesnt matter. Code uses randomInt to retrieve a random number within array size.
        // ** If add new color, remember to update switch case in setQuestion

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                colorArray.add(Color.RED);
                break;
            case Constants.MODE_MEDIUM:
                colorArray.add(Color.YELLOW);
                colorArray.add(Color.CYAN);
                colorArray.add(Color.GREEN);
                colorArray.add(Color.BLUE);
                colorArray.add(Color.BLACK);
                colorArray.add(Color.RED);
                colorArray.add(Color.WHITE);
                break;
            case Constants.MODE_HARD:
                colorArray.add(Color.YELLOW);
                colorArray.add(Color.CYAN);
                colorArray.add(Color.GREEN);
                colorArray.add(Color.BLUE);
                colorArray.add(Color.BLACK);
                colorArray.add(Color.RED);
                colorArray.add(Color.WHITE);
                break;
            case Constants.MODE_INSANE:
                colorArray.add(Color.YELLOW);
                colorArray.add(Color.CYAN);
                colorArray.add(Color.GREEN);
                colorArray.add(Color.BLUE);
                colorArray.add(Color.BLACK);
                colorArray.add(Color.RED);
                colorArray.add(Color.WHITE);
                colorArray.add(Color.parseColor("#800080")); // purple
                colorArray.add(Color.parseColor("#FF69B4")); // hot pink
                colorArray.add(Color.parseColor("#FFA500")); // orange
                colorArray.add(Color.parseColor("#800000")); // maroon
                colorArray.add(Color.GRAY);
                break;
        }
    }

    private void changeBackgroundColor(){
        switch (levelDifficulty){
            case Constants.MODE_EASY:
                if(changeColorView!= null) {
                    changeColorView.setBackgroundColor(Color.RED);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // dismiss red background after 1 sec of showing
                            // prevent dismissal of red color if user pressed, so he can see the screen as red
                            if(!userPressed)
                            changeColorView.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 800);
                }
                break;
            case Constants.MODE_MEDIUM:
                if(changeColorView!= null) {
                    changeColorView.setBackgroundColor(requiredColor);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // dismiss colored background after 0.8s sec of showing
                            // prevent dismissal of colored color if user pressed, so he can see the color that he pressed
                            if(!userPressed)
                                changeColorView.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 800);
                }
                break;
            case Constants.MODE_HARD:
            case Constants.MODE_INSANE:
                if(changeColorView!= null)
                changeColorView.setBackgroundColor(colorArray.get(randomGenerator.nextInt(colorArray.size())));
                break;
        }
    }

    private class RandomTimerTask extends TimerTask {
        private int timerRequired;
        RandomTimerTask(int timerRequired) {
            this.timerRequired = timerRequired;
        }
        @Override
        public void run() {

            switch (timerRequired) {
                case 1: { // special call for easy/medium that toggles between a color and no color
                    int delay = ((randomGenerator.nextInt(6)) + 2) * 1000;
                    timer.schedule(new RandomTimerTask(timerRequired), delay);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeBackgroundColor();
                        }
                    });
                    break;
                }
                default: {
                    // call for hard/insane mode that keeps toggling the color
                    int delay;
                    if(levelDifficulty == Constants.MODE_HARD) {
                        delay = ((randomGenerator.nextInt(3)) + 1) * 1000;
                    }
                    else {
                        delay = (int) (((randomGenerator.nextDouble()) * 1000) + (randomGenerator.nextDouble() * 1000)); // between 0ms and 2000ms
                    }
                    timer.schedule(new RandomTimerTask(timerRequired), delay);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeBackgroundColor();
                        }
                    });
                    break;
                }
            }
        }
    }
}
