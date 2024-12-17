package com.rokue.game.entities;

import com.rokue.game.util.Position;

public class Rune {

    protected Position position;

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
    }
}