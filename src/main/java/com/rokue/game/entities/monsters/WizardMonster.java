package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.TeleportRune;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class WizardMonster extends Monster {

    public WizardMonster(Position startPosition) {
        super(startPosition, new TeleportRune(null)); // Assign the teleport behavior
    }

    @Override
    public void move() {
        // WizardMonster does not move
    }

    @Override
    public void update(Hero hero, Hall hall) {
        System.out.println("WizardMonster: Teleporting the rune...");
        if (hall.getRune() != null) {
            hall.getRune().moveRandomly(hall);
        }
    }
}
