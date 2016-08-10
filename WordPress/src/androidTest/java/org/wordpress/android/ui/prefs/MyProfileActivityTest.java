package org.wordpress.android.ui.prefs;


import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.runner.RunWith;
import org.wordpress.android.R;

import java.util.Random;

import it.polimi.testing.lifecycle.ActivityRuleLifecycleTest;
import it.polimi.testing.lifecycle.DestroyCallback;
import it.polimi.testing.lifecycle.PauseCallback;
import it.polimi.testing.lifecycle.RecreateCallback;
import it.polimi.testing.lifecycle.RotationCallback;
import it.polimi.testing.lifecycle.StopCallback;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class MyProfileActivityTest extends ActivityRuleLifecycleTest<MyProfileActivity> {
    @Override
    protected ActivityTestRule<MyProfileActivity> initializeActivityTestRule() {
        return new ActivityTestRule<>(MyProfileActivity.class);
    }

    @Nullable
    @Override
    public RotationCallback testRotation() {
        return new RotationCallback() {
            private String name;

            @Override
            public void beforeRotation() {
                // Open first name dialog
                onView(withId(R.id.first_name_row))
                        .check(matches(isDisplayed()))
                        .perform(click());

                // Type name
                name = "MyFirstName" + (new Random().nextInt(100));
                onView(withId(R.id.my_profile_dialog_input))
                        .check(matches(isDisplayed()))
                        .perform(replaceText(name));

                // Confirm
                onView(withText("OK"))
                        .perform(click());

                // Check name in the button
                onView(withId(R.id.first_name))
                        .check(matches(allOf(isDisplayed(), withText(name))));
            }

            @Override
            public void afterRotation() {
                // Check name in the button
                onView(withId(R.id.first_name))
                        .check(matches(allOf(isDisplayed(), withText(name))));
            }
        };
    }

    @Nullable
    @Override
    protected RecreateCallback testRecreation() {
        return null;
    }

    @Nullable
    @Override
    protected PauseCallback testPause() {
        return null;
    }

    @Nullable
    @Override
    protected StopCallback testStop() {
        return null;
    }

    @Nullable
    @Override
    protected DestroyCallback testDestroy() {
        return null;
    }
}