package org.wordpress.android.event_testing.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;

public class MediaUploadFailureEvent extends MediaUploadOutcomeEvent {
    public static Matcher<MediaUploadFailureEvent> isMediaUploadFailure() {
        return new FeatureMatcher<MediaUploadFailureEvent, Void>(anything(""), "is media upload failure", "") {
            @Override
            protected Void featureValueOf(final MediaUploadFailureEvent actual) {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "{Media upload failure}";
    }
}
