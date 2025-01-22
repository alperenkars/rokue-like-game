package com.rokue.game.util;

import java.io.Serializable;

public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;
    private Position position;
    private Object content;

    public Cell(Position position) {
        this.position = position;
        this.content = null;
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