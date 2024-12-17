package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;


public abstract class Enchantment {
    private boolean isCollected;

    public Enchantment() {
        this.isCollected = false;
    }


    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        this.isCollected = true;
    }

    public abstract void applyEffect(Hero hero);

    public abstract void use(Hero hero);

    public abstract boolean update(Hero hero);
}
