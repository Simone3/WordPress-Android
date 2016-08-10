package org.wordpress.android.event_testing.observables;

import org.wordpress.android.event_testing.events.BusEvent;
import org.wordpress.android.event_testing.events.MediaUploadFailureEvent;
import org.wordpress.android.event_testing.events.MediaUploadProgressEvent;
import org.wordpress.android.event_testing.events.MediaUploadSuccessEvent;
import org.wordpress.android.ui.media.services.MediaEvents;

import rx.Observable;
import rx.Subscriber;

class MediaUploadOnSubscribe implements Observable.OnSubscribe<BusEvent> {
    @Override
    public void call(final Subscriber<? super BusEvent> subscriber) {
        new EventBusSubscriber() {
            @SuppressWarnings("unused")
            public void onEventMainThread(MediaEvents.MediaUploadSucceeded event) {
                if(!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new MediaUploadSuccessEvent());
                }
            }

            @SuppressWarnings("unused")
            public void onEventMainThread(MediaEvents.MediaUploadFailed event) {
                if(!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new MediaUploadFailureEvent());
                }
            }

            @SuppressWarnings("unused")
            public void onEventMainThread(MediaEvents.MediaUploadProgress event) {
                if(!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new MediaUploadProgressEvent(event.mProgress));
                }
            }
        };
    }
}
