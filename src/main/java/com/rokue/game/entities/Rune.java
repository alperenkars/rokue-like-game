package com.rokue.game.entities;

import java.util.Random;

import com.rokue.game.util.Position;

public class Rune {

    protected Position position;
    private Random rand = new Random();
    private boolean collected = false;

    public Rune(Position position) {
        this.position = position;
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

    public void collect(Hero hero) {
        hero.addToInventory(this); // rune added to inventory
        this.setCollected(true);
        System.out.println("Rune at position " +position+ " collected and added to inventory!");
    }

    public void moveRandomly(Hall hall) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 100; // Prevent infinite loop
        
        while (attempts < MAX_ATTEMPTS) {
            Position newPos = new Position(rand.nextInt(hall.getWidth()), rand.nextInt(hall.getHeight()));
            if (hall.getCell(newPos).getContent() == null && !newPos.equals(hall.getHero().getPosition())) {
                this.position = newPos;
                return;
            }
            attempts++;
        }
        
        // If we couldn't find a valid position after max attempts, try one last time with more distance from hero
        Position heroPos = hall.getHero().getPosition();
        while (true) {
            Position newPos = new Position(rand.nextInt(hall.getWidth()), rand.nextInt(hall.getHeight()));
            if (hall.getCell(newPos).getContent() == null && 
                newPos.distance(heroPos) > 2.0) { 
                this.position = newPos;
                return;
            }
        }
    }
}