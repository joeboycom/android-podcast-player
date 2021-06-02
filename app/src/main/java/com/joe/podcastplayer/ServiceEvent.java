package com.joe.podcastplayer;

public class ServiceEvent {
    public enum Action {
        SERVICE_STARTED,
        SERVICE_SHUT_DOWN
    }

    public final Action action;

    public ServiceEvent(Action action) {
        this.action = action;
    }
}
