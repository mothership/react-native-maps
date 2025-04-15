package com.rnmaps.fabric.event;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;

public class OnNavigationRouteFailedToLoadEvent extends Event<OnNavigationRouteFailedToLoadEvent> {
    public static final String EVENT_NAME = "onNavigationRouteFailedToLoad";

    private final WritableMap payload;

    public OnNavigationRouteFailedToLoadEvent(int surfaceId, int viewId, WritableMap payload) {
        super(surfaceId, viewId);
        this.payload = payload;
    }

    @NonNull
    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public WritableMap getEventData() {
        return payload;
    }
}
