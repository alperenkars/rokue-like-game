package com.rokue.game.entities;

import com.rokue.game.events.EventManager;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

public class Hero {
    private Position position;
    private int lives;
    private EventManager eventManager;

    public Hero(Position startPosition, EventManager eventManager) {
        this.position = startPosition;
        this.lives = 3; // Default lives
        this.eventManager = eventManager;
    }

    public Position getPosition() {
        return position;
    }

    public void move(Position newPosition) {
        this.position = newPosition;
    }

    public void interactWithCell(Cell cell, Hall currentHall) {
        if (cell.getContent() instanceof Rune) {
            Rune rune = (Rune) cell.getContent();
            rune.setCollected(true);
            System.out.println("Hero: Collected a rune at position " + cell.getPosition());

            eventManager.notify("RUNE_COLLECTED", currentHall);
        }
    }

    public void decreaseLife() {
        lives--;
        if (lives <= 0) {
            System.out.println("Hero: Out of lives. Game Over!");
            eventManager.notify("HERO_DEAD", null);
        }
    }

    public int getLives() {
        return lives;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}