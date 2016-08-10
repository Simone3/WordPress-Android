package org.wordpress.android.event_testing.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;

public class MediaUploadProgressEvent extends BusEvent {
    private float progress;

    public MediaUploadProgressEvent(float progress) {
        this.progress = progress;
    }

    public float getProgress() {
        return progress;
    }

    public static Matcher<MediaUploadProgressEvent> isMediaUploadProgress() {
        return new FeatureMatcher<MediaUploadProgressEvent, Float>(anything(""), "is media upload progress", "") {
            @Override
            protected Float featureValueOf(final MediaUploadProgressEvent actual) {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "{Media upload progress "+(progress*100)+"%}";
    }
}
