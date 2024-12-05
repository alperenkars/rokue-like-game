package com.rokue.game;

import com.rokue.game.states.GameState;
import com.rokue.game.states.MainMenu;
import com.rokue.game.events.EventManager;

public class GameSystem {

    private GameState currentState;
    private boolean isRunning;
    private EventManager eventManager;

    public GameSystem() {
        this.isRunning = true;
        this.eventManager = new EventManager();
        setState(new MainMenu());
    }

    public void setState(GameState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = newState;
        currentState.enter(this);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void start() {
        System.out.println("Game Engine Started");
        while (isRunning) {
            update();
        }
    }

    public void stop() {
        isRunning = false;
        System.out.println("Game Engine Stopped");
    }

    private void update() {
        if (currentState != null) {
            currentState.update(this);
        }
    }
}
