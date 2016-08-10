package org.wordpress.android.ui.posts;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wordpress.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.editor.EditorFragment;
import org.wordpress.android.editor.EditorFragmentAbstract;
import org.wordpress.android.editor.SourceViewEditText;
import org.wordpress.android.event_testing.EventUtils;
import org.wordpress.android.event_testing.events.MediaUploadProgressEvent;
import org.wordpress.android.event_testing.observables.EventBusObservable;
import org.wordpress.android.models.Blog;
import org.wordpress.android.models.Post;
import org.wordpress.android.ui.prefs.SiteSettingsInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Comparator;

import it.polimi.testing.temporalassertions.core.EventMonitor;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static it.polimi.testing.temporalassertions.core.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.core.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.core.AtLeast.atLeast;
import static it.polimi.testing.temporalassertions.core.Exactly.exactly;
import static it.polimi.testing.temporalassertions.core.Exist.exist;
import static it.polimi.testing.temporalassertions.core.Exist.existsAnEventThat;
import static it.polimi.testing.temporalassertions.core.ExistBetweenConstraint.between;
import static it.polimi.testing.temporalassertions.core.Not.isNotSatisfied;
import static it.polimi.testing.temporalassertions.core.ProvidedThat.providedThat;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_DETACH;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.isFragmentLifecycleEvent;
import static it.polimi.testing.temporalassertions.events.MenuClickEvent.isMenuClick;
import static it.polimi.testing.temporalassertions.events.ToastEvent.isToastDisplay;
import static it.polimi.testing.temporalassertions.matchers.AnyEvent.anyEvent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.wordpress.android.event_testing.events.HtmlToggleEvent.isHtmlToggle;
import static org.wordpress.android.event_testing.events.MediaUploadCancelEvent.isMediaUploadCancel;
import static org.wordpress.android.event_testing.events.MediaUploadOutcomeEvent.isMediaUploadOutcome;
import static org.wordpress.android.event_testing.events.MediaUploadProgressEvent.isMediaUploadProgress;
import static org.wordpress.android.event_testing.events.PostChangeEvent.isPostChange;
import static org.wordpress.android.event_testing.events.PostUploadStart.isPostUploadStart;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EditPostActivityTest
{
    private SourceViewEditText editorTitleView;
    private SourceViewEditText editorContentView;

    @Rule
    public IntentsTestRule<EditPostActivity> activityTestRule =
            new IntentsTestRule<EditPostActivity>(EditPostActivity.class, false, false)
            {
                @Override
                public void beforeActivityLaunched()
                {
                    // Initialize the monitor before each test
                    EventMonitor.getInstance().initialize();
                }

                @Override
                public void afterActivityLaunched()
                {
                    // Add observables used by all tests
                    EventMonitor.getInstance().observe(EventBusObservable.mediaUploadEvents());
                    getEditorViews();
                    EventMonitor.getInstance().observe(EventUtils.postChanges(editorTitleView, editorContentView));

                    // Add checks valid for all tests
                    EventMonitor.getInstance().checkThat(
                            "Switched to HTML even if a media item is uploading!",
                            anEventThat(isHtmlToggle())
                            .cannotHappenBetween(
                                anEventThat(isMediaUploadProgress()),
                                anEventThat(isMediaUploadOutcome())));

                    EventMonitor.getInstance().checkThat(
                            "Post content changed after the upload started!",
                            providedThat(
                                existsAnEventThat(isPostUploadStart()))
                            .then(
                                anEventThat(isPostChange())
                                .canHappenOnlyBefore(
                                    anEventThat(isPostUploadStart()))));

                    EventMonitor.getInstance().checkThat(
                            "Clicking on 'publish' didn't perform the expected actions!",
                            atLeast(1).eventsWhereEach(
                                anyEvent(
                                    isPostUploadStart(),
                                    isToastDisplay()))
                            .mustHappenAfter(
                                anEventThat(isMenuClick(R.id.menu_save_post))));

                    EventMonitor.getInstance().checkThat(
                            "Media upload wasn't cancelled when editor was removed!",
                            providedThat(
                                exist(
                                    between(
                                        anEventThat(isMediaUploadProgress()),
                                        anEventThat(isMediaUploadOutcome())),
                                    exactly(1))
                                .eventsWhereEach(isFragmentLifecycleEvent(ON_DETACH)))
                            .then(
                                atLeast(1).eventsWhereEach(isMediaUploadCancel())
                                .mustHappenAfter(
                                    anEventThat(isFragmentLifecycleEvent(ON_DETACH)))));

                    EventMonitor.getInstance().checkThat(
                            "Race condition between publish and upload media!",
                            isNotSatisfied(
                                exist(
                                    between(
                                        anEventThat(isMediaUploadProgress()),
                                        anEventThat(
                                            anyEvent(
                                                isMediaUploadOutcome(),
                                                isMediaUploadProgress()))),
                                    atLeast(1))
                                .eventsWhereEach(isPostUploadStart())));

                    EventMonitor.getInstance().checkThat(
                            "Media upload progress updates are not sent correctly!",
                            allEventsWhereEach(isMediaUploadProgress())
                            .areOrdered(new Comparator<MediaUploadProgressEvent>()
                            {
                                @Override
                                public int compare(MediaUploadProgressEvent e1, MediaUploadProgressEvent e2)
                                {
                                    return Float.compare(e1.getProgress(), e2.getProgress());
                                }
                            }));
                }

                @Override
                public void afterActivityFinished()
                {
                    // At the end of each test, stop the verification
                    EventMonitor.getInstance().stopVerification();
                }
            };

    @Test
    public void testUploadImage()
    {
        // Start activity and verification, and mock the image selection
        launchActivity();
        startVerification();
        setupCameraResult();

        // Test actions
        onView(withId(R.id.format_bar_button_html))
                .perform(click());

        onView(withId(R.id.sourceview_title))
                .perform(replaceText("My Title"));

        onView(withId(R.id.sourceview_content))
                .perform(replaceText("My Content."));

        onView(withId(R.id.format_bar_button_html))
                .perform(click());

        onView(withId(R.id.format_bar_button_media))
                .perform(click());

        onView(withText(R.string.select_photo))
                .perform(click());

        sleep(3000); // Espresso waits for upload end (network) but there's some delay afterwards: without this, it says
                     // "still uploading" but it's not true TODO shouldn't make it flaky but fix if possible just to be safe

        onView(withContentDescription(R.string.publish_post))
            .perform(click());
    }

    @Test
    public void testPublishError()
    {
        // Start activity
        launchActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        // Add checks specific for this test
        EventMonitor.getInstance().checkThat(
                "The error toast wasn't displayed!",
                exactly(1).eventsWhereEach(
                    isToastDisplay(equalTo(context.getString(R.string.error_publish_empty_post))))
                .mustHappenAfter(
                    anEventThat(isMenuClick(R.id.menu_save_post))));

        // Start verification
        startVerification();

        // Test actions
        onView(withId(R.id.format_bar_button_html))
                .perform(click());

        onView(withContentDescription(R.string.publish_post))
                .perform(click());
    }


    /*** OTHER TEST CASES ***/





    /************** HELPER METHODS **************/

    private void startVerification()
    {
        EventMonitor.getInstance().startVerification(
                EventMonitor.getLoggerEventsSubscriber(),
                EventMonitor.getLoggerResultsSubscriber());
    }

    private void launchActivity()
    {
        Blog blog = WordPress.getCurrentBlog();
        if(blog == null) throw new IllegalStateException("Blog is null");

        Post newPost = new Post(blog.getLocalTableBlogId(), false);
        newPost.setCategories("[" + SiteSettingsInterface.getDefaultCategory(InstrumentationRegistry.getTargetContext()) +"]");
        newPost.setPostFormat(SiteSettingsInterface.getDefaultFormat(InstrumentationRegistry.getTargetContext()));
        WordPress.wpDB.savePost(newPost);

        Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), EditPostActivity.class);
        intent.putExtra(EditPostActivity.EXTRA_POSTID, newPost.getLocalTablePostId());
        intent.putExtra(EditPostActivity.EXTRA_IS_PAGE, false);
        intent.putExtra(EditPostActivity.EXTRA_IS_NEW_POST, true);
        activityTestRule.launchActivity(intent);
    }

    private void setupCameraResult()
    {
        try
        {
            // Create file in device storage from the test image resource
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File file = new File(extStorageDirectory, "_wordpress_image_for_testing.PNG");
            if(!file.exists())
            {
                Bitmap bm = BitmapFactory.decodeResource(
                        InstrumentationRegistry.getTargetContext().getResources(),
                        R.drawable.wordpress_image_for_testing);

                FileOutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            }

            // Return image Uri with Espresso Intents whenever it is requested
            Intents.init();
            Intent resultData = new Intent();
            resultData.setData(Uri.fromFile(file));
            Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
            intending(not(isInternal())).respondWith(result);
        }
        catch(IOException e)
        {
            throw new IllegalStateException("Error during camera setup");
        }
    }

    private void sleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e)
        {
            throw new IllegalStateException("Sleep interrupted!");
        }
    }

    private void getEditorViews()
    {
        try
        {
            EditorFragmentAbstract f = activityTestRule.getActivity().mEditorFragment;
            if(!(f instanceof EditorFragment)) throw new IllegalStateException("Test works only with non-legacy fragment!");
            EditorFragment fragment = (EditorFragment) f;

            Field field = fragment.getClass().getDeclaredField("mSourceViewTitle");
            field.setAccessible(true);
            editorTitleView = (SourceViewEditText) field.get(fragment);

            field = fragment.getClass().getDeclaredField("mSourceViewContent");
            field.setAccessible(true);
            editorContentView = (SourceViewEditText) field.get(fragment);
        }
        catch(Exception e)
        {
            throw new IllegalStateException("Reflection error!");
        }
    }
}