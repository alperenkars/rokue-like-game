package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class StabDagger implements MonsterBehaviour {
    private static final int STAB_COOLDOWN = 90; // 90 frames = 1.5 seconds at 60 FPS
    private int cooldownCounter = 0;

    public void act(Hero hero, Monster monster) {
        if (cooldownCounter > 0) {
            cooldownCounter--;
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 1.0) { // Distance of 1.0 means adjacent (including diagonals)
            System.out.println("Stab Dagger: The fighter stabs the hero!");
            hero.getEventManager().notify("HERO_STABBED", null);
            cooldownCounter = STAB_COOLDOWN;
        } else {
            System.out.println("Stab Dagger: Fighter is too far to stab. Distance: " + dist);
        }
    }
}
