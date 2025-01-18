package com.rokue.game.behaviour;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeleportHero implements MonsterBehaviour {
    private final Hall currentHall;

    public TeleportHero(Hall currentHall) {
        this.currentHall = currentHall;
    }

    @Override
    public void act(Hero hero, Monster monster) {
        if (currentHall == null || hero == null || monster == null) {
            return;
        }

        // Eğer wizard “removed” ise, teleport işlemi yapmayalım.
        if (monster instanceof WizardMonster && ((WizardMonster) monster).isRemoved()) {
            return;
        }

        List<Position> emptyPositions = new ArrayList<>();
        for (int x = 0; x < currentHall.getWidth(); x++) {
            for (int y = 0; y < currentHall.getHeight(); y++) {
                Position position = new Position(x, y);
                Cell cell = currentHall.getCell(position);
                if (cell != null && cell.isEmpty()) {
                    emptyPositions.add(position);
                }
            }
        }

        if (emptyPositions.isEmpty()) {
            System.out.println("TeleportHero: No empty positions available for teleportation.");
            return;
        }

        Random random = new Random();
        Position randomPosition = emptyPositions.get(random.nextInt(emptyPositions.size()));
        hero.setPosition(randomPosition);
        System.out.println("TeleportHero: Hero teleported to position: " + randomPosition);
    }
}