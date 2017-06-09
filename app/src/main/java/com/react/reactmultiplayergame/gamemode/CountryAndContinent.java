package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CurvedAndTiledDrawable;

import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

/**
 * Created by gan on 15/4/17.
 * Tap when the country shown matches the continent
 * <p>
 * Insane mode = 1 sec timer
 *
 * Accounted for translation and screenSize
 */

public class CountryAndContinent extends GameMode {
    private ArrayList<String[]> continentArray = new ArrayList<String[]>();
    // 0 = correct.
    private int correctOrWrong;
    private int levelDifficulty;
    private Context context;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.countryandcontinent);
        player2Question.setText(R.string.countryandcontinent);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        this.levelDifficulty = levelDifficulty;
        this.context = context;

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_continentcountry);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);
            drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#DC91AB"), PorterDuff.Mode.MULTIPLY));

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        int currentSelectionWrong;
        int currentSelection;

        initializeArray();
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
            case Constants.MODE_MEDIUM:
                // 33% chance of correct
                correctOrWrong = randomGenerator.nextInt(3);
                break;
            case Constants.MODE_HARD:
            case Constants.MODE_INSANE:
                // 25% chance of correct
                correctOrWrong = randomGenerator.nextInt(4);
                break;
        }

        float currentTextSize = context.getResources().getDimension(R.dimen._24ssp);
        float smallerTextSize = context.getResources().getDimension(R.dimen._18ssp);
        int _10dp = (int) context.getResources().getDimension(R.dimen._10sdp);


        // The current continent selected. Use this same number that is initialize ONCE to set up top and bottom text.
        currentSelection = randomGenerator.nextInt(continentArray.size());
        // initialise Once so the text is same for both top and bottom
        int currentSelectedText = randomGenerator.nextInt(continentArray.get(currentSelection).length - 1) + 1; // -1 + 1 to ensure first element is not called


        // Account for if currentSelection (which is continent, not country name) is last element of array.
        // If it is, then decrease curentSelectionWrong by 1. Otherwise just increment it by 1 to force a wrong number.
        if (currentSelection == continentArray.size() - 1) {
            // currentSelectionWrong is the wrong continent selected
            currentSelectionWrong = currentSelection - 1;
        } else {
            currentSelectionWrong = currentSelection + 1;
        }

        // bottomTextCountryName = the country text called.
        TextView bottomTextCountryName = new TextView(context);
        bottomTextCountryName.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);

        // call a random continent, then in the continent selected, get one of the word randomly in the array. -1 and +1 so that the result will be from 1 to the last element inclusive, excluding
        // the first element of index 0, since first element is the name of the continent itself.
        // Country is Always correct. Only the continent name is used to toggle correct or wrong
        bottomTextCountryName.setText(continentArray.get(currentSelection)[currentSelectedText]);
        bottomTextCountryName.setPadding(_10dp, 0, _10dp, 0);
        bottomTextCountryName.setGravity(Gravity.CENTER);
        bottomTextCountryName.setSingleLine();
        AutofitHelper.create(bottomTextCountryName);


        // bottomTextContinentName = i.e. Asia, Australia, Africa
        TextView bottomTextContinentName = new TextView(context);
        bottomTextContinentName.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallerTextSize);
        // Call the 0 index, i.e. First element of the Array to get the continent name
        if (correctOrWrong == 0) {
            // correct text
            bottomTextContinentName.setText("- " + continentArray.get(currentSelection)[0] + " -");
        } else {
            // set a wrong language name
            bottomTextContinentName.setText("- " + continentArray.get(currentSelectionWrong)[0] + " -");
        }
        bottomTextContinentName.setGravity(Gravity.CENTER_HORIZONTAL);
        bottomTextContinentName.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);

        RelativeLayout.LayoutParams bottomTextParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams bottomTextLanguageParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout bottomOverall = new LinearLayout(context);
        bottomOverall.setOrientation(LinearLayout.VERTICAL);
        bottomOverall.setGravity(Gravity.CENTER);

        bottomOverall.addView(bottomTextCountryName, bottomTextParam);
        bottomOverall.addView(bottomTextContinentName, bottomTextLanguageParam);

        RelativeLayout.LayoutParams bottomOverallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
        bottomOverallParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        parentLayout.addView(bottomOverall, bottomOverallParam);


        //// Top view ////

        TextView topTextCountryName = new TextView(context);
        topTextCountryName.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);

        topTextCountryName.setText(continentArray.get(currentSelection)[currentSelectedText]);
        topTextCountryName.setPadding(_10dp, 0, _10dp, 0);
        topTextCountryName.setGravity(Gravity.CENTER);
        topTextCountryName.setSingleLine();
        topTextCountryName.setRotation(180);
        AutofitHelper.create(topTextCountryName);


        TextView topTextContinentName = new TextView(context);
        topTextContinentName.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallerTextSize);
        // Call the 0 index, i.e. First element of the Array to get the language name
        if (correctOrWrong == 0) {
            // correct text
            topTextContinentName.setText("- " + continentArray.get(currentSelection)[0] + " -");
        } else {
            // set a wrong language name
            topTextContinentName.setText("- " + continentArray.get(currentSelectionWrong)[0] + " -");
        }
        topTextContinentName.setGravity(Gravity.CENTER_HORIZONTAL);
        topTextContinentName.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
        topTextContinentName.setRotation(180);

        RelativeLayout.LayoutParams topTextParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams topTextLanguageParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout topOverall = new LinearLayout(context);
        topOverall.setOrientation(LinearLayout.VERTICAL);
        topOverall.setGravity(Gravity.CENTER);

        topOverall.addView(topTextContinentName, topTextLanguageParam);
        topOverall.addView(topTextCountryName, topTextParam);

        RelativeLayout.LayoutParams topOverallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
        topOverallParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        parentLayout.addView(topOverall, topOverallParam);


        int textColor = Color.parseColor("#FEF5F8");
        bottomTextCountryName.setTypeface(Typeface.DEFAULT_BOLD);
        bottomTextCountryName.setTextColor(textColor);
        bottomTextContinentName.setTextColor(textColor);
        topTextCountryName.setTextColor(textColor);
        topTextCountryName.setTypeface(Typeface.DEFAULT_BOLD);
        topTextContinentName.setTextColor(textColor);


    }


    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_countryandcontinent);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_countryandcontinent;
    }

    @Override
    public boolean requireDefaultTimer() {
        return false;
    }

    @Override
    public boolean requireATimer() {
        return true;
    }

    @Override
    public boolean wantToShowTimer() {
        return true;
    }

    @Override
    public int customTimerDuration() {
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
            case Constants.MODE_MEDIUM:
                return 2000;
            case Constants.MODE_HARD:
                return 1500;
            case Constants.MODE_INSANE:
                return 1000;
            default:
                return 2000;
        }
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }


    @Override
    public boolean getCurrentQuestionAnswer() {
        return correctOrWrong == 0;
    }

    private void initializeArray() {
        // ** First element in array will be the continent name

        String[] asia = context.getResources().getStringArray(R.array.gameMode_countryAndContinent_Country_Asia);
        String[] africa = context.getResources().getStringArray(R.array.gameMode_countryAndContinent_Country_Africa);
        String[] oceania = context.getResources().getStringArray(R.array.gameMode_countryAndContinent_Country_Oceania);
        String[] northamerica = context.getResources().getStringArray(R.array.gameMode_countryAndContinent_Country_NorthAmerica);
        String[] southamerica = context.getResources().getStringArray(R.array.gameMode_countryAndContinent_Country_SouthAmerica);
        String[] europe = context.getResources().getStringArray(R.array.gameMode_countryAndContinent_Country_Europe);

        continentArray.add(asia);
        continentArray.add(africa);
        continentArray.add(oceania);
        continentArray.add(northamerica);
        continentArray.add(southamerica);
        continentArray.add(europe);
        // order of adding doesnt matter
    }
}
