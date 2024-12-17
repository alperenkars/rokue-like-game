package com.rokue.game;

import com.rokue.game.input.IInputProvider;
import com.rokue.game.render.IRenderer;
import com.rokue.game.states.GameState;

public class GameSystem {
    private GameState currentState;
    private boolean isRunning;
    private IInputProvider inputProvider;
    private IRenderer renderer;

    public GameSystem(IInputProvider inputProvider, IRenderer renderer) {
        this.isRunning = true;
        this.inputProvider = inputProvider;
        this.renderer = renderer;
    }

    public void setState(GameState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = newState;
        currentState.enter(this);
    }

    public void update() {
        if (isRunning && currentState != null) {
            currentState.update(this);
        }
    }

    public void render() {
        if (currentState != null && renderer != null) {
            renderer.render(currentState);
        }
    }

    public void stop() {
        isRunning = false;
        System.out.println("Game Stopped");
    }

    public GameState getCurrentState() {
        return currentState;
    }
}