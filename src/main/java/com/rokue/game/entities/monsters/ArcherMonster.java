package com.rokue.game.entities.monsters;

import com.rokue.game.entities.Hero;
import com.rokue.game.events.EventManager;
import com.rokue.game.entities.enchantments.CloakOfProtection;
import com.rokue.game.util.Position;

public class ArcherMonster extends Monster {
    private static final int DETECTION_RANGE = 4;
    private static final long SHOOT_INTERVAL_MS = 1000;
    private long lastShotTime;

    public ArcherMonster(Position position, EventManager eventManager) {
        super(position, eventManager);
        this.lastShotTime = System.currentTimeMillis();
        eventManager.notify("MONSTER_SPAWNED", "Archer");
    }

    @Override
    public void update(Hero hero) {
        Position heroPosition = hero.getPosition();
        Position archerPosition = getPosition();
        double distance = archerPosition.distanceTo(heroPosition);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastShotTime >= SHOOT_INTERVAL_MS) {
            shootArrow(hero, distance);
            lastShotTime = currentTime;
        }
    }

    private void shootArrow(Hero hero, double distance) {
        eventManager.notify("ARCHER_ARROW_SHOT", this.getPosition());

        boolean isCloaked = hero.isEnchantmentActive(CloakOfProtection.class);

        if (distance < DETECTION_RANGE && !isCloaked) {
            hero.decreaseLife();
            eventManager.notify("ARCHER_HIT_HERO", hero.getLives());
            System.out.println("ArcherMonster: Hit Hero! Hero's remaining lives: " + hero.getLives());
        } else {
            System.out.println("ArcherMonster: Arrow shot but Hero is cloaked or out of range.");
        }
    }

}
