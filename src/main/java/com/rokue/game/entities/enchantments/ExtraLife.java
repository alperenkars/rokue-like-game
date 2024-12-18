package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class ExtraLife extends Enchantment {
    public ExtraLife(Position position) {
        super(position);
    }

    public void applyEffect(Hero hero) {
        System.out.println("ExtraLife: Adding 1 life.");
        hero.getEventManager().notify("ADD_LIVES", null);
    }
}