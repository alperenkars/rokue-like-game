package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class LuringGem extends Enchantment {

    public LuringGem(Position position) {
        super(position);
    }
    public void applyEffect(Hero hero) {
        System.out.println("LuringGem: Distracting FighterMonsters.");
        hero.getEventManager().notify("DISTRACTION", null);
    }
}
