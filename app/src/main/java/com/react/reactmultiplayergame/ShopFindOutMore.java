package com.react.reactmultiplayergame;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.helper.MusicManager;
import com.react.reactmultiplayergame.helper.SoundPoolManager;
import com.react.reactmultiplayergame.helper.Utils;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

public class ShopFindOutMore extends AppCompatActivity {
    // used to determine if mediaplayer should continue playing or stop
    private boolean onResumeCalledBefore = false;
    private boolean goingToPreviousActivity = false;
    private List<Content> contentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ShopFindOutMoreAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_findoutmore);

        // Using a recyclerview adapter to fetch all the game modes to prevent out of memory error

        contentList.add(new Content(getString(R.string.dialog_animalkingdom), R.drawable.findoutmore_animalkingdom, getString(R.string.findoutmore_animalkingdom_hard), getString(R.string.findoutmore_animalkingdom_insane), false));
        contentList.add(new Content(getString(R.string.dialog_circlecollide), R.drawable.findoutmore_collidingcircle, getString(R.string.findoutmore_collidingcircle_hard), getString(R.string.findoutmore_collidingcircle_insane), false));
        contentList.add(new Content(getString(R.string.dialog_mixingcolors), R.drawable.findoutmore_colormixer, getString(R.string.findoutmore_colormixer_hard), getString(R.string.findoutmore_colormixer_insane), false));
        contentList.add(new Content(getString(R.string.dialog_screencolor), R.drawable.findoutmore_coloredscreen, getString(R.string.findoutmore_coloredscreen_hard), getString(R.string.findoutmore_coloredscreen_insane), false));
        contentList.add(new Content(getString(R.string.dialog_fivecolorspanel), R.drawable.findoutmore_colorstripes, getString(R.string.findoutmore_fivecolorpanel_hard), getString(R.string.findoutmore_fivecolorpanel_insane), false));
        contentList.add(new Content(getString(R.string.dialog_countryandcontinent), R.drawable.findoutmore_country, getString(R.string.findoutmore_countryandcontinent_hard), getString(R.string.findoutmore_countryandcontinent_insane), false));
        contentList.add(new Content(getString(R.string.dialog_solareclipse), R.drawable.findoutmore_eclipse, getString(R.string.findoutmore_eclipse_hard), getString(R.string.findoutmore_eclipse_insane), false));
        contentList.add(new Content(getString(R.string.dialog_mathequation), R.drawable.findoutmore_math, getString(R.string.findoutmore_mathequation_hard), getString(R.string.findoutmore_mathequation_insane), false));
        contentList.add(new Content(getString(R.string.dialog_fiveyellowcircle), R.drawable.findoutmore_fiveyellowcircle, getString(R.string.findoutmore_fiveyellowcircles_hard), getString(R.string.findoutmore_fiveyellowcircles_insane), false));
        contentList.add(new Content(getString(R.string.dialog_foodparadise), R.drawable.findoutmore_food, getString(R.string.findoutmore_foodparadise_hard), getString(R.string.findoutmore_foodparadise_insane), false));
        contentList.add(new Content(getString(R.string.dialog_identifyinglanguage), R.drawable.findoutmore_identifylanguage, getString(R.string.findoutmore_identifylanguage_hard), getString(R.string.findoutmore_identifylanguage_insane), false));
        contentList.add(new Content(getString(R.string.dialog_illusioncircle), R.drawable.findoutmore_illusioncircle, getString(R.string.findoutmore_illusioncircle_hard), getString(R.string.findoutmore_illusioncircle_insane), false));
        contentList.add(new Content(getString(R.string.dialog_illusiongear), R.drawable.findoutmore_illusiongear, getString(R.string.findoutmore_illusiongear_hard), getString(R.string.findoutmore_illusiongear_insane), false));
        contentList.add(new Content(getString(R.string.dialog_illusionrectangle), R.drawable.findoutmore_illusionrect, getString(R.string.findoutmore_illusionrect_hard), getString(R.string.findoutmore_illusionrect_insane), false));
        contentList.add(new Content(getString(R.string.dialog_mandn), R.drawable.findoutmore_mandn, getString(R.string.findoutmore_mandn_hard), getString(R.string.findoutmore_mandn_insane), false));
        contentList.add(new Content(getString(R.string.dialog_perfectmatch), R.drawable.findoutmore_perfectmatch, getString(R.string.findoutmore_perfectmatch_hard), getString(R.string.findoutmore_perfectmatch_insane), false));
        contentList.add(new Content(getString(R.string.dialog_colormatchwords), R.drawable.findoutmore_colormatchwords, getString(R.string.findoutmore_colormatchwords_hard), getString(R.string.findoutmore_colormatchwords_insane), false));
        contentList.add(new Content(getString(R.string.dialog_gradientorb), R.drawable.findoutmore_gradientorb, getString(R.string.findoutmore_gradientorb_hard), getString(R.string.findoutmore_gradientorb_insane), false));
        contentList.add(new Content(getString(R.string.dialog_neonarrows), R.drawable.findoutmore_neonarrow, getString(R.string.findoutmore_neonarrow_hard), getString(R.string.findoutmore_neonarrow_insane), false));
        contentList.add(new Content(getString(R.string.dialog_oandx), R.drawable.findoutmore_oandx, getString(R.string.findoutmore_oandx_hard), getString(R.string.findoutmore_oandx_insane), false));
        contentList.add(new Content(getString(R.string.dialog_oddandvowel), R.drawable.findoutmore_oddandvowel, getString(R.string.findoutmore_oddandvowel_hard), getString(R.string.findoutmore_oddandvowel_insane), false));
        contentList.add(new Content(getString(R.string.dialog_smilingface), R.drawable.findoutmore_smilingfaces, getString(R.string.findoutmore_smilingface_hard), getString(R.string.findoutmore_smilingface_insane), false));
        contentList.add(new Content(getString(R.string.dialog_spam), R.drawable.findoutmore_touchingspam, getString(R.string.findoutmore_touchingspam_hard), getString(R.string.findoutmore_touchingspam_insane), false));
        contentList.add(new Content(getString(R.string.dialog_unitconversion), R.drawable.findoutmore_unitconversion, getString(R.string.findoutmore_unitconversion_hard), getString(R.string.findoutmore_unitconversion_insane), false));

        recyclerView = (RecyclerView) findViewById(R.id.shopfindoutmore_recycler_view);

        mAdapter = new ShopFindOutMoreAdapter(contentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        ImageView shopBack = (ImageView) findViewById(R.id.shop_backbutton);
        shopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!goingToPreviousActivity) {
            // we only want to stop mediaplayer if it app is going background, and NOT when app is onbackpress to go back to previous activity
            // this is achieve by setting the flag goingToPreviousActivity to be true when we onBackPressed.
            // but if user click home button to go home page, goingToPreviousActivity will be false, and stopmediaplayer will be triggered
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
        SoundPoolManager.getInstance().playSound(2);
        // signify to onPause that we do not stop playing mediaplayer
        goingToPreviousActivity = true;
        super.onBackPressed();
    }

}

