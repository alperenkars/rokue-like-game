package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public abstract class Enchantment {
    private boolean isCollected;
    protected Position position;

    public Enchantment(Position position) {
        this.position = position;
        this.isCollected = false;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        this.isCollected = true;
    }

    public Position getPosition() {
        return position;
    }

    public abstract void applyEffect(Hero hero);

}