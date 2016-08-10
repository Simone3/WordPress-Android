package org.wordpress.android.event_testing;

import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.wordpress.android.event_testing.events.PostChangeEvent;

import rx.Observable;
import rx.functions.Func1;

public class EventUtils {
    public static Observable<PostChangeEvent> postChanges(TextView titleView, TextView contentView) {
        Observable<PostChangeEvent> titleChanges = textChanges(titleView);
        Observable<PostChangeEvent> contentChanges = textChanges(contentView);

        // TODO also add other fields changes, as listed in Post#hasChanges() method

        if (titleChanges != null && contentChanges != null) {
            return titleChanges.mergeWith(contentChanges);
        } else if (titleChanges != null) {
            return titleChanges;
        } else {
            return contentChanges;
        }
    }

    private static Observable<PostChangeEvent> textChanges(final TextView textView) {
        Observable<PostChangeEvent> observable = null;
        if (textView != null) {
            observable = RxTextView.textChanges(textView)
                    .map(new Func1<CharSequence, PostChangeEvent>() {
                        @Override
                        public PostChangeEvent call(CharSequence charSequence) {
                            return new PostChangeEvent(textView.getId());
                        }
                    });
        }
        return observable;
    }
}
