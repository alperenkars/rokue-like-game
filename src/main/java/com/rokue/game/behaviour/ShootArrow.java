package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class ShootArrow implements MonsterBehaviour {
    private static final long SHOOT_COOLDOWN_MS = 3000; // 3 seconds cooldown
    private static final long SHOOT_DURATION_MS = 1000; // 1 second attack duration
    private long lastShootTime = 0;
    private long currentShootStartTime = 0;
    private boolean isShooting = false;

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
        
        // If we're in cooldown, do nothing
        if (currentTime - lastShootTime < SHOOT_COOLDOWN_MS) {
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 4.0) {
            if (!isShooting) {
                // Start new shot
                isShooting = true;
                currentShootStartTime = currentTime;
                System.out.println("ShootArrow: The archer starts shooting!");
                hero.getEventManager().notify("HERO_HIT_BY_ARROW", null);
            } else if (currentTime - currentShootStartTime >= SHOOT_DURATION_MS) {
                // End shot and start cooldown
                isShooting = false;
                lastShootTime = currentTime;
                System.out.println("ShootArrow: The archer finished shooting!");
            }
        } else {
            System.out.println("ShootArrow: The arrow missed the hero. Distance: " + dist);
        }
    }
}
