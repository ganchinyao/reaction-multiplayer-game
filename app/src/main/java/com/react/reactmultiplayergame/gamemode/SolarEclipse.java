package com.react.reactmultiplayergame.gamemode;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;
import com.react.reactmultiplayergame.helper.Utils;

import java.util.List;
import java.util.Random;

/**
 * Created by gan on 10/4/17.
 * User tap when the eclipse move and cover over the Sun completely
 *
 * Accounted for
 */

public class SolarEclipse extends GameMode {
    private ImageView sunImageView;
    private int initial_X = 0;
    private int initial_Y = 0;
    private boolean isFullEclipse = false;
    private ValueAnimator eclipseAnimation;
    private float final_X, final_Y;
    private int circleDiameter, eclipseGlow_additionalRadius;
    private Bitmap bm, eclipseGlow;
    private ImageView eclipseImageView;
    private Bitmap currentBitmapInAnimation;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.solareclipse);
        player2Question.setText(R.string.solareclipse);
    }

    @Override
    public void setGameContent(LinearLayout rootView, final RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        int _50dp = (int) context.getResources().getDimension(R.dimen._50sdp);
        circleDiameter = parentLayout.getHeight() - _50dp;
        eclipseGlow_additionalRadius = (int) context.getResources().getDimension(R.dimen.gameContent_solareclipse_glow_addsize);


        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_solareclipse);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }


        final Bitmap sunBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.b8);

        eclipseGlow = BitmapFactory.decodeResource(context.getResources(), R.drawable.eclipseglow);
        sunImageView = new ImageView(context);
        if (sunBitmap != null) {
            sunImageView.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(sunBitmap, circleDiameter, circleDiameter, true), 0, 0));
        }


        RelativeLayout.LayoutParams sunLayoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sunLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams eclipseLayoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        eclipseLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);

        parentLayout.addView(sunImageView, sunLayoutParam);


        // create the eclipse bitmap
        BitmapFactory.Options bitmapLoadingOptions = new BitmapFactory.Options();
        bitmapLoadingOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.b9, bitmapLoadingOptions);

        eclipseImageView = new ImageView(context);

        // 8 ways for eclipse to move in:
        // from left to right/ from right to left : move X only
        // from top to bottom/ from bottom to top: move Y only
        // from topleft to bottom right/ from bottom right to top left: move both X and Y equally
        // from bottomleft to top right/ from top right to bottom left : move both X and Y equally
        final int initialPositionOfEclipse;
        if(levelDifficulty == Constants.MODE_EASY) {
            // only case 0 for easy mode
            initialPositionOfEclipse = 0;
        } else {
            // toggle between 8 different position
            initialPositionOfEclipse = randomGenerator.nextInt(8);
        }
        switch (initialPositionOfEclipse) {
            case 0: // left to right
                initial_X = -circleDiameter;
                initial_Y = 0;
                break;
            case 1: // right to left
                initial_X = circleDiameter;
                initial_Y = 0;
                break;
            case 2: // top to bottom
                initial_X = 0;
                initial_Y = -circleDiameter;
                break;
            case 3: // bottom to top
                initial_X = 0;
                initial_Y = circleDiameter;
                break;
            case 4: // topleft to bottom right
                initial_X = -circleDiameter;
                initial_Y = -circleDiameter;
                break;
            case 5: // bottom right to top left
                initial_X = circleDiameter;
                initial_Y = circleDiameter;
                break;
            case 6: // bottom left to top right
                initial_X = -circleDiameter;
                initial_Y = circleDiameter;
                break;
            case 7: // top right to bottom left
                initial_X = circleDiameter;
                initial_Y = -circleDiameter;
                break;
        }

        int mDuration = 0; // duration of eclipse animation before reaching full eclipse
        int additionaSec_ForDialogDelay;
        if(callingNewDialog) {
            additionaSec_ForDialogDelay = dialogDelay;
        } else {
            // no dialog, meaning it is not the first instance for this current gameMode, hence no need to account for additional delay
            additionaSec_ForDialogDelay = 0;
        }
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                mDuration = (randomGenerator.nextInt(2) + 4) * 1000 + additionaSec_ForDialogDelay; // 4/5 second
                break;
            case Constants.MODE_MEDIUM:
                mDuration = (randomGenerator.nextInt(3) + 3) * 1000 + additionaSec_ForDialogDelay; // 3-6 second
                break;
            case Constants.MODE_HARD:
                mDuration = (int) ((randomGenerator.nextInt(5) + 1 + + randomGenerator.nextDouble()) * 1000 + additionaSec_ForDialogDelay); // 1-6 second, including in between decimal sec
                break;
            case Constants.MODE_INSANE:
                mDuration = (int) ((randomGenerator.nextInt(7) + 1 + + randomGenerator.nextDouble()) * 1000 + additionaSec_ForDialogDelay); // 1-8 second, including in between decimal sec
                break;

        }
        eclipseAnimation = ValueAnimator.ofFloat(0f, 1f); // 0 = 0%, 1 = 100% done
        eclipseAnimation.setDuration(mDuration);
        eclipseAnimation.setInterpolator(new LinearInterpolator());
        eclipseAnimation.addUpdateListener(animationUpdate);
        eclipseAnimation.start();

        parentLayout.addView(eclipseImageView, eclipseLayoutParam);
    }

    // eclipse and sunImageView bitmap, clipped to a circle
    private Bitmap getCircleBitmap(Bitmap bitmap, int offset_X, int offset_Y) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = Color.BLACK;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = bitmap.getHeight() / 2; //radius, either .getHeight or .getWidth, assuming bitmap height=width

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(r + offset_X, r + offset_Y, r, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_solareclipse);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        terminateEclipseAnimation();
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        terminateEclipseAnimation();
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        terminateEclipseAnimation();
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        terminateEclipseAnimation();
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_solareclipse;
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
        if(eclipseAnimation!= null) {
            eclipseAnimation.cancel();
            eclipseAnimation.removeAllListeners();
            eclipseAnimation.removeAllUpdateListeners();
            eclipseAnimation = null;
        }
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
       return isFullEclipse;
    }

    private final ValueAnimator.AnimatorUpdateListener animationUpdate = new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
            // the animation algorithem whereby eclipse start from a certain position and return to final full view eclipse position.
            // full view eclipse occurs when final_X and final_Y = 0
            final_X = initial_X - ((float) animation.getAnimatedValue() * (initial_X));
            final_Y = initial_Y - ((float) animation.getAnimatedValue() * (initial_Y));

            if(currentBitmapInAnimation != null ) {
                currentBitmapInAnimation.recycle();
                currentBitmapInAnimation = null;
            }
            currentBitmapInAnimation = getCircleBitmap(Bitmap.createScaledBitmap(bm, circleDiameter, circleDiameter, true), (int) final_X, (int) final_Y);
            eclipseImageView.setImageBitmap(currentBitmapInAnimation);
            if(animation.getAnimatedFraction() >= 0.98 /* 0.98 = 98% done, give 2% leeway as visual may show it is alr completed even at 98%*/) {
                // animation has completed
                isFullEclipse = true;
                // show the glow eclipse
                sunImageView.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(eclipseGlow, circleDiameter + eclipseGlow_additionalRadius, circleDiameter + eclipseGlow_additionalRadius, true), 0, 0));
            }
        }
    };

    private void terminateEclipseAnimation(){
        if(eclipseAnimation!= null) {
            eclipseAnimation.cancel();
            eclipseAnimation.removeAllListeners();
            eclipseAnimation.removeAllUpdateListeners();
            eclipseAnimation = null;
        }
    }
}
