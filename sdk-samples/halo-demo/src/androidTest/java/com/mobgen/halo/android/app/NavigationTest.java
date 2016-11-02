package com.mobgen.halo.android.app;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Trace;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.test.ActivityInstrumentationTestCase2;

import com.mobgen.halo.android.app.ui.modules.partial.ModulesActivity;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


/**
 * Tests the navigation through a general content item in the main activity.
 */
public class NavigationTest extends ActivityInstrumentationTestCase2<ModulesActivity> {

    public NavigationTest() {
        super(ModulesActivity.class);
    }

    @SuppressLint("NewApi")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity();
        if (HaloUtils.isAvailableForVersion(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
            Trace.beginSection(getClass().getName());
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (HaloUtils.isAvailableForVersion(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
            Trace.endSection();
        }
    }

    public void testNavigationToModule() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.dl_modules)).perform(DrawerActions.open());
        onView(withId(R.id.rv_generic)).perform(actionOnItem(allOf(hasDescendant(withText("General Content")),
                hasDescendant(withText("News"))), click()));
        //Check if the activity is opened
        onView(withId(R.id.tv_title)).check(ViewAssertions.matches(withText("News")));
        pressBack();
    }
}
