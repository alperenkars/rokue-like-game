package com.rokue.game.entities;

import java.util.List;
import java.util.Random;

import com.rokue.game.util.Position;

public class Rune {

    protected Position position;
    private Random rand = new Random();
    private boolean collected = false;
    private boolean revealed = false;
    private DungeonObject hiddenUnder;

    public Rune(Position position) {
        this.position = position;
        this.revealed = false;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public DungeonObject getHiddenUnder() {
        return hiddenUnder;
    }

    public void setHiddenUnder(DungeonObject object) {
        this.hiddenUnder = object;
        if (object != null) {
            this.position = object.getPosition();
            this.revealed = false;
        }
    }

    public void moveToRandomObject(Hall hall) {
        List<DungeonObject> objects = hall.getObjects();
        if (objects.isEmpty()) {
            return;
        }
        
        DungeonObject randomObject = objects.get(rand.nextInt(objects.size()));
        setHiddenUnder(randomObject);
    }
}