package com.react.reactmultiplayergame;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.react.reactmultiplayergame.gamemode.AnimalKingdom;
import com.react.reactmultiplayergame.gamemode.CollidingCircle;
import com.react.reactmultiplayergame.gamemode.ColorMatchWords;
import com.react.reactmultiplayergame.gamemode.CountryAndContinent;
import com.react.reactmultiplayergame.gamemode.FiveColorsPanel;
import com.react.reactmultiplayergame.gamemode.FiveYellowCircle;
import com.react.reactmultiplayergame.gamemode.FoodParadise;
import com.react.reactmultiplayergame.gamemode.GameModeInstances;
import com.react.reactmultiplayergame.gamemode.IdentifyingLanguage;
import com.react.reactmultiplayergame.gamemode.IllusionCircle;
import com.react.reactmultiplayergame.gamemode.IllusionGear;
import com.react.reactmultiplayergame.gamemode.IllusionRectangle;
import com.react.reactmultiplayergame.gamemode.MandN;
import com.react.reactmultiplayergame.gamemode.MixingColors;
import com.react.reactmultiplayergame.gamemode.MultiColoredOrb;
import com.react.reactmultiplayergame.gamemode.NeonArrows;
import com.react.reactmultiplayergame.gamemode.NumberComparison;
import com.react.reactmultiplayergame.gamemode.OandX;
import com.react.reactmultiplayergame.gamemode.OddAndVowel;
import com.react.reactmultiplayergame.gamemode.PerfectMatch;
import com.react.reactmultiplayergame.gamemode.ScreenColor;
import com.react.reactmultiplayergame.gamemode.SmilingFace;
import com.react.reactmultiplayergame.gamemode.SolarEclipse;
import com.react.reactmultiplayergame.gamemode.TouchingSpam;
import com.react.reactmultiplayergame.gamemode.UnitConversion;
import com.react.reactmultiplayergame.helper.AutoResizeTextView;
import com.react.reactmultiplayergame.helper.Constants;
import com.react.reactmultiplayergame.helper.CustomGameListViewAdapter;
import com.react.reactmultiplayergame.helper.CustomGameListViewModel;
import com.react.reactmultiplayergame.helper.MusicManager;
import com.react.reactmultiplayergame.helper.SoundPoolManager;
import com.react.reactmultiplayergame.helper.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitHelper;

