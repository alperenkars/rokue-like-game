package com.rokue.ui;

import com.rokue.game.render.IRenderer;
import com.rokue.game.states.GameState;
import com.rokue.game.states.PlayMode;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.util.Position;
import com.rokue.game.util.Cell;
import com.rokue.game.render.TileLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PlayModeUI extends JPanel implements IRenderer {
    private PlayMode playMode;

    // Hall rendering constants
    private final int hallX = 20;
    private final int hallY = 20;
    private final int cellCountX = 5;
    private final int cellCountY = 5;
    private final int originalTileSize = 16;
    private final int scaleFactor = 5;
    private final int cellWidth = originalTileSize * scaleFactor;   // 80
    private final int cellHeight = originalTileSize * scaleFactor;  // 80
    private final int hallWidth = cellCountX * cellWidth;           // 400
    private final int hallHeight = cellCountY * cellHeight;         // 400

    // UI panel coordinates
    private final int uiX = hallX + hallWidth + 50; // 20 + 400 + 50 = 470
    private final int uiTopY = 100;

    // Tileset loader and tiles
    private TileLoader tileLoader;
    private BufferedImage floorTile;
    private BufferedImage wallTile;

    // Entities
    private BufferedImage playerImage;
    private BufferedImage runeImage;

    // Define tile positions in the tileset
    private static final int FLOOR_TILE_X = 6;  // Dark floor tile position
    private static final int FLOOR_TILE_Y = 0;
    
    private static final int WALL_TOP_X = 4;    // Top wall tile position
    private static final int WALL_TOP_Y = 0;
    
    private static final int WALL_SIDE_X = 3;   // Side wall tile position
    private static final int WALL_SIDE_Y = 1;
    
    private static final int CORNER_TL_X = 3;   // Top-left corner position
    private static final int CORNER_TL_Y = 0;
    
    private static final int CORNER_TR_X = 5;   // Top-right corner position
    private static final int CORNER_TR_Y = 0;

    public PlayModeUI(PlayMode playMode) {
        this.playMode = playMode;
        setBackground(new Color(43, 27, 44));  // Dark purple background

        // Initialize tile loader with the dungeon tileset
        tileLoader = new TileLoader("/assets/0x72_16x16DungeonTileset.v5.png", 
                                  originalTileSize, originalTileSize, scaleFactor);

        // Load specific tiles from the tileset
        floorTile = tileLoader.getTile(FLOOR_TILE_X, FLOOR_TILE_Y);
        wallTile = tileLoader.getTile(WALL_TOP_X, WALL_TOP_Y);

        try {
            playerImage = ImageIO.read(getClass().getResource("/assets/player.png"));
            runeImage = ImageIO.read(getClass().getResource("/assets/cloakreveallure.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPreferredSize(new Dimension(hallX + hallWidth + 300, hallY + hallHeight + 50));
    }

    private void drawHall(Graphics g, Hall hall) {
        for (int y = 0; y < cellCountY; y++) {
            for (int x = 0; x < cellCountX; x++) {
                int screenX = hallX + x * cellWidth;
                int screenY = hallY + y * cellHeight;

                if (y == 0) {  // Top wall
                    if (x == 0) {
                        g.drawImage(tileLoader.getTile(CORNER_TL_X, CORNER_TL_Y), 
                                  screenX, screenY, cellWidth, cellHeight, null);
                    } else if (x == cellCountX - 1) {
                        g.drawImage(tileLoader.getTile(CORNER_TR_X, CORNER_TR_Y), 
                                  screenX, screenY, cellWidth, cellHeight, null);
                    } else {
                        g.drawImage(tileLoader.getTile(WALL_TOP_X, WALL_TOP_Y), 
                                  screenX, screenY, cellWidth, cellHeight, null);
                    }
                } else if (y == cellCountY - 1) {  // Bottom wall
                    // Similar logic for bottom wall...
                } else if (x == 0 || x == cellCountX - 1) {  // Side walls
                    g.drawImage(tileLoader.getTile(WALL_SIDE_X, WALL_SIDE_Y), 
                              screenX, screenY, cellWidth, cellHeight, null);
                } else {  // Floor
                    g.drawImage(floorTile, screenX, screenY, cellWidth, cellHeight, null);
                }
            }
        }
    }

    @Override
    public void render(GameState state) {
        if (state instanceof PlayMode) {
            this.playMode = (PlayMode) state;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (playMode == null) return;

        // Enable antialiasing for smoother rendering
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Hall hall = playMode.getCurrentHall();
        Hero hero = playMode.getHero();

        // Draw the hall
        drawHall(g2d, hall);

        // Draw hero
        Position heroPos = hero.getPosition();
        int heroX = hallX + heroPos.getX() * cellWidth;
        int heroY = hallY + heroPos.getY() * cellHeight;
        if (playerImage != null) {
            g2d.drawImage(playerImage, heroX, heroY, cellWidth, cellHeight, null);
        }

        // Draw UI elements
        drawUI(g2d, hero);
    }

    private void drawUI(Graphics2D g, Hero hero) {
        // Draw inventory panel background
        g.setColor(new Color(43, 27, 44));
        g.fillRect(uiX, uiTopY, 200, 400);

        // Draw time
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time: " + playMode.getRemainingTime() + " seconds", 
                    uiX + 10, uiTopY + 30);

        // Draw hearts
        int heartSize = 20;
        int heartX = uiX + 10;
        int heartY = uiTopY + 50;
        for (int i = 0; i < hero.getLives(); i++) {
            g.setColor(Color.RED);
            g.fillOval(heartX + (i * (heartSize + 5)), heartY, heartSize, heartSize);
        }
    }
}