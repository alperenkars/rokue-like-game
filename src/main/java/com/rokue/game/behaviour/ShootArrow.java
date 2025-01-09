package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class ShootArrow implements MonsterBehaviour {
    private static final int SHOOT_COOLDOWN = 90; // 90 frames = 1.5 seconds at 60 FPS
    private int cooldownCounter = 0;

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
     *   - this.cooldownCounter
     *   - hero's state (through event notification)
     * 
     * @effects
     *   - If cooldownCounter > 0:
     *     - Decrements cooldownCounter by 1
     *     - No other effects
     *   - If cooldownCounter == 0:
     *     - Calculates distance between monster and hero
     *     - If distance <= 4.0:
     *       - Notifies "HERO_HIT_BY_ARROW" event
     *       - Sets cooldownCounter to SHOOT_COOLDOWN (90)
     *     - If distance > 4.0:
     *       - Only logs a miss message
     */
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
