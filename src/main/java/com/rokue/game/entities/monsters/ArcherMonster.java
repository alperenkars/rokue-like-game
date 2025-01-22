package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.ShootArrow;
import com.rokue.game.util.Position;

public class ArcherMonster extends Monster {
    private static final long serialVersionUID = 1L;

    public ArcherMonster(Position startPosition) {
        super(startPosition, new ShootArrow(), "ARCHER");
    }

    // Custom deserialization to restore the transient behaviour field
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setBehaviour(new ShootArrow()); // Restore the behaviour after deserialization
    }
}