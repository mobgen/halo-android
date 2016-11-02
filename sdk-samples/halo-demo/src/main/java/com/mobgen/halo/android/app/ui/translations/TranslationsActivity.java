package com.mobgen.halo.android.app.ui.translations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.translations.callbacks.TranslationsLoadListener;
import com.moxun.tagcloudlib.view.TagCloudView;

/**
 * Demo activity for the translations.
 */
public class TranslationsActivity extends MobgenHaloActivity implements TranslationsLoadListener, View.OnClickListener {

    /**
     * Tag cloud view.
     */
    private TagCloudView mTagCloudView;
    private TranslationsAdapter mAdapter;
    private Button mResyncTranslations;
    private Button mClearTranslations;

    public static void start(Context context) {
        Intent intent = new Intent(context, TranslationsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translations);
        mTagCloudView = (TagCloudView) findViewById(R.id.tag_cloud);
        mResyncTranslations = (Button) findViewById(R.id.bt_resync);
        mClearTranslations = (Button) findViewById(R.id.bt_clear_translations);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String[] keys = new String[]{
                DemoTranslationKeys.RUBBER_KEY,
                DemoTranslationKeys.PEN_KEY,
                DemoTranslationKeys.PENCIL_KEY,
                DemoTranslationKeys.CHAIR_KEY,
                DemoTranslationKeys.WINDOW_KEY,
                DemoTranslationKeys.MOBILE_KEY,
                DemoTranslationKeys.PICTURE_KEY,
                DemoTranslationKeys.SINGER_KEY,
                DemoTranslationKeys.STUDENT_KEY,
                DemoTranslationKeys.DESIGN_KEY,
                DemoTranslationKeys.POINT_KEY,
                DemoTranslationKeys.BEER_KEY
        };
        mAdapter = new TranslationsAdapter(keys, MobgenHaloApplication.getTranslationsApi());
        mTagCloudView.setAdapter(mAdapter);
        mResyncTranslations.setOnClickListener(this);
        mClearTranslations.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_translations, menu);
        String locale = MobgenHaloApplication.getTranslationsApi().locale();
        if(locale.equals(HaloLocale.ENGLISH_UNITED_STATES)) {
            menu.findItem(R.id.lang_us).setChecked(true);
        }else if(locale.equals(HaloLocale.SPANISH_SPAIN)){
            menu.findItem(R.id.lang_es).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.lang_us:
                MobgenHaloApplication.getTranslationsApi().changeLocale(HaloLocale.ENGLISH_UNITED_STATES, this);
                break;
            case R.id.lang_es:
                MobgenHaloApplication.getTranslationsApi().changeLocale(HaloLocale.SPANISH_SPAIN, this);
                break;
        }
        item.setChecked(true);
        mAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.translations_title);
    }

    @Override
    public void onTranslationsLoaded() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobgenHaloApplication.getTranslationsApi().removeLoadCallback(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_resync) {
            MobgenHaloApplication.getTranslationsApi().load(this);
        }else if(v.getId() == R.id.bt_clear_translations){
            MobgenHaloApplication.getTranslationsApi().clearCachedTranslations();
            mAdapter.notifyDataSetChanged();
        }
    }
}
