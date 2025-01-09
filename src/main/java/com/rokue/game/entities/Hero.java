package com.rokue.game.entities;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.rokue.game.actions.MoveAction;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

public class Hero {
    private volatile Position position;
    private final AtomicInteger lives;
    private EventManager eventManager;
    private volatile boolean isDead = false;
    private final ReentrantLock movementLock = new ReentrantLock();

    public Hero(Position startPosition, EventManager eventManager) {
        this.position = startPosition;
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
            rune.setCollected(true);
            System.out.println("Hero: Collected a rune at position " + cell.getPosition());

            eventManager.notify("RUNE_COLLECTED", currentHall);
        }
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
}