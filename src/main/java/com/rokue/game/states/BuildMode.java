package com.rokue.game.states;

import com.rokue.game.GameSystem;

public class BuildMode implements GameState {

    public void enter(GameSystem system) {
        System.out.println("Entering Play Mode");
    }

    public void update(GameSystem system) {
        System.out.println("Updating Play Mode");
    }

    public void exit(GameSystem system) {
        System.out.println("Exiting Play Mode");
    }
}

