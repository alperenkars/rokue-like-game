package com.rokue.game.entities;

import com.rokue.game.util.Position;

import java.util.Random;

public class Rune {

    protected Position position;
    private Random rand = new Random();

    public Rune(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setCollected(boolean b) {
    }

    public void moveRandomly(Hall hall) {
        while (true) {
            Position newPos = new Position(rand.nextInt(hall.getWidth()), rand.nextInt(hall.getHeight()));
            if (hall.getCell(newPos).getContent() == null) {
                this.position = newPos;
                return;
            }
        }
    }
}