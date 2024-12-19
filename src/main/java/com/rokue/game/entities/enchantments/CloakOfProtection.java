package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class CloakOfProtection extends Enchantment {

    public CloakOfProtection(Position position) {
        super(position);

    }
    public void applyEffect(Hero hero) {
        System.out.println("CloakOfProtection: Hero is invisible to ArcherMonsters for 20 seconds.");
        hero.getEventManager().notify("INVISIBILITY", 20);
    }
}