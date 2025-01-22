package com.rokue.game.save;

import java.io.Serializable;
import java.util.List;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.Monster;

public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Hall currentHall;
    private final Hero hero;
    private final int remainingTime;
    private final List<Monster> monsters;
    private final List<Enchantment> enchantments;

    public GameSaveData(Hall currentHall, Hero hero, int remainingTime, 
                       List<Monster> monsters, List<Enchantment> enchantments) {
        this.currentHall = currentHall;
        this.hero = hero;
        this.remainingTime = remainingTime;
        this.monsters = monsters;
        this.enchantments = enchantments;
    }

    public Hall getCurrentHall() {
        return currentHall;
    }

    public Hero getHero() {
        return hero;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public List<Enchantment> getEnchantments() {
        return enchantments;
    }
} 