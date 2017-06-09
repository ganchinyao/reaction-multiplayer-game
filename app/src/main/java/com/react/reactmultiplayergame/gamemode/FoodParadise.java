package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 *
 *
 * Insane = 1000ms timer, and 2 picture to see
 *
 * Accounted for translation and screensize
 */

public class FoodParadise extends GameMode {
    private int levelDifficulty;
    private ImageView bottomImage, topImage, bottomImage2, topImage2;
    private TextView bottomText, topText, bottomText2, topText2;

    private String [] textArray;
    private ArrayList<Integer> imageArray = new ArrayList<>();
    //private ArrayList<String> textArray = new ArrayList<>();
    // 0 = correct, 33% chance of correct for easy/medium, 25% for hard
    private int correctOrWrong;
    private String currentText;
    private int currentImageId;
    private Context context;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        if(levelDifficulty == Constants.MODE_INSANE) {
            player1Question.setText(R.string.foodparadise_insane);
            player2Question.setText(R.string.foodparadise_insane);
        }
        else {
            player1Question.setText(R.string.foodparadise);
            player2Question.setText(R.string.foodparadise);
        }
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.levelDifficulty = levelDifficulty;
        this.context = context;
        initialiseTextandImage();

        int currentRandomNumber;
        int textColor = Color.parseColor("#FF8C00");

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_foodparadise);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        int _30dp = (int) context.getResources().getDimension(R.dimen._30sdp);

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
            case Constants.MODE_MEDIUM:
                // 33% chance of correct for easy and medium
                correctOrWrong = randomGenerator.nextInt(3);
                break;
            case Constants.MODE_HARD:
                // 25% chance of correct for hard
                // 0 is ALWAYS the correct one
                correctOrWrong = randomGenerator.nextInt(4);
                break;
            case Constants.MODE_INSANE:
                correctOrWrong = randomGenerator.nextInt(4);
                break;
        }
        currentRandomNumber = randomGenerator.nextInt(imageArray.size());

        // Mode = easy/medium/hard
        if(levelDifficulty != Constants.MODE_INSANE) {
            if (correctOrWrong == 0) {
                // set correct resource, as current question is correct
                currentImageId = imageArray.get(currentRandomNumber);
                currentText = textArray[currentRandomNumber];
            } else {
                currentImageId = imageArray.get(currentRandomNumber);
                // account for case if currentRandomNumber points to last element of array, and subtract 1 away. Else, add 1 to it to purposely create wrong text
                if (currentRandomNumber == imageArray.size() - 1) {
                    currentRandomNumber = currentRandomNumber - 1;
                } else {
                    currentRandomNumber = currentRandomNumber + 1;
                }
                // text and image are now off-sync to create a situation of wrong
                currentText = textArray[currentRandomNumber];
            }

            bottomImage = new ImageView(context);
            Drawable background = ContextCompat.getDrawable(context, currentImageId);
            bottomImage.setBackground(background);


            RelativeLayout.LayoutParams layoutParamsBottomImage = new RelativeLayout.LayoutParams(parentLayout.getHeight() / 2 - _30dp, parentLayout.getHeight() / 2 - _30dp);

            bottomText = new TextView(context);
            bottomText.setText(currentText);
            bottomText.setPadding(_30dp, 0, 0, 0);
            bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._24ssp));
            bottomText.setTextColor(textColor);
            bottomText.setTypeface(Typeface.DEFAULT_BOLD);
            bottomText.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);

            LinearLayout bottomContainer = new LinearLayout(context);
            bottomContainer.setOrientation(LinearLayout.HORIZONTAL);

            bottomContainer.setGravity(Gravity.CENTER);
            bottomContainer.addView(bottomImage, layoutParamsBottomImage);
            bottomContainer.addView(bottomText);

            RelativeLayout.LayoutParams bottomContainerParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, parentLayout.getHeight() / 2);
            bottomContainerParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            bottomContainerParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
            parentLayout.addView(bottomContainer, bottomContainerParam);

            // Top view //
            topImage = new ImageView(context);
            topImage.setBackground(background);
            topImage.setRotation(180);

            RelativeLayout.LayoutParams layoutParamsTopImage = new RelativeLayout.LayoutParams(parentLayout.getHeight() / 2 - _30dp, parentLayout.getHeight() / 2 - _30dp);
            topText = new TextView(context);
            topText.setText(currentText);
            topText.setPadding(_30dp, 0, 0, 0);
            topText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._24ssp));
            topText.setTextColor(textColor);
            topText.setTypeface(Typeface.DEFAULT_BOLD);
            topText.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
            topText.setRotation(180);

            LinearLayout topContainer = new LinearLayout(context);
            topContainer.setOrientation(LinearLayout.HORIZONTAL);

            topContainer.setGravity(Gravity.CENTER);
            topContainer.addView(topText);
            topContainer.addView(topImage,layoutParamsTopImage);

            RelativeLayout.LayoutParams topContainerParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, parentLayout.getHeight() / 2);
            topContainerParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            topContainerParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
            parentLayout.addView(topContainer, topContainerParam);
            ////////
        }
        else {
            int _15dp = (int) context.getResources().getDimension(R.dimen._15sdp);
            // initialize for the sake of initializing. Will be changed at switch below
            int currentImageId2 = 0;
            String currentText2 = "";
            // currentRandomNumber and currentRandomNumber2 are BOTH CORRECT. Use currentRandomNumber_wrong/wrong2 for wrong int
            // initialize for the sake of initializing, but these 2 numbers will be change at the following if statement below
            int currentRandomNumber_wrong1 =0;
            int currentRandomNumber_wrong2 =0;
            int currentRandomNumber2 = randomGenerator.nextInt(imageArray.size());

            if(correctOrWrong != 0) {
                // initialise wrong number
                // account for case if currentRandomNumber points to last element of array, and subtract 1 away. Else, add 1 to it to purposely create wrong text
                if (currentRandomNumber == imageArray.size() - 1) {
                    currentRandomNumber_wrong1 = currentRandomNumber - 1;
                } else {
                    currentRandomNumber_wrong1 = currentRandomNumber + 1;
                }

                if(currentRandomNumber2 == imageArray.size() -1) {
                    currentRandomNumber_wrong2 = currentRandomNumber2 -1;
                } else {
                    currentRandomNumber_wrong2 = currentRandomNumber2 +1;
                }
            }

            switch(correctOrWrong) {
                case 0: // both correct
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray[currentRandomNumber];
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray[currentRandomNumber2];
                    break;
                case 1: // bottom left correct, bottom right wrong
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray[currentRandomNumber];
                    // image is correct, but get the wrong text. CANNOT USE both currentRandomNumber_wrong for both image and text, as this will means setting a
                    // correct text in-sync with the image!!
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray[currentRandomNumber_wrong2];
                    break;
                case 2: // bottom left wrong, bottom right correct
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray[currentRandomNumber_wrong1];
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray[currentRandomNumber2];
                    break;
                case 3: // both wrong
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray[currentRandomNumber_wrong1];
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray[currentRandomNumber_wrong2];
                    break;
            }


            bottomImage = new ImageView(context);
            Drawable background = ContextCompat.getDrawable(context, currentImageId);
            bottomImage.setBackground(background);

            bottomImage2 = new ImageView(context);
            Drawable background2 = ContextCompat.getDrawable(context, currentImageId2);
            bottomImage2.setBackground(background2);

            bottomText = new TextView(context);
            bottomText.setText(currentText);
            bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            bottomText.setTypeface(Typeface.DEFAULT_BOLD);
            bottomText.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
            bottomText.setTextColor(textColor);
            bottomText.setSingleLine();
            bottomText.setGravity(Gravity.CENTER_HORIZONTAL);
            AutofitHelper.create(bottomText);

            bottomText2 = new TextView(context);
            bottomText2.setText(currentText2);
            bottomText2.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            bottomText2.setTypeface(Typeface.DEFAULT_BOLD);
            bottomText2.setTextColor(textColor);
            bottomText2.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
            bottomText2.setSingleLine();
            bottomText2.setGravity(Gravity.CENTER_HORIZONTAL);
            AutofitHelper.create(bottomText2);

            RelativeLayout.LayoutParams bottomLeftImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _15dp, (parentLayout.getHeight() /2) /3*2 - _15dp);
            RelativeLayout.LayoutParams bottomLeftTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout bottomLeftLinearLayout = new LinearLayout(context);
            bottomLeftLinearLayout.setOrientation(LinearLayout.VERTICAL);
            bottomLeftLinearLayout.addView(bottomImage, bottomLeftImageParams);
            bottomLeftLinearLayout.addView(bottomText, bottomLeftTextParams);
            bottomLeftLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams bottomRightImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _15dp, (parentLayout.getHeight()/2)/3*2 - _15dp);
            RelativeLayout.LayoutParams bottomRightTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout bottomRightLinearLayout = new LinearLayout(context);
            bottomRightLinearLayout.setOrientation(LinearLayout.VERTICAL);
            bottomRightLinearLayout.addView(bottomImage2, bottomRightImageParams);
            bottomRightLinearLayout.addView(bottomText2, bottomRightTextParams);
            bottomRightLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams bottomLeftOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _15dp);
            RelativeLayout.LayoutParams bottomRightOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _15dp);
            LinearLayout bottomOverallLinearLayout = new LinearLayout(context);
            bottomOverallLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            bottomOverallLinearLayout.addView(bottomLeftLinearLayout, bottomLeftOverallParam);
            bottomOverallLinearLayout.addView(bottomRightLinearLayout, bottomRightOverallParam);
            bottomOverallLinearLayout.setGravity(Gravity.BOTTOM);

            RelativeLayout.LayoutParams bottomOverallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() /2);
            bottomOverallParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            parentLayout.addView(bottomOverallLinearLayout, bottomOverallParam);


            //// Top View ////
            topImage = new ImageView(context);
            topImage.setBackground(background);
            topImage.setRotation(180);

            topImage2 = new ImageView(context);
            topImage2.setBackground(background2);
            topImage2.setRotation(180);

            topText = new TextView(context);
            topText.setText(currentText);
            topText.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            topText.setTypeface(Typeface.DEFAULT_BOLD);
            topText.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
            topText.setTextColor(textColor);
            topText.setSingleLine();
            topText.setGravity(Gravity.CENTER_HORIZONTAL);
            topText.setRotation(180);
            AutofitHelper.create(topText);

            topText2 = new TextView(context);
            topText2.setText(currentText2);
            topText2.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            topText2.setTypeface(Typeface.DEFAULT_BOLD);
            topText2.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
            topText2.setTextColor(textColor);
            topText2.setSingleLine();
            topText2.setGravity(Gravity.CENTER_HORIZONTAL);
            topText2.setRotation(180);
            AutofitHelper.create(topText2);

            RelativeLayout.LayoutParams topLeftImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _15dp, (parentLayout.getHeight() /2) /3*2 - _15dp);
            RelativeLayout.LayoutParams topLeftTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout topLeftLinearLayout = new LinearLayout(context);
            topLeftLinearLayout.setOrientation(LinearLayout.VERTICAL);
            topLeftLinearLayout.addView(topText, topLeftTextParams);
            topLeftLinearLayout.addView(topImage, topLeftImageParams);
            topLeftLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams topRightImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _15dp, (parentLayout.getHeight()/2)/3*2 - _15dp);
            RelativeLayout.LayoutParams topRightTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout topRightLinearLayout = new LinearLayout(context);
            topRightLinearLayout.setOrientation(LinearLayout.VERTICAL);
            topRightLinearLayout.addView(topText2, topRightTextParams);
            topRightLinearLayout.addView(topImage2, topRightImageParams);
            topRightLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams topLeftOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _15dp);
            RelativeLayout.LayoutParams topRightOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _15dp);
            LinearLayout topOverallLinearLayout = new LinearLayout(context);
            topOverallLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            topOverallLinearLayout.addView(topRightLinearLayout, topRightOverallParam);
            topOverallLinearLayout.addView(topLeftLinearLayout, topLeftOverallParam);
            topOverallLinearLayout.setGravity(Gravity.TOP);

            RelativeLayout.LayoutParams topOverallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() /2);
            topOverallParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            parentLayout.addView(topOverallLinearLayout, topOverallParam);
            /////


        }

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_foodparadise);
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

        return R.raw.gamemode_foodparadise;
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
        if(levelDifficulty == Constants.MODE_EASY || levelDifficulty == Constants.MODE_MEDIUM) {
            return 2000;
        }
        else return 1000;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }


    @Override
    public boolean getCurrentQuestionAnswer() {
        return correctOrWrong==0;
    }

    private void initialiseTextandImage(){
        // ** When adding new image and text, remember to put the Image and Text relative to each other.
        // E.g. if place a new image at position 10, make sure place the text at position 10 of the textArray as well,
        // as this is how to class determines correct answer. setGameContent above has logic to set a wrong text for when the question is wrong
        imageArray.add(R.drawable.xapple);
        imageArray.add(R.drawable.xaubergine);
        imageArray.add(R.drawable.xavocado);
        imageArray.add(R.drawable.xbanana);
        imageArray.add(R.drawable.xbeer);
        imageArray.add(R.drawable.xblueberries);
        imageArray.add(R.drawable.xbread);
        imageArray.add(R.drawable.xbroccoli);
        imageArray.add(R.drawable.xcabbage);
        imageArray.add(R.drawable.xcake);
        imageArray.add(R.drawable.xcandy);
        imageArray.add(R.drawable.xcarrot);
        imageArray.add(R.drawable.xcheese);
        imageArray.add(R.drawable.xcherries);
        imageArray.add(R.drawable.xchickenleg);
        imageArray.add(R.drawable.xchili);
        imageArray.add(R.drawable.xchocolate);
        imageArray.add(R.drawable.xcookies);
        imageArray.add(R.drawable.xcorn);
        imageArray.add(R.drawable.xcrab);
        imageArray.add(R.drawable.xcupcake);
        imageArray.add(R.drawable.xdimsum);
        imageArray.add(R.drawable.xdoughnut);
        imageArray.add(R.drawable.xegg);
        imageArray.add(R.drawable.xfish);
        imageArray.add(R.drawable.xfrappe);
        imageArray.add(R.drawable.xfries);
        imageArray.add(R.drawable.xgingerbread);
        imageArray.add(R.drawable.xgrapes);
        imageArray.add(R.drawable.xhamburguer);
        imageArray.add(R.drawable.xicecream);
        imageArray.add(R.drawable.xkiwi);
        imageArray.add(R.drawable.xlemon);
        imageArray.add(R.drawable.xlobster);
        imageArray.add(R.drawable.xmartini);
        imageArray.add(R.drawable.xmineralwater);
        imageArray.add(R.drawable.xnoodles);
        imageArray.add(R.drawable.xonion);
        imageArray.add(R.drawable.xorange);
        imageArray.add(R.drawable.xpannacotta);
        imageArray.add(R.drawable.xpear);
        imageArray.add(R.drawable.xpineapple);
        imageArray.add(R.drawable.xpizza);
        imageArray.add(R.drawable.xpopcorn);
        imageArray.add(R.drawable.xpudding);
        imageArray.add(R.drawable.xrice);
        imageArray.add(R.drawable.xroastchicken);
        imageArray.add(R.drawable.xsandwich);
        imageArray.add(R.drawable.xsausage);
        imageArray.add(R.drawable.xsteak);
        imageArray.add(R.drawable.xstrawberry);
        imageArray.add(R.drawable.xsushi);
        imageArray.add(R.drawable.xtomato);
        imageArray.add(R.drawable.xwatermelon);

        textArray = context.getResources().getStringArray(R.array.gameMode_FoodParadise_FoodName);

    }

}
