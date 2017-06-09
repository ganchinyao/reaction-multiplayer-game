package com.react.reactmultiplayergame.helper;

/**
 * Created by gan on 16/3/17.
 */

public class Constants {
    //Not meant to be initialize
    //Use class for constant value via static
    private Constants(){}

    public static final int MODE_EASY = 1;
    public static final int MODE_MEDIUM = 2;
    public static final int MODE_HARD = 3;
    public static final int MODE_INSANE = 4;
    public static final int MODE_TWOPLAYER = 2;
    public static final int MODE_THREEPLAYER = 3;
    public static final int MODE_FOURPLAYER = 4;
    public static final int GAMEMODE_QUICKPLAY = -1024; // number doesnt matter, so long as it differ from customGame below
    public static final int GAMEMODE_CUSTOMGAME = -2048;
    public static final int PREFERENCE_SCORE_SYSTEM_1 = 1;
    public static final int PREFERENCE_SCORE_SYSTEM_2 = 2;
    public static final int PREFERENCE_SCORE_SYSTEM_3 = 3;
    public static final int PREFERENCE_SCORE_SYSTEM_4 = 4;
    public static final int PREFERENCE_SCORE_SYSTEM_5 = 5;
    public static final int PREFERENCE_SCORE_SYSTEM_10 = 10;
    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;
    public static final int PLAYER3 = 3;
    public static final int PLAYER4 = 4;
    public static final int DRAW = -1;
    public static final boolean CORRECT = true;
    public static final int PREFERENCE_QUESTION_DELAY_1000 = 1000;
    public static final int PREFERENCE_QUESTION_DELAY_1500 = 1500;
    public static final int PREFERENCE_QUESTION_DELAY_2000 = 2000;
    public static final int PREFERENCE_QUESTION_DELAY_2500 = 2500;
    public static final int PREFERENCE_QUESTION_DELAY_3000 = 3000;
    public static final int PREFERENCE_QUESTION_DELAY_4000 = 4000;
    public static final String dash = "/";
    public static final String iap_sku_Ads = "com.react.reactmultiplayer.ad";
    public static final String iap_sku_InsaneMode = "com.react.reactmultiplayer.ins"; //TODO match this sku with console sku
    public static final String publickey = "SAMPLE_TO_REPLACE";

    public static final String admob_Interstitial_Id = "SAMPLE_TO_REPLACE";
    public static final String fb_nativehomepagead_Id = "SAMPLE_TO_REPLACE";

    public static final String admob_NativeExpress_Id = "SAMPLE_TO_REPLACE";
}
