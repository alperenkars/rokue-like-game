package com.rokue.game.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.rokue.game.actions.MoveAction;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

public class Hero {
    private final List<String> inventory; //for the enchantments
    private volatile Position position;
    private final AtomicInteger lives;
    private EventManager eventManager;
    private volatile boolean isDead = false;
    private final ReentrantLock movementLock = new ReentrantLock();

    public Hero(Position startPosition, EventManager eventManager, List<String> inventory) {
        this.position = startPosition;
        this.inventory = new ArrayList<>();
        this.lives = new AtomicInteger(3); // Default lives
        this.eventManager = eventManager;
    }

    public Position getPosition() {
        return position;
    }

    public void move(MoveAction.Direction direction, Hall currentHall) {
        movementLock.lock();
        try {
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
                Object content = currentHall.getCell(newPosition).getContent();
                if (content == null || content instanceof Rune) {
                    this.position = newPosition;
                } else {
                    System.out.println("Cannot move through objects.");
                }
            } else {
                System.out.println("Cannot move outside the hall boundaries.");
            }
        } finally {
            movementLock.unlock();
        }
    }

    public void interactWithRune(Cell cell, Hall currentHall) {
        if (cell.getContent() instanceof Rune) {
            Rune rune = (Rune) cell.getContent();
            if (rune.isRevealed() && !rune.isCollected()) {
                rune.setCollected(true);
                cell.setContent(null);  // Clear the cell
                System.out.println("Hero: Collected a rune at position " + cell.getPosition());
                eventManager.notify("RUNE_COLLECTED", currentHall);
            }
        }
    }

    public boolean checkForRune(Position position, Hall currentHall, DungeonObject clickedObject) {
        // Only check the clicked object for rune
        Rune rune = currentHall.getRune();
        if (rune != null && clickedObject.equals(rune.getHiddenUnder()) && !rune.isCollected()) {
            rune.setRevealed(true);
            // Set rune position to the object's position
            rune.setPosition(clickedObject.getPosition());
            // Update the cell content after object is removed
            Cell runeCell = currentHall.getCell(clickedObject.getPosition());
            if (runeCell != null) {
                runeCell.setContent(rune);
            }
            System.out.println("Hero: Found a rune hidden under " + clickedObject.getName());
            eventManager.notify("RUNE_REVEALED", currentHall);
            return true;
        }
        return false;
    }

    public synchronized void decreaseLife() {
        if (isDead) {
            return; // Prevent multiple death notifications
        }
        
        int currentLives = lives.decrementAndGet();
        if (currentLives <= 0 && !isDead) {
            isDead = true;
            System.out.println("Hero: Out of lives. Game Over!");
            eventManager.notify("HERO_DEAD", null);
        }
    }

    public int getLives() {
        return lives.get();
    }

    public void setPosition(Position newPosition) {
        movementLock.lock();
        try {
            this.position = newPosition;
        } finally {
            movementLock.unlock();
        }
    }

    public EventManager getEventManager() {
        return eventManager;
    }
    public List<String> getInventory() {
        return inventory;
    }
    public void addToInventory(String item) {
        inventory.add(item);
        System.out.println("Hero: Added " + item + " to inventory.");
    }
    public void removeFromInventory(String item) {
        if (inventory.remove(item)) {
            System.out.println("Hero: Removed " + item + " from inventory.");
        } else {
            System.out.println("Hero: Item " + item + " not found in inventory.");
        }
    }
    public boolean hasItem(String item) {
        return inventory.contains(item);
    }
}