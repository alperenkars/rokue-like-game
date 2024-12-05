package com.rokue.game.util;

public class Cell {
    private Position position;
    private Object content;

    public Cell(Position position) {
        this.position = position;
        this.content = null; // Empty by default
    }

    public Position getPosition() {
        return position;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public boolean isEmpty() {
        return content == null;
    }
}
