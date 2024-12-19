package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class StabDagger implements MonsterBehaviour {
    public void act(Hero hero, Monster monster) {
        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 1.0) {
            System.out.println("Stab Dagger: The fighter stabs the hero!");
            hero.getEventManager().notify("HERO_STABBED", null);
        } else {
            System.out.println("Stab Dagger: Fighter is too far to stab. Distance: " + dist);
        }
    }
}
