package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;

public interface WizardStrategy extends MonsterBehaviour {
    @Override
    default void act(Hero hero, Monster monster) {
        if (monster instanceof WizardMonster) {
            executeStrategy(hero, (WizardMonster) monster);
        }
    }

    void executeStrategy(Hero hero, WizardMonster wizard);
} 