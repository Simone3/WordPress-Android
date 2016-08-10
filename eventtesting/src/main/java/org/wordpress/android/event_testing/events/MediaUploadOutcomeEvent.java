package org.wordpress.android.event_testing.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;

public abstract class MediaUploadOutcomeEvent extends BusEvent {
    public static Matcher<MediaUploadOutcomeEvent> isMediaUploadOutcome() {
        return new FeatureMatcher<MediaUploadOutcomeEvent, Void>(anything(""),
                "is any media upload outcome (success/failure/cancel)", "") {
            @Override
            protected Void featureValueOf(final MediaUploadOutcomeEvent actual) {
                return null;
            }
        };
    }
}
