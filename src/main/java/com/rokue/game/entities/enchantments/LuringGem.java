package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class LuringGem extends Enchantment {

    @Override
    public void applyEffect(Hero hero) {
        System.out.println("LuringGem: Distracting FighterMonsters.");
        char direction = 'D';
        Position gemPosition = calculateGemPosition(hero.getPosition(), direction);
        hero.getEventManager().notify("DISTRACTION", gemPosition);
    }

    private Position calculateGemPosition(Position heroPosition, char direction) {
        switch (direction) {
            case 'A':
                return new Position(heroPosition.getX() - 1, heroPosition.getY());
            case 'D':
                return new Position(heroPosition.getX() + 1, heroPosition.getY());
            case 'W':
                return new Position(heroPosition.getX(), heroPosition.getY() - 1);
            case 'S':
                return new Position(heroPosition.getX(), heroPosition.getY() + 1);
            default:
                return heroPosition;
        }
    }
}