class ShopFindOutMoreAdapter extends RecyclerView.Adapter<ShopFindOutMoreAdapter.MyViewHolder> {

    private List<Content> contentList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView gameModeTitle, hardMode_text, insaneMode_text;
        private ImageView mainImageView;
        private ExpandableLayout expandableLayout;
        private RelativeLayout relativeLayout;
        private ImageView arrowImage;

        private MyViewHolder(View view) {
            super(view);
            gameModeTitle = (TextView) view.findViewById(R.id.shopfindoutmoreadapter_gameModeText);
            hardMode_text = (TextView) view.findViewById(R.id.shopfindoutmoreadapter_hardText);
            insaneMode_text = (TextView) view.findViewById(R.id.shopfindoutmoreadapter_insaneText);
            mainImageView = (ImageView) view.findViewById(R.id.shopfindoutmoreadapter_mainImage);
            expandableLayout = (ExpandableLayout) view.findViewById(R.id.shopfindoutmoreadapter_expandableLayout);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.shopfindoutmoreadapter_relativelayout);
            arrowImage = (ImageView) view.findViewById(R.id.shopfindoutmoreadapter_downArrowImage);
        }
    }


     ShopFindOutMoreAdapter(List<Content> contentList) {
        this.contentList = contentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_findoutmore_adapterlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Content content = contentList.get(position);
        holder.gameModeTitle.setText(content.getGameModeTitle());
        holder.hardMode_text.setText(content.getHardMode_text());
        holder.insaneMode_text.setText(content.getInsaneMode_text());
        holder.mainImageView.setImageResource(content.getMainImageID());

        if(content.getIsExpanded()) {
            // use the IsExpanded from Content class to keep track if current item is expanded ornot
            // becos recyclerview will recycle content and if we do not keep track inside the Content class, when u scroll down u will see expanded on item that has not been expanded yet becoz it is reusing the top that has
            // alr been expanded.
            // Cant use isExpanded() method from expandable layout becoz it will be recycled also, hence we need to set a boolean to each of the content itself

            // true means to expand and 2nd param means dont animate
            holder.expandableLayout.setExpanded(true, false);

            holder.relativeLayout.setBackground(null);
            holder.arrowImage.setImageResource(R.drawable.faqarrow_up);
            holder.gameModeTitle.setTextColor(Color.parseColor("#FF4081"));
        } else {
            // impt to put this else
            // collapse and dont animate
            holder.expandableLayout.setExpanded(false, false);

            // need to set back all the below to be the state where it is not expanded
            // this is becos as recyclerview recycle the view, and u scroll down, the recycled item is counted as "clicked" and has the properties changed to clicked state alr
            holder.relativeLayout.setBackgroundResource(R.drawable.faqbottomlineonlybackground);
            holder.arrowImage.setImageResource(R.drawable.faqarrow_down);
            holder.gameModeTitle.setTextColor(Color.BLACK);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.expandableLayout.isExpanded()) {
                    // close the expandable
                    content.setIsExpanded(false);
                    SoundPoolManager.getInstance().playSound(5);
                    holder.expandableLayout.collapse();
                    holder.relativeLayout.setBackgroundResource(R.drawable.faqbottomlineonlybackground);
                    holder.arrowImage.setImageResource(R.drawable.faqarrow_down);
                    holder.gameModeTitle.setTextColor(Color.BLACK);
                } else {
                    // open the expandable
                    content.setIsExpanded(true);
                    SoundPoolManager.getInstance().playSound(4);
                    holder.expandableLayout.expand();
                    holder.relativeLayout.setBackground(null);
                    holder.arrowImage.setImageResource(R.drawable.faqarrow_up);
                    holder.gameModeTitle.setTextColor(Color.parseColor("#FF4081"));

                    // inner onClick listener to dismiss the expandable by clicking on the expandable itself
                    holder.expandableLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            content.setIsExpanded(false);
                            SoundPoolManager.getInstance().playSound(5);
                            holder.expandableLayout.collapse();
                            holder.relativeLayout.setBackgroundResource(R.drawable.faqbottomlineonlybackground);
                            holder.arrowImage.setImageResource(R.drawable.faqarrow_down);
                            holder.gameModeTitle.setTextColor(Color.BLACK);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }
}

