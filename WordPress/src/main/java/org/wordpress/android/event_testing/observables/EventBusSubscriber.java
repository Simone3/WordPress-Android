package org.wordpress.android.event_testing.observables;

import de.greenrobot.event.EventBus;

abstract class EventBusSubscriber {
    EventBusSubscriber() {
        EventBus.getDefault().register(this);
    }
}
