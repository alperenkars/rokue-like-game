package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.ShootArrow;
import com.rokue.game.util.Position;

public class ArcherMonster extends Monster {
    public ArcherMonster(Position startPosition) {
        super(startPosition, new ShootArrow(), "ARCHER");
    }
}