class Content {
    private int mainImageID;
    private String gameModeTitle, hardMode_text, insaneMode_text;
    private boolean isExpanded; // flag to keep track of each item to see if it should expand or collapse. This is impt becos recyclerview will recycle the view to be used again after u scroll

    Content() {
    }

    Content(String gameModeTitle, int mainImageID, String hardMode_text, String insaneMode_text, Boolean isExpanded) {
        this.gameModeTitle = gameModeTitle;
        this.mainImageID = mainImageID;
        this.hardMode_text = hardMode_text;
        this.insaneMode_text = insaneMode_text;
        this.isExpanded = isExpanded;
    }

    String getGameModeTitle() {
        return gameModeTitle;
    }

    void setGameModeTitle(String name) {
        this.gameModeTitle = name;
    }

    String getInsaneMode_text() {
        return insaneMode_text;
    }

    void setInsaneMode_text(String insaneMode_text) {
        this.insaneMode_text = insaneMode_text;
    }

    String getHardMode_text() {
        return hardMode_text;
    }

    void setHardMode_text(String hardMode_text) {
        this.hardMode_text = hardMode_text;
    }

    int getMainImageID () {
        return mainImageID;
    }

    void setMainImageID (int mainImageID) {
        this.mainImageID = mainImageID;
    }

    void setIsExpanded (boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    boolean getIsExpanded() {
        return this.isExpanded;
    }

}

