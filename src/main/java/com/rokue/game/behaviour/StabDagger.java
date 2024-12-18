package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;

public class StabDagger implements MonsterBehaviour {
    private int damage;
    public StabDagger(){
        this.damage = 10;
    }

    public void act(Hero hero, Hall hall) {
        System.out.println("Stab Dagger");
    }

    public int getDamage(){
        return damage;
    }

}