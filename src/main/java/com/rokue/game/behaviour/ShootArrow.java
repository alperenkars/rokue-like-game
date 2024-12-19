package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class ShootArrow implements MonsterBehaviour {

    @Override
    public void act(Hero hero, Monster monster) {
        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 4.0) {
            System.out.println("ShootArrow: The arrow hits the hero!");
            hero.getEventManager().notify("HERO_HIT_BY_ARROW", null);
        } else {
            System.out.println("ShootArrow: The arrow missed the hero. Distance: " + dist);
        }
    }
}
