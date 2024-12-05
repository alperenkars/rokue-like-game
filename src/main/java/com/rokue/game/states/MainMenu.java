package com.rokue.game.states;

import com.rokue.game.GameSystem;

public class MainMenu implements GameState{
    public void enter(GameSystem system) {
        System.out.println("Entering Main Menu");
    }

    public void update(GameSystem system) {
        System.out.println("Updating Main Menu");
    }

    public void exit(GameSystem system) {
        System.out.println("Exiting Main Menu");
    }
}
