package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class ShootArrow implements MonsterBehaviour {
    private static final long SHOOT_COOLDOWN_MS = 1500; // 1.5 seconds in milliseconds
    private long lastShootTime = 0;

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
        if (currentTime - lastShootTime < SHOOT_COOLDOWN_MS) {
            return;
        }

        double dist = monster.getPosition().distance(hero.getPosition());
        if (dist <= 4.0) {
            System.out.println("ShootArrow: The arrow hits the hero!");
            hero.getEventManager().notify("HERO_HIT_BY_ARROW", null);
            lastShootTime = currentTime;
        } else {
            System.out.println("ShootArrow: The arrow missed the hero. Distance: " + dist);
        }
    }
}
