package com.rokue.game.factories;

import java.util.Random;

import com.rokue.game.entities.monsters.ArcherMonster;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.util.Position;

public class MonsterFactory {
    private static Random rand = new Random();

    public static Monster createRandomMonster(int maxX, int maxY) {
        int monsterType = rand.nextInt(3);
        Position spawnPos = new Position(rand.nextInt(maxX), rand.nextInt(maxY));
        return createMonster(monsterType, spawnPos);
    }

    public static Monster createMonster(int type, Position position) {
        switch(type) {
            case 0:
                return new ArcherMonster(position);
            case 1:
                return new FighterMonster(position);
            case 2:
                return new WizardMonster(position);
            default:
                return new FighterMonster(position);
        }
    }
}