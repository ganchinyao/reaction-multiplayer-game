package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Html;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

/**
 * Created by gan on 7/4/17.
 *
 * // tap when the converted unit is correct
 *
 * Insane = original number is a double, others all start with Int.
 */

public class UnitConversion extends GameMode{
    private int levelDifficulty;
    private double originalNumber, finalNumber;
    private ArrayList<String[]> unitsArray = new ArrayList<>();
    private Converter converter = new Converter();
    // 0 = correct
    private int correctOrWrong;
    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.unitconversion);
        player2Question.setText(R.string.unitconversion);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        this.levelDifficulty = levelDifficulty;

        // Set customize gameContent background
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pattern_numbercomparison);
        if(bitmap!= null) {
            CurvedAndTiledDrawable drawable = new CurvedAndTiledDrawable(context, bitmap);
            drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#00695C"), PorterDuff.Mode.MULTIPLY));

            // add a new view instead of setting parentLayout.addView so that gameContent.removeAllView will auto remove this background when new game mode starts
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(drawable);
            parentLayout.addView(view);
        }

        initiliaze();
        int currentSelection, randomArrayIndexAfter_Wrong, randomArrayIndexBefore, randomArrayIndexAfter; // randomArrayIndex takes into account the different elements in array to ensure no IndexOutOfBounds
        int _20dp = (int) context.getResources().getDimension(R.dimen._20sdp);
        String originalUnit = "";
        String finalUnit = "";
        String finalUnitWrong;

        switch (levelDifficulty) {
            case Constants.MODE_EASY:
                correctOrWrong = randomGenerator.nextInt(3);
                originalNumber = 1;
                // higher the additionalValue = easier to guess since difference is huge. MUST add at least +1 at the end so that 0 will not be a value. 0 cannot be a result because correctAnswer + 0 is still correctAnswer, not wrongAnswer
                break;
            case Constants.MODE_MEDIUM:
                // 33% chance of correct
                correctOrWrong = randomGenerator.nextInt(3);
                originalNumber =  randomGenerator.nextInt(2) + 1; // dontwan original number to be 0
                break;
            case Constants.MODE_HARD:
                correctOrWrong = randomGenerator.nextInt(4);
                originalNumber = randomGenerator.nextInt(10) +5;
                break;
            case Constants.MODE_INSANE:
                // 25% chance of correct
                DecimalFormat df = new DecimalFormat("0.00");
                correctOrWrong = randomGenerator.nextInt(4);
                originalNumber = Double.parseDouble(df.format(randomGenerator.nextInt(101) + randomGenerator.nextDouble() + 20));
                break;
        }


        // initialise only once so that top and bottom produce same text.
        currentSelection = randomGenerator.nextInt(unitsArray.size());
        // randomArrayIndex takes into account the various size of the String [] in unitsArray
        // Before = the unit chosen at left side of eqn. After = the unit at right side of Eqn.
        // These units are used to toggle in the SAME unitArray to convert between different elements of unitsArray.
        // NOT to choose other unitsArray because they are grouped according to their logic. E.g. 2 degree = 3km -> Illogical
        randomArrayIndexBefore = randomGenerator.nextInt(unitsArray.get(currentSelection).length -1) +1; // -1 +1 to ensure First element is NOT called, as First element is not a unit

        while(true){
            // continuously loop to ensure final unit is not same as original unit
            randomArrayIndexAfter = randomGenerator.nextInt(unitsArray.get(currentSelection).length -1) +1;
            if(randomArrayIndexAfter != randomArrayIndexBefore) break;
        }

        while(true){
            // continuously loop to ensure randomIndexWrong is not equals to randomUnitBefore and After
            randomArrayIndexAfter_Wrong = randomGenerator.nextInt(unitsArray.get(currentSelection).length -1) +1;
            if(randomArrayIndexAfter_Wrong != randomArrayIndexBefore && randomArrayIndexAfter_Wrong != randomArrayIndexAfter) break;
        }

        originalUnit = unitsArray.get(currentSelection)[randomArrayIndexBefore];
        finalUnit = unitsArray.get(currentSelection)[randomArrayIndexAfter];
        finalUnitWrong = unitsArray.get(currentSelection)[randomArrayIndexAfter_Wrong];

        String textToShow = "";
        if(correctOrWrong ==0) {
            // show correct conversion
            calculateFinalAnswer(unitsArray.get(currentSelection)[0], originalUnit, finalUnit); // Initialize the var finalNumber
            textToShow = originalNumber + "\u00A0 " + "<i>" + originalUnit + "</i>"   +"\u00A0\u00A0=\u00A0\u00A0" + finalNumber + "\u00A0 " +  "<i>" + finalUnit + "</i>";
        } else {
            // purposely show wrong conversion number
            calculateFinalAnswer(unitsArray.get(currentSelection)[0], originalUnit, finalUnitWrong); // Initiliaze the var finalNumber. finalNumber now returns a value from false final unit
            textToShow = originalNumber + "\u00A0 " + "<i>" + originalUnit + "</i>" +"\u00A0\u00A0=\u00A0\u00A0" + finalNumber + "\u00A0 " + "<i>" + finalUnit + "</i>";
        }

        // bottomText = the foregin language text called.
        TextView bottomText = new TextView(context);
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._22ssp));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            bottomText.setText(Html.fromHtml(textToShow,Html.FROM_HTML_MODE_LEGACY));
        } else {
            bottomText.setText(Html.fromHtml(textToShow));
        }
        bottomText.setPadding(_20dp, 0, _20dp, 0);
        bottomText.setGravity(Gravity.CENTER);
        bottomText.setSingleLine();
        AutofitHelper.create(bottomText);

        RelativeLayout.LayoutParams bottomTextParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight()/2);
        bottomTextParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        parentLayout.addView(bottomText, bottomTextParam);

        //// Top view ////
        TextView topText = new TextView(context);
        topText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen._22ssp));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            topText.setText(Html.fromHtml(textToShow,Html.FROM_HTML_MODE_LEGACY));
        } else {
            topText.setText(Html.fromHtml(textToShow));
        }
        topText.setPadding(_20dp, 0, _20dp, 0);
        topText.setGravity(Gravity.CENTER);
        topText.setSingleLine();
        topText.setRotation(180);
        AutofitHelper.create(topText);

        RelativeLayout.LayoutParams topTextParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight()/2);
        topTextParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        parentLayout.addView(topText, topTextParam);

        if(levelDifficulty == Constants.MODE_HARD || levelDifficulty == Constants.MODE_INSANE) {
            // change color if mode is hard/ insane
            String colorArray [] = {
                    // can add or remove elements without breaking code
                    "#FFEB3B", // yellow
                    "#A7FFEB", // light teal
                    "#18FFFF", // cyan
                    "#FF80AB", // pink
                    "#E0E0E0", // light gray
                    "#FF9800", // orange
                    "#69F0AE", // light green
                    "#80DEEA", // light blue
                    "#B39DDB", // light purple
                    "#ECEFF1", // light blue gray
            };

            int textColor = Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]);
            bottomText.setTextColor(textColor);
            topText.setTextColor(textColor);
        } else {
            // easy and medium one fix white color
            bottomText.setTextColor(Color.parseColor("#ECEFF1"));
            topText.setTextColor(Color.parseColor("#ECEFF1"));
        }

    }

    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_unitconversion);
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
        return R.raw.gamemode_unitconversion;
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
        return 2000;
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {

    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        if(correctOrWrong ==0) return true;
        else return false;
    }

    private void initiliaze(){
        // can have various length array, as code in setGameContent has taken into account the varying array length.
        // ** FIRST element of the array MUST BE a string to call the method to convert, via a switch statement in setGameContent.
        // ** If add new String [], remember to update the switch statement in calculateFinalAnswer to direct the route to call the respective method to convert unit
        // DONT change the name of all the First element here, as they are link to the switch statement in calculateFinalAnswer
        String [] lengthConverter = {"lengthConverter" ,"in", "ft", "yd", "Miles", "mm", "cm", "m", "km" };
        String [] volumeConverter = {"volumeConverter", "mL", "L", "kL", "Pint", "Quart", "Gallon" , "Cup"};
        String [] massConverter = {"massConverter" ,"lb", "kg", "g", "mg"};
        String [] temperatureConverter = {"temperatureConverter" ,"??C", "??F", "K"};
        // add conversion for Pa
        String [] pressureConverter = {"pressureConverter", "Torr", "atm", "mmHg", "bar"};
        String [] timeConverter = {"timeConverter", "sec", "min", "hrs", "Days", "Weeks", "Months"};

        unitsArray.add(lengthConverter);
        unitsArray.add(volumeConverter);
        unitsArray.add(massConverter);
        unitsArray.add(temperatureConverter);
        unitsArray.add(pressureConverter);
        unitsArray.add(timeConverter);
        // order doesn't matter
    }

    private void calculateFinalAnswer(String whatConverterMethodToCall, String originalUnit, String finalUnit) {
        // to 2 d.p
        DecimalFormat df = new DecimalFormat("0.000000");
        switch (whatConverterMethodToCall) {
            // make sure Param whatConverterMethodToCall passed is the [0] element of unitsArray
            // use to direct the path to the correct method for converting based on different types of conversion required
            case "lengthConverter":
                finalNumber =  Double.parseDouble(df.format(converter.lengthConvert(originalNumber, originalUnit, finalUnit)));
                break;
            case "volumeConverter":
                finalNumber =  Double.parseDouble(df.format(converter.volumeConvert(originalNumber, originalUnit, finalUnit)));
                break;
            case "massConverter":
                finalNumber =  Double.parseDouble(df.format(converter.massConvert(originalNumber, originalUnit, finalUnit)));
                break;
            case "temperatureConverter":
                finalNumber =  Double.parseDouble(df.format(converter.tempConvert(originalNumber, originalUnit, finalUnit)));
                break;
            case "pressureConverter":
                finalNumber =  Double.parseDouble(df.format(converter.pressureConvert(originalNumber, originalUnit, finalUnit)));
                break;
            case "timeConverter":
                finalNumber =  Double.parseDouble(df.format(converter.timeConvert(originalNumber, originalUnit, finalUnit)));
                break;
        }
    }

