package com.mobgen.halo.android.app.ui.translations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobgen.halo.android.translations.HaloTranslationsApi;
import com.moxun.tagcloudlib.view.TagsAdapter;

public class TranslationsAdapter extends TagsAdapter {

    private String[] mTranslationKeys;
    private HaloTranslationsApi mTranslationApi;

    public TranslationsAdapter(@NonNull String[] translationKeys, @NonNull HaloTranslationsApi translations){
        mTranslationKeys = translationKeys;
        mTranslationApi = translations;
    }

    @Override
    public int getCount() {
        return mTranslationKeys.length;
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        TextView view = new TextView(context);
        mTranslationApi.textOn(view, (String) getItem(position));
        return view;
    }

    @Override
    public Object getItem(int position) {
        return mTranslationKeys[position];
    }

    @Override
    public int getPopularity(int position) {
        return 1;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }
}
