package com.react.reactmultiplayergame.gamemode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.react.reactmultiplayergame.helper.Constants;

/**
 * Created by gan on 17/3/17.
 */

public class GameActivity extends Fragment {
    private int levelDifficulty = Constants.MODE_MEDIUM;
//    public GameActivity(int levelDifficulty){
//        this.levelDifficulty = levelDifficulty;
//    };
    private void setGame(){}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
