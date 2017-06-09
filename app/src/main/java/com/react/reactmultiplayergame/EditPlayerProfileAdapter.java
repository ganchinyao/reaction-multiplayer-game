package com.react.reactmultiplayergame;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.react.reactmultiplayergame.helper.AutoResizeTextView;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.SoundPoolManager;
import com.react.reactmultiplayergame.helper.Utils;

import me.grantland.widget.AutofitTextView;

public class EditPlayerProfileAdapter extends PagerAdapter {

    private Context mContext;
    private static EditText [] editTextArray = new EditText[4];


    public EditPlayerProfileAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);

        //this method instantiateItem will be called automatcally for 4 times by viewpagers, for all 4 viewpagers,
        // with int position varying from 0 to 3.

        final int playerArray [] = {Constants.PLAYER1, Constants.PLAYER2, Constants.PLAYER3, Constants.PLAYER4};
        final EditText editText = (EditText) layout.findViewById(com.react.reactmultiplayergame.R.id.editplayername_edittext);
        AutoResizeTextView resetButton = (AutoResizeTextView) layout.findViewById(com.react.reactmultiplayergame.R.id.editplayername_resetButton);

        if (editText != null) {
            editTextArray[position] = editText; // set a reference to be called in MainActivity.java
            editText.setText(Utils.getPlayerName(mContext, playerArray[position]));

        }
            if (resetButton != null) {
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundPoolManager.getInstance().playSound(0);
                        String defaultPlayerName[] = {mContext.getString(com.react.reactmultiplayergame.R.string.player1), mContext.getString(com.react.reactmultiplayergame.R.string.player2),
                                mContext.getString(com.react.reactmultiplayergame.R.string.player3), mContext.getString(com.react.reactmultiplayergame.R.string.player4)};
                        for(int i=0; i<4; i++){
                            // reset all the custom names to default names
                            if(editTextArray[i] != null)
                            editTextArray[i].setText(defaultPlayerName[i]);
                        }
                    }
                });

        }

        // dynamically inflate all the 10 buttons. As this method will be auto call for all 4 viewpager, hence we only need instantiate for 1 viewpager, i.e. 10 circle

        PercentRelativeLayout topHalf = (PercentRelativeLayout) layout.findViewById(com.react.reactmultiplayergame.R.id.editplayername_tophalfTags);
        PercentRelativeLayout bottomHalf = (PercentRelativeLayout) layout.findViewById(com.react.reactmultiplayergame.R.id.editplayername_bottomhalfTags);
        final ImageView [] imageViewArray = new ImageView[10];
        LinearLayout topLinearLayout = new LinearLayout(mContext);
        LinearLayout bottomLinearLayout = new LinearLayout(mContext);
        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        final int colorArray [] = {com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_yellow, com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_orange, com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_pink,
                com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_red, com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_white, com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_cyan,
                com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_green, com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_purple, com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_brown, com.react.reactmultiplayergame.R.color.editplayerprofile_colortag_black};
        for(int i=0, half = imageViewArray.length/2; i<10; i++){
            final int temp = i; // use in the onClickListener since cant reference i inside inner method
            imageViewArray[i] = new ImageView(mContext);

            switch (position){
                // reason we set up this switch-if statement is becos we do not want to setImageResource twice to the default one,
                // since the default one will be set to a selectedCircle in the switch statement below.
                // Hence, we spend resources to check switch-if, which is less costly than setting up additional 4 imageResource, only to be overridden below
                case 0:
                    if(i != Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER1)) {
                        imageViewArray[i].setImageResource(R.drawable.editplayername_tagcircleselector);
                    }
                    break;
                case 1:
                    if(i != Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER2)) {
                        imageViewArray[i].setImageResource(R.drawable.editplayername_tagcircleselector);
                    }
                    break;
                case 2:
                    if(i != Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER3)) {
                        imageViewArray[i].setImageResource(R.drawable.editplayername_tagcircleselector);
                    }
                    break;
                case 3:
                    if(i != Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER4)) {
                        imageViewArray[i].setImageResource(com.react.reactmultiplayergame.R.drawable.editplayername_tagcircleselector);
                    }
                    break;
            }
            imageViewArray[i].setColorFilter(ContextCompat.getColor(mContext, colorArray[i]));
            imageViewArray[i].setClickable(true);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            if(i<half){
                topLinearLayout.addView(imageViewArray[i], param);
            } else {
                bottomLinearLayout.addView(imageViewArray[i], param);
            }

            imageViewArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundPoolManager.getInstance().playSound(0);
                    // set all circle to non-selected
                    for(int j =0; j<10; j++){
                        if(j == temp) continue;
                        imageViewArray[j].setImageResource(com.react.reactmultiplayergame.R.drawable.editplayername_tagcircleselector);
                    }
                    // then set the current circle to selected
                    imageViewArray[temp].setImageResource(R.drawable.editplayername_circleselected);
                    switch (position){
                        // different switch for the 4 different viewpager
                        // here we store the reference to which color the user has selected inside the static var.
                        // in MainActivity.java, when user press "Done", we simply query these 4 static var and set the new color there.
                        // when user pressed cancel, we do not need to do anything
                        case 0:
                            EditPlayerProfileAdapter.getColorTag_p1 = temp;
                            break;
                        case 1:
                            EditPlayerProfileAdapter.getColorTag_p2 = temp;
                            break;
                        case 2:
                            EditPlayerProfileAdapter.getColorTag_p3 = temp;
                            break;
                        case 3:
                            EditPlayerProfileAdapter.getColorTag_p4 = temp;
                            break;
                    }
                }
            });

        }
        RelativeLayout.LayoutParams overallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams overallParam2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        topHalf.addView(topLinearLayout, overallParam);
        bottomHalf.addView(bottomLinearLayout, overallParam2);

        switch (position){
            case 0:
                imageViewArray[Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER1)].setImageResource(com.react.reactmultiplayergame.R.drawable.editplayername_circleselected);
                break;
            case 1:
                imageViewArray[Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER2)].setImageResource(com.react.reactmultiplayergame.R.drawable.editplayername_circleselected);
                break;
            case 2:
                imageViewArray[Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER3)].setImageResource(com.react.reactmultiplayergame.R.drawable.editplayername_circleselected);
                break;
            case 3:
                imageViewArray[Utils.getPlayerColorTagPosition(mContext, Constants.PLAYER4)].setImageResource(com.react.reactmultiplayergame.R.drawable.editplayername_circleselected);
                break;
        }

        return layout;
    }

    static int getColorTag_p1 = 1; // default color if user did not select color. Same as the Utils one
    static int getColorTag_p2 = 3;
    static int getColorTag_p3 = 5;
    static int getColorTag_p4 = 7;
    static EditText [] getEditTextArray () {
        // return a reference of the 4 editTexts so that mainActivity can query them on "Done" button
        return editTextArray;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private enum ModelObject {

        PLAYER1(com.react.reactmultiplayergame.R.layout.quickplay_popup_playerprofile_p1viewpager),
        PLAYER2(com.react.reactmultiplayergame.R.layout.quickplay_popup_playerprofile_p2viewpager),
        PLAYER3(com.react.reactmultiplayergame.R.layout.quickplay_popup_playerprofile_p3viewpager),
        PLAYER4(com.react.reactmultiplayergame.R.layout.quickplay_popup_playerprofile_p4viewpager);

        private int mLayoutResId;

        ModelObject(int layoutResId) {
            mLayoutResId = layoutResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }

    }

}
