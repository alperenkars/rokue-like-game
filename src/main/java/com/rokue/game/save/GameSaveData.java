package com.rokue.game.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final List<Hall> halls;
    private final int currentHallIndex;
    private final Hero hero;
    private final int remainingTime;

    public GameSaveData(List<Hall> halls, int currentHallIndex, Hero hero, int remainingTime) {
        this.halls = new ArrayList<>(halls);
        this.currentHallIndex = currentHallIndex;
        this.hero = hero;
        this.remainingTime = remainingTime;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public int getCurrentHallIndex() {
        return currentHallIndex;
    }

    public Hero getHero() {
        return hero;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
} 