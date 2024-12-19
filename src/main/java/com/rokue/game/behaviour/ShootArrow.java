package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class ShootArrow implements MonsterBehaviour {
    private static final int SHOOT_COOLDOWN = 90; // 90 frames = 1.5 seconds at 60 FPS
    private int cooldownCounter = 0;

    @Override
    public void act(Hero hero, Monster monster) {
        if (cooldownCounter > 0) {
            cooldownCounter--;
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 4.0) {
            System.out.println("ShootArrow: The arrow hits the hero!");
            hero.getEventManager().notify("HERO_HIT_BY_ARROW", null);
            cooldownCounter = SHOOT_COOLDOWN;
        } else {
            System.out.println("ShootArrow: The arrow missed the hero. Distance: " + dist);
        }
    }
}
