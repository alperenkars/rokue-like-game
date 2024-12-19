package com.rokue.game.entities;


import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

import java.util.ArrayList;
import java.util.List;

public class Hall {
    private String name;
    private int width;
    private int height;
    private Cell[][] grid;
    private Rune rune;
    private List<Monster> monsters;
    private Position luringGemPosition;
    private List<Enchantment> enchantments;

    public Hall(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        this.monsters = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        initializeGrid();
    }

    private void initializeGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(new Position(x, y));
            }
        }
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
        this.rune = rune;
        Position runePosition = rune.getPosition();
        getCell(runePosition).setContent(rune);
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
            monster.update(hero, this);
            if (monster instanceof FighterMonster fighterMonster) {
                fighterMonster.move(this, luringGemPosition);
            }
        }

        Cell currentCell = getCell(hero.getPosition());
        if (currentCell != null && currentCell.getContent() instanceof Rune) {
            hero.interactWithRune(currentCell, this);
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
}