package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class TeleportRune implements MonsterBehaviour {
    private static final long TELEPORT_COOLDOWN_MS = 5000; // 5 seconds
    private long lastTeleportTime = 0;

    public void act(Hero hero, Monster monster) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime < TELEPORT_COOLDOWN_MS) {
            return;
        }

        System.out.println("Teleport Rune: Teleporting...");
        hero.getEventManager().notify("RUNE_TELEPORTED", null);
        lastTeleportTime = currentTime;
    }
}
