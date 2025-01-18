package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Rune;

public class ChallengingWizardStrategy implements WizardStrategy {
    private long lastTeleportTime = 0;
    private static final long TELEPORT_INTERVAL = 3000; // 3 seconds in milliseconds

    @Override
    public void executeStrategy(Hero hero, WizardMonster wizard) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime >= TELEPORT_INTERVAL) {
            Hall hall = wizard.getCurrentHall();
            if (hall != null && hall.getRune() != null && !hall.getRune().isCollected()) {
                System.out.println("Wizard: Teleporting rune to challenge the hero");
                wizard.getEventManager().notify("RUNE_TELEPORTED", hall);
                lastTeleportTime = currentTime;
            }
        }
    }
} 