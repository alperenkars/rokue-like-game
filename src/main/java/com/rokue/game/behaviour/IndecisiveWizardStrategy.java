package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.WizardMonster;

public class IndecisiveWizardStrategy implements WizardStrategy {
    private long appearanceTime;
    private static final long DISAPPEAR_DELAY = 2000; // 2 seconds in milliseconds

    public IndecisiveWizardStrategy() {
        this.appearanceTime = System.currentTimeMillis();
    }

    @Override
    public void executeStrategy(Hero hero, WizardMonster wizard) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - appearanceTime >= DISAPPEAR_DELAY) {
            wizard.markForRemoval();
        }
    }
} 