import static com.react.reactmultiplayergame.helper.Utils.obsfucate_publicAPIKey;
import static com.react.reactmultiplayergame.helper.Utils.subset_APIKey_beforeDash;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BillingProcessor.IBillingHandler {
    private RelativeLayout quickplayTwoPlayers_Button, quickplayThreePlayers_Button, quickplayFourPlayers_Button;
    private ImageView quickplay_close, quickplayEasyIcon, quickplayMediumIcon, quickplayHardIcon, quickplayInsaneIcon,
            quickplayEasyRectangle, quickplayMediumRectangle, quickplayHardRectangle, quickplayInsaneRectangle,
            quickplayTwoPlayerIcon, quickplayThreePlayerIcon, quickplayFourPlayerIcon,
            quickplayTwoPlayerRectangle, quickplayThreePlayerRectangle, quickplayFourPlayerRectangle, muteSoundButton;
    private int quickplayLevelDifficultySelection;
    private int quickplayNumberOfPlayerSelection;
    private LinearLayout fourbuttons_overallContainer;
    private RelativeLayout quickplayButton, customGameButton, shopButton, rateThisAppButton;
    private int orangeColor;
    private PopupWindow quickplay_popupWindow, quickplay_editplayerPopUpWindow, shopPopUpWindow, customgame_popupWindow;
    private AutoResizeTextView quickplay_easyText, quickplay_mediumText, quickplay_hardText, quickplay_insaneText;
    // used in InsaneMode dialog, editPlayerProfile dialog, and start button whereby we don't want to playCloseMenuSound in "Go To Shop", "Done" and "Start" button click
    // becos dialog.onDismiss play close menu sound, but on pressing "Go To Shop", dialog is dismissed too, but we DO NOT want to play close menu , hence we set this var to false
    private boolean playCloseMenuSound = true;
    private int customGame_currentNumberOfModes_int; // cant set local becoz used in inner class
    private NativeAd nativeAd;
    private boolean loadAdError = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    BillingProcessor bp;
    private String fakeAPIKey = Constants.publickey;
    private String fb_nativeAdID = Constants.fb_nativehomepagead_Id; // the reason we declare global so on decompiling it will not appeaer inside method instead

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.react.reactmultiplayergame.R.layout.homepage);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        orangeColor = ContextCompat.getColor(MainActivity.this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage);

        bp = BillingProcessor.newBillingProcessor(this, constructActual_APIKey(), this); // doesn't bind
        bp.initialize(); // binds

        if (!Utils.hasAlreadyMeasuredDeviceSize(MainActivity.this)) {
            // has not measured size, hence start measuring.
            // this check ensures that this method will be called only once as it is stored in sharedpreference
            calculateScreenSize();
        }


        fourbuttons_overallContainer = (LinearLayout) findViewById(com.react.reactmultiplayergame.R.id.fourmainbuttons_container);
        quickplayButton = new RelativeLayout(this);
        customGameButton = new RelativeLayout(this);
        shopButton = new RelativeLayout(this);
        rateThisAppButton = new RelativeLayout(this);

        fourbuttons_overallContainer.post(new Runnable() {
            @Override
            public void run() {
                init_fourMainButtons_layout();
            }
        });


        quickplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPoolManager.getInstance().playSound(1);
                quickplayPopup();
            }
        });
        customGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_customGamePopUp();
            }
        });
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPoolManager.getInstance().playSound(1);
                show_shopPopUp();
            }
        });
        rateThisAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPoolManager.getInstance().playSound(0);
                String PACKAGE_NAME = getApplicationContext().getPackageName();
                String APP_URL = getResources().getString(R.string.google_play) + PACKAGE_NAME;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(APP_URL));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        muteSoundButton = (ImageView) findViewById(com.react.reactmultiplayergame.R.id.homepageMute);
        if (isSoundMuted()) {
            // true = setMuteIcon
            // programmatically set the sound icon to be playing/mute, which takes into account sharedPreference
            muteSoundButton.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_soundmuteselector);
        } else {
            muteSoundButton.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_soundplayingselector);
        }
        muteSoundButton.setOnClickListener(this);

        ImageView homepageSettings = (ImageView) findViewById(R.id.homepageSettingButton);
        homepageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPoolManager.getInstance().playSound(0);
                // set this flag to true since we want to retain background music, and onPause will not stop background music
                MusicManager.isGoingNextActivity = true;
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ImageView homepageHelpButton = (ImageView) findViewById(R.id.homepageHelpButton);
        homepageHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPoolManager.getInstance().playSound(0);
                MusicManager.isGoingNextActivity = true;
                Intent intent = new Intent(MainActivity.this, HowToPlay.class);
                startActivity(intent);
            }
        });

        ImageView shareThisAppButton = (ImageView) findViewById(R.id.homepageShareButton);
        shareThisAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPoolManager.getInstance().playSound(0);
                Toast.makeText(MainActivity.this, R.string.sharethisapptoast, Toast.LENGTH_SHORT).show();
                try
                { String PACKAGE_NAME = getApplicationContext().getPackageName();
                    String APP_URL = getResources().getString(R.string.google_play) + PACKAGE_NAME;
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    String sAux = "\nTry out this Game!!\n\n";
                    sAux = sAux +  APP_URL +"\n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                }
                catch(Exception e)
                { //e.toString();
                }
            }
        });

        // fb native home page ad
        // need to call one showNativeAd here becos the showNativeAd at onResume will not be called on first launch since loadAdError == false
        if(!Utils.hasPurchasedRemoveAds(this)) {
            showNativeAd();
        }
    }

    private void showNativeAd() {
        nativeAd = new NativeAd(this, fb_nativeAdID.replace("4", "0"));
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // set to true so onResume showNativeAd will be called again
                loadAdError = true;
                // hide ads when theres no fill
                LinearLayout adsContainer = (LinearLayout) findViewById(R.id.ads_container);
                adsContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // set it back to false in case it was true before
                loadAdError = false;
                // show ads agn
                LinearLayout adsContainer = (LinearLayout) findViewById(R.id.ads_container);
                if(adsContainer.getVisibility() == View.INVISIBLE) {
                    adsContainer.setVisibility(View.VISIBLE);
                }

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = (ImageView) findViewById(R.id.homepageAdsIcon);
                TextView nativeAdTitle = (TextView) findViewById(R.id.homepageAdsTitle);
                TextView nativeAdCallToAction_textView= (TextView) findViewById(R.id.homepage_ad_calltoaction_textview);
                RelativeLayout nativeAdCallToAction = (RelativeLayout) findViewById(R.id.homepage_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                nativeAdCallToAction_textView.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Add the AdChoices icon
                RelativeLayout adChoicesContainer = (RelativeLayout) findViewById(R.id.homepage_adchoiceicon);
                AdChoicesView adChoicesView = new AdChoicesView(MainActivity.this, nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(nativeAdCallToAction);

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        nativeAd.loadAd();
    }

    // method to call when Quick Play RelativeLayout is pressed
    private void quickplayPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(com.react.reactmultiplayergame.R.layout.quickplay_popup, null);

        quickplay_popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);

        quickplay_popupWindow.setTouchable(true);
        quickplay_popupWindow.setFocusable(true);
        quickplay_popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // so that onbackpress can close the popupwindow
        quickplay_popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        quickplay_popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        quickplay_popupWindow.showAtLocation(popupView, Gravity.FILL, 0, 0);
        quickplay_popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // play sound onDismiss. This takes into account both on dismiss by backpress, or click outside popup
                // no overlapping becos clicking outside popup only trigger popup.dismiss, and no sound is triggered there, except here
                if (playCloseMenuSound) {
                    SoundPoolManager.getInstance().playSound(2);
                }
                playCloseMenuSound = true;
            }
        });

        initializeDifficultyNumberPlayersClickEvent(popupView);

        quickplay_close = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_popupClose);
        quickplay_close.setOnClickListener(this);
        // these container are the empty linearlayout in quickplay_popup.xml and here we set them onclicklistener
        // to close the popup. This gives the effect of closing the popup on touching outside of the screen.
        // We cannot programmatically set popup to close outside of popup becos there is "no outside of popup" since the outside are actually empty linearlayout used to
        // separate and position our actual popup. Hence we set up the popupdismiss manually
        LinearLayout dismissPopUp_Container1, dismissPopUp_Container2, dismissPopUp_Container3;
        dismissPopUp_Container1 = (LinearLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplaypopup_emptyContainer1);
        dismissPopUp_Container2 = (LinearLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplaypopup_emptyContainer2);
        dismissPopUp_Container3 = (LinearLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplaypopup_emptyContainer3);

        dismissPopUp_Container1.setOnClickListener(this);
        dismissPopUp_Container2.setOnClickListener(this);
        dismissPopUp_Container3.setOnClickListener(this);

        ImageView quickplayStart = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_start_Button);
        quickplayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String levelDifficulty = "";
                switch (quickplayLevelDifficultySelection){
                    case Constants.MODE_EASY:
                        levelDifficulty = "Easy";
                        break;
                    case Constants.MODE_MEDIUM:
                        levelDifficulty = "Medium";
                        break;
                    case Constants.MODE_HARD:
                        levelDifficulty = "Hard";
                        break;
                    case Constants.MODE_INSANE:
                        levelDifficulty = "Insane";
                        break;
                }

                SoundPoolManager.getInstance().playSound(6); // play start sound
                playCloseMenuSound = false;
                //TODO these gameModes words are not placed for translation: AnimalKingdom, CollidingCircle, ColorMatchWords, FiveColorsPanel, IdentiyingLanguage,MixingColors,NumberComparison,OddAndVowel, TouchingSpam, UnitConversion
                //TODO review ^ these gameModes again and put hardcoded string into string.xml

                Bundle params = new Bundle();
                switch (quickplayNumberOfPlayerSelection) {
                    case Constants.MODE_TWOPLAYER:
                        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Quick_Play");
                        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Two_Players");
                        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, levelDifficulty);
                        mFirebaseAnalytics.logEvent("Start_Game", params);

                        // set the selected difficulty as the "default difficulty" during next pop up selection
                        // also QuickPlay mode later will use Utils.getDifficultyPreference to play the respective difficulty
                        Utils.setDifficultyPreference(MainActivity.this, quickplayLevelDifficultySelection);
                        setNoOfPlayersPreference(quickplayNumberOfPlayerSelection); // set the current NoOfPlayers as the next default mode, same concept as difficulty above
                        Intent intent = new Intent(MainActivity.this, QuickPlayTwoPlayer.class);
                        // must pass the intent, as custom game is using same QuickPlayTwoPlayer.java too, hence this is how we separate if it is from quickplay or custom game
                        intent.putExtra("isitQuickPlayOrCustomGame", Constants.GAMEMODE_QUICKPLAY);
                        startActivity(intent);
                        if(quickplay_popupWindow != null) {
                            quickplay_popupWindow.dismiss();
                            quickplay_popupWindow = null;
                        }
                        break;
                    case Constants.MODE_THREEPLAYER:
                        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Quick_Play");
                        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Three_Players");
                        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, levelDifficulty);
                        mFirebaseAnalytics.logEvent("Start_Game", params);

                        Utils.setDifficultyPreference(MainActivity.this, quickplayLevelDifficultySelection);
                        setNoOfPlayersPreference(quickplayNumberOfPlayerSelection);
                        Intent intent2 = new Intent(MainActivity.this, QuickPlayThreePlayer.class);
                        intent2.putExtra("isitQuickPlayOrCustomGame", Constants.GAMEMODE_QUICKPLAY);
                        startActivity(intent2);
                        if(quickplay_popupWindow != null) {
                            quickplay_popupWindow.dismiss();
                            quickplay_popupWindow = null;
                        }
                        break;
                    case Constants.MODE_FOURPLAYER:
                        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Quick_Play");
                        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Four_Players");
                        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, levelDifficulty);
                        mFirebaseAnalytics.logEvent("Start_Game", params);

                        Utils.setDifficultyPreference(MainActivity.this, quickplayLevelDifficultySelection);
                        setNoOfPlayersPreference(quickplayNumberOfPlayerSelection);
                        Intent intent3 = new Intent(MainActivity.this, QuickPlayFourPlayer.class);
                        intent3.putExtra("isitQuickPlayOrCustomGame", Constants.GAMEMODE_QUICKPLAY);
                        startActivity(intent3);
                        if(quickplay_popupWindow != null) {
                            quickplay_popupWindow.dismiss();
                            quickplay_popupWindow = null;
                        }
                        break;

                }
            }
        });
    }

    // call this method and pass in the viewGroup associated with the respective xml
    // this method will initialize the "quick play pop up page" containing the 4 buttons difficulty, 3 buttons players, editplayer profile, and start button
    // currently it is used for both the quickplay popup, and 2nd page of customgame popup
    // ** Assuming we used the same ID for both the xml **
    private void initializeDifficultyNumberPlayersClickEvent(View popupView) {
        PercentRelativeLayout quickplay_EasyButton, quickplay_MediumButton, quickplay_HardButton, quickplay_InsaneButton;
        quickplay_EasyButton = (PercentRelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_easy_Button);
        quickplay_MediumButton = (PercentRelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_medium_Button);
        quickplay_HardButton = (PercentRelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_hard_Button);
        quickplay_InsaneButton = (PercentRelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_insane_Button);
        quickplayEasyIcon = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_easyicon);
        quickplayMediumIcon = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_mediumicon);
        quickplayHardIcon = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_hardicon);
        quickplayInsaneIcon = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_insaneicon);
        quickplay_easyText = (AutoResizeTextView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_easytext);
        quickplay_mediumText = (AutoResizeTextView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_mediumtext);
        quickplay_hardText = (AutoResizeTextView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_hardtext);
        quickplay_insaneText = (AutoResizeTextView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_insanetext);
        quickplayEasyRectangle = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_easybackground);
        quickplayMediumRectangle = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_mediumbackground);
        quickplayHardRectangle = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_hardbackground);
        quickplayInsaneRectangle = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_insanebackground);

        quickplayTwoPlayers_Button = (RelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_twoplayer_Button);
        quickplayThreePlayers_Button = (RelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_threeplayer_Button);
        quickplayFourPlayers_Button = (RelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_fourplayer_Button);
        quickplayTwoPlayerIcon = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_twoplayericon);
        quickplayThreePlayerIcon = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_threeplayericon);
        quickplayFourPlayerIcon = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_fourplayericon);
        quickplayTwoPlayerRectangle = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_twoplayerbackground);
        quickplayThreePlayerRectangle = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_threeplayerbackground);
        quickplayFourPlayerRectangle = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_fourplayerbackground);

        ImageView editPlayerName = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_editplayername_Button);
        editPlayerName.setOnClickListener(this);

        quickplay_EasyButton.setOnClickListener(this);
        quickplay_MediumButton.setOnClickListener(this);
        quickplay_HardButton.setOnClickListener(this);
        quickplayTwoPlayers_Button.setOnClickListener(this);
        quickplayThreePlayers_Button.setOnClickListener(this);
        quickplayFourPlayers_Button.setOnClickListener(this);


        if (Utils.hasPurchasedInsaneMode(this)) {
            // has purchased insane mode, hence unlock this mode
            ImageView lockIcon = (ImageView) popupView.findViewById(R.id.quickplay_insaneLockIcon);
            lockIcon.setVisibility(View.GONE);
            Drawable insaneIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.popup_difficultyinsaneicon, null);
            insaneIcon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.primaryOrangePopUp_LIGHT), PorterDuff.Mode.SRC_ATOP));
            quickplayInsaneIcon.setImageDrawable(insaneIcon);

            quickplay_InsaneButton.setOnClickListener(this);
        } else {
            // has not purchased insane mode, hence lock this mode
            Drawable insaneIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.popup_difficultyinsaneicon, null);
            insaneIcon.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#FFF3E0"), PorterDuff.Mode.SRC_ATOP));
            quickplayInsaneIcon.setImageDrawable(insaneIcon);

            quickplay_InsaneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundPoolManager.getInstance().playSound(0);;
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                    View insaneDialogView = inflater.inflate(R.layout.insane_customalertdialog, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(insaneDialogView);

                    final AlertDialog dialogInsaneMode = builder.create();
                    Window dialogWindow = dialogInsaneMode.getWindow();
                    dialogWindow.setBackgroundDrawable(new ColorDrawable(0));

                    dialogInsaneMode.setCancelable(true);
                    dialogInsaneMode.show();

                    //Grab the window of the dialog, and change the width
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialogWindow.getAttributes());
                    //This makes the dialog take up the full width
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialogWindow.setAttributes(lp);

                    RelativeLayout cancelButton = (RelativeLayout) insaneDialogView.findViewById(R.id.insanecustomlayout_cancelbutton);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogInsaneMode.dismiss();
                        }
                    });

                    RelativeLayout goToShopButton = (RelativeLayout) insaneDialogView.findViewById(R.id.insanecustomlayout_shopButton);
                    goToShopButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // set this var to false so only the general click sound is played, and no close menu sound is played on dialog dismiss listener
                            playCloseMenuSound = false;
                            SoundPoolManager.getInstance().playSound(0);
                            dialogInsaneMode.dismiss();
                            show_shopPopUp();
                        }
                    });

                    dialogInsaneMode.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (playCloseMenuSound) {
                                SoundPoolManager.getInstance().playSound(2);
                            }
                            // must reset it to true here in the event it got triggered by goToShopButton and playCloseMenuSound is set to false
                            // cannot reset it in goToShopButton place, becoz it might be set to true before dismiss has finish calling
                            playCloseMenuSound = true;
                        }
                    });
                }
            });
        }

        // set up the default difficulty based on what user was last at
        quickplay_popup_DifficultyHighlighter(Utils.getDifficultyPreference(MainActivity.this));

        // highlight the numberOfPlayers the user was last at, default to 2 players
        quickplayNumberOfPlayers_Highlighter(getNoOfPlayersPreference());

        // set the start button selector states
        Drawable startButtonDefault = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.popup_startbutton, null);
        startButtonDefault.mutate();
        startButtonDefault.setColorFilter(null);

        Drawable startButton_Selected = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.popup_startbutton, null);
        startButton_Selected.mutate();
        startButton_Selected.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage_DARK), PorterDuff.Mode.SRC_ATOP));

        StateListDrawable startButton_selectorStates = new StateListDrawable();
        startButton_selectorStates.addState(new int[]{android.R.attr.state_pressed}, startButton_Selected);
        startButton_selectorStates.addState(new int[]{android.R.attr.state_focused}, startButton_Selected);
        startButton_selectorStates.addState(new int[]{}, startButtonDefault);

        ImageView quickplayStart = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.quickplay_start_Button);
        quickplayStart.setBackground(startButton_selectorStates);
    }

    // set all state to inactive state except for the param state, which is highlighted
    private void quickplayNumberOfPlayers_Highlighter(int noOfPlayers) {
        // Highlight the param state, and set all other state to inactive
        switch (noOfPlayers) {
            case Constants.MODE_TWOPLAYER:
                quickplayTwoPlayerIcon.setColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));
                quickplayTwoPlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty_selected);

                quickplayThreePlayerIcon.setColorFilter(null);
                quickplayThreePlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayFourPlayerIcon.setColorFilter(null);
                quickplayFourPlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayNumberOfPlayerSelection = Constants.MODE_TWOPLAYER; // used for start button to go to 2 player mode
                break;
            case Constants.MODE_THREEPLAYER:
                quickplayThreePlayerIcon.setColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));
                quickplayThreePlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty_selected);

                quickplayTwoPlayerIcon.setColorFilter(null);
                quickplayTwoPlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayFourPlayerIcon.setColorFilter(null);
                quickplayFourPlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayNumberOfPlayerSelection = Constants.MODE_THREEPLAYER;
                break;
            case Constants.MODE_FOURPLAYER:
                quickplayFourPlayerIcon.setColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));
                quickplayFourPlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty_selected);

                quickplayTwoPlayerIcon.setColorFilter(null);
                quickplayTwoPlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayThreePlayerIcon.setColorFilter(null);
                quickplayThreePlayerRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayNumberOfPlayerSelection = Constants.MODE_FOURPLAYER;
                break;
        }
    }

    // highlight the difficulty that is passed in in param, and set all the rest to default inactive state
    private void quickplay_popup_DifficultyHighlighter(int difficulty) {
        int defaultOrangeLight = ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangePopUp_LIGHT);
        // set all to default inactive state, except for the param state which is set to active highlighted state
        switch (difficulty) {
            case Constants.MODE_EASY:
                quickplayEasyRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty_selected);
                quickplayEasyIcon.setColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));
                quickplay_easyText.setTextColor(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));

                quickplayMediumIcon.setColorFilter(null);
                quickplayHardIcon.setColorFilter(null);
                quickplayInsaneIcon.setColorFilter(null);
                quickplay_mediumText.setTextColor(defaultOrangeLight);
                quickplay_hardText.setTextColor(defaultOrangeLight);
                quickplay_insaneText.setTextColor(defaultOrangeLight);
                quickplayMediumRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayHardRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayInsaneRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayLevelDifficultySelection = Constants.MODE_EASY; // used in start button to determine what difficulty to go to in game mode
                break;
            case Constants.MODE_MEDIUM:
                quickplayMediumRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty_selected);
                quickplayMediumIcon.setColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));
                quickplay_mediumText.setTextColor(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));

                quickplayEasyIcon.setColorFilter(null);
                quickplayHardIcon.setColorFilter(null);
                quickplayInsaneIcon.setColorFilter(null);
                quickplay_easyText.setTextColor(defaultOrangeLight);
                quickplay_hardText.setTextColor(defaultOrangeLight);
                quickplay_insaneText.setTextColor(defaultOrangeLight);
                quickplayEasyRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayHardRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayInsaneRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayLevelDifficultySelection = Constants.MODE_MEDIUM;
                break;
            case Constants.MODE_HARD:
                quickplayHardRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty_selected);
                quickplayHardIcon.setColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));
                quickplay_hardText.setTextColor(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));

                quickplayEasyIcon.setColorFilter(null);
                quickplayMediumIcon.setColorFilter(null);
                quickplayInsaneIcon.setColorFilter(null);
                quickplay_easyText.setTextColor(defaultOrangeLight);
                quickplay_mediumText.setTextColor(defaultOrangeLight);
                quickplay_insaneText.setTextColor(defaultOrangeLight);
                quickplayEasyRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayMediumRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayInsaneRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayLevelDifficultySelection = Constants.MODE_HARD;
                break;
            case Constants.MODE_INSANE:
                quickplayInsaneRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty_selected);
                quickplayInsaneIcon.setColorFilter(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));
                quickplay_insaneText.setTextColor(ContextCompat.getColor(this, com.react.reactmultiplayergame.R.color.primaryOrangeForMainPage));

                quickplayEasyIcon.setColorFilter(null);
                quickplayMediumIcon.setColorFilter(null);
                quickplayHardIcon.setColorFilter(null);
                quickplay_easyText.setTextColor(defaultOrangeLight);
                quickplay_mediumText.setTextColor(defaultOrangeLight);
                quickplay_hardText.setTextColor(defaultOrangeLight);
                quickplayEasyRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayMediumRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayHardRectangle.setBackgroundResource(com.react.reactmultiplayergame.R.drawable.roundedrectangle_popupdifficulty);
                quickplayLevelDifficultySelection = Constants.MODE_INSANE;
                break;
        }
    }


    private void init_fourMainButtons_layout() {
        Typeface tf = Typeface.create("sans-serif-condensed", Typeface.BOLD_ITALIC);
        // run these in post method runnable so getWidth and getHeight will not return 0
        int containerWidth = fourbuttons_overallContainer.getWidth();
        int containerHeight = fourbuttons_overallContainer.getHeight() / 2;

        int buttonWidth = (int) (0.84 * containerWidth / 2); // 84% of half of the total width, leaving 16% margin space
        int buttonHeight = (int) (0.8 * containerHeight); // 20% margin space
        // 12% of half of the width, meaning set left by 12%, leaving 4% for the right since in total there is 16% margin space
        // hence at the end of margin, there will be 12% of half of width for both left and right margin, and center separation will be total 8% (4% *2)
        int marginButton = (int) (0.12 * containerWidth / 2);

        Drawable buttonBackgroundNormal = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.homepage_quickplay, null);
        buttonBackgroundNormal.mutate();
        buttonBackgroundNormal.setColorFilter(null);

        Drawable buttonBackgroundDarken = ResourcesCompat.getDrawable(getResources(), com.react.reactmultiplayergame.R.drawable.homepage_quickplay, null);
        buttonBackgroundDarken.mutate();
        buttonBackgroundDarken.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#e9ecf3"), PorterDuff.Mode.MULTIPLY));

        // need to create 4 different statesArray for the 4 different button, since we cannot use the same one throughout all 4
        StateListDrawable[] statesArray = new StateListDrawable[4];
        for (int i = 0; i < statesArray.length; i++) {
            statesArray[i] = new StateListDrawable();
            statesArray[i].addState(new int[]{android.R.attr.state_pressed}, buttonBackgroundDarken);
            statesArray[i].addState(new int[]{android.R.attr.state_focused}, buttonBackgroundDarken);
            statesArray[i].addState(new int[]{}, buttonBackgroundNormal);
        }


        quickplayButton.setLayoutParams(new LinearLayout.LayoutParams(containerWidth / 2, containerHeight));
        customGameButton.setLayoutParams(new LinearLayout.LayoutParams(containerWidth / 2, containerHeight));
        shopButton.setLayoutParams(new LinearLayout.LayoutParams(containerWidth / 2, containerHeight));
        rateThisAppButton.setLayoutParams(new LinearLayout.LayoutParams(containerWidth / 2, containerHeight));

        // ** Start of Quick Play layout ** //
        ImageView quickplayBackground = new ImageView(MainActivity.this);
        quickplayBackground.setBackground(statesArray[0]); // manually set statesArray[0], [1], [2], [3] to all the 4 elements
        quickplayBackground.setScaleType(ImageView.ScaleType.FIT_XY);

        RelativeLayout.LayoutParams quickplayImage_LayoutParam = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
        quickplayImage_LayoutParam.addRule(RelativeLayout.CENTER_VERTICAL);
        quickplayImage_LayoutParam.setMargins(marginButton, 0, 0, 0);

        TextView quickPlay_text = new TextView(MainActivity.this);
        quickPlay_text.setText(com.react.reactmultiplayergame.R.string.quick_play);
        quickPlay_text.setGravity(Gravity.CENTER_VERTICAL);
        quickPlay_text.setSingleLine();
        quickPlay_text.setTextColor(orangeColor);
        quickPlay_text.setTypeface(tf);
        quickPlay_text.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
        // buttonHeight/4 should be safe, and with Autofit text it will ensure editText text size no large than 1 singleline
        quickPlay_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonHeight / 4);
        AutofitHelper.create(quickPlay_text);

        // text occupy 50% of the button, while the icon occupy 40%, remaining 10% is the space from text to the edge, so that text does not cut off at edge
        RelativeLayout.LayoutParams quickplayText_Param = new RelativeLayout.LayoutParams((int) (0.5 * buttonWidth), buttonHeight);
        quickplayText_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        quickplayText_Param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // marginText 0.04 is the available spacing from right , until it aligns with the background relativelayout button (see above comment "leaving 4% for right")
        // marginText moves another 10% of buttonWidth so that text does not write until the edge, but instead at most 10% away from edge
        quickplayText_Param.rightMargin = (int) (0.04 * containerWidth / 2 + 0.1 * buttonWidth);

        // icon occupies 20% of the 40% space available. Margin it by 15% of button width, so there is 5% left on the right side and not too close to text
        ImageView quickplayIcon = new ImageView(MainActivity.this);
        quickplayIcon.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_quickplayicon);

        RelativeLayout.LayoutParams quickplayIcon_Param = new RelativeLayout.LayoutParams((int) (0.2 * buttonWidth), (int) (0.2 * buttonWidth));
        quickplayIcon_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        quickplayIcon_Param.leftMargin = (int) (marginButton + 0.15 * buttonWidth); // move by marginButton, and add additional 15% of button width to have editText margin left


        // ** Start of Custom Game layout ** //
        ImageView customGameBackground = new ImageView(MainActivity.this);
        customGameBackground.setBackground(statesArray[1]);
        customGameBackground.setRotationY(180); // flip this vertically
        customGameBackground.setScaleType(ImageView.ScaleType.FIT_XY);

        RelativeLayout.LayoutParams customGameImage_LayoutParam = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
        customGameImage_LayoutParam.addRule(RelativeLayout.CENTER_VERTICAL);
        customGameImage_LayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        customGameImage_LayoutParam.rightMargin = marginButton;

        TextView customGame_text = new TextView(MainActivity.this);
        customGame_text.setText(com.react.reactmultiplayergame.R.string.custom_game);
        customGame_text.setGravity(Gravity.CENTER_VERTICAL);
        customGame_text.setSingleLine();
        customGame_text.setTextColor(orangeColor);
        customGame_text.setTypeface(tf);
        customGame_text.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
        // buttonHeight/4 should be safe, and with Autofit text it will ensure editText text size no large than 1 singleline
        customGame_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonHeight / 4);
        AutofitHelper.create(customGame_text);

        RelativeLayout.LayoutParams customGameText_Param = new RelativeLayout.LayoutParams((int) (0.6 * buttonWidth), buttonHeight);
        customGameText_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        customGameText_Param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // 0.12 is the amount shifted by the background button, and additioal 0.07 button width shift so text does not go to the edge
        customGameText_Param.rightMargin = (int) (0.12 * containerWidth / 2 + 0.07 * buttonWidth);

        ImageView customGameIcon = new ImageView(MainActivity.this);
        customGameIcon.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_customgameicon);

        RelativeLayout.LayoutParams customGameIcon_Param = new RelativeLayout.LayoutParams((int) (0.2 * buttonWidth), (int) (0.2 * buttonWidth));
        customGameIcon_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        customGameIcon_Param.leftMargin = (int) (0.04 * containerWidth / 2 + +0.1 * buttonWidth);

        // ** start of shop layout ** //

        ImageView shopBackground = new ImageView(MainActivity.this);
        shopBackground.setBackground(statesArray[2]);
        shopBackground.setScaleType(ImageView.ScaleType.FIT_XY);

        RelativeLayout.LayoutParams shopImage_LayoutParam = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
        shopImage_LayoutParam.addRule(RelativeLayout.CENTER_VERTICAL);
        shopImage_LayoutParam.setMargins(marginButton, 0, 0, 0);

        TextView shop_text = new TextView(MainActivity.this);
        shop_text.setText(com.react.reactmultiplayergame.R.string.shop);
        shop_text.setGravity(Gravity.CENTER_VERTICAL);
        shop_text.setSingleLine();
        shop_text.setTextColor(orangeColor);
        shop_text.setTypeface(tf);
        shop_text.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
        // buttonHeight/4 should be safe, and with Autofit text it will ensure editText text size no large than 1 singleline
        shop_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonHeight / 4);
        AutofitHelper.create(shop_text);

        RelativeLayout.LayoutParams shopText_Param = new RelativeLayout.LayoutParams((int) (0.4 * buttonWidth), buttonHeight);
        shopText_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        shopText_Param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // marginText 0.04 is the available spacing from right , until it aligns with the background relativelayout button (see above comment "leaving 4% for right")
        // marginText moves another 10% of buttonWidth so that text does not write until the edge, but instead at most 10% away from edge
        shopText_Param.rightMargin = (int) (0.04 * containerWidth / 2 + 0.1 * buttonWidth);

        ImageView shopIcon = new ImageView(MainActivity.this);
        shopIcon.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_shopicon);

        RelativeLayout.LayoutParams shopIcon_Param = new RelativeLayout.LayoutParams((int) (0.2 * buttonWidth), (int) (0.2 * buttonWidth));
        shopIcon_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        shopIcon_Param.leftMargin = (int) (marginButton + 0.25 * buttonWidth);

        // ** start of rate this app button ** //

        ImageView rateThisAppBackground = new ImageView(MainActivity.this);
        rateThisAppBackground.setBackground(statesArray[3]);
        rateThisAppBackground.setRotationY(180);
        rateThisAppBackground.setScaleType(ImageView.ScaleType.FIT_XY);

        RelativeLayout.LayoutParams rateThisAppImage_LayoutParam = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
        rateThisAppImage_LayoutParam.addRule(RelativeLayout.CENTER_VERTICAL);
        rateThisAppImage_LayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rateThisAppImage_LayoutParam.rightMargin = marginButton;

        TextView rateThisApp_text = new TextView(MainActivity.this);
        rateThisApp_text.setText(com.react.reactmultiplayergame.R.string.rate_this_app);
        rateThisApp_text.setGravity(Gravity.CENTER_VERTICAL);
        rateThisApp_text.setSingleLine();
        rateThisApp_text.setTextColor(orangeColor);
        rateThisApp_text.setTypeface(tf);
        rateThisApp_text.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
        // buttonHeight/4 should be safe, and with Autofit text it will ensure editText text size no large than 1 singleline
        rateThisApp_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonHeight / 4);
        AutofitHelper.create(rateThisApp_text);

        RelativeLayout.LayoutParams rateThisAppText_Param = new RelativeLayout.LayoutParams((int) (0.6 * buttonWidth), buttonHeight);
        rateThisAppText_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        rateThisAppText_Param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // 0.12 is the amount shifted by the background button, and additioal 0.07 button width shift so text does not go to the edge
        rateThisAppText_Param.rightMargin = (int) (0.12 * containerWidth / 2 + 0.07 * buttonWidth);

        ImageView rateThisAppIcon = new ImageView(MainActivity.this);
        rateThisAppIcon.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_ratethisappicon);

        RelativeLayout.LayoutParams rateThisAppIcon_Param = new RelativeLayout.LayoutParams((int) (0.2 * buttonWidth), (int) (0.2 * buttonWidth));
        rateThisAppIcon_Param.addRule(RelativeLayout.CENTER_VERTICAL);
        rateThisAppIcon_Param.leftMargin = (int) (0.04 * containerWidth / 2 + +0.1 * buttonWidth);


        // *** start of add view *** //

        quickplayButton.addView(quickplayBackground, quickplayImage_LayoutParam);
        quickplayButton.addView(quickPlay_text, quickplayText_Param);
        quickplayButton.addView(quickplayIcon, quickplayIcon_Param);
        customGameButton.addView(customGameBackground, customGameImage_LayoutParam);
        customGameButton.addView(customGame_text, customGameText_Param);
        customGameButton.addView(customGameIcon, customGameIcon_Param);
        shopButton.addView(shopBackground, shopImage_LayoutParam);
        shopButton.addView(shop_text, shopText_Param);
        shopButton.addView(shopIcon, shopIcon_Param);
        rateThisAppButton.addView(rateThisAppBackground, rateThisAppImage_LayoutParam);
        rateThisAppButton.addView(rateThisApp_text, rateThisAppText_Param);
        rateThisAppButton.addView(rateThisAppIcon, rateThisAppIcon_Param);


        LinearLayout topHalfContainer = new LinearLayout(MainActivity.this);
        topHalfContainer.setOrientation(LinearLayout.HORIZONTAL);
        topHalfContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, containerHeight));
        topHalfContainer.addView(quickplayButton);
        topHalfContainer.addView(customGameButton);

        LinearLayout bottomHalfContainer = new LinearLayout(MainActivity.this);
        bottomHalfContainer.setOrientation(LinearLayout.HORIZONTAL);
        bottomHalfContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, containerHeight));
        bottomHalfContainer.addView(shopButton);
        bottomHalfContainer.addView(rateThisAppButton);

        LinearLayout overallContainer = new LinearLayout(this);
        overallContainer.setOrientation(LinearLayout.VERTICAL);
        overallContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        overallContainer.addView(topHalfContainer);
        overallContainer.addView(bottomHalfContainer);
        fourbuttons_overallContainer.addView(overallContainer);

    }

    private String jumbleUp_APIKey(String currentString, int incrementNumber) {
        String bar = "";
        for (char c : currentString.toCharArray()) {
            bar += Character.toString((char) (((c + incrementNumber))));
        }
        return bar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case com.react.reactmultiplayergame.R.id.quickplay_easy_Button:
                // reason not to set the playSound in the Highlighter method is becoz the Highlighter method is also called
                // in the onCreate method for the first time, and hence 2 sound will be played (pop up window + click sound) when u open the pop up window
                SoundPoolManager.getInstance().playSound(0); // play general click sound
                quickplay_popup_DifficultyHighlighter(Constants.MODE_EASY);
                break;

            case com.react.reactmultiplayergame.R.id.quickplay_medium_Button:
                SoundPoolManager.getInstance().playSound(0); // play general click sound
                quickplay_popup_DifficultyHighlighter(Constants.MODE_MEDIUM);
                break;

            case com.react.reactmultiplayergame.R.id.quickplay_hard_Button:
                SoundPoolManager.getInstance().playSound(0); // play general click sound
                quickplay_popup_DifficultyHighlighter(Constants.MODE_HARD);
                break;

            case com.react.reactmultiplayergame.R.id.quickplay_insane_Button:
                SoundPoolManager.getInstance().playSound(0); // play general click sound
                quickplay_popup_DifficultyHighlighter(Constants.MODE_INSANE);
                break;


            case com.react.reactmultiplayergame.R.id.quickplay_twoplayer_Button:
                SoundPoolManager.getInstance().playSound(0); // play general click sound
                quickplayNumberOfPlayers_Highlighter(Constants.MODE_TWOPLAYER);
                break;

            case com.react.reactmultiplayergame.R.id.quickplay_threeplayer_Button:
                SoundPoolManager.getInstance().playSound(0); // play general click sound
                quickplayNumberOfPlayers_Highlighter(Constants.MODE_THREEPLAYER);
                break;

            case com.react.reactmultiplayergame.R.id.quickplay_fourplayer_Button:
                SoundPoolManager.getInstance().playSound(0); // play general click sound
                quickplayNumberOfPlayers_Highlighter(Constants.MODE_FOURPLAYER);
                break;


            case com.react.reactmultiplayergame.R.id.quickplaypopup_emptyContainer1:
            case com.react.reactmultiplayergame.R.id.quickplaypopup_emptyContainer2:
            case com.react.reactmultiplayergame.R.id.quickplaypopup_emptyContainer3:
            case com.react.reactmultiplayergame.R.id.quickplay_popupClose:
                dismissPopUpWindow(0);
                break;

            case com.react.reactmultiplayergame.R.id.homepageMute:
                // set to sharedPreference if we should mute globally, and other sound player will reference from Utils.getIfSoundIsMuted to decide if it should play sound ornot
                if (Utils.getIfSoundIsMuted(MainActivity.this)) {
                    // set it to become no longer muted, i.e. playing sound
                    Utils.setIfSoundIsMuted(MainActivity.this, false);
                    SoundPoolManager.getInstance().playSound(0); // play general click sound
                    muteSoundButton.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_soundplayingselector);
                    MusicManager.playMediaPlayer();
                } else {
                    // set it to muted, i.e. no longer playing sound
                    Utils.setIfSoundIsMuted(MainActivity.this, true);
                    muteSoundButton.setImageResource(com.react.reactmultiplayergame.R.drawable.homepage_soundmuteselector);
                    MusicManager.pauseMediaPlayer();
                }
                break;

            case com.react.reactmultiplayergame.R.id.quickplay_editplayername_Button:
                initializeEditPlayerProfileScreen();
                break;

            case com.react.reactmultiplayergame.R.id.editplayername_emptyContainer1:
            case com.react.reactmultiplayergame.R.id.editplayername_emptyContainer2:
            case com.react.reactmultiplayergame.R.id.editplayername_emptyContainer3:
            case com.react.reactmultiplayergame.R.id.editplayername_popupClose:
                dismissPopUpWindow(1); // dismiss only edit play name and reset the close button at the back popup window to visible
                if (quickplay_close != null)
                    quickplay_close.setAlpha(1f);
                break;

            case com.react.reactmultiplayergame.R.id.editplayername_cancelButton:
                if (quickplay_close != null)
                    quickplay_close.setAlpha(1f); // set the quickplay_close back to view again since we set it to 0 when the editplayerprofile popup appears
                dismissPopUpWindow(2); // only dismiss the editplayername popup
                break;

            case com.react.reactmultiplayergame.R.id.editplayername_doneButton:
                mFirebaseAnalytics.logEvent("EditPlayerProfile_Done", null);

                if (quickplay_close != null)
                    quickplay_close.setAlpha(1f); // set the quickplay_close back to view again since we set it to 0 when the editplayerprofile popup appears
                // add customized name to sharedPreference, which will be retrieve later during QuickPlayTwoPlayer.java to set the player name textView
                int[] player = {Constants.PLAYER1, Constants.PLAYER2, Constants.PLAYER3, Constants.PLAYER4};
                int[] colorTag = {EditPlayerProfileAdapter.getColorTag_p1, EditPlayerProfileAdapter.getColorTag_p2,
                        EditPlayerProfileAdapter.getColorTag_p3, EditPlayerProfileAdapter.getColorTag_p4};
                for (int i = 0; i < 4; i++) {
                    // iterate 4 times for 4 players
                    if (EditPlayerProfileAdapter.getEditTextArray()[i] != null)
                        Utils.setPlayerName(MainActivity.this, player[i], EditPlayerProfileAdapter.getEditTextArray()[i].getText().toString());

                    Utils.setPlayerColorTag(MainActivity.this, player[i], colorTag[i]);
                }
                // dont want to play close menu sound, used as a condition in onDismissListener
                playCloseMenuSound = false;
                SoundPoolManager.getInstance().playSound(0); // instead we play general click sound
                dismissPopUpWindow(2);
                break;


            // SHOP //
            case R.id.shop_emptyContainer1:
            case R.id.shop_emptyContainer2:
            case R.id.shop_emptyContainer3:
            case R.id.shop_popupClose:
                dismissPopUpWindow(3);
                break;

            case R.id.shop_findoutmoreTextView:
                SoundPoolManager.getInstance().playSound(0);
                // set this field to true and music player will not be paused
                MusicManager.isGoingNextActivity = true;
                Intent intent = new Intent(MainActivity.this, ShopFindOutMore.class);
                startActivity(intent);
                break;

            case R.id.shop_restoreYourPurchase:
                // first check if there is network connection
                ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    // there is internet connection. Proceed.
                    bp.loadOwnedPurchasesFromGoogle();
                    boolean hasPurchasedEither = false;
                    if(bp.isPurchased(Constants.iap_sku_Ads)) {
                        // user has purchased ads before
                        Utils.setPurchasedRemoveAds(MainActivity.this, true);
                        if (shopPopUpWindow != null) {
                            dismissPopUpWindow(3);
                        }
                        if (nativeAd != null && nativeAd.isAdLoaded()) {
                            nativeAd.destroy();
                            nativeAd = null;
                            LinearLayout adsContainer = (LinearLayout) findViewById(R.id.ads_container);
                            adsContainer.setVisibility(View.INVISIBLE);
                        }
                        hasPurchasedEither = true;
                    }
                    if(bp.isPurchased(Constants.iap_sku_InsaneMode)) {
                        // user has purchased insaneModeBefore
                        Utils.setPurchasedInsaneMode(this, true);
                        if (shopPopUpWindow != null) {
                            dismissPopUpWindow(3);
                        }
                        hasPurchasedEither = true;
                    }

                    if(hasPurchasedEither) {
                        // show alertdialog
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setView(inflater.inflate(R.layout.successfullyrestored_alertdialog, null));

                        final AlertDialog alertDialogSuccessfullyPurchased = builder.create();
                        Window dialogWindow = alertDialogSuccessfullyPurchased.getWindow();
                        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));

                        alertDialogSuccessfullyPurchased.setCancelable(true);
                        alertDialogSuccessfullyPurchased.show();

                        //Grab the window of the dialog, and change the width
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialogWindow.getAttributes());
                        //This makes the dialog take up the full width
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialogWindow.setAttributes(lp);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.restorepurchasednopurchase), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // no network connection
                    Toast.makeText(MainActivity.this, getString(R.string.restorepurchasednointernet), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.shop_removeAds_BUYNOWButton:
                bp.purchase(this, Constants.iap_sku_Ads);
                break;

            case R.id.shop_unlockInsane_BUYNOWButton:
                bp.purchase(this, Constants.iap_sku_InsaneMode);
                break;

            // Custom Game //
            case R.id.customgame_emptyContainer1:
            case R.id.customgame_emptyContainer2:
            case R.id.customgame_emptyContainer3:
            case R.id.customgame_popupClose:
                dismissPopUpWindow(4);
                break;

            default:
                break;
        }
    }

    private String thirdpart_APIKey() {
        return obsfucate_publicAPIKey(jumbleUp_APIKey(subset_APIKey_beforeDash(fakeAPIKey, 5), -1).getBytes()) + Constants.dash
                + obsfucate_publicAPIKey(jumbleUp_APIKey(subset_APIKey_beforeDash(fakeAPIKey, 6), -1).getBytes()) + Constants.dash;
    }

    // used to determine which numberOfPlayers mode the user was at just now
    // e.g. if user played 3 player just now, upon quitting and reopening the pop up again, the default shown will be 3 players now
    private void setNoOfPlayersPreference(int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("NumberOfPlayers_Preference", value);
        editor.apply();
    }

    private int getNoOfPlayersPreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        return preferences.getInt("NumberOfPlayers_Preference", Constants.MODE_TWOPLAYER);
    }

    private void initializeEditPlayerProfileScreen() {
        SoundPoolManager.getInstance().playSound(0);
        // launch edit player profile screen
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(com.react.reactmultiplayergame.R.layout.quickplay_popup_playerprofile, null);

        quickplay_editplayerPopUpWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        quickplay_editplayerPopUpWindow.setTouchable(true);
        quickplay_editplayerPopUpWindow.setFocusable(true);
        quickplay_editplayerPopUpWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // so that onbackpress can close the popupwindow

        quickplay_editplayerPopUpWindow.showAtLocation(popupView, Gravity.FILL, 0, 0);

        quickplay_editplayerPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // we dont play close menu sound if "Done" is pressed. Otherwise we play close menu sound
                if (playCloseMenuSound) {
                    SoundPoolManager.getInstance().playSound(2);
                }
                // reinitialize in the event it was triggered by "Done" to set it to false
                playCloseMenuSound = true;
            }
        });

        final LinearLayout editplayerName_emptyContainer1, editplayerName_emptyContainer2, editplayerName_emptyContainer3;
        editplayerName_emptyContainer1 = (LinearLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_emptyContainer1);
        editplayerName_emptyContainer2 = (LinearLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_emptyContainer2);
        editplayerName_emptyContainer3 = (LinearLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_emptyContainer3);
        editplayerName_emptyContainer1.setOnClickListener(MainActivity.this);
        editplayerName_emptyContainer2.setOnClickListener(MainActivity.this);
        editplayerName_emptyContainer3.setOnClickListener(MainActivity.this);

        final ImageView editplayerName_close = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_popupClose);
        editplayerName_close.setOnClickListener(MainActivity.this);

        RelativeLayout editplayerName_CancelButton = (RelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_cancelButton);
        RelativeLayout editplayerName_DoneButton = (RelativeLayout) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_doneButton);
        editplayerName_CancelButton.setOnClickListener(MainActivity.this);
        editplayerName_DoneButton.setOnClickListener(MainActivity.this);

        ImageView editplayername_leftArrow = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_playerleftarrow);
        ImageView editplayername_rightArrow = (ImageView) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_playerrightarrow);

        final AutoResizeTextView editplayername_currentPlayer = (AutoResizeTextView) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_currentplayer);
        final ViewPager viewPager = (ViewPager) popupView.findViewById(com.react.reactmultiplayergame.R.id.editplayername_viewpagerContainer);
        viewPager.setAdapter(new EditPlayerProfileAdapter(this));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SoundPoolManager.getInstance().playSound(3);
                switch (position) {
                    case 0:
                        editplayername_currentPlayer.setText(com.react.reactmultiplayergame.R.string.player_1);
                        break;
                    case 1:
                        editplayername_currentPlayer.setText(com.react.reactmultiplayergame.R.string.player_2);
                        break;
                    case 2:
                        editplayername_currentPlayer.setText(com.react.reactmultiplayergame.R.string.player_3);
                        break;
                    case 3:
                        editplayername_currentPlayer.setText(com.react.reactmultiplayergame.R.string.player_4);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // basically what we doing here is that when we click on edit text, we want to set the outside empty container to become focusable so that when we click outside,
        // it will gain focus and close the keyboard (at this time the click outside will not trigger dismiss popup. Only the 2nd time u click outside will trigger dismiss popup.
        // the 2nd method inside here is that in the event user did not click outside but click inside the popup to dismiss the keyboard, we must set the outside container
        // focus to false to allow them to click outside container to dismiss.
        // what happens is if u set focusable in touchmode to true, u must click twice on the container to close it.
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<EditPlayerProfileAdapter.getEditTextArray().length; i++) {
                    EditPlayerProfileAdapter.getEditTextArray()[i].setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            editplayerName_emptyContainer1.setFocusableInTouchMode(true);
                            editplayerName_emptyContainer2.setFocusableInTouchMode(true);
                            editplayerName_emptyContainer2.setFocusableInTouchMode(true);
                            editplayerName_close.setFocusableInTouchMode(true);
                            return false;
                        }
                    });

                    // method to force the softkeyboard to close when touched outside of the keyboard
                    EditPlayerProfileAdapter.getEditTextArray()[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                editplayerName_emptyContainer1.setFocusableInTouchMode(false);
                                editplayerName_emptyContainer2.setFocusableInTouchMode(false);
                                editplayerName_emptyContainer2.setFocusableInTouchMode(false);
                                editplayerName_close.setFocusableInTouchMode(false);
                            }
                        }
                    });
                }
            }
        });


        editplayername_leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab > 0) {
                    // decrease the tab to go to previous page
                    viewPager.setCurrentItem(--tab);
                } else if (tab == 0) {
                    // if at p1 first position, send to last position p4
                    viewPager.setCurrentItem(3);
                }
            }
        });

        editplayername_rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab == 3) {
                    // at p4, hence transit to p1 upon right arrow
                    viewPager.setCurrentItem(0);
                } else {
                    // else increase the tab
                    viewPager.setCurrentItem(++tab);
                }
            }
        });

        // set the close to disappear so that during editText when the view is push up, we will not see 2 close image
        if (quickplay_close != null)
            quickplay_close.setAlpha(0f);
    }

    // dismiss the quickplay/ customgame/ shop pop up window depending on the param passed
    private void dismissPopUpWindow(int popUpWindowToDismiss) {
        // here we dont play dismiss popup sound at all.
        // All the dismiss popup sound is handled in popup.onDismissListener to also take into account dismiss by back press
        // In the event of special case like "Done" button whereby popup is dismiss but we want to play general click sound instead,
        // that is handled in the respective "Done" button on click listener by setting a boolean flag of playCloseMenuSound
        switch (popUpWindowToDismiss) {
            case 0:
                // quick play button popup window
                if(quickplay_popupWindow != null) { // add a null check in case somehow user can execute this twice and cause a NPE
                    quickplay_popupWindow.dismiss();
                    quickplay_popupWindow = null;
                }
                break;
            case 1:
                // same as case 2 becoz last time this was actually dismiss both. But then changed to dismiss 1, and we did not reformat the case
                if(quickplay_editplayerPopUpWindow != null) {
                    quickplay_editplayerPopUpWindow.dismiss();
                    quickplay_editplayerPopUpWindow = null;
                }
                break;
            case 2:
                // quick play edit player name. ONLY dismiss edit player name, i.e. for the cancel and done button in player profile
                if(quickplay_editplayerPopUpWindow != null) {
                    quickplay_editplayerPopUpWindow.dismiss();
                    quickplay_editplayerPopUpWindow = null;
                }
                break;
            case 3:
                // shop popup dismiss
                if(shopPopUpWindow != null) {
                    shopPopUpWindow.dismiss();
                    shopPopUpWindow = null; // impt to set to null to prevent memory leak
                }
                break;
            case 4:
                // customgame popup dismiss
                if(customgame_popupWindow != null) {
                    customgame_popupWindow.dismiss();
                    customgame_popupWindow = null;
                }
                break;
        }

    }


    @Override
    protected void onPause() {
        // when we go to the game mode activity, current activity will be onPause.
        // this also takes into account user press home button or take phone call e.g, transitting away from the app,
        // hence we stop all background sound
        super.onPause();

        // we do not set isGoingNextActivity to true when user start game, hence this method will be called and the media player is auto close.
        // we only set this field to true when user go to pages like settings and findoutmore insane where we still want to continue background music
        if(!MusicManager.isGoingNextActivity) {
            // it is not activated by going next activity, i.e. this call here is activated by using pressing home or similar
            // hence we stop media player
            MusicManager.stopMediaPlayer();
        }
    }

    @Override
    protected void onResume() {
        // when we press back from game mode, this activity will onResume from onPause, and we need to reinstate all soundPool resources again since we released onPause
        super.onResume();

        if(!MusicManager.isGoingNextActivity){
            // this is only called on first oncreate, or after user press home btn and back to the game agn, which means mediaplayer is paused and now needs to be played agn
            MusicManager.initializeAndPlayMediaPlayer(getApplicationContext(), isSoundMuted());
        }

        // we have to reinstatiate this flag to false agn in case user set it to true when they going to next activity. If user set it to true when going next activity such as settings,
        // on pause stop music player will not be called. But when that activity pops off the stack, we do not want onResume here to initializeMediaPlayer again (the previous line).
        // hence since flag is still true, we did not reinitialize. But we have to set it to false agn so that on user home press it is reinitialize.
        MusicManager.isGoingNextActivity = false;


        if(!Utils.hasPurchasedRemoveAds(this)) {
            if (loadAdError) {
                // wont get call on first start becos loadAdError is false. Hence we put one more showNativeAd itself at onCreate
                // this call on resume tells if native ad is loaded previously. Boolean loadAdError is set to true if there is call to onAdError, and false if ad loaded successfully.
                // Therefore if previously the ad failed to load, onResume it will try to load again. If previously ads has succeed, then loadAdError is false and it will not try
                // to load again here
                showNativeAd();
            }
        }

    }

    private String constructActual_APIKey(){
        // call this method to get the ACTUAL Correct API Key
        // The rest of the parts that construct the actual API key are split over multiple area to cause more confusion
        String actualKey = "";
        actualKey += Utils.firstPart_APIKey(); // first part
        actualKey += Constants.dash;
        actualKey += secondpart_APIKey();
        actualKey += thirdpart_APIKey();
        actualKey += obsfucate_publicAPIKey(jumbleUp_APIKey(subset_APIKey_beforeDash(fakeAPIKey, 7), -1).getBytes());
        actualKey += Utils.lastPart_APIKey();

        return actualKey;
    }


    private boolean isSoundMuted() {
        return Utils.getIfSoundIsMuted(MainActivity.this);
    }

    private void calculateScreenSize() {
        Display display = MainActivity.this.getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            if (realMetrics.widthPixels > 0 && realMetrics.heightPixels > 0) {
                Utils.setCurrentDeviceWidth(this, realMetrics.widthPixels);
                Utils.setCurrentDeviceHeight(this, realMetrics.heightPixels);
            } else {
                // something wrong with the above method, try the method below for backup
                try {
                    Method mGetRawH = Display.class.getMethod("getRawHeight");
                    Method mGetRawW = Display.class.getMethod("getRawWidth");
                    if ((Integer) mGetRawW.invoke(display) > 0 && (Integer) mGetRawH.invoke(display) > 0) {
                        Utils.setCurrentDeviceWidth(this, (Integer) mGetRawW.invoke(display));
                        Utils.setCurrentDeviceHeight(this, (Integer) mGetRawH.invoke(display));
                    } else {
                        // try another method...
                        Utils.setCurrentDeviceWidth(this, Resources.getSystem().getDisplayMetrics().widthPixels);
                        Utils.setCurrentDeviceHeight(this, Resources.getSystem().getDisplayMetrics().heightPixels);
                    }
                } catch (Exception e) {
                    //this may not be 100% accurate, but it's all we've got
                    Utils.setCurrentDeviceWidth(this, display.getWidth());
                    Utils.setCurrentDeviceHeight(this, display.getHeight());
                }
            }

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");

                if ((Integer) mGetRawW.invoke(display) > 0 && (Integer) mGetRawH.invoke(display) > 0) {
                    Utils.setCurrentDeviceWidth(this, (Integer) mGetRawW.invoke(display));
                    Utils.setCurrentDeviceHeight(this, (Integer) mGetRawH.invoke(display));
                } else {
                    // try another method...
                    Utils.setCurrentDeviceWidth(this, Resources.getSystem().getDisplayMetrics().widthPixels);
                    Utils.setCurrentDeviceHeight(this, Resources.getSystem().getDisplayMetrics().heightPixels);
                }
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                Utils.setCurrentDeviceWidth(this, display.getWidth());
                Utils.setCurrentDeviceHeight(this, display.getHeight());

            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            Utils.setCurrentDeviceWidth(this, display.getWidth());
            Utils.setCurrentDeviceHeight(this, display.getHeight());
        }

        if(Utils.getCurrentDeviceHeight(this) > 0 && Utils.getCurrentDeviceHeight(this) > 0) {
            // successfully measured, hence we can set this flag to true.
            // If somehow one of them is not measured, i.e. <= 0 in value, then we do not set this flag to true. And the next time
            // We call oncreate again, we try to measure again since this flag is still false
            Utils.sethasMeasuredDeviceSize(MainActivity.this, true);
        }

    }

    private void show_shopPopUp() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView;
        if (!Utils.hasPurchasedRemoveAds(this) && !Utils.hasPurchasedInsaneMode(this)) {
            // has not purchased ads, has not purchase insane mode, i.e. has not purchase anything
            popupView = layoutInflater.inflate(R.layout.shop_popup, null);
        } else if (Utils.hasPurchasedRemoveAds(this) && !Utils.hasPurchasedInsaneMode(this)) {
            // only purchased before ads, hence show the layout with gray out ads
            popupView = layoutInflater.inflate(R.layout.shop_popup_purchasedremovedads, null);

            Drawable removeAdsDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.shop_removeadsbackground, null);
            ImageView removeAdsBackground = (ImageView) popupView.findViewById(R.id.shop_removeadsbackground);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0f);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            removeAdsDrawable.setColorFilter(filter);
            removeAdsBackground.setBackground(removeAdsDrawable);
        } else if (!Utils.hasPurchasedRemoveAds(this) && Utils.hasPurchasedInsaneMode(this)) {
            // only purchased before insane mode, hence show the layout with gray out insane mode
            popupView = layoutInflater.inflate(R.layout.shop_popup_purchasedinsanemode, null);

            Drawable unlockinsaneDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.shop_insanemodebackground, null);
            ImageView unlockInsaneBackground = (ImageView) popupView.findViewById(R.id.shop_unlockInsanebackground);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0f);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            unlockinsaneDrawable.setColorFilter(filter);
            unlockInsaneBackground.setBackground(unlockinsaneDrawable);
        } else {
            // has alr purchased both insane and ads, hence show both gray out version
            popupView = layoutInflater.inflate(R.layout.shop_popup_purchasedboth, null);

            Drawable removeAdsDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.shop_removeadsbackground, null);
            ImageView removeAdsBackground = (ImageView) popupView.findViewById(R.id.shop_removeadsbackground);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0f);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            removeAdsDrawable.setColorFilter(filter);
            removeAdsBackground.setBackground(removeAdsDrawable);

            Drawable unlockinsaneDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.shop_insanemodebackground, null);
            ImageView unlockInsaneBackground = (ImageView) popupView.findViewById(R.id.shop_unlockInsanebackground);
            ColorMatrix matrix2 = new ColorMatrix();
            matrix2.setSaturation(0f);
            ColorMatrixColorFilter filter2 = new ColorMatrixColorFilter(matrix2);
            unlockinsaneDrawable.setColorFilter(filter2);
            unlockInsaneBackground.setBackground(unlockinsaneDrawable);
        }
        shopPopUpWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT,
                true);
        shopPopUpWindow.setTouchable(true);
        shopPopUpWindow.setFocusable(true);
        shopPopUpWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // so that onbackpress can close the popupwindow
        shopPopUpWindow.showAtLocation(popupView, Gravity.FILL, 0, 0);
        shopPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                SoundPoolManager.getInstance().playSound(2);
            }
        });

        AutoResizeTextView adsPrice = (AutoResizeTextView) popupView.findViewById(R.id.shop_removeAds_PRICE);
        AutoResizeTextView insanePrice = (AutoResizeTextView) popupView.findViewById(R.id.shop_unlockInsane_PRICE);

        ArrayList<String> sku_ID = new ArrayList<>();
        sku_ID.add(Constants.iap_sku_Ads); // index 0
        sku_ID.add(Constants.iap_sku_InsaneMode); // index 1
        List<SkuDetails> skuDetails = bp.getPurchaseListingDetails(sku_ID);
        if(skuDetails!= null && skuDetails.size() >= 2) {
            // just checking if skuDetails is not null. can use >0 or == 2 instead of >=2 doesnt matter, becos we expect 2 entries
            adsPrice.setText(skuDetails.get(0).priceText); // priceText includes the currency and the value, e.g £2.99, 3,99€, $4.99
            insanePrice.setText(skuDetails.get(1).priceText);
        }

        AutoResizeTextView findOutMore = (AutoResizeTextView) popupView.findViewById(R.id.shop_findoutmoreTextView);
        findOutMore.setPaintFlags(findOutMore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        AutoResizeTextView restoreYourPurchase = (AutoResizeTextView) popupView.findViewById(R.id.shop_restoreYourPurchase);
        restoreYourPurchase.setPaintFlags(restoreYourPurchase.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        LinearLayout dismissPopUp_Container1, dismissPopUp_Container2, dismissPopUp_Container3;
        dismissPopUp_Container1 = (LinearLayout) popupView.findViewById(R.id.shop_emptyContainer1);
        dismissPopUp_Container2 = (LinearLayout) popupView.findViewById(R.id.shop_emptyContainer2);
        dismissPopUp_Container3 = (LinearLayout) popupView.findViewById(R.id.shop_emptyContainer3);
        ImageView shopPopupClose = (ImageView) popupView.findViewById(R.id.shop_popupClose);

        dismissPopUp_Container1.setOnClickListener(this);
        dismissPopUp_Container2.setOnClickListener(this);
        dismissPopUp_Container3.setOnClickListener(this);
        shopPopupClose.setOnClickListener(this);

        AutoResizeTextView shop_removeAds_BuyNowButton = (AutoResizeTextView) popupView.findViewById(R.id.shop_removeAds_BUYNOWButton);
        AutoResizeTextView shop_insaneMode_BuyNowButton = (AutoResizeTextView) popupView.findViewById(R.id.shop_unlockInsane_BUYNOWButton);

        findOutMore.setOnClickListener(this);
        restoreYourPurchase.setOnClickListener(this);
        if (!Utils.hasPurchasedRemoveAds(this)) {
            // only set button on click for event where user has not purchased yet
            shop_removeAds_BuyNowButton.setOnClickListener(this);
        }
        if (!Utils.hasPurchasedInsaneMode(this)) {
            // only set button on click for event where user has not purchased yet
            shop_insaneMode_BuyNowButton.setOnClickListener(this);
        }


    }

    private void show_customGamePopUp() {
        SoundPoolManager.getInstance().playSound(1);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.customgame_popup, null);

        customgame_popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);

        customgame_popupWindow.setTouchable(true);
        customgame_popupWindow.setFocusable(true);
        customgame_popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // so that onbackpress can close the popupwindow
        customgame_popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        customgame_popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        customgame_popupWindow.showAtLocation(popupView, Gravity.FILL, 0, 0);
        customgame_popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (playCloseMenuSound) {
                    SoundPoolManager.getInstance().playSound(2);
                }
                playCloseMenuSound = true;
            }
        });


        LinearLayout dismissPopUp_Container1, dismissPopUp_Container2, dismissPopUp_Container3;
        dismissPopUp_Container1 = (LinearLayout) popupView.findViewById(R.id.customgame_emptyContainer1);
        dismissPopUp_Container2 = (LinearLayout) popupView.findViewById(R.id.customgame_emptyContainer2);
        dismissPopUp_Container3 = (LinearLayout) popupView.findViewById(R.id.customgame_emptyContainer3);
        // we use the same global var quickplay_close as quick play one, so that we are able to setalpha to 0 if we open editplayer profile.
        // in quick play it will be initialize to quick play version, in custom game here to custom game version. So its ok
        quickplay_close = (ImageView) popupView.findViewById(R.id.customgame_popupClose);

        dismissPopUp_Container1.setOnClickListener(this);
        dismissPopUp_Container2.setOnClickListener(this);
        dismissPopUp_Container3.setOnClickListener(this);
        quickplay_close.setOnClickListener(this);

        final ImageView customgame_circlePage1 = (ImageView) popupView.findViewById(R.id.customgame_circlePage1);
        final ImageView customgame_circlePage2 = (ImageView) popupView.findViewById(R.id.customgame_circlePage2);

        final ViewPager viewPager = (ViewPager) popupView.findViewById(R.id.customgame_viewpagerContainer);
        final CustomGamePopupViewPagerAdapter viewPagerAdapter = new CustomGamePopupViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SoundPoolManager.getInstance().playSound(3);
                switch (position) {
                    case 0:
                        customgame_circlePage1.setImageResource(R.drawable.customgame_circleselected);
                        customgame_circlePage2.setImageResource(R.drawable.customgame_circlenotselected);
                        break;
                    case 1:
                        customgame_circlePage1.setImageResource(R.drawable.customgame_circlenotselected);
                        customgame_circlePage2.setImageResource(R.drawable.customgame_circleselected);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.post(new Runnable() {
            @Override
            public void run() {

                ViewGroup rootViewPage1 = viewPagerAdapter.getRootViewPage1();
                ViewGroup rootViewPage2 = viewPagerAdapter.getRootViewPage2();

                AutoResizeTextView selectAllButton = (AutoResizeTextView) rootViewPage1.findViewById(R.id.customgame_selectAll_Button);
                AutoResizeTextView selectNoneButton = (AutoResizeTextView) rootViewPage1.findViewById(R.id.customgame_selectNone_Button);
                final AutoResizeTextView randomizeOrderButton = (AutoResizeTextView) rootViewPage1.findViewById(R.id.customgame_randomizeOrder_Button);
                final TextView customGame_numberOfModes_textView = (TextView) rootViewPage1.findViewById(R.id.customgame_numberSelected);
                customGame_currentNumberOfModes_int = 0; // impt to set it back to 0 on every recreate, else the global var will keep increasing or decreasing

                if (Utils.getCustomGameRandomizeOrderOn(MainActivity.this)) {
                    // if initial open it is in random order, then we set the orange version. Else if it is in sequence order, we set the gray
                    randomizeOrderButton.setBackgroundResource(R.drawable.editplayername_doneselector);
                    randomizeOrderButton.setTextColor(Color.parseColor("#FFFFFF"));

                } else {
                    randomizeOrderButton.setBackgroundResource(R.drawable.customgame_randomizeorderoffstate);
                    randomizeOrderButton.setTextColor(Color.parseColor("#EEEEEE"));
                }

                final List<CustomGameListViewModel> myList = new ArrayList<CustomGameListViewModel>();

                // update this list will not break code, as all other use myList.size().
                // *** Add all gameMode here in alphabetical order ***
                // Do not need to change any other place when adding new class here, as all other codes are accounted for
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_animalkingdom)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_circlecollide)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_mixingcolors)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_screencolor)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_fivecolorspanel)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_countryandcontinent)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_solareclipse)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_mathequation)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_fiveyellowcircle)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_foodparadise)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_identifyinglanguage)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_illusioncircle)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_illusiongear)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_illusionrectangle)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_mandn)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_perfectmatch)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_colormatchwords)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_gradientorb)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_neonarrows)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_oandx)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_oddandvowel)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_smilingface)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_spam)));
                myList.add(new CustomGameListViewModel(MainActivity.this.getString(R.string.dialog_unitconversion)));



                int preferenceArray[] = Utils.getCustomGameModePreference(MainActivity.this, myList.size());
                for (int i = 0; i < myList.size(); i++) {
                    // at the top all the .add causes the default false to be applied to all the objects in myList
                    // here we set the myList selected states to whatever its in the sharepreference
                    myList.get(i).setSelected(preferenceArray[i]);
                    // selected above is use to tell if it is selected, here below we set the actual number
                    myList.get(i).setSequenceNumber(preferenceArray[i]);

                    // then we find out how many are true among all the game modes
                    if (preferenceArray[i] > 0) {
                        // loop through from first element to last element, if it is true, increase the currentNumberOfModes
                        customGame_currentNumberOfModes_int++;
                    }
                }

                // set the first initial number of text when viewpager is loaded, then change the text accordingly in the onclick event below
                customGame_numberOfModes_textView.setText(customGame_currentNumberOfModes_int + "/" + myList.size());

                // page 1
                final ListView listView = (ListView) rootViewPage1.findViewById(R.id.customGameListView);

                final ArrayAdapter<CustomGameListViewModel> listAdapter = new CustomGameListViewAdapter(MainActivity.this, myList);

                listView.setAdapter(listAdapter);

                // ListView Item Click Listener

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SoundPoolManager.getInstance().playSound(0);
                        // Random order and sequence order use the same algorithm below -> IMPT for custom game mode to work, otherwise may get NPE for random order if we
                        // unselected previous one
                        // Essentially random order is the same as sequence order, they are numbered from small to large.
                        // It is just in QuickPlay.java we apply shuffle() if it is random order to mix up the game mode

                            // set back to unselected state
                            if (myList.get(position).isSelected() > 0) {
                                // store the value that was deselected into a var, and compare this var with all the other value through a for loop below,
                                // and if the current number is found to be bigger than this old selected value, then decrement it by 1
                                int oldSelectedValue = myList.get(position).getSequenceNumber();
                                for (int i = 0; i < myList.size(); i++) {
                                    if (myList.get(i).getSequenceNumber() > oldSelectedValue) {
                                        myList.get(i).setSequenceNumber(myList.get(i).getSequenceNumber() - 1);
                                        // set the one that was greater than the last pressed in index i to be lesser than 1.
                                        // Note we dont minus one here, becoz we have alr setSequenceNumber to be lesser than 1 in the sentence above, hence it is reflected
                                        Utils.setCustomGameModePreference(MainActivity.this, myList.get(i).getSequenceNumber(), i);
                                    }
                                }
                                myList.get(position).setSelected(0);
                                myList.get(position).setSequenceNumber(0);
                                // set the one that was pressed to 0 to be deselected at index position
                                Utils.setCustomGameModePreference(MainActivity.this, 0, position);
                                customGame_currentNumberOfModes_int--;
                                customGame_numberOfModes_textView.setText(customGame_currentNumberOfModes_int + "/" + myList.size());

                            } else {
                                // set to selected state
                                customGame_currentNumberOfModes_int++;
                                myList.get(position).setSelected(customGame_currentNumberOfModes_int);
                                myList.get(position).setSequenceNumber(customGame_currentNumberOfModes_int);
                                Utils.setCustomGameModePreference(MainActivity.this, customGame_currentNumberOfModes_int, position);
                                customGame_numberOfModes_textView.setText(customGame_currentNumberOfModes_int + "/" + myList.size());
                        }
                        listAdapter.notifyDataSetChanged();
                    }

                });


                selectAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundPoolManager.getInstance().playSound(0);
                        customGame_currentNumberOfModes_int = 0;
                        for (int i = 0; i < myList.size(); i++) {
                            customGame_currentNumberOfModes_int++;
                            // set it to check
                            myList.get(i).setSelected(customGame_currentNumberOfModes_int);
                            // set its number
                            myList.get(i).setSequenceNumber(customGame_currentNumberOfModes_int);
                            Utils.setCustomGameModePreference(MainActivity.this, customGame_currentNumberOfModes_int, i);
                        }
                        customGame_numberOfModes_textView.setText(myList.size() + "/" + myList.size());
                        listAdapter.notifyDataSetChanged();
                    }
                });

                selectNoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundPoolManager.getInstance().playSound(2);
                        for (int i = 0; i < myList.size(); i++) {
                            myList.get(i).setSelected(0);
                            myList.get(i).setSequenceNumber(0); // impt to reset sequence number
                            Utils.setCustomGameModePreference(MainActivity.this, 0, i);
                        }
                        customGame_currentNumberOfModes_int = 0;
                        customGame_numberOfModes_textView.setText("0/" + myList.size());
                        listAdapter.notifyDataSetChanged();
                    }
                });

                randomizeOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundPoolManager.getInstance().playSound(0);
                        if (Utils.getCustomGameRandomizeOrderOn(MainActivity.this)) {
                            // currently it is in random order, hence we set it to sequence order
                            Utils.setCustomGameRamdomizeOrder(MainActivity.this, false);
                            randomizeOrderButton.setBackgroundResource(R.drawable.customgame_randomizeorderoffstate);
                            randomizeOrderButton.setTextColor(Color.parseColor("#EEEEEE"));
                        } else {
                            // currently it is in sequence order, hence we set it to random order
                            Utils.setCustomGameRamdomizeOrder(MainActivity.this, true);
                            randomizeOrderButton.setBackgroundResource(R.drawable.editplayername_doneselector);
                            randomizeOrderButton.setTextColor(Color.parseColor("#FFFFFF"));

                        }
                        listAdapter.notifyDataSetChanged();
                    }
                });


                // page 2
                initializeDifficultyNumberPlayersClickEvent(rootViewPage2);
                ImageView quickplayStart = (ImageView) rootViewPage2.findViewById(com.react.reactmultiplayergame.R.id.quickplay_start_Button);

                final ArrayList<GameModeInstances> customMode = new ArrayList<GameModeInstances>();


                quickplayStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (customGame_currentNumberOfModes_int == 0) {
                            Toast.makeText(MainActivity.this, R.string.customgame0gamemode, Toast.LENGTH_SHORT).show();
                        } else {
                            // set the levelDifficulty to be a string and passed it as a param below to log into Analytics
                            String levelDifficulty = "";
                            switch (quickplayLevelDifficultySelection) {
                                case Constants.MODE_EASY:
                                    levelDifficulty = "Easy";
                                    break;
                                case Constants.MODE_MEDIUM:
                                    levelDifficulty = "Medium";
                                    break;
                                case Constants.MODE_HARD:
                                    levelDifficulty = "Hard";
                                    break;
                                case Constants.MODE_INSANE:
                                    levelDifficulty = "Insane";
                                    break;
                            }

                            // check from first game mode, to the last game mode selected
                            // The 2 for loops here check through all the gameMode Selected and add all into customMode arrayList, in order
                            for (int sequenceNo = 1; sequenceNo <= customGame_currentNumberOfModes_int; sequenceNo++) {
                                // inner for loop then loop through all items in myList to see which one correspond to the current sequence
                                for (int j = 0; j < myList.size(); j++) {
                                    if (myList.get(j).getSequenceNumber() == sequenceNo) {
                                        // pass j into param as current position is the selected one, hence it will return the correct gameMode instance
                                        // in customGame_checkGameMode we have also added value to bundle which we passed at logEvent below
                                        customMode.add(customGame_checkGameMode(j, levelDifficulty));
                                    }
                                }
                            }

                            if (customMode.size() < 1) {
                                // this block should not be activated.
                                // but just in case the algorithm for this custom game has something wrong that i didnt catch earlier
                                // this will prevent a NPE if there is no gameMode and we proceed to QuickPlay.java
                                // here since smth went wrong, we just set all game mode to unselected, and ask user to select again.

                                Toast.makeText(MainActivity.this, getString(R.string.customgame_somethingwrong), Toast.LENGTH_SHORT).show();
                                // code for select no mode. Set all selected mode to none
                                for (int i = 0; i < myList.size(); i++) {
                                    myList.get(i).setSelected(0);
                                    myList.get(i).setSequenceNumber(0); // impt to reset sequence number
                                    Utils.setCustomGameModePreference(MainActivity.this, 0, i);
                                }
                                customGame_currentNumberOfModes_int = 0;
                                customGame_numberOfModes_textView.setText("0/" + myList.size());
                                listAdapter.notifyDataSetChanged();
                            } else {

                            // the same as the quickPlay start method, but with addition to take into account which class to play
                            SoundPoolManager.getInstance().playSound(6); // play start sound
                            playCloseMenuSound = false;

                            Bundle params = new Bundle();
                            switch (quickplayNumberOfPlayerSelection) {
                                case Constants.MODE_TWOPLAYER:
                                    params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Custom_Game");
                                    params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Two_Players");
                                    params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, levelDifficulty);
                                    mFirebaseAnalytics.logEvent("Start_Game", params);

                                    Utils.setDifficultyPreference(MainActivity.this, quickplayLevelDifficultySelection);
                                    setNoOfPlayersPreference(quickplayNumberOfPlayerSelection);
                                    Intent intent = new Intent(MainActivity.this, QuickPlayTwoPlayer.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("customGameModeToPlay", customMode);
                                    intent.putExtras(bundle);
                                    intent.putExtra("isitQuickPlayOrCustomGame", Constants.GAMEMODE_CUSTOMGAME);
                                    startActivity(intent);
                                    if(customgame_popupWindow != null) {
                                        customgame_popupWindow.dismiss();
                                        customgame_popupWindow = null;
                                    }
                                    break;
                                case Constants.MODE_THREEPLAYER:
                                    params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Custom_Game");
                                    params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Three_Players");
                                    params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, levelDifficulty);
                                    mFirebaseAnalytics.logEvent("Start_Game", params);

                                    Utils.setDifficultyPreference(MainActivity.this, quickplayLevelDifficultySelection);
                                    setNoOfPlayersPreference(quickplayNumberOfPlayerSelection);
                                    Intent intent2 = new Intent(MainActivity.this, QuickPlayThreePlayer.class);
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putSerializable("customGameModeToPlay", customMode);
                                    intent2.putExtras(bundle2);
                                    intent2.putExtra("isitQuickPlayOrCustomGame", Constants.GAMEMODE_CUSTOMGAME);
                                    startActivity(intent2);
                                    if(customgame_popupWindow != null) {
                                        customgame_popupWindow.dismiss();
                                        customgame_popupWindow = null;
                                    }
                                    break;
                                case Constants.MODE_FOURPLAYER:
                                    params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Custom_Game");
                                    params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Four_Players");
                                    params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, levelDifficulty);
                                    mFirebaseAnalytics.logEvent("Start_Game", params);

                                    Utils.setDifficultyPreference(MainActivity.this, quickplayLevelDifficultySelection);
                                    setNoOfPlayersPreference(quickplayNumberOfPlayerSelection);
                                    Intent intent3 = new Intent(MainActivity.this, QuickPlayFourPlayer.class);
                                    Bundle bundle3 = new Bundle();
                                    bundle3.putSerializable("customGameModeToPlay", customMode);
                                    intent3.putExtras(bundle3);
                                    intent3.putExtra("isitQuickPlayOrCustomGame", Constants.GAMEMODE_CUSTOMGAME);
                                    startActivity(intent3);
                                    if(customgame_popupWindow != null) {
                                        customgame_popupWindow.dismiss();
                                        customgame_popupWindow = null;
                                    }
                                    break;

                            }
                        }
                    }
                    }
                });

            }
        });

        customgame_circlePage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to 1st page
                viewPager.setCurrentItem(0);
            }
        });

        customgame_circlePage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to 2nd page
                viewPager.setCurrentItem(1);
            }
        });

    }

    private String secondpart_APIKey() {
        return obsfucate_publicAPIKey(jumbleUp_APIKey(subset_APIKey_beforeDash(fakeAPIKey, 2), -1).getBytes()) + Constants.dash +
                obsfucate_publicAPIKey(jumbleUp_APIKey(subset_APIKey_beforeDash(fakeAPIKey, 3), -1).getBytes()) + Constants.dash +
                obsfucate_publicAPIKey(jumbleUp_APIKey(subset_APIKey_beforeDash(fakeAPIKey, 4), -1).getBytes()) + Constants.dash;
    }

    // TODO manually add new cases when there are more gameModes, and change the case number to fit in according to alphabetical order <- IMPT!
    // Since fabric only log 10 category for each attribute, therefore we only put 10 Game Modes in Total CustomGame Counts 1
    // Hence we create 3 customAttribute of Total CustomGame Counts 1/2/3 to store up to 30 game modes.
    // In the future if we have more than 30, just create a new Total CustomGame Counts 4
    private GameModeInstances customGame_checkGameMode (int currentGameModePositionInListView, String levelDifficulty) {
        Bundle bundle = new Bundle();

        // the case correspond to the position of the custom game selected
        switch (currentGameModePositionInListView) {
            case 0:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "AnimalKingdom_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "AnimalKingdom");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(AnimalKingdom.class);
            case 1:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CollidingCircle_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CollidingCircle");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(CollidingCircle.class);
            case 2:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "MixingColors_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MixingColors");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(MixingColors.class);
            case 3:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ScreenColor_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "ScreenColor");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(ScreenColor.class);
            case 4:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "FiveColorsPanel_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FiveColorsPanel");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(FiveColorsPanel.class);
            case 5:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CountryAndContinent_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CountryAndContinent");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(CountryAndContinent.class);
            case 6:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SolarEclipse_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SolarEclipse");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(SolarEclipse.class);
            case 7:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NumberComparison_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "NumberComparison");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(NumberComparison.class);
            case 8:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "FiveYellowCircle_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FiveYellowCircle");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(FiveYellowCircle.class);
            case 9:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "FoodParadise_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodParadise");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(FoodParadise.class);
            case 10:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IdentifyingLanguage_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "IdentifyingLanguage");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(IdentifyingLanguage.class);
            case 11:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IllusionCircle_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "IllusionCircle");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(IllusionCircle.class);
            case 12:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IllusionGear_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "IllusionGear");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(IllusionGear.class);
            case 13:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IllusionRectangle_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "IllusionRectangle");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(IllusionRectangle.class);
            case 14:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "MandN_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MandN");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(MandN.class);
            case 15:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PerfectMatch_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "PerfectMatch");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(PerfectMatch.class);
            case 16:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ColorMatchWords_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "ColorMatchWords");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(ColorMatchWords.class);
            case 17:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "MultiColoredOrb_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MultiColoredOrb");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(MultiColoredOrb.class);
            case 18:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NeonArrows_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "NeonArrows");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(NeonArrows.class);
            case 19:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OandX_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "OandX");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(OandX.class);
            case 20:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OddAndVowel_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "OddAndVowel");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(OddAndVowel.class);
            case 21:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SmilingFace_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SmilingFace");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(SmilingFace.class);
            case 22:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TouchingSpam_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "TouchingSpam");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(TouchingSpam.class);
            case 23:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "UnitConversion_" + levelDifficulty);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "UnitConversion");
                mFirebaseAnalytics.logEvent("Custom_GameModes", bundle);
                return new GameModeInstances(UnitConversion.class);
            default: return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
    /*
    * Called when BillingProcessor was initialized and it's ready to purchase
    */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
    /*
    * Called when requested PRODUCT ID was successfully purchased
    */
        switch (productId) {
            case Constants.iap_sku_Ads: {
                Utils.setPurchasedRemoveAds(MainActivity.this, true);
                if (shopPopUpWindow != null) {
                    dismissPopUpWindow(3);
                }
                if (nativeAd != null && nativeAd.isAdLoaded()) {
                    nativeAd.destroy();
                    nativeAd = null;

                    LinearLayout adsContainer = (LinearLayout) findViewById(R.id.ads_container);
                    adsContainer.setVisibility(View.INVISIBLE);
                }
                // show alertdialog
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(inflater.inflate(R.layout.successfullypurchased_alertdialog, null));

                final AlertDialog alertDialogSuccessfullyPurchased = builder.create();
                Window dialogWindow = alertDialogSuccessfullyPurchased.getWindow();
                dialogWindow.setBackgroundDrawable(new ColorDrawable(0));

                alertDialogSuccessfullyPurchased.setCancelable(true);
                alertDialogSuccessfullyPurchased.show();

                //Grab the window of the dialog, and change the width
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogWindow.getAttributes());
                //This makes the dialog take up the full width
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                break;
            }
            case Constants.iap_sku_InsaneMode: {
                Utils.setPurchasedInsaneMode(this, true);
                if (shopPopUpWindow != null) {
                    dismissPopUpWindow(3);
                }
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(inflater.inflate(R.layout.successfullypurchased_alertdialog, null));

                final AlertDialog alertDialogSuccessfullyPurchased = builder.create();
                Window dialogWindow = alertDialogSuccessfullyPurchased.getWindow();
                dialogWindow.setBackgroundDrawable(new ColorDrawable(0));

                alertDialogSuccessfullyPurchased.setCancelable(true);
                alertDialogSuccessfullyPurchased.show();

                //Grab the window of the dialog, and change the width
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogWindow.getAttributes());
                //This makes the dialog take up the full width
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                break;
            }
        }

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    /*
    * Called when some error occurred. See Constants class for more details
    *
    * Note - this includes handling the case where the user canceled the buy dialog:
    * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
    */
    if(errorCode == com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE || errorCode == com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ERROR) {
        // no internet
        // must runonuithread to prevent a crash
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), getString(R.string.errorpurchased_noconnection), Toast.LENGTH_SHORT).show();
            }
        });
    }
    else if(errorCode != com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
        // don't show this if user explicitly cancel, becos it doesnt make sense
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), getString(R.string.errorpurchased), Toast.LENGTH_SHORT).show();
            }
        });
    }
    }

    @Override
    public void onPurchaseHistoryRestored() {
    /*
    * Called when purchase history was restored and the list of all owned PRODUCT ID's
    * was loaded from Google Play
    */
    }

}
