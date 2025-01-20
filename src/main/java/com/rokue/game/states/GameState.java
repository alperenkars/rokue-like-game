package com.rokue.game.states;

import java.util.List;

import com.rokue.game.GameSystem;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;

public interface GameState {
    void enter(GameSystem system);
    void update(GameSystem system);
    void exit(GameSystem system);
    List<Hall> getHalls();
    Hero getHero();
    Object getRemainingTime();
    int getCurrentHallIndex();
}
