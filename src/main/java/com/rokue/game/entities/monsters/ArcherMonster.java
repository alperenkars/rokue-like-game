package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.MonsterBehaviour;
import com.rokue.game.behaviour.ShootArrow;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class ArcherMonster extends Monster {
    public ArcherMonster(Position startPosition) {
        super(startPosition, new ShootArrow(null));


    }
    public void move() {
        System.out.println("ArcherMonster: Moving towards the hero!");
    }
    @Override
    public void update(Hero hero, Hall hall) {
        if (this.getBehaviour() != null) {
            this.getBehaviour().act(hero, hall);
        }

    }
}