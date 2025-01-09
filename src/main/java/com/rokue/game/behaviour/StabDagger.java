package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class StabDagger implements MonsterBehaviour {
    private static final long HIT_COOLDOWN_MS = 3000; // 3 seconds cooldown after successful hit
    private static final long MISS_COOLDOWN_MS = 1000; // 1 second cooldown after miss
    private long lastStabTime = 0;
    private boolean wasLastStabHit = false;

    public void act(Hero hero, Monster monster) {
        long currentTime = System.currentTimeMillis();
        long cooldown = wasLastStabHit ? HIT_COOLDOWN_MS : MISS_COOLDOWN_MS;
        
        // Check cooldown
        if (currentTime - lastStabTime < cooldown) {
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 1.0) { // Distance of 1.0 means adjacent (including diagonals)
            // Hit
            System.out.println("Stab Dagger: The fighter stabs the hero!");
            hero.getEventManager().notify("HERO_STABBED", null);
            wasLastStabHit = true;
        } else {
            // Miss
            System.out.println("Stab Dagger: Fighter is too far to stab. Distance: " + dist);
            wasLastStabHit = false;
        }
        lastStabTime = currentTime;
    }
}
