package com.rokue.game.entities;

import com.rokue.game.util.Position;

public class Rune {
    private Position position; 
    private boolean isCollected;

    public Rune(Position position) {
        this.position = position;
        this.isCollected = false;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public void moveRandomly(Hall hall) {
        // Teleport the rune to a random position within the hall
        Position randomPos;
        do {
            randomPos = Position.random(hall.getWidth(), hall.getHeight());
        } while (!hall.isWithinBounds(randomPos) || hall.getCell(randomPos).getContent() != null);

        this.position = randomPos;
        System.out.println("Rune teleported to new position: " + position);
    }
}
