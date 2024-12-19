package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public class TeleportRune implements MonsterBehaviour {
    private static final int TELEPORT_COOLDOWN = 300;
    private int cooldownCounter = 0;

    public void act(Hero hero, Monster monster) {
        if (cooldownCounter > 0) {
            cooldownCounter--;
            return;
        }

        System.out.println("Teleport Rune");
        hero.getEventManager().notify("RUNE_TELEPORTED", null);
        cooldownCounter = TELEPORT_COOLDOWN;
    }
}
