package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.StabDagger;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.util.Position;

class FighterMonster extends Monster {
    public FighterMonster(Position startPosition) {
        super(startPosition, new StabDagger());
    }

    public void move() {
        System.out.println("ArcherMonster: Moving towards the hero!");
    }

}