package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.MonsterBehaviour;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public abstract class Monster implements MonsterBehaviour {
    protected Position position;

    public Monster(Position startPosition) {
        this.position = startPosition;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public abstract void update(Hero hero, Hall hall);

    public abstract void move();
}
