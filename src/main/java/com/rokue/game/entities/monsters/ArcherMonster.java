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

    @Override
    public void update(Hero hero, Hall hall) {
        System.out.println("ArcherMonster: Shooting arrows!");
        if (distanceTo(hero.getPosition()) < 4) {
            hero.decreaseLife();
        }
    }

    public void move() {
        System.out.println("ArcherMonster: Moving towards the hero!");
    }

    private int distanceTo(Position heroPosition) {
        return Math.abs(position.getX() - heroPosition.getX()) + Math.abs(position.getY() - heroPosition.getY());
    }
}