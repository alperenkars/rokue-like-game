package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;

public interface MonsterBehaviour {
    void act(Hero hero, Hall hall);
}