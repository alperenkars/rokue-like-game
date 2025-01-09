package com.rokue.game.events;

@FunctionalInterface
public interface EventListener {
    void onEvent(String event, Object data);
}
