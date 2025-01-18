package com.rokue.game.behaviour;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.entities.Hall;
import com.rokue.game.util.Position;
import java.util.Random;

public class HelpfulWizardStrategy implements WizardStrategy {
    private boolean hasHelped = false;
    private Random random = new Random();
    private long spawnTime;
    private static final long INITIAL_DELAY = 500; // 500ms delay to allow rendering

    public HelpfulWizardStrategy() {
        this.spawnTime = System.currentTimeMillis();
    }

    @Override
    public void executeStrategy(Hero hero, WizardMonster wizard) {
        if (!hasHelped) {
            // Wait for initial delay
            if (System.currentTimeMillis() - spawnTime < INITIAL_DELAY) {
                return;
            }

            Hall hall = wizard.getCurrentHall();
            if (hall == null) return;
            
            // Find a random empty position
            Position newPosition;
            do {
                newPosition = new Position(
                    random.nextInt(hall.getWidth()),
                    random.nextInt(hall.getHeight())
                );
            } while (hall.getCell(newPosition).getContent() != null);

            // Move hero to new position
            hall.getCell(hero.getPosition()).setContent(null);
            hero.setPosition(newPosition);
            hall.getCell(newPosition).setContent(hero);
            
            hasHelped = true;
            wizard.markForRemoval();
        }
    }
} 