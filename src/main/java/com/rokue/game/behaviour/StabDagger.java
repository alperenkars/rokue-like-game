package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class StabDagger implements MonsterBehaviour {
    private static final long STAB_COOLDOWN_MS = 3000; // 3 seconds cooldown
    private static final long STAB_DURATION_MS = 1000; // 1 second attack duration
    private long lastStabTime = 0;
    private long currentStabStartTime = 0;
    private boolean isStabbing = false;

    public void act(Hero hero, Monster monster) {
        long currentTime = System.currentTimeMillis();
        
        // If we're in cooldown, do nothing
        if (currentTime - lastStabTime < STAB_COOLDOWN_MS) {
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 1.0) { // Distance of 1.0 means adjacent (including diagonals)
            if (!isStabbing) {
                // Start new stab
                isStabbing = true;
                currentStabStartTime = currentTime;
                System.out.println("Stab Dagger: The fighter starts stabbing!");
                hero.getEventManager().notify("HERO_STABBED", null);
            } else if (currentTime - currentStabStartTime >= STAB_DURATION_MS) {
                // End stab and start cooldown
                isStabbing = false;
                lastStabTime = currentTime;
                System.out.println("Stab Dagger: The fighter finished stabbing!");
            }
        } else {
            System.out.println("Stab Dagger: Fighter is too far to stab. Distance: " + dist);
        }
    }
}
