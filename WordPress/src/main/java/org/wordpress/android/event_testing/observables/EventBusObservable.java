package org.wordpress.android.event_testing.observables;

import org.wordpress.android.event_testing.events.BusEvent;

import rx.Observable;

public class EventBusObservable {
    public static Observable<BusEvent> mediaUploadEvents() {
        return Observable.create(new MediaUploadOnSubscribe()).onBackpressureDrop();
    }
}
