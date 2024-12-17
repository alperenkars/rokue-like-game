package com.rokue.game.entities;

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

    public void move(Position newPosition) {
        this.position = newPosition;
        eventManager.notify("HERO_MOVED", this.position);
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
            cell.setContent(null); // Enchantment alındıktan sonra hücre boşaltılır
            System.out.println("Hero: Collected enchantment - " + enchantment.getClass().getSimpleName());
        }
    }

    public void decreaseLife() {
        lives--;
        eventManager.notify("HERO_LIFE_CHANGED", this.lives);

        if (lives <= 0) {
            System.out.println("Hero: Out of lives. Game Over!");
            eventManager.notify("HERO_DEAD", null);
        } else {
            System.out.println("Hero: Lost a life. Remaining lives: " + lives);
            eventManager.notify("HERO_HIT", this.lives);
        }
    }

    public void increaseLife(int amount) {
        this.lives += amount;
        eventManager.notify("HERO_LIFE_CHANGED", this.lives);
        System.out.println("Hero: Gained " + amount + " life(s). Total lives: " + lives);
    }

    public int getLives() {
        return lives;
    }

    public EventManager getEventManager() {
        return eventManager;
    }


    public void addToBag(Enchantment enchantment) {
        bag.add(enchantment);
        eventManager.notify("ENCHANTMENT_ADDED_TO_BAG", enchantment);
        System.out.println("Hero: Enchantment added to bag - " + enchantment.getClass().getSimpleName());
    }


    public void removeFromBag(Enchantment enchantment) {
        bag.remove(enchantment);
        eventManager.notify("ENCHANTMENT_REMOVED_FROM_BAG", enchantment);
        System.out.println("Hero: Enchantment removed from bag - " + enchantment.getClass().getSimpleName());
    }


    public <T extends Enchantment> T findEnchantment(Class<T> clazz) {
        for (Enchantment e : bag) {
            if (clazz.isInstance(e)) {
                return clazz.cast(e);
            }
        }
        return null;
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
