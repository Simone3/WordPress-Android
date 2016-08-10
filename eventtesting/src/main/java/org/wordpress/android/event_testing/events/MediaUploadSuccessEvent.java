package org.wordpress.android.event_testing.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;

public class MediaUploadSuccessEvent extends MediaUploadOutcomeEvent
{
    public static Matcher<MediaUploadSuccessEvent> isMediaUploadSuccess() {
        return new FeatureMatcher<MediaUploadSuccessEvent, Void>(anything(""), "is media upload success", "") {
            @Override
            protected Void featureValueOf(final MediaUploadSuccessEvent actual) {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "{Media upload success}";
    }
}
