package org.wordpress.android.ui.reader;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;
import android.webkit.WebView;

import org.junit.runner.RunWith;
import org.wordpress.android.R;

import it.polimi.testing.lifecycle.ActivityRuleLifecycleTest;
import it.polimi.testing.lifecycle.DestroyCallback;
import it.polimi.testing.lifecycle.PauseCallback;
import it.polimi.testing.lifecycle.RecreateCallback;
import it.polimi.testing.lifecycle.RotationCallback;
import it.polimi.testing.lifecycle.StopCallback;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ReaderPostPagerActivityTest extends ActivityRuleLifecycleTest<ReaderPostPagerActivity>
{
    @Override
    protected ActivityTestRule<ReaderPostPagerActivity> getActivityTestRule()
    {
        return new ActivityTestRule<ReaderPostPagerActivity>(ReaderPostPagerActivity.class, false, false)
        {
            @Override
            protected void afterActivityLaunched()
            {
                onWebView(allOf(isAssignableFrom(WebView.class), withId(R.id.reader_webview)))
                        .forceJavascriptEnabled();
            }
        };
    }

    @Nullable
    @Override
    protected PauseCallback testPause()
    {
        return new PauseCallback()
        {
            // TODO obviously this is not the best way, probably better to mock the WebView with custom content
            private final static long BLOG_ID = 114212435L;
            private final static long POST_ID = 23L;
            private final static String SELECTOR = "#content .post-content";
            private final static String POST_CONTENT_BEGINNING = "Marley was dead";
            private final static String POST_CONTENT_END = "What right have you to be merry";

            @Override
            public void beforePause()
            {
                // Start activity with correct extras
                Intent intent = new Intent();
                intent.putExtra("is_single_post", true);
                intent.putExtra(ReaderConstants.ARG_BLOG_ID, BLOG_ID);
                intent.putExtra(ReaderConstants.ARG_POST_ID, POST_ID);
                activityTestRule.launchActivity(intent);

                // Check that the whole post is shown
                onWebView(allOf(isAssignableFrom(WebView.class), withId(R.id.reader_webview)))
                        .withElement(findElement(Locator.CSS_SELECTOR, SELECTOR))
                        .check(webMatches(getText(), containsString(POST_CONTENT_BEGINNING)))
                        .check(webMatches(getText(), containsString(POST_CONTENT_END)));
            }

            @Override
            public void whilePaused()
            {
                // Do nothing here
            }

            @Override
            public void afterResume()
            {
                // Check that the whole post is shown
                onWebView(allOf(isAssignableFrom(WebView.class), withId(R.id.reader_webview)))
                        .withElement(findElement(Locator.CSS_SELECTOR, SELECTOR))
                        .check(webMatches(getText(), containsString(POST_CONTENT_BEGINNING)))
                        .check(webMatches(getText(), containsString(POST_CONTENT_END)));
            }
        };
    }

    @Nullable
    @Override
    protected RotationCallback testRotation()
    {
        return null;
    }

    @Nullable
    @Override
    protected RecreateCallback testRecreation()
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
}
