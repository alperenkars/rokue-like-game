package com.rokue.game.entities;

import com.rokue.game.actions.MoveAction;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hero {
    private Position position;
    private int lives;
    private EventManager eventManager;
    private List<Enchantment> bag;
    private List<Enchantment> activeEnchantments;

    public Hero(Position startPosition, EventManager eventManager) {
        this.position = startPosition;
        this.lives = 3;
        this.eventManager = eventManager;
        this.bag = new ArrayList<>();
        this.activeEnchantments = new ArrayList<>();
    }
    public Position getPosition() {
        return position;
    }

    public void move(MoveAction.Direction direction, Hall currentHall) {
        Position newPosition = position;
        switch (direction) {
            case UP:
                if (position.getY() > 1) {
                    newPosition = new Position(position.getX(), position.getY() - 1);
                }
                break;
            case DOWN:
                if (position.getY() < currentHall.getHeight() - 2) {
                    newPosition = new Position(position.getX(), position.getY() + 1);
                }
                break;
            case LEFT:
                newPosition = new Position(position.getX() - 1, position.getY());
                break;
            case RIGHT:
                newPosition = new Position(position.getX() + 1, position.getY());
                break;
        }

        if (currentHall != null && currentHall.getCell(newPosition) != null) {
            this.position = newPosition;
        } else {
            System.out.println("Cannot move outside the hall boundaries.");
        }
    }

    public void interactWithCell(Cell cell, Hall currentHall) {
        if (cell.getContent() instanceof Rune) {
            Rune rune = (Rune) cell.getContent();
            rune.setCollected(true);
            System.out.println("Hero: Collected a rune at position " + cell.getPosition());
            eventManager.notify("RUNE_COLLECTED", currentHall);
        }

        if (cell.getContent() instanceof Enchantment) {
            Enchantment enchantment = (Enchantment) cell.getContent();
            enchantment.applyEffect(this);
            cell.setContent(null);
            System.out.println("Hero: Collected enchantment - " + enchantment.getClass().getSimpleName());
        }
    }

    public void decreaseLife() {
        lives--;
        if (lives <= 0) {
            System.out.println("Hero: Out of lives. Game Over!");
            eventManager.notify("HERO_DEAD", null);
        }
    }

    public void increaseLife() {
        this.lives++;
    }

    public int getLives() {
        return lives;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public EventManager getEventManager() {
        return eventManager;
    }


    public void addToBag(Enchantment enchantment) {
        bag.add(enchantment);
        System.out.println("Hero: Enchantment added to bag - " + enchantment.getClass().getSimpleName());
    }


    public void removeFromBag(Enchantment enchantment) {
        bag.remove(enchantment);
        System.out.println("Hero: Enchantment removed from bag - " + enchantment.getClass().getSimpleName());
    }

    public void useEnchantment(Enchantment enchantment) {
        if (bag.contains(enchantment) && enchantment.isCollected()) {
            enchantment.use(this);
            removeFromBag(enchantment);
            eventManager.notify("ENCHANTMENT_USED", enchantment);
            System.out.println("Hero: Used enchantment - " + enchantment.getClass().getSimpleName());
        }
    }

    public <T extends Enchantment> boolean isEnchantmentActive(Class<T> clazz) {
        for (Enchantment e : activeEnchantments) {
            if (clazz.isInstance(e)) {
                return true;
            }
        }
        return false;
    }

    public void activateEnchantment(Enchantment enchantment) {
        activeEnchantments.add(enchantment);
        eventManager.notify("ENCHANTMENT_ACTIVATED", enchantment);
    }

    public void deactivateEnchantment(Enchantment enchantment) {
        activeEnchantments.remove(enchantment);
        eventManager.notify("ENCHANTMENT_DEACTIVATED", enchantment);
    }

    public void update() {
        Iterator<Enchantment> iterator = activeEnchantments.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();
            boolean isActive = enchantment.update(this);
            if (!isActive) {
                iterator.remove();
                eventManager.notify("ENCHANTMENT_EXPIRED", enchantment);
                System.out.println("Hero: Enchantment expired - " + enchantment.getClass().getSimpleName());
            }
        }
    }

}
