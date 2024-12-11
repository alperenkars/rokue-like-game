package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;

public interface MonsterBehaviour {
    void move();
    void update(Hero hero, Hall hall);

}