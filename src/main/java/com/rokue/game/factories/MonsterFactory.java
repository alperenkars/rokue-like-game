package com.rokue.game.factories;

import java.util.Random;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.monsters.ArcherMonster;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.util.Position;

public class MonsterFactory {
    private static Random rand = new Random();

    public static Monster createRandomMonster(Hall hall) {
        int monsterType = rand.nextInt(3);
        // This may cause an infinite loop if the hall is full, refactor later
        while (true) {
            Position spawnPos = new Position(rand.nextInt(hall.getWidth()), rand.nextInt(hall.getHeight()));
            if (hall.getCell(spawnPos).getContent() == null) {
                switch(monsterType) {
                    case 0:
                        return new ArcherMonster(spawnPos);
                    case 1:
                        return new FighterMonster(spawnPos);
                    case 2:
                        return new WizardMonster(spawnPos);
                }
            }
        }
    }
}
