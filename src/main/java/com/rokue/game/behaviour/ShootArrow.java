package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;

public class ShootArrow implements MonsterBehaviour {
    public void act(Hero hero, Hall hall) {
        System.out.println("Shoot Arrow");
    }
}
