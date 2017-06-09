package com.react.reactmultiplayergame;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.helper.AutoResizeTextView;
import com.react.reactmultiplayergame.helper.MusicManager;
import com.react.reactmultiplayergame.helper.MyListPreference;
import com.react.reactmultiplayergame.helper.SoundPoolManager;
import com.react.reactmultiplayergame.helper.Utils;

import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * Created by gan on 7/5/17.
 */

public class SettingsActivity extends AppCompatActivity {
    // used to determine if mediaplayer should continue playing or stop
    private boolean onResumeCalledBefore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        ImageView backButton = (ImageView) toolbar.findViewById(R.id.settings_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.settings_fragmentContainer, new SettingsFragment()).commit();

    }



    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

            // "nested" is the <Preference android:key="nested" android:persistent="false"/>`
            // this will cause a new fragment to replace with the existing fragment, use only for inner screenpreference. E.g. pressing faq to go next page.
            // we need to do this method of creating new fragment to preserve the toolbar
            // this is used for faq page
            findPreference( "nested_faqpage" ).setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick( Preference preference ) {
                    SoundPoolManager.getInstance().playSound(0);
                    getFragmentManager().beginTransaction().replace( R.id.settings_fragmentContainer, new FAQNestedFragment() ).addToBackStack(FAQNestedFragment.class.getSimpleName() ).commit();
                    return true;
                }
            } );

            findPreference( "settings_policy" ).setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick( Preference preference ) {
                    SoundPoolManager.getInstance().playSound(0);
                    getFragmentManager().beginTransaction().replace( R.id.settings_fragmentContainer, new PolicyNestedFragment() ).addToBackStack(PolicyNestedFragment.class.getSimpleName() ).commit();
                    return true;
                }
            } );

            findPreference( "settings_feedback" ).setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick( Preference preference ) {
                    SoundPoolManager.getInstance().playSound(0);
                    ((SettingsActivity) getActivity()).sendFeedback(new String [] {getString(R.string.email)}, getString(R.string.feedback));

                    return true;
                }
            } );

            findPreference( "settings_developersletter" ).setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick( Preference preference ) {
                    SoundPoolManager.getInstance().playSound(0);
                    getFragmentManager().beginTransaction().replace( R.id.settings_fragmentContainer, new DeveloperMsgNestedFragment() ).addToBackStack(DeveloperMsgNestedFragment.class.getSimpleName() ).commit();
                    return true;
                }
            } );

            findPreference( "settings_credits" ).setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick( Preference preference ) {
                    SoundPoolManager.getInstance().playSound(0);
                    getFragmentManager().beginTransaction().replace( R.id.settings_fragmentContainer, new CreditsNestedFragment() ).addToBackStack(CreditsNestedFragment.class.getSimpleName() ).commit();
                    return true;
                }
            } );

            findPreference( "settings_contactus" ).setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick( Preference preference ) {
                    SoundPoolManager.getInstance().playSound(0);
                    ((SettingsActivity) getActivity()).sendFeedback(new String [] {getString(R.string.emailcontactus)}, getString(R.string.contact));
                    return true;
                }
            } );




            MyListPreference scorePerCorrectAnsPreference = (MyListPreference) findPreference("User_Score_Preference");
            if(scorePerCorrectAnsPreference.getValue()==null) {
                // to ensure we don't get a null value
                // set first value by default
                scorePerCorrectAnsPreference.setValueIndex(0);
            }
            scorePerCorrectAnsPreference.setSummary(getString(R.string.current) + "  " + scorePerCorrectAnsPreference.getValue());
            scorePerCorrectAnsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // set the summary text to be the new value whenever user changed it
                    preference.setSummary(getString(R.string.current) + "  " +newValue.toString());
                    return true;
                }
            });




            MyListPreference scorePerWrongAnsPreference = (MyListPreference) findPreference("User_Score_Preference_Minus");
            if(scorePerWrongAnsPreference.getValue()==null) {
                // to ensure we don't get a null value
                // set first value by default
                scorePerWrongAnsPreference.setValueIndex(0);
            }
            if(scorePerWrongAnsPreference.getValue().equals("0")) {
                scorePerWrongAnsPreference.setSummary(getString(R.string.current) + "  " + scorePerWrongAnsPreference.getValue());
            } else {
                scorePerWrongAnsPreference.setSummary(getString(R.string.current) + "  -" + scorePerWrongAnsPreference.getValue());
            }
            scorePerWrongAnsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue.toString().equals("0")) {
                        preference.setSummary(getString(R.string.current) + "  " +newValue.toString());
                    } else {
                        preference.setSummary(getString(R.string.current) + "  -" +newValue.toString());
                    }

                    return true;
                }
            });


            MyListPreference roundPerGamePreference = (MyListPreference) findPreference("QuickPlay_Total_Number_Of_Rounds");
            if(roundPerGamePreference.getValue()==null) {
                // to ensure we don't get a null value
                // set first value by default
                roundPerGamePreference.setValueIndex(0);
            }
            roundPerGamePreference.setSummary(getString(R.string.roundpergame_message) + "\n" + getString(R.string.current) + "  " +roundPerGamePreference.getValue());
            roundPerGamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(getString(R.string.roundpergame_message) + "\n" + getString(R.string.current) + "  " + newValue.toString());
                    return true;
                }
            });


            MyListPreference callNextGameRoundPreference = (MyListPreference) findPreference("To_Next_GameMode_Preference");
            if(callNextGameRoundPreference.getValue()==null) {
                // to ensure we don't get a null value
                // set first value by default
                callNextGameRoundPreference.setValueIndex(0);
            }
            if(callNextGameRoundPreference.getValue().equals("1")) {
                callNextGameRoundPreference.setSummary(getString(R.string.callnextgameround_desc) + "\n" + getString(R.string.current) + "  " + callNextGameRoundPreference.getValue() +
                        " " +getString(R.string.correctanswer));
            } else {
                callNextGameRoundPreference.setSummary(getString(R.string.callnextgameround_desc) + "\n" + getString(R.string.current) + "  " + callNextGameRoundPreference.getValue() +
                        " " +getString(R.string.correctanswers));
            }
            callNextGameRoundPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue.toString().equals("1")) {
                        // set the string with correct answer (without s)
                        preference.setSummary(getString(R.string.callnextgameround_desc) + "\n" + getString(R.string.current) + "  " + newValue.toString() + " " +getString(R.string.correctanswer));
                    } else {
                        preference.setSummary(getString(R.string.callnextgameround_desc) + "\n" + getString(R.string.current) + "  " + newValue.toString() + " " +getString(R.string.correctanswers));
                    }
                    return true;
                }
            });

            MyListPreference answerDelayTimePreference = (MyListPreference) findPreference("User_Delay_Preference");
            if(answerDelayTimePreference.getValue()==null) {
                // to ensure we don't get a null value
                // set first value by default
                answerDelayTimePreference.setValueIndex(0);
            }
            if(answerDelayTimePreference.getValue().equals("2000")) {
                // we do this becoz we do not want the (Default) word to appear
                answerDelayTimePreference.setSummary(getString(R.string.answerdelaytime_desc) + "\n" + getString(R.string.current) + "  " + "2 " + getString(R.string.second));
            } else {
                answerDelayTimePreference.setSummary(getString(R.string.answerdelaytime_desc) + "\n" + getString(R.string.current) + "  " +answerDelayTimePreference.getEntry());
            }

            answerDelayTimePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    double temp;
                    switch (newValue.toString()) {
                        case "500":
                            temp = 0.5;
                            break;
                        case "1000":
                            temp = 1;
                            break;
                        case "1500":
                            temp = 1.5;
                            break;
                        case "2000":
                            temp = 2;
                            break;
                        case "2500":
                            temp = 2.5;
                            break;
                        case "3000":
                            temp = 3;
                            break;
                        case "3500":
                            temp = 3.5;
                            break;
                        case "4000":
                            temp = 4;
                            break;
                        case "4500":
                            temp = 4.5;
                            break;
                        case "5000":
                            temp = 5;
                            break;
                        default: temp = 0;

                    }
                    if(temp != 0) {
                        preference.setSummary(getString(R.string.answerdelaytime_desc) + "\n" + getString(R.string.current) + "  " + temp + " " + getString(R.string.second));
                    } else {
                        // in case we forgot to add more cases if there is more cases available, we dont set the current: preference
                        preference.setSummary(getString(R.string.answerdelaytime_desc));
                    }

                    return true;
                }
            });

        }
    }

    // note here we do not have to extend PreferenceFragment unlike the fragment class above, becoz in FAQ page, we only need display a fragment page, and no need use preference api
    public static class FAQNestedFragment extends Fragment {
        // inner class to change the fragment so as to preserve actionbar in inner nested preferencescreen
        // basically we cannot use the android api of nested preference screen, because the nested toolbar will not appear
        // what we are doing here is to create different xxx.xml and xxxFragment extends PreferenceFragment and begintransaction of fragment for different nested preference screen

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.settings_faqexpandablelistview, container, false);
            ((SettingsActivity) getActivity()).setToolBarText(getString(R.string.faq));

            final RelativeLayout faq_questionArray [] = {(RelativeLayout) rootView.findViewById(R.id.faq_question1) , (RelativeLayout) rootView.findViewById(R.id.faq_question2),
                    (RelativeLayout) rootView.findViewById(R.id.faq_question3), (RelativeLayout) rootView.findViewById(R.id.faq_question4),
                    (RelativeLayout) rootView.findViewById(R.id.faq_question5), (RelativeLayout) rootView.findViewById(R.id.faq_question6)};


            final ExpandableLayout faq_answerArray [] = { (ExpandableLayout) rootView.findViewById(R.id.faq_answer1), (ExpandableLayout) rootView.findViewById(R.id.faq_answer2),
                    (ExpandableLayout) rootView.findViewById(R.id.faq_answer3), (ExpandableLayout) rootView.findViewById(R.id.faq_answer4),
                    (ExpandableLayout) rootView.findViewById(R.id.faq_answer5), (ExpandableLayout) rootView.findViewById(R.id.faq_answer6),
            };

            final ImageView arrowViewArray [] = { (ImageView) rootView.findViewById(R.id.faq_qn1ImageView), (ImageView) rootView.findViewById(R.id.faq_qn2ImageView),
                    (ImageView) rootView.findViewById(R.id.faq_qn3ImageView), (ImageView) rootView.findViewById(R.id.faq_qn4ImageView),
                    (ImageView) rootView.findViewById(R.id.faq_qn5ImageView), (ImageView) rootView.findViewById(R.id.faq_qn6ImageView),};

            for(int i=0; i< faq_questionArray.length; i++) {
                final int temp = i;
                faq_questionArray[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(faq_answerArray[temp].isExpanded()) {
                            // close the expandable
                            SoundPoolManager.getInstance().playSound(5);
                            faq_answerArray[temp].collapse();
                            faq_questionArray[temp].setBackgroundResource(R.drawable.faqbottomlineonlybackground);
                            arrowViewArray[temp].setImageResource(R.drawable.faqarrow_down);
                        } else {
                            // open the expandable
                            SoundPoolManager.getInstance().playSound(4);
                            faq_answerArray[temp].expand();
                            faq_questionArray[temp].setBackground(null);
                            arrowViewArray[temp].setImageResource(R.drawable.faqarrow_up);

                            // inner onClick listener to dismiss the expandable by clicking on the expandable itself
                            faq_answerArray[temp].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SoundPoolManager.getInstance().playSound(5);
                                    faq_answerArray[temp].collapse();
                                    faq_questionArray[temp].setBackgroundResource(R.drawable.faqbottomlineonlybackground);
                                    arrowViewArray[temp].setImageResource(R.drawable.faqarrow_down);
                                }
                            });
                        }
                    }
                });
            }
            return rootView;
        }

        @Override
        public void onDestroyView() {
            // when faq page onbackpress, set back toolbar to be "Settings"
            super.onDestroyView();
            ((SettingsActivity)getActivity()).setToolBarText(getString(R.string.settings));
        }
    }



    public static class PolicyNestedFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.settings_policyfragment, container, false);
            ((SettingsActivity) getActivity()).setToolBarText(getString(R.string.policy));

            final RelativeLayout policy_questionArray [] = {(RelativeLayout) rootView.findViewById(R.id.settings_policy1) , (RelativeLayout) rootView.findViewById(R.id.settings_policy2)};


            final ExpandableLayout policy_answerArray [] = { (ExpandableLayout) rootView.findViewById(R.id.settings_policy_answer1), (ExpandableLayout) rootView.findViewById(R.id.settings_policy_answer2)};

            final ImageView arrowViewArray [] = { (ImageView) rootView.findViewById(R.id.settings_policy_ImageView1), (ImageView) rootView.findViewById(R.id.settings_policy_ImageView2)};

            for(int i=0; i< policy_answerArray.length; i++) {
                final int temp = i;
                policy_questionArray[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(policy_answerArray[temp].isExpanded()) {
                            // close the expandable
                            SoundPoolManager.getInstance().playSound(5);
                            policy_answerArray[temp].collapse();
                            policy_questionArray[temp].setBackgroundResource(R.drawable.faqbottomlineonlybackground);
                            arrowViewArray[temp].setImageResource(R.drawable.faqarrow_down);
                        } else {
                            // open the expandable
                            SoundPoolManager.getInstance().playSound(4);
                            policy_answerArray[temp].expand();
                            policy_questionArray[temp].setBackground(null);
                            arrowViewArray[temp].setImageResource(R.drawable.faqarrow_up);

                            // inner onClick listener to dismiss the expandable by clicking on the expandable itself
                            policy_answerArray[temp].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SoundPoolManager.getInstance().playSound(5);
                                    policy_answerArray[temp].collapse();
                                    policy_questionArray[temp].setBackgroundResource(R.drawable.faqbottomlineonlybackground);
                                    arrowViewArray[temp].setImageResource(R.drawable.faqarrow_down);
                                }
                            });
                        }
                    }
                });
            }
            return rootView;
        }

        @Override
        public void onDestroyView() {
            // when onbackpress, set back toolbar to be "Settings"
            super.onDestroyView();
            ((SettingsActivity)getActivity()).setToolBarText(getString(R.string.settings));
        }
    }


    public static class DeveloperMsgNestedFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.settings_developersletterfragment, container, false);
            ((SettingsActivity) getActivity()).setToolBarText(getString(R.string.developersletter));

            return rootView;
        }

        @Override
        public void onDestroyView() {
            // when onbackpress, set back toolbar to be "Settings"
            super.onDestroyView();
            ((SettingsActivity)getActivity()).setToolBarText(getString(R.string.settings));
        }
    }

    public static class CreditsNestedFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.settings_creditsfragment, container, false);
            ((SettingsActivity) getActivity()).setToolBarText(getString(R.string.credits));

            // These is how to set up a link via string html: 3 steps. 1) declare the var, 2) write inside the if-else below, 3) setMovementMethod below
            TextView subtlepatternCCBYLink = (TextView) rootView.findViewById(R.id.credit_subtlepatternCCBYLink);
            TextView freepiklinktext3 = (TextView) rootView.findViewById(R.id.credit_freepikLink);
            TextView soundlinktext1 = (TextView) rootView.findViewById(R.id.credits_soundtext1);
            TextView soundlinktext2 = (TextView) rootView.findViewById(R.id.credits_soundtext2);
            TextView soundlinktext3 = (TextView) rootView.findViewById(R.id.credits_soundtext3);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                subtlepatternCCBYLink.setText(Html.fromHtml(getString(R.string.creditpage_imagesubtlepattern_desc),Html.FROM_HTML_MODE_LEGACY));
                freepiklinktext3.setText(Html.fromHtml(getString(R.string.creditpage_freepikLink),Html.FROM_HTML_MODE_LEGACY));
                soundlinktext1.setText(Html.fromHtml(getString(R.string.creditpage_sound_text1),Html.FROM_HTML_MODE_LEGACY));
                soundlinktext2.setText(Html.fromHtml(getString(R.string.creditpage_sound_text2),Html.FROM_HTML_MODE_LEGACY));
                soundlinktext3.setText(Html.fromHtml(getString(R.string.creditpage_sound_text3),Html.FROM_HTML_MODE_LEGACY));
            } else {
                subtlepatternCCBYLink.setText(Html.fromHtml(getString(R.string.creditpage_imagesubtlepattern_desc)));
                freepiklinktext3.setText(Html.fromHtml(getString(R.string.creditpage_freepikLink)));
                soundlinktext1.setText(Html.fromHtml(getString(R.string.creditpage_sound_text1)));
                soundlinktext2.setText(Html.fromHtml(getString(R.string.creditpage_sound_text2)));
                soundlinktext3.setText(Html.fromHtml(getString(R.string.creditpage_sound_text3)));
            }
            subtlepatternCCBYLink.setMovementMethod(LinkMovementMethod.getInstance());
            freepiklinktext3.setMovementMethod(LinkMovementMethod.getInstance());
            soundlinktext1.setMovementMethod(LinkMovementMethod.getInstance());
            soundlinktext2.setMovementMethod(LinkMovementMethod.getInstance());
            soundlinktext3.setMovementMethod(LinkMovementMethod.getInstance());
            return rootView;
        }

        @Override
        public void onDestroyView() {
            // when onbackpress, set back toolbar to be "Settings"
            super.onDestroyView();
            ((SettingsActivity)getActivity()).setToolBarText(getString(R.string.settings));
        }
    }

    private void setToolBarText(String text) {
        AutoResizeTextView toolbartext = (AutoResizeTextView) findViewById(R.id.settings_toolbartext);
        if(toolbartext != null)
        toolbartext.setText(text);
    }

    // used for both send feedback and contact us at 2 different email address
    private void sendFeedback(String[] addresses, String subject) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(Intent.createChooser(intent, getString(R.string.sendemailvia)));
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()) {
            // we only want to stop mediaplayer if it app is going background, and NOT when app is onbackpress to go back to previous activity
            // We also cannot modify onBackpress to use a flag becos when say user is in faq page, he can press back to return to setting activity too (not finishing this activity yet)
            // therefore we use isFinishing
            // isFinishing() will only be called if user click back and this activity is finishing, i.e. going back to main menu
            // it will not be called if user go to background from settingsactivity, or press back while in faq and returning to main setting page
            MusicManager.stopMediaPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(onResumeCalledBefore) {
            // will not be called the first time this activity is instantiated, since the music is alr playing
            // will be called in subseqent called to onResume, which will occur when app go to background and come back to foregoround again
            MusicManager.initializeAndPlayMediaPlayer(this, Utils.getIfSoundIsMuted(this));
        }
        onResumeCalledBefore = true;
    }

    @Override
    public void onBackPressed() {
        SoundPoolManager.getInstance().playSound(2); // play sound
        super.onBackPressed();
    }
}