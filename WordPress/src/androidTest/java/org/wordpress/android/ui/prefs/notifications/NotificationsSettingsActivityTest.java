package org.wordpress.android.ui.prefs.notifications;

import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.runner.RunWith;
import org.wordpress.android.R;

import it.polimi.testing.lifecycle.ActivityRuleLifecycleTest;
import it.polimi.testing.lifecycle.DestroyCallback;
import it.polimi.testing.lifecycle.PauseCallback;
import it.polimi.testing.lifecycle.RecreateCallback;
import it.polimi.testing.lifecycle.RotationCallback;
import it.polimi.testing.lifecycle.StopCallback;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class NotificationsSettingsActivityTest extends ActivityRuleLifecycleTest<NotificationsSettingsActivity>
{
    @Override
    protected ActivityTestRule<NotificationsSettingsActivity> initializeActivityTestRule()
    {
        return new ActivityTestRule<>(NotificationsSettingsActivity.class);
    }

    @Nullable
    @Override
    protected RotationCallback testRotation()
    {
        return new RotationCallback()
        {
            // TODO obviously this is not the best way, probably better to mock the fetched posts
            private final static String BLOG_NAME = "testpolimi";

            @Override
            public void beforeRotation()
            {
                // Click on the blog
                onData(allOf(is(instanceOf(PreferenceScreen.class)), withTitleText(BLOG_NAME)))
                        .perform(click());

                // Check that we are in the notifications settings
                onView(withText(R.string.notifications_tab))
                        .check(matches(isDisplayed()));
            }

            @Override
            public void afterRotation()
            {
                // Check that we are in the notifications settings
                onView(withText(R.string.notifications_tab))
                        .check(matches(isDisplayed()));
            }
        };
    }

    @Nullable
    @Override
    protected PauseCallback testPause()
    {
        return null;
    }

    @Nullable
    @Override
    protected StopCallback testStop()
    {
        return null;
    }

    @Nullable
    @Override
    protected DestroyCallback testDestroy()
    {
        return null;
    }

    @Nullable
    @Override
    protected RecreateCallback testRecreation()
    {
        return null;
    }
}