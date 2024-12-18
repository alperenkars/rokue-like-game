package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.MonsterBehaviour;
import com.rokue.game.behaviour.TeleportRune;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

class WizardMonster extends Monster {
    public WizardMonster(Position startPosition) {
        super(startPosition, new TeleportRune());
    }

    @Override
    public void update(Hero hero, Hall hall) {
        System.out.println("WizardMonster: Teleporting rune!");
        if (hall.getRune() != null) {
            hall.getRune().moveRandomly(hall);
        }
    }

    public void move() {
        return; // This monster does not move but this method is required by the abstract class.
    }

}