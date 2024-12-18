package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.MonsterBehaviour;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public abstract class Monster {
    protected Position position;
    private MonsterBehaviour behaviour;

    public Monster(Position startPosition, MonsterBehaviour behaviour) {
        this.position = startPosition;
        this.behaviour = behaviour;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public abstract void move();
    public abstract void move(Hall hall);

    public void update(Hero hero, Hall hall) {
        behaviour.act(hero, hall);
    }

}
