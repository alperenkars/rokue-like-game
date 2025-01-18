package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.util.Position;

public class StabDagger implements MonsterBehaviour {
    private static final long HIT_COOLDOWN_MS = 3000; // 3 seconds cooldown after successful hit
    private static final long MISS_COOLDOWN_MS = 1000; // 1 second cooldown after miss
    private long lastStabTime = 0;
    private boolean wasLastStabHit = false;
    private Position targetPosition = null;

    public void setTargetPosition(Position pos) {
        this.targetPosition = pos;
    }

    /**
     * Simulates a fighter monster attempting to stab the hero with a dagger.
     * 
     * @requires 
     *   - hero != null
     *   - monster != null
     *   - hero.getPosition() != null
     *   - monster.getPosition() != null
     *   - hero.getEventManager() != null
     * 
     * @modifies
     *   - this.lastStabTime
     *   - this.wasLastStabHit
     *   - hero's state (through event notification)
     * 
     * @effects
     *   - Checks the time since the last stab:
     *     - If the time elapsed < cooldown:
     *       - No action is taken
     *     - If the time elapsed >= cooldown:
     *       - Calculates the distance between monster and hero
     *       - If distance <= 1.0:
     *         - Notifies "HERO_STABBED" event
     *         - Updates lastStabTime to the current time
     *         - Sets wasLastStabHit to true
     *       - If distance > 1.0:
     *         - Logs a miss message
     *         - Updates lastStabTime to the current time
     *         - Sets wasLastStabHit to false
     */

    public void act(Hero hero, Monster monster) {
        long currentTime = System.currentTimeMillis();
        long cooldown = wasLastStabHit ? HIT_COOLDOWN_MS : MISS_COOLDOWN_MS;
        
        if (currentTime - lastStabTime < cooldown) {
            return;
        }

        if (targetPosition != null) {
            double distToTarget = monster.getPosition().distance(targetPosition);
            if (distToTarget <= 0.1) {
                targetPosition = null;
            }
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 1.0) {
            System.out.println("Stab Dagger: The fighter stabs the hero!");
            hero.getEventManager().notify("HERO_STABBED", null);
            wasLastStabHit = true;
        } else {
            System.out.println("Stab Dagger: Fighter is too far to stab. Distance: " + dist);
            wasLastStabHit = false;
        }
        lastStabTime = currentTime;
    }
}
