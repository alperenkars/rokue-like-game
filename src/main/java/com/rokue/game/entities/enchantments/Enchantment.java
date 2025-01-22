package com.rokue.game.entities.enchantments;

import java.io.Serializable;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public abstract class Enchantment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Position position;
    private boolean collected;

    public Enchantment(Position position) {
        this.position = position;
        this.collected = false;
    }

    public abstract void applyEffect(Hero hero);

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
    }
}