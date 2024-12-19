package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class TeleportRune implements MonsterBehaviour {
    public void act(Hero hero, Monster monster) {
        System.out.println("Teleport Rune");
        hero.getEventManager().notify("HERO_HIT_BY_ARROW", null);
    }

}
