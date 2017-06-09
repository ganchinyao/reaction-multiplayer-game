package com.react.reactmultiplayergame.helper;

import android.app.Application;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.react.reactmultiplayergame.R;
import io.fabric.sdk.android.Fabric;

/**
 * Created by gan on 10/5/17.
 */

// static methods all call sound pool via here
public class SoundPoolManager extends Application{

    private static SoundPoolManager singleton;
    private static SoundPool soundPool;
    private int popUpWindowSoundId, closeMenuSoundId, generalClickSoundId, swipePagesId, dropDownOpenId, dropDownCloseId, startButtonId;
    private boolean popUpSoundLoaded, closeMenuSoundLoaded, generalClickSoundLoaded, swipePagesLoaded, dropDownOpenLoaded, dropDownCloseLoaded, startButtonLoaded;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        singleton = this;
        createSoundPool(this);
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }

    @SuppressWarnings("deprecation")
    private void createSoundPool(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(7)
                    .build();
        } else {
            soundPool = new SoundPool(7, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    // 0 means success according to android documentation
                    // determine which sound is ready via an if-else. Cant use switch since the soundId is not editText constant expression
                    if (sampleId == generalClickSoundId) {
                        generalClickSoundLoaded = true;
                    } else if (sampleId == popUpWindowSoundId) {
                        popUpSoundLoaded = true;
                    } else if (sampleId == closeMenuSoundId) {
                        closeMenuSoundLoaded = true;
                    } else if (sampleId == swipePagesId) {
                        swipePagesLoaded = true;
                    } else if (sampleId == dropDownOpenId) {
                        dropDownOpenLoaded = true;
                    } else if (sampleId == dropDownCloseId) {
                        dropDownCloseLoaded = true;
                    } else if (sampleId == startButtonId) {
                        startButtonLoaded = true;
                    }
                }
            }
        });

        // load all the various sound to their respective soundId
        dropDownCloseId = soundPool.load(context, R.raw.dropdownclose, 1);
        popUpWindowSoundId = soundPool.load(context, com.react.reactmultiplayergame.R.raw.homepage_popupappear, 1);
        closeMenuSoundId = soundPool.load(context, com.react.reactmultiplayergame.R.raw.homepage_buttonclosequickplay, 1);
        generalClickSoundId = soundPool.load(context, com.react.reactmultiplayergame.R.raw.homepage_buttonclicked, 1);
        swipePagesId = soundPool.load(context, R.raw.swipesound, 1);
        dropDownOpenId = soundPool.load(context, R.raw.dropdownopen, 1);
        startButtonId = soundPool.load(context, R.raw.startbutton, 1);

    }


    public static SoundPoolManager getInstance(){
        return singleton;
    }

    // all soundPool play sound to call this method, and passed in different param for different sound
    // method check if the global mute button is active, as well as if soundPool has finished loading the sound
    public void playSound(int soundToPlay){
        if (soundPool != null && !Utils.getIfSoundIsMuted(this)) {
            switch (soundToPlay) {
                case 0:
                    // play general click sound
                    if (generalClickSoundLoaded)
                        soundPool.play(generalClickSoundId, 1, 1, 1, 0, 1f);
                    break;
                case 1:
                    // play popup appear sound
                    if (popUpSoundLoaded)
                        soundPool.play(popUpWindowSoundId, 1, 1, 1, 0, 1f);
                    break;
                case 2:
                    // play menu close sound
                    if (closeMenuSoundLoaded)
                        soundPool.play(closeMenuSoundId, 1, 1, 1, 0, 1f);
                    break;
                case 3:
                    // play swipe pages sound
                    if(swipePagesLoaded)
                        soundPool.play(swipePagesId, 1, 1, 1, 0, 1f);
                    break;
                case 4:
                    // play drop down open sound
                    if(dropDownOpenLoaded)
                        soundPool.play(dropDownOpenId, 1, 1, 1, 0, 1f);
                    break;
                case 5:
                    // play drop down closed sound
                    if(dropDownCloseLoaded)
                        soundPool.play(dropDownCloseId, 1, 1, 1, 0, 1f);
                    break;
                case 6:
                    // play start button sound
                    if(startButtonLoaded)
                        soundPool.play(startButtonId, 1, 1, 1, 0, 1f);
                    break;
            }
        }

    }

}
