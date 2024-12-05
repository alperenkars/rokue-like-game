package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;

class CloakOfProtection extends Enchantment {

    public void applyEffect(Hero hero) {
        System.out.println("CloakOfProtection: Hero is invisible to ArcherMonsters for 20 seconds.");
        hero.getEventManager().notify("INVISIBILITY", 20);
    }
}