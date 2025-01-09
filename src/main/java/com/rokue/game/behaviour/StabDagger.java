package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class StabDagger implements MonsterBehaviour {
    private static final long STAB_COOLDOWN_MS = 1500; // 1.5 seconds in milliseconds
    private long lastStabTime = 0;

    public void act(Hero hero, Monster monster) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastStabTime < STAB_COOLDOWN_MS) {
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 1.0) { // Distance of 1.0 means adjacent (including diagonals)
            System.out.println("Stab Dagger: The fighter stabs the hero!");
            hero.getEventManager().notify("HERO_STABBED", null);
            lastStabTime = currentTime;
        } else {
            System.out.println("Stab Dagger: Fighter is too far to stab. Distance: " + dist);
        }
    }
}
