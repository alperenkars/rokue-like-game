package com.rokue.game.actions;

public class ClickDungeonAction implements IAction {
    private int x;
    private int y;

    public ClickDungeonAction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
