package com.rokue.game.entities;

import com.rokue.game.actions.MoveAction;
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

    public void setPosition(Position position) {
        this.position = position;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}