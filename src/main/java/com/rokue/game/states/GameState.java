package com.rokue.game.states;

import com.rokue.game.GameSystem;

public interface GameState {
    void enter(GameSystem system);
    void update(GameSystem system);
    void exit(GameSystem system);
 
}
