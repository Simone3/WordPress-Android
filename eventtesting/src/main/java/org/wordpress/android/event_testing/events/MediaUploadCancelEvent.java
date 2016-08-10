package org.wordpress.android.event_testing.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;

public class MediaUploadCancelEvent extends MediaUploadOutcomeEvent
{
    public static Matcher<MediaUploadCancelEvent> isMediaUploadCancel() {
        return new FeatureMatcher<MediaUploadCancelEvent, Void>(anything(""), "is media upload cancel", "") {
            @Override
            protected Void featureValueOf(final MediaUploadCancelEvent actual) {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "{Media upload cancel}";
    }
}
