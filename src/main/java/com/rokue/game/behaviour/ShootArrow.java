package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class ShootArrow implements MonsterBehaviour {
    private static final long HIT_COOLDOWN_MS = 3000; // 3 seconds cooldown after successful hit
    private static final long MISS_COOLDOWN_MS = 1000; // 1 second cooldown after miss
    private long lastShootTime = 0;
    private boolean wasLastShotHit = false;

    /**
     * Simulates an archer monster shooting an arrow at the hero.
     * 
     * @requires 
     *   - hero != null
     *   - monster != null
     *   - hero.getPosition() != null
     *   - monster.getPosition() != null
     *   - hero.getEventManager() != null
     * 
     * @modifies
     *   - this.lastShootTime
     *   - hero's state (through event notification)
     * 
     * @effects
     *   - If not enough time has passed since last shot:
     *     - No effects
     *   - If enough time has passed:
     *     - Calculates distance between monster and hero
     *     - If distance <= 4.0:
     *       - Notifies "HERO_HIT_BY_ARROW" event
     *       - Updates lastShootTime
     *     - If distance > 4.0:
     *       - Only logs a miss message
     */
    @Override
    public void act(Hero hero, Monster monster) {
        long currentTime = System.currentTimeMillis();
        long cooldown = wasLastShotHit ? HIT_COOLDOWN_MS : MISS_COOLDOWN_MS;
        
        // Check cooldown
        if (currentTime - lastShootTime < cooldown) {
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 4.0) {
            // Hit
            System.out.println("ShootArrow: The archer hits the hero!");
            hero.getEventManager().notify("HERO_HIT_BY_ARROW", null);
            wasLastShotHit = true;
        } else {
            // Miss
            System.out.println("ShootArrow: The arrow missed the hero. Distance: " + dist);
            wasLastShotHit = false;
        }
        lastShootTime = currentTime;
    }
}
