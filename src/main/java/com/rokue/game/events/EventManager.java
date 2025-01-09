package com.rokue.game.events;

import java.util.*;

public class EventManager {
    private Map<String, List<EventListener>> listeners = new HashMap<>();

    public void subscribe(String eventType, EventListener listener) {
        listeners.putIfAbsent(eventType, new ArrayList<>());
        listeners.get(eventType).add(listener);
    }

    public void unsubscribe(String eventType, EventListener listener) {
        if (listeners.containsKey(eventType)) {
            listeners.get(eventType).remove(listener);
        }
    }

    public void notify(String eventType, Object data) {
        if (listeners.containsKey(eventType)) {
            for (EventListener listener : listeners.get(eventType)) {
                listener.onEvent(eventType, data);
            }
        }
    }

    public void unsubscribeAll(String eventType) {
        if (listeners.containsKey(eventType)) {
            listeners.get(eventType).clear();
        }
    }
}