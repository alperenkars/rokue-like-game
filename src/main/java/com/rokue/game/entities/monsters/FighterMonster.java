package com.rokue.game.entities.monsters;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.util.Position;

class FighterMonster extends Monster {
    public FighterMonster(Position startPosition) {
        super(startPosition);
    }

    @Override
    public void update(Hero hero, Hall hall) {
        System.out.println("FighterMonster: Moving randomly!");
        Position newPosition = hall.getNeighbors(position).getFirst().getPosition();
        setPosition(newPosition);
    }
}