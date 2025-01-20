package com.rokue.game.states;

import java.io.Serializable;
import java.util.List;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;

public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Hero hero;
    private List<Hall> halls;
    private int currentHallIndex;
    private int remainingTime;

    // Constructor
    public GameSaveData(Hero hero, List<Hall> halls, int currentHallIndex, int remainingTime) {
        this.hero = hero;
        this.halls = halls;
        this.currentHallIndex = currentHallIndex;
        this.remainingTime = remainingTime;
    }

    // Getters
    public Hero getHero() {
        return hero;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public int getCurrentHallIndex() {
        return currentHallIndex;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