/*
 * Modifications:
 * 9/18/2015 by Marc Kuniansky, Created the class and implemented the lengthConvert method.
 * After the implementation of the lengthConvert, we stopped adding modification tags- because this was a copy/paste/edit job,
 * no major changes were made that we felt the need to document.
 */
    /**
     * This class contains all of the code for the conversions that take place. Each conversion
     * has its own method, and is only called by one activity.
     *
     * Created by Marc Kuniansky on September 17, 2015
     *
     * @author Marc Kuniansky and Noah Getz
     */
    class Converter
    { // Begin converter class

        //Constructor
        /**
         * Constructor with no parameters
         */
        public Converter()
        {
            //Can just be empty, there are no global variables
        }

        /**
         * Takes a number, a length unit, and a new unit and converts the number to the new unit.
         *
         * Accepted units: inches, feet, yards, miles, millimeters, centimeters, meters, kilometers
         * Marc Kuniansky
         * @param originalNum must be a valid double.
         * @param originalUnit must be a string
         * @param desiredUnit must be a string
         * @return the new double
         */
        public double lengthConvert(double originalNum, String originalUnit, String desiredUnit)
        {
            //Make two variable doubles, one the original double and one the new one
            double num1 = originalNum;
            double num2 = 0.0d;

            //Store the units into new strings. I find this to be safer, as I can't override the originals this way.
            //Also convert them to lower case
            String original = originalUnit.toLowerCase();
            String newU = desiredUnit.toLowerCase();

            //Now there is a series of if statements to check which units are being converted from/to and
            //to do the proper operation
            switch(original)
            {
                case "in":
                { //Begin converting from inches
                    switch(newU)
                    {
                        case "in":
                            num2 = num1;
                            break;
                        case "ft":
                            //12 inches in a foot, so divide by 12
                            num2 = num1 / 12.0d;
                            break;
                        case "yd":
                            //36 inches in a yard, so divide by 36
                            num2 = num1 / 36.0d;
                            break;
                        case "miles":
                            //63,360 inches in a mile
                            num2 = num1 / 63360.0d;
                            break;
                        case "mm":
                            //There are 25.4 millimeters in an inch, so multiply inches by 25.4
                            num2 = num1 * 25.4d;
                            break;
                        case "cm":
                            //2.54cm in an inch, multiply inches by 2.54
                            num2 = num1 * 2.54d;
                            break;
                        case "m":
                            //0.0254 meters to an inch, so multiply inches by 0.0254
                            num2 = num1 * 0.0254d;
                            break;
                        case "km":
                            //0.0000254km in an inch
                            num2 = num1 * 0.0000254d;
                            break;
                    }
                    break;
                } //End converting from inches

                //Next, converting from feet
                case "ft":
                { //Begin conversions from feet
                    switch(newU)
                    {
                        case "in":
                            //12 inches in a foot, multiply by 12
                            num2 = num1*12.0d;
                            break;
                        case "ft":
                            num2 = num1;
                            break;
                        case "yd":
                            //3 feet in a yard
                            num2 = num1/3.0d;
                            break;
                        case "miles":
                            //5,280 feet in a mile
                            num2 = num1/5280.0d;
                            break;
                        case "mm":
                            //304.8 millimeters in a foot
                            num2 = num1*304.8d;
                            break;
                        case "cm":
                            //30.48 Centimeters in a foot
                            num2 = num1*30.48d;
                            break;
                        case "m":
                            //0.3048 meters in a foot
                            num2 = num1*0.3048d;
                            break;
                        case "km":
                            //0.0003048 kilometers in a meter
                            num2 = num1*0.0003048d;
                            break;
                    }
                    break;
                } //End conversions from feet

                //Next, conversions from yards
                case "yd":
                { //Begin conversions from yards
                    switch(newU) {
                        case "in":
                            //36 inches in a yard
                            num2 = num1 * 36.0d;
                            break;
                        case "ft":
                            //3 feet to a yard
                            num2 = num1 * 3.0d;
                            break;
                        case "yd":
                            num2 = num1;
                            break;
                        case "miles":
                            //1760 yards in a mile
                            num2 = num1 / 1760.0d;
                            break;
                        case "mm":
                            //914.4 millimeters in a yard
                            num2 = num1 * 914.4d;
                            break;
                        case "cm":
                            //91.44 centimeters in a yard
                            num2 = num1*91.44d;
                            break;
                        case "m":
                            num2 = num1*0.9144d;
                            break;
                        case "km":
                            //1,093.61 yards in a kilometer
                            num2 = num1/1093.61d;
                            break;
                    }
                    break;
                } //End conversions from yards

                //Next, convert from miles
                case "miles":
                { //Begin conversions from miles
                    switch(newU)
                    {
                        case "in":
                            //6330 inches in a mile
                            num2 = num1*6330.0d;
                            break;
                        case "ft":
                            //5280 feet in a mile
                            num2 = num1*5280.0d;
                            break;
                        case "yd":
                            //1760 yards in a mile
                            num2 = num1*1760.0d;
                            break;
                        case "miles":
                            num2 = num1;
                            break;
                        case "mm":
                            //1,609,000 millimeters in a mile
                            num2 = num1*1609340.0d;
                            break;
                        case "cm":
                            //16,0934 centimeters in a miles
                            num2 = num1*160934.0d;
                            break;
                        case "m":
                            //1609.34 meters in a mile
                            num2 = num1*1609.34d;
                            break;
                        case "km":
                            //1.60934 kilometers in a mile
                            num2 = num1*1.60934d;
                            break;
                    }
                    break;
                } //End converting from miles

                //Next, convert from millimeters
                case "mm":
                { //Begin converting from millimeters
                    switch(newU)
                    {
                        case "in":
                            num2 = num1*25.4d;
                            break;
                        case "ft":
                            num2 = num1/304.8d;
                            break;
                        case "yd":
                            num2 = num1/914.4d;
                            break;
                        case "miles":
                            num2 = num1/1609000.0d;
                            break;
                        case "mm":
                            num2 = num1;
                            break;
                        case "cm":
                            num2 = metricConvert(num1, "milli", "centi");
                            break;
                        case "m":
                            num2 = metricConvert(num1, "milli", "unit");
                            break;
                        case "km":
                            num2 = metricConvert(num1, "milli", "kilo");
                            break;
                    }
                    break;
                } //End converting from millimeters

                //Next, convert from centimeters
                case "cm":
                { //Begin converting from centimeters
                    switch(newU)
                    {
                        case "in":
                            num2 = num1 / 2.54d;
                            break;
                        case "ft":
                            num2 = num1 / 30.48d;
                            break;
                        case "yd":
                            num2 = num1 / 91.44d;
                            break;
                        case "miles":
                            num2 = num1/160934.0d;
                            break;
                        case "mm":
                            num2 = metricConvert(num1, "centi", "milli");
                            break;
                        case "cm":
                            num2 = num1;
                            break;
                        case "m":
                            num2 = metricConvert(num1, "centi", "unit");
                            break;
                        case "km":
                            num2 = metricConvert(num1, "centi", "kilo");
                            break;
                    }
                    break;
                } //End converting from centimeters

                //Next, convert from meters
                case "m":
                { //Begin converting from meters
                    switch(newU) {
                        case "in":
                            num2 = num1 * 39.3701d;
                            break;
                        case "ft":
                            num2 = num1 * 3.28084d;
                            break;
                        case "yd":
                            num2 = num1*1.09361d;
                            break;
                        case "miles":
                            num2 = num1/1609.34d;
                            break;
                        case "mm":
                            num2 = metricConvert(num1, "unit", "milli");
                            break;
                        case "cm":
                            num2 = metricConvert(num1, "unit", "centi");
                            break;
                        case "m":
                            num2 = num1;
                            break;
                        case "km":
                            num2 = this.metricConvert(num1, "unit", "kilo");
                            break;
                    }
                    break;
                } //End converting from meters

                //Finally, try converting from kilometers
                case "km":
                { //Begin converting from kilometers
                    switch(newU)
                    {
                        case "in":
                            num2 = num1*39370.1d;
                            break;
                        case "ft":
                            num2 = num1*3280.84d;
                            break;
                        case "yd":
                            num2 = num1*1093.61d;
                            break;
                        case "miles":
                            num2 = num1/1.60934d;
                            break;
                        case "mm":
                            num2 = metricConvert(num1, "kilo", "milli");
                            break;
                        case "cm":
                            num2 = metricConvert(num1, "kilo", "centi");
                            break;
                        case "m":
                            num2 = metricConvert(num1, "kilo", "unit");
                            break;
                        case "km":
                            num2 = num1;
                            break;
                    }
                    break;
                } //End converting from kilometers
            }

            //num2 is the number we want; return it
            return num2;
        } // End lengthConvert

        /**
         * Takes a number, a temperature unit, and a new unit and converts the number to the new unit.
         *
         * Accepted units: Fahrenheit, Kelvin, Celsius
         * Noah Getz
         * @param originalNum must be a valid double.
         * @param originalUnit must be a string
         * @param desiredUnit must be a string
         * @return the new double
         */
        public double tempConvert(double originalNum, String originalUnit, String desiredUnit)
        { //Begin tempConvert
            //Make two variable doubles, one the original double and one the new one
            double num2 = 0.0d;
            double num3;

            //Store the units into new strings. I find this to be safer, as I can't override the originals this way.
            //Also convert them to lower case
            String original = originalUnit;
            String newU = desiredUnit;

            switch(original)
            { //Begin conversion table
                case "??C":
                { //Begin converting from Celsius
                    switch(newU)
                    {
                        case "??C":
                            num2 = originalNum;
                            break;
                        case "??F":
                            num2 = (originalNum*(9.0/5.0)) + 32d;
                            break;
                        case "K":
                            num2 = originalNum + 273.15d;
                            break;
                    }
                    break;
                } //end converting from Celsius
                case "??F":
                { //Begin converting from Fahrenheit
                    switch(newU)
                    {
                        case "??C":
                            num3 = (-originalNum - 32d);
                            num2 = num3 * (5.0/9.0);
                            break;
                        case "??F":
                            num2 = originalNum;
                            break;
                        case "K":
                            num2 = ((originalNum - 32d)*(5.0/9.0)) + 273.15d;
                            break;
                    }
                    break;
                } //End converting from Fahrenheit
                case "K":
                { //Begin converting from Kelvin
                    switch(newU)
                    {
                        case "??C":
                            num2 = originalNum - 273.15d;
                            break;
                        case "??F":
                            num2 = ((originalNum - 273.15d) *(9.0/5.0)) + 32d;
                            break;
                        case "K":
                            num2 = originalNum;
                            break;
                    }
                    break;
                }
            } //End conversion table

            //Return the final number, num2
            return num2;
        } //End tempConvert

        /**
         * Takes a number, a volume unit, and a new unit and converts the number to the new unit.
         *
         * Accepted units: Milliliter, liter, kiloliter, pint, quart, gallon, cup.
         *
         * Noah Getz
         * @param originalNum must be a valid double.
         * @param originalUnit must be a string
         * @param desiredUnit must be a string
         * @return the new double
         */
        public double volumeConvert(double originalNum, String originalUnit, String desiredUnit)
        { //Begin volumeConvert

            //Make two variable doubles, one the original double and one the new one
            double num1 = originalNum;
            double num2 = 0.0d;

            //Store the units into new strings. I find this to be safer, as I can't override the originals this way.
            //Also convert them to lower case
            String original = originalUnit.toLowerCase();
            String newU = desiredUnit.toLowerCase();

            switch(original)
            { //Begin conversion table
                case "ml":
                    switch(newU)
                    {
                        case "ml":
                            num2 = num1;
                            break;
                        case "l":
                            num2 = metricConvert(num1, "milli", "unit");
                            break;
                        case "kl":
                            num2 = metricConvert(num1, "milli", "kilo");
                            break;
                        case "pint":
                            num2 = originalNum * .00211338d;
                            break;
                        case "quart":
                            num2 = originalNum * .00105669d;
                            break;
                        case "gallon":
                            num2 = originalNum * .000264172d;
                            break;
                        case "cup":
                            num2 = originalNum * .00422675d;
                            break;
                    }
                    break;

                case "l":
                    switch(newU)
                    {
                        case "ml":
                            num2 = metricConvert(num1, "unit", "milli");
                            break;
                        case "l":
                            num2 = num1;
                            break;
                        case "kl":
                            num2 = metricConvert(num1, "unit", "kilo");
                            break;
                        case "pint":
                            num2 = originalNum * 2.11338d;
                            break;
                        case "quart":
                            num2 = originalNum * 1.05669d;
                            break;
                        case "gallon":
                            num2 = originalNum * 0.264172d;
                            break;
                        case "cup":
                            num2 = originalNum * 4.22675d;
                            break;
                    }
                    break;

                case "kl":
                    switch(newU)
                    {
                        case "ml":
                            num2 = metricConvert(num1, "kilo", "milli");
                            break;
                        case "l":
                            num2 = metricConvert(num1, "kilo", "unit");
                            break;
                        case "kl":
                            num2 = num1;
                            break;
                        case "pint":
                            num2 = originalNum * 2113.38d;
                            break;
                        case "quart":
                            num2 = originalNum * 1056.69d;
                            break;
                        case "gallon":
                            num2 = originalNum * 264.172d;
                            break;
                        case "cup":
                            num2 = originalNum * 4226.75d;
                            break;
                    }
                    break;

                case "pint":
                    switch(newU)
                    {
                        case "ml":
                            num2 = originalNum * 473.176d;
                            break;
                        case "l":
                            num2 = originalNum * 0.473176d;
                            break;
                        case "kl":
                            num2 = originalNum * .00047176d;
                            break;
                        case "pint":
                            num2 = num1;
                            break;
                        case "quart":
                            num2 = originalNum * .5d;
                            break;
                        case "gallon":
                            num2 = originalNum * .125d;
                            break;
                        case "cup":
                            num2 = originalNum * 2.0d;
                            break;
                    }
                    break;

                case "quart":
                    switch(newU)
                    {
                        case "ml":
                            num2 = originalNum * 946.353d;
                            break;
                        case "l":
                            num2 = originalNum * 0.946353d;
                            break;
                        case "kl":
                            num2 = originalNum * .000946353d;
                            break;
                        case "pint":
                            num2 = originalNum * 2.0d;
                            break;
                        case "quart":
                            num2 = num1;
                            break;
                        case "gallon":
                            num2 = originalNum * .25d;
                            break;
                        case "cup":
                            num2 = originalNum * 4.0d;
                            break;
                    }
                    break;

                case "gallon":
                    switch(newU)
                    {
                        case "ml":
                            num2 = originalNum * 3785.41d;
                            break;
                        case "l":
                            num2 = originalNum * 3.78541d;
                            break;
                        case "kl":
                            num2 = originalNum * .00378541d;
                            break;
                        case "pint":
                            num2 = originalNum * 8.0d;
                            break;
                        case "quart":
                            num2 = originalNum * 4.0d;
                            break;
                        case "gallon":
                            num2 = num1;
                            break;
                        case "cup":
                            num2 = originalNum * 16.0d;
                            break;
                    }
                    break;

                case "cup":
                    switch(newU)
                    {
                        case "ml":
                            num2 = originalNum * 236.588d;
                            break;
                        case "l":
                            num2 = originalNum * 0.236588d;
                            break;
                        case "kl":
                            num2 = originalNum * .000236588d;
                            break;
                        case "pint":
                            num2 = originalNum * .5d;
                            break;
                        case "quart":
                            num2 = originalNum * .25d;
                            break;
                        case "gallon":
                            num2 = originalNum * .0625d;
                            break;
                        case "cup":
                            num2 = originalNum;
                            break;
                    }
                    break;
            } //End conversion table
            //Return the final number, num2
            return num2;
        } //End volume convert

        /**
         * Takes a number, a mass unit, and a new unit and converts the number to the new unit.
         *
         * Accepted units: pounds, kilograms, grams, milligrams
         * Noah Getz
         * @param originalNum must be a valid double.
         * @param originalUnit must be a string
         * @param desiredUnit must be a string
         * @return the new double
         */
        public double massConvert(double originalNum, String originalUnit, String desiredUnit)
        { //Begin massConvert
            //Make two variable doubles, one the original double and one the new one
            double num1 = originalNum;
            double num2 = 0.0d;


            //Store the units into new strings. I find this to be safer, as I can't override the originals this way.
            //Also convert them to lower case
            String original = originalUnit.toLowerCase();
            String newU = desiredUnit.toLowerCase();

            switch(original)
            { //Begin conversion table
                case "lb":
                    switch(newU)
                    {
                        case "lb":
                            num2 = originalNum;
                            break;
                        case "kg":
                            num2 = originalNum * 0.453592d;
                            break;
                        case "g":
                            num2 = originalNum * 453.592d;
                            break;
                        case "mg":
                            num2 = originalNum * 453592.0d;
                            break;
                    }
                    break;
                case "kg":
                    switch(newU)
                    {
                        case "lb":
                            num2 = originalNum * 2.20462d;
                            break;
                        case "kg":
                            num2 = originalNum;
                            break;
                        case "g":
                            num2 = metricConvert(num1, "kilo", "unit");
                            break;
                        case "mg":
                            num2 = metricConvert(num1, "kilo", "milli");
                            break;
                    }
                    break;
                case "g":
                    switch(newU)
                    {
                        case "lb":
                            num2 = originalNum * 0.00220462d;
                            break;
                        case "kg":
                            num2 = metricConvert(num1, "unit", "kilo");
                            break;
                        case "g":
                            num2 = originalNum;
                            break;
                        case "mg":
                            num2 = metricConvert(num1, "unit", "milli");
                            break;
                    }
                    break;
                case "mg":
                    switch(newU)
                    {
                        case "lb":
                            num2 = originalNum * .0000022046d;
                            break;
                        case "kg":
                            num2 = metricConvert(num1, "milli", "kilo");
                            break;
                        case "g":
                            num2 = metricConvert(num1, "milli", "unit");
                            break;
                        case "mg":
                            num2 = originalNum;
                            break;
                    }
                    break;
            } //End conversion table

            //Return the final number
            return num2;
        } //End massConvert

        /**
         * Takes a number, a pressure unit, and a new unit and converts the number to the new unit.
         *
         * Accepted units: torr, atm, mmHg, barr
         * Noah Getz
         * @param originalNum must be a valid double.
         * @param originalUnit must be a string
         * @param desiredUnit must be a string
         * @return the new double
         */
        public double pressureConvert(double originalNum, String originalUnit, String desiredUnit){
            //Make two variable doubles, one the original double and one the new one
            double num2 = 0.0d;


            //Store the units into new strings. I find this to be safer, as I can't override the originals this way.
            //Also convert them to lower case
            String original = originalUnit.toLowerCase();
            String newU = desiredUnit.toLowerCase();


            if(original.equals("torr")) {
                if (newU.equals("torr")) {
                    num2 = originalNum;

                }
                else if (newU.equals("atm")) {
                    num2 = originalNum * 0.0013157893594d;
                }

                else if (newU.equals("mmhg")) {
                    num2 = originalNum * 0.99999984999d;
                }

                else{
                    num2 = originalNum * 0.0013332237d;
                }
            }

            else if(original.equals("atm")) {
                if (newU.equals("atm")) {
                    num2 = originalNum;

                }
                else if (newU.equals("torr")) {
                    num2 = originalNum * 760.00006601d;
                }

                else if (newU.equals("mmhg")) {
                    num2 = originalNum * 759.999952d;
                }

                else{
                    num2 = originalNum * 1.0132501d;
                }
            }

            else if(original.equals("mmhg")) {
                if (newU.equals("mmhg")) {
                    num2 = originalNum;

                }
                else if (newU.equals("torr")) {
                    num2 = originalNum * 1.00000015d;
                }

                else if (newU.equals("atm")) {
                    num2 = originalNum * 0.0013157895568d;
                }

                else{
                    num2 = originalNum *  0.0013332239d;
                }
            }

            else{
                if (newU.equals("bar")) {
                    num2 = originalNum;

                }
                else if (newU.equals("torr")) {
                    num2 = originalNum * 750.06167382d;
                }

                else if (newU.equals("atm")) {
                    num2 = originalNum * 0.98692316931d;
                }

                else{
                    num2 = originalNum *  750.0615613d;
                }
            }
            return num2;
        }

        /**
         * Converts a number from one unit of time to another
         *
         * Units of time supported: seconds, minutes, hours, days, weeks, months, years
         *
         * Marc Kuniansky
         * @param originalNumber must be a valid double
         * @param originalUnit must be a valid String matching one of the supported units
         * @param newUnit must be a valid String matching one of the supported units
         * @return a double, the converted number
         */
        public double timeConvert(double originalNumber, String originalUnit, String newUnit)
        { //Begin convertTime
            //Make two doubles, one that holds the original and one that will be redefined where needed
            double num1 = originalNumber;
            double num2 = 0.0d;

            //Make two strings, capturing the units fed to the method
            String originalU = originalUnit.toLowerCase();
            String newU = newUnit.toLowerCase();

            //Convert from seconds
            switch (originalU) {
                case "sec" :
                { //Begin converting from seconds

                    switch(newU)
                    {
                        case "sec":
                            num2 = num1;
                            break;
                        case "min":
                            num2 = num1/60.0d;
                            break;
                        case "hrs":
                            num2 = num1/3600.0d;
                            break;
                        case "days":
                            num2 = num1*0.000011574d;
                            break;
                        case "weeks":
                            num2 = num1*0.0000016534d;
                            break;
                        case "months":
                            num2 = num1*0.00000038027d;
                            break;
                        case "years":
                            num2 = num1*0.000000031689d;
                            break;
                    }
                } break; //End converting from seconds

                //Convert from minutes
                case "min":
                { //Begin converting from minutes
                    switch(newU)
                    {
                        case "min":
                            num2 = num1;
                            break;
                        case "sec":
                            num2 = num1*60.0d;
                            break;
                        case "hrs":
                            num2 = num1/60.0d;
                            break;
                        case "days":
                            num2 = num1/1440.0d;
                            break;
                        case "weeks":
                            num2 = num1/10080.0d;
                            break;
                        case "months":
                            num2 = num1/43829.1d;
                            break;
                        case "years":
                            num2 = num1/525949.0d;
                    }
                } break; //End converting from minutes

                //Convert from hours
                case "hrs":
                { //Begin converting from hours
                    switch(newU)
                    {
                        case "hrs":
                            num2 = num1;
                            break;
                        case "sec":
                            num2 = num1*3600.0d;
                            break;
                        case "min":
                            num2 = num1*60.0d;
                            break;
                        case "days":
                            num2 = num1/24.0d;
                            break;
                        case "weeks":
                            num2 = num1/168.0d;
                            break;
                        case "months":
                            num2 = num1/730.484d;
                            break;
                        case "years":
                            num2 = num1/8765.81d;
                            break;
                    }
                } break; //End converting from hours

                //Convert from days
                case "days":
                { //Begin converting from days
                    switch(newU)
                    {
                        case "days":
                            num2 = num1;
                            break;
                        case "sec":
                            num2 = num1*86400.0d;
                            break;
                        case "min":
                            num2 = num1*1440.0d;
                            break;
                        case "hrs":
                            num2 = num1*24.0d;
                            break;
                        case "weeks":
                            num2 = num1/7.0d;
                            break;
                        case "months":
                            num2 = num1/30.4368d;
                            break;
                        case "years":
                            num2 = num1/365.242d;
                            break;
                    }
                } break; //End converting from days

                case "weeks":
                { //Begin converting from weeks
                    switch(newU)
                    {
                        case "weeks":
                            num2 = num1;
                            break;
                        case "sec":
                            num2 = num1*604800.0d;
                            break;
                        case "min":
                            num2 = num1*10080.0d;
                            break;
                        case "hrs":
                            num2 = num1*168.0d;
                            break;
                        case "days":
                            num2 = num1*7.0d;
                            break;
                        case "months":
                            num2 = num1/4.34812d;
                            break;
                        case "years":
                            num2 = num1/52.1775d;
                            break;
                    }
                } break;//End converting from weeks

                //Convert from months
                case "months":
                { //Begin converting from months
                    switch(newU)
                    {
                        case "months":
                            num2 = num1;
                            break;
                        case "sec":
                            num2 = num1*2630000.0d;
                            break;
                        case "min":
                            num2 = num1*43829.1d;
                            break;
                        case "hrs":
                            num2 = num1*730.484d;
                            break;
                        case "days":
                            num2 = num1*30.4368d;
                            break;
                        case "weeks":
                            num2 = num1*4.34812d;
                            break;
                        case "years":
                            num2 = num1/12.0d;
                            break;
                    }
                } break; //End converting from months

                //Convert from years
                case "years":
                    switch (newU) {
                        case "years":
                            num2 = num1;
                            break;
                        case "sec":
                            num2 = num1 * 31560000.0d;
                            break;
                        case "min":
                            num2 = num1 * 525949.0d;
                            break;
                        case "hrs":
                            num2 = num1 * 8765.81d;
                            break;
                        case "days":
                            num2 = num1 * 365.242d;
                            break;
                        case "weeks":
                            num2 = num1 * 52.1775d;
                            break;
                        case "months":
                            num2 = num1 * 12.0d;
                            break;
                    } break;
            } //End converting from years

            return num2;
        } //End convertTime

        /**
         * Converts a number from one unit of force to another.
         *
         * Units accepted: Newtons, pound force
         *
         * Marc Kuniansky
         * @param originalNumber must be a valid double
         * @param originalUnit must be a valid String matching one of the supported units
         * @param newUnit must be a valid String matching one of the supported units
         * @return a double, the converted unit.
         */
        public double forceConvert(double originalNumber, String originalUnit, String newUnit)
        { //Begin convertForce
            //Make two doubles, one that holds the original and one that will be redefined where needed
            double num1 = originalNumber;
            double num2 = 0.0d;

            //Make two strings, capturing the units fed to the method
            String originalU = originalUnit.toLowerCase();
            String newU = newUnit.toLowerCase();

            //The series of switch statements below figures out what unit to convert from/to, and does so.
            switch(originalU)
            {
                case "pound force":
                    switch(newU)
                    {
                        case "pound force":
                            num2 = num1;
                            break;
                        case "newtons":
                            num2 = num1*4.448222d;
                            break;
                    }
                    break;
                case "newtons":
                    switch (newU) {
                        case "newtons":
                            num2 = num1;
                            break;
                        case "pound force":
                            num2 = num1 / 4.448222d;
                            break;
                    }
            }
            return num2;
        } //End convertForce

        /**
         * Converts a number from one unit of speed to another
         *
         * Recognized speed units: miles per hour, feet per second, meters per second,
         * kilometers per second, kilometers per hour.
         *
         * Marc Kuniansky
         * @param originalNumber must be a valid double
         * @param originalUnit must be a valid String recognized by the method
         * @param newUnit must be a valid String recognized by the method
         * @return double, the converted unit.
         */
        public double speedConvert(double originalNumber, String originalUnit, String newUnit)
        { //Begin convertSpeed
            //Make two doubles, one that holds the original and one that will be redefined where needed
            double num1 = originalNumber;
            double num2 = 0.0d;

            //Make two strings, capturing the units fed to the method
            String originalU = originalUnit.toLowerCase();
            String newU = newUnit.toLowerCase();

            //The series of if statements below figures out what unit to convert from/to, and does so.

            switch(originalU)
            { //Begin conversion table
                case "miles per hour":
                    switch(newU)
                    { //Begin converting from miles per hour
                        case "miles per hour":
                            num2 = originalNumber;
                            break;
                        case "feet per second":
                            num2 = num1*1.46667d;
                            break;
                        case "kilometers per second":
                            num2 = num1*0.00044704d;
                            break;
                        case "kilometers per hour":
                            num2 = num1*1.60934d;
                            break;
                        case "meters per second":
                            num2 = num1*0.44704d;
                            break;
                    } //End converting from miles per hour
                    break;
                case "feet per second":
                    switch(newU)
                    { //Begin converting from feet per second
                        case "miles per hour":
                            num2 = num1*0.681818d;
                            break;
                        case "feet per second":
                            num2 = originalNumber;
                            break;
                        case "kilometers per second":
                            num2 = num1*0.0003048d;
                            break;
                        case "kilometers per hour":
                            num2 = num1*1.09728d;
                            break;
                        case "meters per second":
                            num2 = num1*0.3048d;
                            break;
                    } //End converting from feet per second
                    break;
                case "kilometers per second":
                    switch(newU)
                    { //Begin converting from kilometers per second
                        case "miles per hour":
                            num2 = num1*2236.93629d;
                            break;
                        case "feet per second":
                            num2 = num1*3280.8399d;
                            break;
                        case "kilometers per second":
                            num2 = originalNumber;
                            break;
                        case "kilometers per hour":
                            num2 = num1*3600.0d;
                            break;
                        case "meters per second":
                            num2 = num1*0.277778d;
                            break;
                    } //End converting from kilometers per second
                    break;
                case "kilometers per hour":
                    switch(newU)
                    { //Begin converting from kilometers per hour
                        case "miles per hour":
                            num2 = num1*2.23694d;
                            break;
                        case "feet per second":
                            num2 = num1*0.911344d;
                            break;
                        case "kilometers per second":
                            num2 = num1*0.000277777778d;
                            break;
                        case "kilometers per hour":
                            num2 = originalNumber;
                            break;
                        case "meters per second":
                            num2 = num1*0.277778d;
                            break;
                    } //end converting from kilometers per hour
                    break;
                case "meters per second":
                    switch(newU)
                    { //Begin converting from meters per second
                        case "miles per hour":
                            num2 = num1*2.23694d;
                            break;
                        case "feet per second":
                            num2 = num1*3.28084d;
                            break;
                        case "kilometers per second":
                            num2 = num1*0.001d;
                            break;
                        case "kilometers per hour":
                            num2 = num1*3.6d;
                            break;
                        case "meters per second":
                            num2 = originalNumber;
                            break;
                    } //End converting from meters per second
                    break;
            } //End conversion table

            //Return the result
            return num2;
        } //End convertSpeed

        /**
         * Converts a number from one unit of speed to another.
         *
         * Accepted units: square inches, square feet, square yards, square miles, square meters, square kilometers, acres
         *
         * Marc Kuniansky
         * @param originalNumber must be a valid double
         * @param originalUnit must be a valid String matching one of the supported units
         * @param newUnit must be a valid String matching one of the supported units
         * @return a double, the converted unit.
         */
        public double areaConvert(double originalNumber, String originalUnit, String newUnit)
        { //Begin convertArea
            //Make two doubles, one that holds the original and one that will be redefined where needed
            double num1 = originalNumber;
            double num2 = 0.0d;

            //Make two strings, capturing the units fed to the method
            String originalU = originalUnit.toLowerCase();
            String newU = newUnit.toLowerCase();

            switch(originalU)
            {
                //Begin unit conversions
                case "square inches":
                    switch(newU)
                    { //Begin converting from square inches
                        case "square inches":
                            num2 = num1;
                            break;
                        case "square feet":
                            num2 = num1/144.0d;
                            break;
                        case "square yards":
                            num2 = num1/1296.0d;
                            break;
                        case "square miles":
                            num2 = num1/4014000000.0d;
                            break;
                        case "acres":
                            num2 = num1/6273000.0d;
                            break;
                        case "square kilometers":
                            num2 = num1/1550000000.0d;
                            break;
                        case "square meters":
                            num2 = num1/1550.0d;
                            break;
                    } //End converting from square inches
                    break;
                case "square feet":
                    switch(newU)
                    { //Begin converting from square feet
                        case "square inches":
                            num2 = num1*144.0d;
                            break;
                        case "square feet":
                            num2 = num1;
                            break;
                        case "square yards":
                            num2 = num1/9.0d;
                            break;
                        case "square miles":
                            num2 = num1/27880000.0d;
                            break;
                        case "acres":
                            num2 = num1/43560.0d;
                            break;
                        case "square kilometers":
                            num2 = num1/10760000.0d;
                            break;
                        case "square meters":
                            num2 = num1/10.7639d;
                            break;
                    } //End converting from square feet
                    break;
                case "square yards":
                    switch(newU)
                    { //Begin converting from square yards
                        case "square inches":
                            num2 = num1*1296.0d;
                            break;
                        case "square feet":
                            num2 = num1*9.0d;
                            break;
                        case "square yards":
                            num2 = num1;
                            break;
                        case "square miles":
                            num2 = num1/3098000.0d;
                            break;
                        case "acres":
                            num2 = num1/4840.0d;
                            break;
                        case "square kilometers":
                            num2 = num1/1196000.0d;
                            break;
                        case "square meters":
                            num2 = num1/1.19599d;
                            break;
                    }//End converting from square yards
                    break;
                case "square miles":
                    switch(newU)
                    { //Begin converting from square miles
                        case "square inches":
                            num2 = num1*4014000000.0d;
                            break;
                        case "square feet":
                            num2 = num1*27880000.0d;
                            break;
                        case "square yards":
                            num2 = num1*3098000.0d;
                            break;
                        case "square miles":
                            num2 = num1;
                            break;
                        case "acres":
                            num2 = num1*640.0d;
                            break;
                        case "square kilometers":
                            num2 = num1*2.58999d;
                            break;
                        case "square meters":
                            num2 = num1*2590000.0d;
                            break;
                    }//End converting from square miles
                    break;
                case "acres":
                    switch(newU)
                    {//Begin converting from acres
                        case "square inches":
                            num2 = num1*6273000.0d;
                            break;
                        case "square feet":
                            num2 = num1*43560.0d;
                            break;
                        case "square yards":
                            num2 = num1*4840.0d;
                            break;
                        case "square miles":
                            num2 = num1/640.0d;
                            break;
                        case "square acres":
                            num2 = num1;
                            break;
                        case "square kilometers":
                            num2 = num1/247.105d;
                            break;
                        case "square meters":
                            num2 = num1*4046.86d;
                            break;
                    } //End converting from acres
                    break;
                case "square kilometers":
                    switch(newU)
                    { //Begin converting from square kilometers
                        case "square inches":
                            num2 = num1*1550000000.0d;
                            break;
                        case "square feet":
                            num2 = num1*10760000.0d;
                            break;
                        case "square yards":
                            num2 = num1*1196000.0d;
                            break;
                        case "square miles":
                            num2 = num1/2.58999d;
                            break;
                        case "acres":
                            num2 = num1;
                            break;
                        case "square kilometers":
                            num2 = num1;
                            break;
                        case "square meters":
                            num2 = num1*1000000.0d;
                            break;
                    } //End converting from square kilometers
                    break;
                case "square meters":
                    switch(newU)
                    { //Begin converting from square meters
                        case "square inches":
                            num2 = num1*1550.0d;
                            break;
                        case "square feet":
                            num2 = num1*10.7639d;
                            break;
                        case "square yards":
                            num2 = num1*1.19599d;
                            break;
                        case "square miles":
                            num2 = num1/2590000.0d;
                            break;
                        case "acres":
                            num2 = num1/4046.86d;
                            break;
                        case "square kilometers":
                            num2 = num1/1000000.0d;
                            break;
                        case "square meters":
                            num2 = num1;
                            break;
                    } //End converting from square meters
            } //End conversion table

            //Return the resulting number from the conversion table above
            return num2;
        } //End convertArea

        /**
         * Converts between metric prefixes. The type of unit is unimportant- the metric system operates on a base 10 system
         * and so converting between, say, millimeters and meters is exactly the same as converting between milliliters and liters.
         *
         * Accepted prefixes: yotta, zeta, exa, peta, tera, giga, mega, kilo, hecto, deka, UNIT,
         * deci, centi, milli, micro, nano, pico, femto, atto, zepto, yocto
         *
         * Marc Kuniansky
         * @param originalNumber must be a valid double
         * @param originalUnit must be a valid String matching one of the supported units
         * @param newUnit must be a valid String matching one of the supported units
         * @return a double, the converted number
         */
        public double metricConvert(double originalNumber, String originalUnit, String newUnit)
        { //Begin metricConvert
            //This can use a slightly different, and much easier, algorithm than the others.
            //Because metric is so well organized, it doesn't matter what number is input- the conversion factors are the same.
            //So if I take the original number and convert it to UNITS (which is x*10^0) then convert from UNITS to the new unit,
            //I can very easily do these conversions with very little work. I will heavily utilize the math class here, I need to
            //use exponents quite a bit to simplify life.

            //First, I will need four doubles: the original number, the UNIT number, the final number, and a variable with which to catch the
            //powers of 10.
            double num1 = originalNumber;
            double unitNum = 0d;
            double finalNum = 0d;
            double tenP;

            //I like to grab the two strings to prevent accidental editing/deletion. I also send them to lower case.
            String originalUn = originalUnit.toLowerCase();
            String newUn = newUnit.toLowerCase();

//            String originalU;
//            if(originalUn.contains(" ")) {
//                originalU = originalUn.substring(0, originalUn.indexOf(" "));
//            }
//            else
//            {
//                originalU = originalUn;
//            }

            //If the string from the MetricActivity spinners is passed, there will be a space. Remove everything after it.
//            String newU;
//            if(newUn.contains(" ")) {
//                newU = newUn.substring(0, newUn.indexOf(" "));
//            }
//            else
//            {
//                newU = newUn;
//            }
            //String newU = newUn.substring(0, newUn.indexOf(" "));
            //Next, I use the first of two switch statements. This converts the original number to UNITS, or x*10^0.
            switch(originalUn)
            {
                case "yotta":
                    //Yotta is 10^24 units
                    tenP = allExponents(10d, 24d);
                    unitNum = tenP*num1;
                    break;
                case "zeta":
                    //Zeta is 10^21
                    tenP = allExponents(10d, 21d);
                    unitNum = tenP*num1;
                    break;
                case "exa":
                    //Exa is 10^18
                    tenP = allExponents(10d, 18d);
                    unitNum = tenP*num1;
                    break;
                case "peta":
                    //Peta is 10^15
                    tenP = allExponents(10d, 15d);
                    unitNum = tenP*num1;
                    break;
                case "tera":
                    //Tera is 10^12
                    tenP = allExponents(10d, 12d);
                    unitNum = tenP*num1;
                    break;
                case "giga":
                    //Giga is 10^9
                    tenP = allExponents(10d, 9d);
                    unitNum = tenP*num1;
                    break;
                case "mega":
                    //Mega is 10^6
                    tenP = allExponents(10d, 6d);
                    unitNum = tenP*num1;
                    break;
                case "kilo":
                    //Kilo is 10^3
                    tenP = allExponents(10d, 3d);
                    unitNum = tenP*num1;
                    break;
                case "hecto":
                    //Hecto is 10^2
                    tenP = allExponents(10d, 2d);
                    unitNum = tenP*num1;
                    break;
                case "deka":
                    //Deka is 10^1
                    tenP = allExponents(10d, 1d);
                    unitNum = tenP*num1;
                    break;
                case "unit":
                    //UNIT is the target, 10^0
                    tenP = allExponents(10d, 0d);
                    unitNum = tenP*num1;
                    break;
                case "deci":
                    //Deci is 10^-1
                    tenP = allExponents(10d, -1d);
                    unitNum = num1*tenP;
                    break;
                case "centi":
                    //Centi is 10^-2
                    tenP = allExponents(10d, -2d);
                    unitNum = num1*tenP;
                    break;
                case "milli":
                    //Milli is 10^-3
                    tenP = allExponents(10d, -3d);
                    unitNum = num1*tenP;
                    break;
                case "micro":
                    //Micro is 10^-6
                    tenP = allExponents(10d, -6d);
                    unitNum = num1*tenP;
                    break;
                case "nano":
                    //Nano is 10^-9
                    tenP = allExponents(10d, -9d);
                    unitNum = num1*tenP;
                    break;
                case "pico":
                    //Pico is 10^-12
                    tenP = allExponents(10d, -12d);
                    unitNum = num1*tenP;
                    break;
                case "femto":
                    //Femto is 10^-15
                    tenP = allExponents(10d, -15d);
                    unitNum = num1*tenP;
                    break;
                case "atto":
                    //Atto is 10^-18
                    tenP = allExponents(10d, -18d);
                    unitNum = num1*tenP;
                    break;
                case "zepto":
                    //Zepto is 10^-21
                    tenP = allExponents(10d, -21d);
                    unitNum = num1*tenP;
                    break;
                case "yocto":
                    //Yocto is 10^-24
                    tenP = allExponents(10d, -24d);
                    unitNum = num1*tenP;
                    break;
            }

            //Next is a switch statement for all possible cases of the new unit. It takes
            //the number given by the first switch, unitNum, and converts it to the new unit
            //using math.
            switch(newUn)
            { //Begin converting from base units (10^0) to new units.
                case "yotta":
                    //Yotta is 10^24 units
                    tenP = allExponents(10d, 24d);
                    finalNum = unitNum/tenP;
                    break;
                case "zeta":
                    //Zeta is 10^21
                    tenP = allExponents(10d, 21d);
                    finalNum = unitNum/tenP;
                    break;
                case "exa":
                    //Exa is 10^18
                    tenP = allExponents(10d, 18d);
                    finalNum = unitNum/tenP;
                    break;
                case "peta":
                    //Peta is 10^15
                    tenP = allExponents(10d, 15d);
                    finalNum = unitNum/tenP;
                    break;
                case "tera":
                    //Tera is 10^12
                    tenP = allExponents(10d, 12d);
                    finalNum = unitNum/tenP;
                    break;
                case "giga":
                    //Giga is 10^9
                    tenP = allExponents(10d, 9d);
                    finalNum = unitNum/tenP;
                    break;
                case "mega":
                    //Mega is 10^6
                    tenP = allExponents(10d, 6d);
                    finalNum = unitNum/tenP;
                    break;
                case "kilo":
                    //Kilo is 10^3
                    tenP = allExponents(10d, 3d);
                    finalNum = unitNum/tenP;
                    break;
                case "hecto":
                    //Hecto is 10^2
                    tenP = allExponents(10d, 2d);
                    finalNum = unitNum/tenP;
                    break;
                case "deka":
                    //Deka is 10^1
                    tenP = allExponents(10d, 1d);
                    finalNum = unitNum/tenP;
                    break;
                case "unit":
                    //UNIT is the target, 10^0
                    tenP = allExponents(10d, 0d);
                    finalNum = unitNum/tenP;
                    break;
                case "deci":
                    //Deci is 10^-1
                    tenP = allExponents(10d, -1d);
                    finalNum = unitNum/tenP;
                    break;
                case "centi":
                    //Centi is 10^-2
                    tenP = allExponents(10d, -2d);
                    finalNum = unitNum/tenP;
                    break;
                case "milli":
                    //Milli is 10^-3
                    tenP = allExponents(10d, -3d);
                    finalNum = unitNum/tenP;
                    break;
                case "micro":
                    //Micro is 10^-6
                    tenP = allExponents(10d, -6d);
                    finalNum = unitNum/tenP;
                    break;
                case "nano":
                    //Nano is 10^-9
                    tenP = allExponents(10d, -9d);
                    finalNum = unitNum/tenP;
                    break;
                case "pico":
                    //Pico is 10^-12
                    tenP = allExponents(10d, -12d);
                    finalNum = unitNum/tenP;
                    break;
                case "femto":
                    //Femto is 10^-15
                    tenP = allExponents(10d, -15d);
                    finalNum = unitNum/tenP;
                    break;
                case "atto":
                    //Atto is 10^-18
                    tenP = allExponents(10d, -18d);
                    finalNum = unitNum/tenP;
                    break;
                case "zepto":
                    //Zepto is 10^-21
                    tenP = allExponents(10d, -21d);
                    finalNum = unitNum/tenP;
                    break;
                case "yocto":
                    //Yocto is 10^-24
                    tenP = allExponents(10d, -24d);
                    finalNum = unitNum/tenP;
                    break;
            } //End converting from base units (10^0) to new units.

            //Finally, return the final number
            return finalNum;
        } //End metricConvert

        /**
         * Helping method which will allow an exponent to be either positive or negative, unlike the math pow() method.
         * Thanks to this page for the idea to do this, and for the skeleton code- http://stackoverflow.com/questions/4364634/calculate-the-power-of-any-exponent-negative-or-positive
         *
         * Marc Kuniansky
         * @param base must be a valid double
         * @param exponent must be a valid double
         * @return the resulting number
         */
        private double allExponents(double base, double exponent)
        { //Begin allExponents
            double b = base;
            double e = Math.abs(exponent);
            double finalNum;
            if (exponent > 0)
            {
                finalNum = Math.pow(base, e);
            }
            else if (exponent < 0)
            {
                double p = Math.pow(base, e);
                finalNum = 1/p;
            }
            else
            {
                finalNum = 1;
            }

            return finalNum;
        } //End allExponents


    } //End converter class
}
