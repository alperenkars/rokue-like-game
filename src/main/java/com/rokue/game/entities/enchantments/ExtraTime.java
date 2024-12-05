package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;

class ExtraTime extends Enchantment {
    private int timeToAdd;

    public ExtraTime(int timeToAdd) {
        this.timeToAdd = timeToAdd;
    }

    public void applyEffect(Hero hero) {
        System.out.println("ExtraTime: Adding " + timeToAdd + " seconds.");
        hero.getEventManager().notify("ADD_TIME", timeToAdd);
    }
}
