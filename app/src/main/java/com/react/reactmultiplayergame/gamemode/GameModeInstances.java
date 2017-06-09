package com.react.reactmultiplayergame.gamemode;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by gan on 23/3/17.
 *
 * Store the various game mode in this class
 */

public class GameModeInstances implements Serializable {

    private Class modeClass;
    public GameModeInstances(Class modeClass) {
        this.modeClass = modeClass;
    }
    public Class getModeClass(){
        return modeClass;
    }


}

