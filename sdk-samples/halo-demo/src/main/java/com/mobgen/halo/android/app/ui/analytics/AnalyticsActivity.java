package com.mobgen.halo.android.app.ui.analytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Analytics activity to show how the analytics work
 */
public class AnalyticsActivity extends MobgenHaloActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AnalyticFragmentAdapter mAdapter;

    public static void startActivity(@NonNull Context context){
        Intent intent = new Intent(context, AnalyticsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        setToolbarTitle(getString(R.string.analytics_addon));
        mTabLayout = (TabLayout) findViewById(R.id.tl_analytics_tabs);
        mViewPager = (ViewPager) findViewById(R.id.tl_viewpager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mAdapter = new AnalyticFragmentAdapter(getSupportFragmentManager());
        mAdapter.addFragment(AnalyticsFragment.create(AnalyticsFragment.FIREBASE_PROVIDER), getString(R.string.analytics_fragment_firebase));
        mAdapter.addFragment(AnalyticsFragment.create(AnalyticsFragment.HALO_PROVIDER), getString(R.string.analytics_fragment_halo));
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public static class AnalyticFragmentAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments;
        private final List<String> mTitles;

        public AnalyticFragmentAdapter(FragmentManager manager) {
            super(manager);
            mFragments = new ArrayList<>(2);
            mTitles = new ArrayList<>(2);
        }

        public void addFragment(@NonNull Fragment fragment, @NonNull String title){
            mFragments.add(fragment);
            mTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
