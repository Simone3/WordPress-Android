package org.wordpress.android.event_testing.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

import static org.hamcrest.Matchers.anything;

public class PostUploadStart extends Event {
    public static Matcher<PostUploadStart> isPostUploadStart() {
        return new FeatureMatcher<PostUploadStart, Void>(anything(""), "is post upload start", "") {
            @Override
            protected Void featureValueOf(final PostUploadStart actual) {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "{Post upload start}";
    }
}
