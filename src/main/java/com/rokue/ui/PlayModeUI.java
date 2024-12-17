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

    public PlayModeUI(PlayMode playMode) {
        this.playMode = playMode;
        setBackground(Color.BLACK);

        tileLoader = new TileLoader("/assets/0x72_16x16DungeonTileset.v5.png", originalTileSize, originalTileSize, scaleFactor);

        floorTile = tileLoader.getTile(1,0); // Example floor tile
        wallTile = tileLoader.getTile(0,0);  // Example wall tile

        try {
            playerImage = ImageIO.read(getClass().getResource("/assets/player.png"));
            runeImage = ImageIO.read(getClass().getResource("/assets/cloakreveallure.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPreferredSize(new Dimension(hallX + hallWidth + 300, hallY + hallHeight + 50));
    }

    @Override
    public void render(GameState state) {
        if (state instanceof PlayMode) {
            this.playMode = (PlayMode) state;
        }
        repaint();
    }

    private boolean isBoundaryCell(int x, int y) {
        return x == 0 || x == cellCountX - 1 || y == 0 || y == cellCountY - 1;
    }

    // Update the paintComponent method to use the isBoundaryCell method
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (playMode == null) return;

        Hall hall = playMode.getCurrentHall();
        Hero hero = playMode.getHero();
        int remainingTime = playMode.getRemainingTime();

        int width = hall.getWidth();
        int height = hall.getHeight();

        for (int y = 0; y < cellCountY; y++) {
            for (int x = 0; x < cellCountX; x++) {
                int screenX = hallX + x * cellWidth;
                int screenY = hallY + y * cellHeight;

                BufferedImage tileToDraw;

                // Select floor or wall tiles
                if (isBoundaryCell(x, y)) {
                    tileToDraw = wallTile; // Walls at boundaries
                } else {
                    tileToDraw = floorTile; // Floors in the middle
                }

                // Draw the tile scaled to 80x80
                g.drawImage(tileToDraw, screenX, screenY, cellWidth, cellHeight, null);

                // Draw entities (hero, rune, etc.) here
                if (x == 2 && y == 2 && runeImage != null) { // Example: Rune at (2,2)
                    g.drawImage(runeImage, screenX, screenY, cellWidth, cellHeight, null);
                }

            }
        }

        // Draw hero
        Position heroPos = hero.getPosition();
        int heroX = hallX + heroPos.getX() * cellWidth;
        int heroY = hallY + heroPos.getY() * cellHeight;
        if (playerImage != null) {
            // Draw hero scaled to exactly one cell
            g.drawImage(playerImage, heroX, heroY, heroX + cellWidth, heroY + cellHeight,
                    0, 0, playerImage.getWidth(), playerImage.getHeight(), null);
        } else {
            // fallback hero representation
            g.setColor(Color.BLUE);
            g.fillOval(heroX + cellWidth/4, heroY + cellHeight/4, cellWidth/2, cellHeight/2);
        }

        // Draw UI panel
        g.setColor(new Color(43, 27, 44));
        g.fillRect(uiX, uiTopY, 150, 100);

        g.setColor(Color.WHITE);
        g.drawString("Time : " + remainingTime + " seconds", uiX + 10, uiTopY + 20);

        g.setColor(Color.RED);
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < hero.getLives(); i++) {
            hearts.append("♥ ");
        }
        g.drawString(hearts.toString(), uiX + 10, uiTopY + 40);
    }
}