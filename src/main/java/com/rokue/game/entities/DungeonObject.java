package com.rokue.game.entities;

import com.rokue.game.util.Position;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;


public class DungeonObject extends JLabel implements Serializable {
    private static final long serialVersionUID = 1L; // Add a serialVersionUID
    private final String name;
    private final String imagePath;
    private Position position;
    private final int widthInCells;  // Number of cells wide
    private final int heightInCells; // Number of cells tall

    public DungeonObject(String name, String imagePath, int widthInCells, int heightInCells) {
        this.name = name;
        this.imagePath = imagePath;
        this.widthInCells = widthInCells;
        this.heightInCells = heightInCells;

        ImageIcon icon = new ImageIcon(new File(imagePath).getAbsolutePath());
        setIcon(icon);
        setSize(icon.getIconWidth(), icon.getIconHeight());
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getWidthInCells() {
        return widthInCells;
    }

    public int getHeightInCells() {
        return heightInCells;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
