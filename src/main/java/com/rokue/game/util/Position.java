package com.rokue.game.util;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Position position) {
        return x == position.getX() && y == position.getY();
    }

    public double distance(Position position) {
        return Math.sqrt(Math.pow(x - position.getX(), 2) + Math.pow(y - position.getY(), 2));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }


}