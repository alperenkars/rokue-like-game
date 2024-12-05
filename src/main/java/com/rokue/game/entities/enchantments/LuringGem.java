package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;

class LuringGem extends Enchantment {

    public void applyEffect(Hero hero) {
        System.out.println("LuringGem: Distracting FighterMonsters.");
        hero.getEventManager().notify("DISTRACTION", null);
    }
}
