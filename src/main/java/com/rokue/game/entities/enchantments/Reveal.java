package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;

class Reveal extends Enchantment {

    @Override
    public void applyEffect(Hero hero) {
        System.out.println("Reveal: Highlighting rune location.");
        hero.getEventManager().notify("REVEAL_RUNE", null);
    }
}