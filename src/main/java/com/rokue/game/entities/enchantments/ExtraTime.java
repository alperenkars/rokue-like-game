package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class ExtraTime extends Enchantment {
    private int timeToAdd;

    public ExtraTime(Position position, int timeToAdd) {
        super(position);
        this.timeToAdd = timeToAdd;
    }
    public void applyEffect(Hero hero) {
        System.out.println("ExtraTime: Adding " + timeToAdd + " seconds.");
        hero.getEventManager().notify("ADD_TIME", timeToAdd);
    }
}
