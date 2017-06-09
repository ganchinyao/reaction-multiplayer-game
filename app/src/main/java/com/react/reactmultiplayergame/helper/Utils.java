package com.react.reactmultiplayergame.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.react.reactmultiplayergame.R;

import java.util.Random;

/**
 * Created by gan on 19/3/17.
 */

public class Utils {

    private Utils() {
    }

    public static boolean canAnimate = false;

    // set scores, round, delay all handled by settingsActivity ListPreference

    // how many points to add for each correct ans
    public static int getScoreSystemPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // need to parse it to Int becoz the sharedPreference stored is a string
        // this is how the settings page, for ListPreference API works. They store it as string
        return Integer.parseInt(preferences.getString("User_Score_Preference", "1"));
    }

    // how many score to add subtract for each correct or wrong answer. Default is 1
    // Note: value should still be positive, becos code uses minus operator to minus score
    public static int getScoreSystemPreference_Minus(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(preferences.getString("User_Score_Preference_Minus", "1"));
    }

    // How long when the answer is displayed before next question get called, i.e. how long the green/red button get lights up
    public static int getAnswerDelayPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(preferences.getString("User_Delay_Preference", "2000"));
    }

    // how long each question is shown to the user before next question is called
    // currently we have no implementation for it, becoz the game mode decides how long is each qn based on difficulty
    // but in QuickPlay.java, we still retrieve getQuestionDelayPreference for countdown timer.
    // Maybe in the future this will be of use
    public static void setQuestionDelayPreference(Context context, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Next_Question_Delay_Preference", value);
        editor.apply();
    }

    // how long each question is shown to the user before next question is called
    public static int getQuestionDelayPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("Next_Question_Delay_Preference", Constants.PREFERENCE_QUESTION_DELAY_2000);
    }

    // how many question user must get correct in total before next game mode is invoke
    public static int getNumberOfCorrectToGoNextGameMode(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(preferences.getString("To_Next_GameMode_Preference", "3"));
    }


    // how many rounds to play, default 10
    public static int getQuickPlayTotalRoundsToPlay(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(preferences.getString("QuickPlay_Total_Number_Of_Rounds", "10"));
    }


    // the global button at home page to mute or unmute sound for the entire application
    public static void setIfSoundIsMuted(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Global_Sound_Mute", value);
        editor.apply();
    }

    public static boolean getIfSoundIsMuted(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("Global_Sound_Mute", false);
    }

    // Call during editplayername, and passed in param currentPlayer for player1/2/3/4, and the respective custom name in currentPlayerName
    public static void setPlayerName(Context context, int currentPlayer, String currentPlayerName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Custom_Player_Name" + currentPlayer, currentPlayerName);
        editor.apply();
    }

    public static String getPlayerName(Context context, int currentPlayer) {
        // currentPlayer is which player, e.g. player1, or player2, or 3 or 4
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (currentPlayer) {
            // different switch to handle the default of player1/2/3/4
            case Constants.PLAYER1:
                return preferences.getString("Custom_Player_Name" + currentPlayer, context.getString(R.string.player1));
            case Constants.PLAYER2:
                return preferences.getString("Custom_Player_Name" + currentPlayer, context.getString(R.string.player2));
            case Constants.PLAYER3:
                return preferences.getString("Custom_Player_Name" + currentPlayer, context.getString(R.string.player3));
            case Constants.PLAYER4:
                return preferences.getString("Custom_Player_Name" + currentPlayer, context.getString(R.string.player4));
            default:
                return "";
        }

    }

    // called when user is at edit profile page and select a customize color
    // NOTE: this method set the color tag AS the POSITION of which circle is selected, i.e. from 0-9, NOT the actual color
    // With the knowledge of the POSITON of the color selected, we then manually query the respective color to be used in QuickPlayTwoPlayer.java
    public static void setPlayerColorTag(Context context, int currentPlayer, int playerColor) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Custom_Player_Color_Tag" + currentPlayer, playerColor);
        editor.apply();
    }

    // get the custom color that user has selected for their color tag.
    // NOTE: this method gets the current POSITION of the circle, i.e. from 0 to 9, which circle is the selected one.
    // Then, using this number, manually implement the respective color via a colorArray.
    // Querying the position is necessary in EditPlayerProfileAdapter where we need to define which is the default position to start with circleSelected as src
    // use the method below getActualTagColor to retrieve the actual hex color
    public static int getPlayerColorTagPosition(Context context, int currentPlayer) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (currentPlayer) {
            // different switch to handle the default of player1/2/3/4
            case Constants.PLAYER1:
                return preferences.getInt("Custom_Player_Color_Tag" + currentPlayer, 1);
            case Constants.PLAYER2:
                return preferences.getInt("Custom_Player_Color_Tag" + currentPlayer, 3);
            case Constants.PLAYER3:
                return preferences.getInt("Custom_Player_Color_Tag" + currentPlayer, 5);
            case Constants.PLAYER4:
                return preferences.getInt("Custom_Player_Color_Tag" + currentPlayer, 7);
            default:
                return 0;
        }
    }

    // this returns the actual color for the player glow, ready to be passed immediately into setColorFilter
    public static int getActualColorTag(Context context, int currentPlayer) {
        int colorArray[] = {R.color.editplayerprofile_colortag_yellow, R.color.editplayerprofile_colortag_orange, R.color.editplayerprofile_colortag_pink,
                R.color.editplayerprofile_colortag_red, R.color.editplayerprofile_colortag_white, R.color.editplayerprofile_colortag_cyan,
                R.color.editplayerprofile_colortag_green, R.color.editplayerprofile_colortag_purple, R.color.editplayerprofile_colortag_brown,
                R.color.editplayerprofile_colortag_black};

        return ContextCompat.getColor(context, colorArray[Utils.getPlayerColorTagPosition(context, currentPlayer)]);
    }


    // used to determine which difficulty the user was at just now.
    // e.g. if user start game at hard, the next time he start game again, the popup will auto show hard at default for him
    // also used in all QuickPlay mode whereby it will retrieve what difficulty level to play based on getDifficultyPreference
    public static void setDifficultyPreference(Context context, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Level_Difficulty_Preference", value);
        editor.apply();
    }

    public static int getDifficultyPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("Level_Difficulty_Preference", Constants.MODE_MEDIUM);
    }


    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void shuffleIntArray(int[] array) {
        // randomly shuffle an int [] instead of using Collections.shuffle which is overkill since it needs to auto wrap/unwrap
        Random rgen = new Random();  // Random number generator

        for (int i = 0; i < array.length; i++) {
            int randomPosition = rgen.nextInt(array.length);
            int temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }
    }

    // keep a shared preference to measure device size only once, so we do not have to keep measuring it every launch
    public static void sethasMeasuredDeviceSize(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Current_Device_Size", value);
        editor.apply();
    }

    public static boolean hasAlreadyMeasuredDeviceSize(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("Current_Device_Size", false);
    }

    // store the current device width
    public static void setCurrentDeviceWidth(Context context, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Current_Device_Width", value);
        editor.apply();
    }

    // store the currentdevice height
    public static void setCurrentDeviceHeight(Context context, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Current_Device_Height", value);
        editor.apply();
    }

    public static int getCurrentDeviceWidth(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("Current_Device_Width", 0);
    }

    public static int getCurrentDeviceHeight(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("Current_Device_Height", 0);
    }


    // set to true if user has purchased remove ads iap
    // here we put a funny string to show to those who reverse engineer the code
    public static void setPurchasedRemoveAds(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("React_isDone_hasDone", value);
        editor.apply();
    }

    public static boolean hasPurchasedRemoveAds(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("React_isDone_hasDone", false);
    }

    // set to true if user has purchased insane mode iap
    // here we put a funny string to show to those who reverse engineer the code
    public static void setPurchasedInsaneMode(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("React_Player_Score_Name", value);
        editor.apply();
    }

    public static boolean hasPurchasedInsaneMode(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("React_Player_Score_Name", false);
    }

    // the mode that the users has selected. state is whether selected or not, and position is which position it is in, from index 0
    // int state > 0 represents true, state == 0 represent false
    public static void setCustomGameModePreference(Context context, int state, int position) {
        String arrayName = "CustomGameModes_Selection";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(arrayName + "_" + position, state);
        editor.apply();
    }

    // Unlike the setter above, this getter returns all the values of all the checkbox in an int array.
    // The setter set one by one the state of the checkbox based on the position of the checkbox from 0 to size-1
    // *** We dont use boolean here even though it is supposed to. Instead, we use if int > 0 to be true, and int == 0 to be false
    // This is so that we can store the int number for sequence mode, and no need to store twice, one for number and one for boolean
    // as this sharedpreference is used for both tick random mode, and number in sequence mode
    public static int[] getCustomGameModePreference(Context context, int arraySize) {
        String arrayName = "CustomGameModes_Selection";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int array[] = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = preferences.getInt(arrayName + "_" + i, 0);
        }
        return array;
    }

    public static String obsfucate_publicAPIKey(byte[] bytes) {
        // Swap upper and lower case letters.
        for (int i = 0; i < bytes.length; i++) {
            if(bytes[i] >= 'A' && bytes[i] <= 'Z') {
                bytes[i] = (byte) ('a' + (bytes[i] - 'A'));
            }
            else if(bytes[i] >= 'a' && bytes[i] <= 'z') {
                bytes[i] = (byte) ('A' + (bytes[i] - 'a'));
            }
        }
        return new String(bytes);
    }

    public static String firstPart_APIKey (){
        return "SAMPLE_TO_REPLACE";
    }

    public static String lastPart_APIKey (){
        return "SAMPLE_TO_REPLACE";
    }

    public static String subset_APIKey_beforeDash (String apikey, int numberOfDash) {
        // return the chunk of string before n number of dash
        // param numberOfDash: e.g if 1, means from beginning till 1st '/' reached. If 2 means from 1st '/' until 2nd '/' -> NOT from beginning till 2nd '/'
        // if 3 means from 2nd '/' till 3rd '/' and so on
        String result = "";
        int counter = 0;
        for (char c : apikey.toCharArray()) {
            if(c == '/') {
                counter++;
                if(counter == numberOfDash) break;
                else {
                    result = ""; // reset result to blank again
                    continue; // impt to skip so that '/' does not get recorded
                }
            }
            result += Character.toString(c);
        }
        return result;
    }

    // whether custom game is in randomize order
    public static void setCustomGameRamdomizeOrder(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("CustomGame_Randomize_Order", value);
        editor.apply();
    }

    public static boolean getCustomGameRandomizeOrderOn(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("CustomGame_Randomize_Order", true); // default is true
    }

}
