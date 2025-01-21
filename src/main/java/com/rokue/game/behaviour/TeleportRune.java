package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.monsters.Monster;

public class TeleportRune implements MonsterBehaviour {
    private static final long TELEPORT_COOLDOWN_MS = 5000; // 5 seconds
    private long lastTeleportTime = 0;
    private Hall currentHall;

    public void setHall(Hall hall) {
        this.currentHall = hall;
    }

    @Override
    public void act(Hero hero, Monster monster) {
        if (currentHall == null) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime < TELEPORT_COOLDOWN_MS) {
            return;
        }

        Rune rune = currentHall.getRune();
        if (rune != null && !rune.isCollected()) {
            System.out.println("Wizard: Teleported rune to a new location");
            hero.getEventManager().notify("RUNE_TELEPORTED", currentHall);
            lastTeleportTime = currentTime;
        }
    }
}
