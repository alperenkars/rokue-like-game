package com.rokue.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.rokue.game.behaviour.TeleportRune;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

public class Hall {
    private String name;
    private int width;
    private int height;
    private Cell[][] grid;
    private Rune rune;
    private List<Monster> monsters;
    private List<DungeonObject> objects;
    private int minObjectRequirement;
    private Position luringGemPosition;
    private List<Enchantment> enchantments;
    private Hero hero;


    public Hall(String name, int width, int height, int minObjectRequirement) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        this.monsters = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.minObjectRequirement = minObjectRequirement;
        initializeGrid();
    }


    private void initializeGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(new Position(x, y));
            }
        }
    }

    public boolean isRequirementMet() {
        return objects.size() >= minObjectRequirement;
    }

    public int getMinObjectRequirement() {
        return minObjectRequirement;
    }

    public Cell getCell(Position position) {
        if (isWithinBounds(position)) {
            return grid[position.getX()][position.getY()];
        }
        return null;
    }

    public boolean isWithinBounds(Position position) {
        return position.getX() >= 0 && position.getX() < width
                && position.getY() >= 0 && position.getY() < height;
    }

    public List<Cell> getNeighbors(Position position) {
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            Position neighborPos = new Position(position.getX() + dir[0], position.getY() + dir[1]);
            if (isWithinBounds(neighborPos)) {
                neighbors.add(getCell(neighborPos));
            }
        }
        return neighbors;
    }

    // Add a DungeonObject to the Hall
    public boolean addObject(DungeonObject object, Position gridPosition) {
        int startX = gridPosition.getX();
        int startY = gridPosition.getY();
        int width = object.getWidthInCells();
        int height = object.getHeightInCells();

        // Ensure the object fits within the hall
        if (startX + width > this.width || startY + height > this.height) {
            return false; // Object doesn't fit
        }

        // Check for collisions with existing objects
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                Cell cell = getCell(new Position(x, y));
                if (cell == null || !cell.isEmpty()) {
                    return false; // Collision detected
                }
            }
        }

        // Place the object in the grid
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                Cell cell = getCell(new Position(x, y));
                if (cell != null) {
                    cell.setContent(object);
                }
            }
        }

        object.setPosition(gridPosition);
        objects.add(object);
        return true;
    }


    // Remove a DungeonObject from the Hall
    public boolean removeObject(Position position) {
        if (!isWithinBounds(position)) {
            return false;
        }

        DungeonObject object = getObjectAt(position);
        if (object != null) {
            Position objectPos = object.getPosition();
            int startX = objectPos.getX();
            int startY = objectPos.getY();
            int width = object.getWidthInCells();
            int height = object.getHeightInCells();

            // First: Remove from objects list
            objects.remove(object);

            // Second: Clear ALL cells that this object occupies (matching how we add objects)
            for (int x = startX; x < startX + width; x++) {
                for (int y = startY; y < startY + height; y++) {
                    Cell cell = getCell(new Position(x, y));
                    if (cell != null) {
                        // Clear any reference to this object
                        cell.setContent(null);
                    }
                }
            }

            // Finally: If there was a rune under this object, place it
            if (rune != null && object.equals(rune.getHiddenUnder()) && !rune.isCollected()) {
                Cell clickedCell = getCell(position);
                if (clickedCell != null) {
                    clickedCell.setContent(rune);
                    rune.setPosition(position);
                }
            }
            
            return true;
        }
        return false;
    }

    // Get all objects in the Hall
    public List<DungeonObject> getObjects() {
        return objects;
    }

    public boolean isCellOccupied(Position position) {
        for (DungeonObject obj : objects) {
            if (obj.getPosition() != null && obj.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    // Get the object at a specific position
    public DungeonObject getObjectAt(Position position) {
        if (!isWithinBounds(position)) {
            return null;
        }

        // Check all objects to see if this position is within their bounds
        for (DungeonObject obj : objects) {
            Position objPos = obj.getPosition();
            if (objPos != null) {
                int startX = objPos.getX();
                int startY = objPos.getY();
                int endX = startX + obj.getWidthInCells();
                int endY = startY + obj.getHeightInCells();

                if (position.getX() >= startX && position.getX() < endX &&
                    position.getY() >= startY && position.getY() < endY) {
                    return obj;
                }
            }
        }
        return null;
    }

    public void addMonster(Monster monster) {
        monsters.add(monster);
        Position monsterPosition = monster.getPosition();
        getCell(monsterPosition).setContent(monster);
    }

    public void removeMonster(Monster monster) {
        monsters.remove(monster);
        getCell(monster.getPosition()).setContent(null);
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public Rune getRune() {
        return rune;
    }

    public void setRune(Rune rune) {
        if (this.rune != null) {
            // Clear the old rune's cell if it was revealed
            if (this.rune.isRevealed() && !this.rune.isCollected()) {
                getCell(this.rune.getPosition()).setContent(null);
            }
        }
        this.rune = rune;
        if (rune != null) {
            // Only set the cell content if the rune is revealed and not collected
            if (rune.isRevealed() && !rune.isCollected()) {
                Position runePosition = rune.getPosition();
                getCell(runePosition).setContent(rune);
            }
            // Initialize rune by hiding it under a random object
            if (rune.getHiddenUnder() == null && !objects.isEmpty()) {
                rune.moveToRandomObject(this);
            }
        }
    }

    public void addEnchantment(Enchantment enchantment) {
        enchantments.add(enchantment);
        Position enchantmentPosition = enchantment.getPosition();
        getCell(enchantmentPosition).setContent(enchantment);
    }

    public void removeEnchantment(Enchantment enchantment) {
        enchantments.remove(enchantment);
        getCell(enchantment.getPosition()).setContent(null);
    }

    public List<Enchantment> getEnchantments() {
        return enchantments;
    }

    public void update(Hero hero) {
        for (Monster monster : new ArrayList<>(monsters)) {
            if (monster instanceof WizardMonster) {
                ((TeleportRune)monster.getBehaviour()).setHall(this);
            }
            monster.update(hero, this);
            if (monster instanceof FighterMonster fighterMonster) {
                fighterMonster.move(this, luringGemPosition);
            }
        }

        // Check if hero is on a revealed rune or enchantment
        Cell currentCell = getCell(hero.getPosition());
        if (currentCell != null) {
            Object content = currentCell.getContent();
            if (content instanceof Rune || content instanceof Enchantment) {
                hero.interactWithObject(currentCell, this);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public void clearMonsters() {
        monsters.clear();
    }

    public void clearEnchantments() {
        enchantments.clear();
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }
}