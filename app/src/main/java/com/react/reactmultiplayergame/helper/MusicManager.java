package com.react.reactmultiplayergame.helper;


import android.content.Context;
import android.media.MediaPlayer;

import com.react.reactmultiplayergame.R;

/**
 * Created by gan on 10/5/17.
 */

public class MusicManager {

    private static MediaPlayer mediaPlayer; // used for background music, whereas soundPool play all the short click sounds
    private static Context mContext;

    // the flag to set to true where we explicitly set intent to go to new activity such as settings and shop find out more, but we still want to play background music
    // this will be used in onpause and onresume to tell if we activate onpause by going to new activity (true), or it is user press homepage to activate onpause(false)
    // if it is the former we keep background music going, and if it is latter we stop background music
    // currently this field is set to true only at 2 places, when user go to settings activity, and when user to go findoutmore activity.
    // do not have to reset it to false manually at other places, becoz it will set back to false onResume at last sentence, and this will work for all scenario
    // ** IMPT ** If in the future we have further activity, and we want the background music to continue playing after we go to the next activity, we MUST do all the followings:
    // 1) set MusicManager.isGoingNextActivity = true in parent activity before we call the new intent to go next activity
    // 2) In the NEW activity file, override onPause, onResume, and onBackpressed
    // 3) Create 2 boolean flag in the NEW activity file called boolean onResumeCalledBefore = false; boolean goingToPreviousActivity = false;
    // 4) Copy paste the onPause, onResume, and onBackpressed found in ShopFindOutMore.java file to new activity file, and WE ARE DONE
    // how this works: when we go new activity, we do not want to initializeMediaPlayer agn, since it is alr playing. Hence, we have a flag call onResumeCallBefore, whereby the
    // initializeMediaPlayer will not be called first time, and only be called subsequent times, i.e when activity goes to background and come back to foreground again
    // We DO NOT want mediaplayer to stop playing music when user go back to previous activity, hence we need a flag goingToPreviousActivity to determine onPause whether to stop the music or not
    // We only stop the music when user go to background in the new activity. Hence, we override onbackpressed and set the flag goingToPreviousActivity to true to signify to onPause that music
    // will not be stop when user go to previousActivity
    //****** IMPT ********** The above steps 2,3,4 ONLY works if pressing backpressed on the new activity will 100% result in the activity being destroyed and return to main menu
    // If you have sub views inside, such as SettingsActivity FAQ page, whereby you pressed back at FAQ page WILL NOT return to main menu but return to main settings instead,
    // then the above 2,3,4 steps WILL NOT WORK, becos when pressing onbackpressed at FAQ page, it will trigger the boolean goingToPreviousActivity = true, where it should not
    // Hence in this situation, we have to copy the SettingsActivity onPause and use isFinishing() method to determine if app is indeed finishing, or just going background
    // But in simpler cases such as ShopFindOutMore where pressing back will 100% result in going to main menu, i.e. no inner view or what not, using the above steps is fine
    // and no extra overhead by using isFinishing()
    public static boolean isGoingNextActivity = false;

    public static void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void initializeAndPlayMediaPlayer(Context context, boolean isSoundMuted) {
        mContext = context;
        mediaPlayer = MediaPlayer.create(mContext, R.raw.homepage_backgroundmusic);
        if (mediaPlayer != null && !isSoundMuted) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }

    public static void pauseMediaPlayer(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // pause background music
            mediaPlayer.pause();
        }
    }

    public static void playMediaPlayer(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            // start playing background music
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }
}
