package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.MonsterBehaviour;
import com.rokue.game.behaviour.TeleportRune;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class WizardMonster extends Monster {
    private boolean isRemoved = false;

    public WizardMonster(Position startPosition) {
        super(startPosition, new TeleportRune());
    }

    public boolean isRemoved() {
        return isRemoved;
    }
    public void markAsRemoved() {
        this.isRemoved = true;
    }
    public void setRemoved(boolean removed) {
        this.isRemoved = removed;
    }

}