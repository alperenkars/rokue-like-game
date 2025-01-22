package com.rokue.game.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Hall currentHall;
    private final Hero hero;
    private final int remainingTime;
    private final List<Monster> monsters;
    private final List<Enchantment> enchantments;
    private final Cell[][] gridState;

    public GameSaveData(Hall currentHall, Hero hero, int remainingTime, 
                       List<Monster> monsters, List<Enchantment> enchantments) {
        this.currentHall = currentHall;
        this.hero = hero;
        this.remainingTime = remainingTime;
        
        // Deep copy monsters and enchantments to preserve their positions
        this.monsters = new ArrayList<>(monsters);
        this.enchantments = new ArrayList<>(enchantments);
        
        // Create a deep copy of the grid state
        Cell[][] originalGrid = currentHall.getGrid();
        this.gridState = new Cell[originalGrid.length][originalGrid[0].length];
        for (int x = 0; x < originalGrid.length; x++) {
            for (int y = 0; y < originalGrid[x].length; y++) {
                Cell originalCell = originalGrid[x][y];
                if (originalCell != null) {
                    this.gridState[x][y] = new Cell(new Position(x, y));
                    Object content = originalCell.getContent();
                    if (content != null) {
                        this.gridState[x][y].setContent(content);
                    }
                }
            }
        }
    }

    public Hall getCurrentHall() {
        return currentHall;
    }

    public Hero getHero() {
        return hero;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public List<Enchantment> getEnchantments() {
        return enchantments;
    }

    public Cell[][] getGridState() {
        return gridState;
    }
} 