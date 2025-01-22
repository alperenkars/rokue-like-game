package com.rokue.game.entities.monsters;

import java.util.Random;

import com.rokue.game.behaviour.StabDagger;
import com.rokue.game.entities.Hall;
import com.rokue.game.util.Position;

public class FighterMonster extends Monster {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    private int moveCounter = 0;
    private static final int MOVE_INTERVAL = 60; // 60 frames = 1 second at 60 FPS

    public FighterMonster(Position startPosition) {
        super(startPosition, new StabDagger(), "FIGHTER");
    }

    // Custom deserialization to restore the transient behaviour field
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setBehaviour(new StabDagger()); // Restore the behaviour after deserialization
    }

    public void move(Hall hall, Position luringGemPosition) {
        moveCounter++;
        if (moveCounter < MOVE_INTERVAL) {
            return;
        }
        moveCounter = 0;

        if (luringGemPosition != null) {
            moveTowardsLuringGem(hall, luringGemPosition);
            return;
        }

        moveRandomly(hall);
    }

    private void moveRandomly(Hall hall) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int attempts = 0;
        
        while (attempts < 4) {
            int[] dir = directions[random.nextInt(directions.length)];
            Position newPosition = new Position(
                position.getX() + dir[0],
                position.getY() + dir[1]
            );

            if (isValidMove(hall, newPosition)) {
                hall.getCell(position).setContent(null);
                position = newPosition;
                hall.getCell(position).setContent(this);
                System.out.println("FighterMonster: Moving randomly.");
                break;
            }
            attempts++;
        }
    }

    private void moveTowardsLuringGem(Hall hall, Position luringGemPosition) {
        int dx = Integer.compare(luringGemPosition.getX(), position.getX());
        int dy = Integer.compare(luringGemPosition.getY(), position.getY());
        
        Position newPosition = new Position(
            position.getX() + dx,
            position.getY() + dy
        );

        if (isValidMove(hall, newPosition)) {
            hall.getCell(position).setContent(null);
            position = newPosition;
            hall.getCell(position).setContent(this);
            System.out.println("FighterMonster: Moving towards luring gem.");
        }
    }

    private boolean isValidMove(Hall hall, Position newPosition) {
        return hall.isWithinBounds(newPosition) && 
               hall.getCell(newPosition) != null && 
               hall.getCell(newPosition).getContent() == null;
    }
}