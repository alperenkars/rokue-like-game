package com.rokue.game.entities;

import java.io.File;
import java.io.Serializable;

import javax.swing.ImageIcon;

import com.rokue.game.util.Position;


public class DungeonObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private transient ImageIcon icon;  // transient because ImageIcon is not serializable
    private String iconPath;  // store the path instead
    private int widthInCells;
    private int heightInCells;
    private Position position;

    public DungeonObject(String name, String iconPath, int widthInCells, int heightInCells) {
        this.name = name;
        this.iconPath = iconPath;
        this.icon = new ImageIcon(new File(iconPath).getAbsolutePath());
        this.widthInCells = widthInCells;
        this.heightInCells = heightInCells;
    }

    // Custom deserialization to restore the icon
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.icon = new ImageIcon(new File(iconPath).getAbsolutePath());
    }

    public String getName() {
        return name;
    }

    public ImageIcon getIcon() {
        return icon;
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
