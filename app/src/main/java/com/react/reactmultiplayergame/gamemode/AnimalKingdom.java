package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
 * Created by gan on 2/4/17.
 * Animal Kingdom is whereby user taps when the name of an animal matches the picture shown
 *
 * Insane = 1000sec timer, and 2 picture to see
 */

public class AnimalKingdom extends GameMode {
    private int levelDifficulty;
    private ImageView bottomImage, topImage, bottomImage2, topImage2;
    private TextView bottomText, topText, bottomText2, topText2;

    private ArrayList<Integer> imageArray = new ArrayList<>();
    private ArrayList<String> textArray = new ArrayList<>();
    // 0 = correct, 33% chance of correct for easy/medium, 25% for hard
    private int correctOrWrong;
    // 0 = correct, 1 2 3 wrong
    private int correctOrWrong_Insane = randomGenerator.nextInt(4);
    private String currentText;
    private int currentImageId;
    private int currentRandomNumber;
    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        if(levelDifficulty == Constants.MODE_INSANE) {
            player1Question.setText(R.string.animalkingdom_insane);
            player2Question.setText(R.string.animalkingdom_insane);
        }
        else {
            player1Question.setText(R.string.animalkingdom);
            player2Question.setText(R.string.animalkingdom);
        }
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {

        this.levelDifficulty = levelDifficulty;
        initialiseTextandImage();
        int r = randomGenerator.nextInt(255);
        int g = randomGenerator.nextInt(255);
        int b = randomGenerator.nextInt(255);
        int randomColor = Color.rgb(r,g,b);

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_animalkingdom);
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
        }
        currentRandomNumber = randomGenerator.nextInt(imageArray.size());

        // Mode = easy/medium/hard
        if(levelDifficulty != Constants.MODE_INSANE) {
            if (correctOrWrong == 0) {
                // set correct resourse as current question is correct
                currentImageId = imageArray.get(currentRandomNumber);
                currentText = textArray.get(currentRandomNumber);
            } else {
                currentImageId = imageArray.get(currentRandomNumber);
                // account for case if currentRandomNumber points to last element of array, and subtract 1 away. Else, add 1 to it to purposely create wrong text
                if (currentRandomNumber == imageArray.size() - 1) {
                    currentRandomNumber = currentRandomNumber - 1;
                } else {
                    currentRandomNumber = currentRandomNumber + 1;
                }
                // text and image are now off-sync to create a situation of wrong
                currentText = textArray.get(currentRandomNumber);
            }

            bottomImage = new ImageView(context);
            Drawable background = ContextCompat.getDrawable(context, currentImageId);
            background.setColorFilter(new PorterDuffColorFilter(randomColor, PorterDuff.Mode.SRC_ATOP));
            bottomImage.setBackground(background);


            RelativeLayout.LayoutParams layoutParamsBottomImage = new RelativeLayout.LayoutParams(parentLayout.getHeight() / 2 - _30dp, parentLayout.getHeight() / 2 - _30dp);

            bottomText = new TextView(context);
            bottomText.setText(currentText);
            bottomText.setTextColor(randomColor);
            bottomText.setPadding(_30dp, 0, 0, 0);
            bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._24ssp));
            bottomText.setTypeface(Typeface.DEFAULT_BOLD);
            bottomText.setShadowLayer(1.5f, -1, 1, Color.GRAY);

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
            topText.setTextColor(randomColor);
            topText.setPadding(_30dp, 0, 0, 0);
            topText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._24ssp));
            topText.setTypeface(Typeface.DEFAULT_BOLD);
            topText.setShadowLayer(1.5f, -1, 1, Color.GRAY);
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
            int r2 = randomGenerator.nextInt(255);
            int g2 = randomGenerator.nextInt(255);
            int b2 = randomGenerator.nextInt(255);
            int _10dp = (int) context.getResources().getDimension(R.dimen._10sdp);
            int randomColor2 = Color.rgb(r2,g2,b2);

            // initialize for the sake of initializing. Will be changed at switch below
            int currentImageId2 = 0;
            String currentText2 = "";
            // currentRandomNumber and currentRandomNumber2 are BOTH CORRECT. Use currentRandomNumber_wrong/wrong2 for wrong int
            // initialize for the sake of initializing, but these 2 numbers will be change at the following if statement below
            int currentRandomNumber_wrong1 =0;
            int currentRandomNumber_wrong2 =0;
            int currentRandomNumber2 = randomGenerator.nextInt(imageArray.size());

            if(correctOrWrong_Insane != 0) {
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

            switch(correctOrWrong_Insane) {
                case 0: // both correct
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray.get(currentRandomNumber);
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray.get(currentRandomNumber2);
                    break;
                case 1: // bottom left correct, bottom right wrong
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray.get(currentRandomNumber);
                    // image is correct, but get the wrong text. CANNOT USE both currentRandomNumber_wrong for both image and text, as this will means setting a
                    // correct text in-sync with the image!!
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray.get(currentRandomNumber_wrong2);
                    break;
                case 2: // bottom left wrong, bottom right correct
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray.get(currentRandomNumber_wrong1);
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray.get(currentRandomNumber2);
                    break;
                case 3: // both wrong
                    currentImageId = imageArray.get(currentRandomNumber);
                    currentText = textArray.get(currentRandomNumber_wrong1);
                    currentImageId2 = imageArray.get(currentRandomNumber2);
                    currentText2 = textArray.get(currentRandomNumber_wrong2);
                    break;
            }


            bottomImage = new ImageView(context);
            Drawable background = ContextCompat.getDrawable(context, currentImageId);
            background.setColorFilter(new PorterDuffColorFilter(randomColor, PorterDuff.Mode.SRC_ATOP));
            bottomImage.setBackground(background);

            bottomImage2 = new ImageView(context);
            Drawable background2 = ContextCompat.getDrawable(context, currentImageId2);
            background2.setColorFilter(new PorterDuffColorFilter(randomColor2, PorterDuff.Mode.SRC_ATOP));
            bottomImage2.setBackground(background2);

            bottomText = new TextView(context);
            bottomText.setText(currentText);
            bottomText.setTextColor(randomColor);
            bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            bottomText.setTypeface(Typeface.DEFAULT_BOLD);
            bottomText.setShadowLayer(1.5f, -1, 1, Color.GRAY);
            bottomText.setSingleLine();
            bottomText.setGravity(Gravity.CENTER_HORIZONTAL);
            AutofitHelper.create(bottomText);

            bottomText2 = new TextView(context);
            bottomText2.setText(currentText2);
            bottomText2.setTextColor(randomColor2);
            bottomText2.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            bottomText2.setTypeface(Typeface.DEFAULT_BOLD);
            bottomText2.setShadowLayer(1.5f, -1, 1, Color.GRAY);
            bottomText2.setSingleLine();
            bottomText2.setGravity(Gravity.CENTER_HORIZONTAL);
            AutofitHelper.create(bottomText2);

            RelativeLayout.LayoutParams bottomLeftImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _10dp, (parentLayout.getHeight() /2) /3*2 - _10dp);
            RelativeLayout.LayoutParams bottomLeftTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout bottomLeftLinearLayout = new LinearLayout(context);
            bottomLeftLinearLayout.setOrientation(LinearLayout.VERTICAL);
            bottomLeftLinearLayout.addView(bottomImage, bottomLeftImageParams);
            bottomLeftLinearLayout.addView(bottomText, bottomLeftTextParams);
            bottomLeftLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams bottomRightImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _10dp, (parentLayout.getHeight()/2)/3*2 - _10dp);
            RelativeLayout.LayoutParams bottomRightTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout bottomRightLinearLayout = new LinearLayout(context);
            bottomRightLinearLayout.setOrientation(LinearLayout.VERTICAL);
            bottomRightLinearLayout.addView(bottomImage2, bottomRightImageParams);
            bottomRightLinearLayout.addView(bottomText2, bottomRightTextParams);
            bottomRightLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams bottomLeftOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _10dp);
            RelativeLayout.LayoutParams bottomRightOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _10dp);
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
            topText.setTextColor(randomColor);
            topText.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            topText.setTypeface(Typeface.DEFAULT_BOLD);
            topText.setShadowLayer(1.5f, -1, 1, Color.GRAY);
            topText.setSingleLine();
            topText.setGravity(Gravity.CENTER_HORIZONTAL);
            topText.setRotation(180);
            AutofitHelper.create(topText);

            topText2 = new TextView(context);
            topText2.setText(currentText2);
            topText2.setTextColor(randomColor2);
            topText2.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen._22ssp));
            topText2.setTypeface(Typeface.DEFAULT_BOLD);
            topText2.setShadowLayer(1.5f, -1, 1, Color.GRAY);
            topText2.setSingleLine();
            topText2.setGravity(Gravity.CENTER_HORIZONTAL);
            topText2.setRotation(180);
            AutofitHelper.create(topText2);

            RelativeLayout.LayoutParams topLeftImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _10dp, (parentLayout.getHeight() /2) /3*2 - _10dp);
            RelativeLayout.LayoutParams topLeftTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout topLeftLinearLayout = new LinearLayout(context);
            topLeftLinearLayout.setOrientation(LinearLayout.VERTICAL);
            topLeftLinearLayout.addView(topText, topLeftTextParams);
            topLeftLinearLayout.addView(topImage, topLeftImageParams);
            topLeftLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams topRightImageParams = new RelativeLayout.LayoutParams((parentLayout.getHeight()/2)/3*2 - _10dp, (parentLayout.getHeight()/2)/3*2 - _10dp);
            RelativeLayout.LayoutParams topRightTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (parentLayout.getHeight()/2)/3);
            LinearLayout topRightLinearLayout = new LinearLayout(context);
            topRightLinearLayout.setOrientation(LinearLayout.VERTICAL);
            topRightLinearLayout.addView(topText2, topRightTextParams);
            topRightLinearLayout.addView(topImage2, topRightImageParams);
            topRightLinearLayout.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams topLeftOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _10dp);
            RelativeLayout.LayoutParams topRightOverallParam = new RelativeLayout.LayoutParams(parentLayout.getWidth()/2, parentLayout.getHeight() /2 - _10dp);
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
        return context.getString(R.string.dialog_animalkingdom);
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

        return R.raw.gamemode_animalkingdom;
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
        if(levelDifficulty == Constants.MODE_INSANE) {
            if(correctOrWrong_Insane == 0) return true;
            else return false;
        }
        else {
            if (correctOrWrong == 0) return true;
            else return false;
        }
    }

    private void initialiseTextandImage(){
        // ** When adding new image and text, remember to put the Image and Text relative to each other.
        // E.g. if place a new image at position 10, make sure place the text at position 10 of the textArray as well,
        // as this is how to class determines correct answer. setGameContent above has logic to set a wrong text for when the question is wrong
        imageArray.add(R.drawable.zalpaca);
        imageArray.add(R.drawable.zbat);
        imageArray.add(R.drawable.zbear);
        imageArray.add(R.drawable.zbeaver);
        imageArray.add(R.drawable.zbee);
        imageArray.add(R.drawable.zbeetle);
        imageArray.add(R.drawable.zbutterfly);
        imageArray.add(R.drawable.zcamel);
        imageArray.add(R.drawable.zcat);
        imageArray.add(R.drawable.zchameleon);
        imageArray.add(R.drawable.zcheetah);
        imageArray.add(R.drawable.zchick);
        imageArray.add(R.drawable.zclownfish);
        imageArray.add(R.drawable.zcow);
        imageArray.add(R.drawable.zcrab);
        imageArray.add(R.drawable.zcrocodile);
        imageArray.add(R.drawable.zdeer);
        imageArray.add(R.drawable.zdog);
        imageArray.add(R.drawable.zdolphin);
        imageArray.add(R.drawable.zduck);
        imageArray.add(R.drawable.zeagle);
        imageArray.add(R.drawable.zelephant);
        imageArray.add(R.drawable.zflamingo);
        imageArray.add(R.drawable.zfox);
        imageArray.add(R.drawable.zfrog);
        imageArray.add(R.drawable.zgiraffe);
        imageArray.add(R.drawable.zgoat);
        imageArray.add(R.drawable.zgorilla);
        imageArray.add(R.drawable.zhedgehog);
        imageArray.add(R.drawable.zhen);
        imageArray.add(R.drawable.zhippopotamus);
        imageArray.add(R.drawable.zhorse);
        imageArray.add(R.drawable.zjellyfish);
        imageArray.add(R.drawable.zkangaroo);
        imageArray.add(R.drawable.zkoala);
        imageArray.add(R.drawable.zlemur);
        imageArray.add(R.drawable.zlion);
        imageArray.add(R.drawable.zllama);
        imageArray.add(R.drawable.zlobster);
        imageArray.add(R.drawable.zmonkey);
        imageArray.add(R.drawable.zmoose);
        imageArray.add(R.drawable.zmouse);
        imageArray.add(R.drawable.zoctopus);
        imageArray.add(R.drawable.zostrich);
        imageArray.add(R.drawable.zowl);
        imageArray.add(R.drawable.zpanda);
        imageArray.add(R.drawable.zparrot);
        imageArray.add(R.drawable.zpelican);
        imageArray.add(R.drawable.zpenguin);
        imageArray.add(R.drawable.zpig);
        imageArray.add(R.drawable.zporcupine);
        imageArray.add(R.drawable.zprawn);
        imageArray.add(R.drawable.zrabbit);
        imageArray.add(R.drawable.zracoon);
        imageArray.add(R.drawable.zrhinoceros);
        imageArray.add(R.drawable.zseal);
        imageArray.add(R.drawable.zshark);
        imageArray.add(R.drawable.zsheep);
        imageArray.add(R.drawable.zsloth);
        imageArray.add(R.drawable.zsnail);
        imageArray.add(R.drawable.zsnake);
        imageArray.add(R.drawable.zspider);
        imageArray.add(R.drawable.zsquid);
        imageArray.add(R.drawable.zsquirrel);
        imageArray.add(R.drawable.zstarfish);
        imageArray.add(R.drawable.zstingray);
        imageArray.add(R.drawable.zswan);
        imageArray.add(R.drawable.ztiger);
        imageArray.add(R.drawable.zturtle);
        imageArray.add(R.drawable.zwalrus);
        imageArray.add(R.drawable.zwhale);
        imageArray.add(R.drawable.zzebra);

        textArray.add("Alpaca");
        textArray.add("Bat");
        textArray.add("Bear");
        textArray.add("Beaver");
        textArray.add("Bee");
        textArray.add("Beetle");
        textArray.add("Butterfly");
        textArray.add("Camel");
        textArray.add("Cat");
        textArray.add("Chameleon");
        textArray.add("Cheetah");
        textArray.add("Chick");
        textArray.add("Clown Fish");
        textArray.add("Cow");
        textArray.add("Crab");
        textArray.add("Crocodile");
        textArray.add("Deer");
        textArray.add("Dog");
        textArray.add("Dolphin");
        textArray.add("Duck");
        textArray.add("Eagle");
        textArray.add("Elephant");
        textArray.add("Flamingo");
        textArray.add("Fox");
        textArray.add("Frog");
        textArray.add("Giraffe");
        textArray.add("Goat");
        textArray.add("Gorilla");
        textArray.add("Hedgehog");
        textArray.add("Hen");
        textArray.add("Hippopotamus");
        textArray.add("Horse");
        textArray.add("Jellyfish");
        textArray.add("Kangaroo");
        textArray.add("Koala");
        textArray.add("Lemur");
        textArray.add("Lion");
        textArray.add("Llama");
        textArray.add("Lobster");
        textArray.add("Monkey");
        textArray.add("Moose");
        textArray.add("Mouse");
        textArray.add("Octopus");
        textArray.add("Ostrich");
        textArray.add("Owl");
        textArray.add("Panda");
        textArray.add("Parrot");
        textArray.add("Pelican");
        textArray.add("Penguin");
        textArray.add("Pig");
        textArray.add("Porcupine");
        textArray.add("Prawn");
        textArray.add("Rabbit");
        textArray.add("Raccoon");
        textArray.add("Rhinoceros");
        textArray.add("Seal");
        textArray.add("Shark");
        textArray.add("Sheep");
        textArray.add("Sloth");
        textArray.add("Snail");
        textArray.add("Snake");
        textArray.add("Spider");
        textArray.add("Squid");
        textArray.add("Squirrel");
        textArray.add("Starfish");
        textArray.add("Stingray");
        textArray.add("Swan");
        textArray.add("Tiger");
        textArray.add("Turtle");
        textArray.add("Walrus");
        textArray.add("Whale");
        textArray.add("Zebra");

    }

}



