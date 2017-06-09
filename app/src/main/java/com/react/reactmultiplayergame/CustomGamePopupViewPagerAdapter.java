package com.react.reactmultiplayergame;


import android.content.Context;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.react.reactmultiplayergame.helper.AutoResizeTextView;


public class CustomGamePopupViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    // used to return the inflated viewgroup to MainActivity to setOnClick listener in MainActivity
    private ViewGroup rootViewPage1;
    private ViewGroup rootViewPage2;

    public CustomGamePopupViewPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);

        if(position == 0) {
            rootViewPage1 = layout;
        } else if(position == 1) {
            rootViewPage2 = layout;
        }

        return layout;
    }

    public ViewGroup getRootViewPage1() {
        return rootViewPage1;
    }

    public ViewGroup getRootViewPage2() {
        return rootViewPage2;
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

        PAGE1(R.layout.customgame_viewpager_page1),
        PAGE2(R.layout.customgame_viewpager_page2);

        private int mLayoutResId;

        ModelObject(int layoutResId) {
            mLayoutResId = layoutResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }
    }

}
