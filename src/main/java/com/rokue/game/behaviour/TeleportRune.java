package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.monsters.Monster;

public class TeleportRune implements MonsterBehaviour {
    private Monster monster;

    public TeleportRune(Monster monster) {
        this.monster = monster;
    }

    @Override
    public void act(Hero hero, Hall hall) {
        Rune rune = hall.getRune();
        if (rune != null && !rune.isCollected()) {
            rune.moveRandomly(hall);
            System.out.println("TeleportRune: Rune has been teleported by the WizardMonster!");
        }
    }
}
