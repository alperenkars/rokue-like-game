package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class StabDagger implements MonsterBehaviour {
    public void act(Hero hero, Monster monster) {
        System.out.println("Stab Dagger");
        hero.getEventManager().notify("HERO_STABBED", null);
    }

}
