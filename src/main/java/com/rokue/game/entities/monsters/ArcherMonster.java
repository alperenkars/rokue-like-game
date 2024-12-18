package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.MonsterBehaviour;
import com.rokue.game.behaviour.ShootArrow;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

class ArcherMonster extends Monster {
    public ArcherMonster(Position startPosition) {
        super(startPosition, new ShootArrow());
    }

    public void move() {
        System.out.println("ArcherMonster: Moving towards the hero!");
    }

    public void move(Hall hall){
        return; //This monster does not use, but the Fighter Monster does.
    }

}