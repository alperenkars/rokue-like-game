package com.rokue.game.actions;

public class MoveAction implements IAction {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction direction;

    public MoveAction(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}