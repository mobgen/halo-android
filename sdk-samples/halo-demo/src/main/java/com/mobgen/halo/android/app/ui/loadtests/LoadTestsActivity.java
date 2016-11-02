package com.mobgen.halo.android.app.ui.loadtests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;

import java.util.ArrayList;
import java.util.List;

import icepick.Icepick;

public class LoadTestsActivity extends MobgenHaloActivity {

    private static final String BUNDLE_MODULE_ID = "bundle_module_id";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String mModuleId;

    public static void start(Context context, String moduleId) {
        Bundle data = new Bundle();
        data.putString(BUNDLE_MODULE_ID, moduleId);
        Intent intent = new Intent(context, LoadTestsActivity.class);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_tests);
        mModuleId = getIntent().getExtras().getString(BUNDLE_MODULE_ID);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if(mModuleId == null){
            throw new IllegalStateException("You need a module id to create a the load test activity");
        }
        mTabLayout = (TabLayout) findViewById(R.id.tl_performance);
        mViewPager = (ViewPager) findViewById(R.id.tl_viewpager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SearchPerformanceFragment.create(mModuleId), getString(R.string.search_title));
        adapter.addFragment(SyncPerformanceFragment.create(mModuleId), getString(R.string.sync_title));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.load_tests_title);
    }
}
