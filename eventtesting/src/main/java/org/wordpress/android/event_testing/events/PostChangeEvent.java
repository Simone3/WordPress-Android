package org.wordpress.android.event_testing.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;

public class PostChangeEvent extends BusEvent {
    private int viewId;

    public PostChangeEvent(int viewId) {
        this.viewId = viewId;
    }

    public static Matcher<PostChangeEvent> isPostChange() {
        return new FeatureMatcher<PostChangeEvent, Float>(anything(""), "is post change", "") {
            @Override
            protected Float featureValueOf(final PostChangeEvent actual) {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "{Post change on view " + viewId + "}";
    }
}
