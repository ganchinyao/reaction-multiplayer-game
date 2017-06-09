package com.react.reactmultiplayergame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.react.reactmultiplayergame.helper.MusicManager;
import com.react.reactmultiplayergame.helper.SoundPoolManager;
import com.react.reactmultiplayergame.helper.Utils;

/**
 * Created by gan on 10/5/17.
 */

public class HowToPlay extends AppCompatActivity {
    // used to determine if mediaplayer should continue playing or stop
    private boolean onResumeCalledBefore = false;
    private boolean goingToPreviousActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.howtoplay);

        Toolbar toolbar = (Toolbar) findViewById(R.id.howtoplay_toolbar);
        setSupportActionBar(toolbar);

        ImageView backButton = (ImageView) toolbar.findViewById(R.id.howtoplay_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        if(!goingToPreviousActivity) {
            // we only want to stop mediaplayer if it app is going background, and NOT when app is onbackpress to go back to previous activity
            // this is achieve by setting the flag goingToPreviousActivity to be true when we onBackPressed.
            // but if user click home button to go home page, goingToPreviousActivity will be false, and stopmediaplayer will be triggered
            MusicManager.stopMediaPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(onResumeCalledBefore) {
            // will not be called the first time this activity is instantiated, since the music is alr playing
            // will be called in subseqent called to onResume, which will occur when app go to background and come back to foregoround again
            MusicManager.initializeAndPlayMediaPlayer(this, Utils.getIfSoundIsMuted(this));
        }
        onResumeCalledBefore = true;
    }

    @Override
    public void onBackPressed() {
        SoundPoolManager.getInstance().playSound(2); // play sound
        // signify to onPause that we do not stop playing mediaplayer
        goingToPreviousActivity = true;
        super.onBackPressed();
    }
}
