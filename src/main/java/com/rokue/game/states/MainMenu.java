package com.rokue.game.states;

import com.rokue.game.GameSystem;
import com.rokue.game.events.EventManager;

public class MainMenu implements GameState{
    private EventManager eventManager;

    public MainMenu(EventManager eventManager) {
        this.eventManager = eventManager;
    }
    public void enter(GameSystem system) {
        System.out.println("Entering Main Menu");
    }

    public void update(GameSystem system) {
        System.out.println("Updating Main Menu");
    }

    public void exit(GameSystem system) {
        System.out.println("Exiting Main Menu");
    }

    public EventManager getEventManager() {
        return eventManager;
    }

}
