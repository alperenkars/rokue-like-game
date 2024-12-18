package com.rokue.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.render.IRenderer;
import com.rokue.game.render.TileLoader;
import com.rokue.game.states.GameState;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;

public class PlayModeUI extends JPanel implements IRenderer {
    private PlayMode playMode;

    // Hall rendering constants
    private final int hallX = 20;
    private final int hallY = 20;
    private final int cellCountX = 20;
    private final int cellCountY = 20;
    private final int originalTileSize = 16;
    private final int scaleFactor = 2;
    private final int cellWidth = originalTileSize * scaleFactor;   // 32
    private final int cellHeight = originalTileSize * scaleFactor;  // 32
    private final int hallWidth = cellCountX * cellWidth;           // 640
    private final int hallHeight = cellCountY * cellHeight;         // 640

    // UI panel coordinates
    private final int uiX = hallX + hallWidth + 20;
    private final int uiTopY = 100;

    private BufferedImage hallBackground;

    // Tileset loader and tiles
    private TileLoader tileLoader;
    private BufferedImage floorTile;
    private BufferedImage wallTile;

    // Entities
    private BufferedImage playerImage;
    private BufferedImage runeImage;
    private BufferedImage fighterMonsterImage;
    private BufferedImage luringGemImage;

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
        setBackground(new Color(43, 27, 44));

        try {
            // Load the hall background image
            hallBackground = ImageIO.read(getClass().getResource("/assets/test_hall.png"));
            playerImage = ImageIO.read(getClass().getResource("/assets/player.png"));
            runeImage = ImageIO.read(getClass().getResource("/assets/cloakreveallure.png"));
            fighterMonsterImage = ImageIO.read(getClass().getResource("/assets/fighter.png"));
            luringGemImage = ImageIO.read(getClass().getResource("/assets/splitted_gem_cloakreveallure4x.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Adjust panel size to accommodate hall and UI
        setPreferredSize(new Dimension(hallX + hallWidth + 200, hallY + hallHeight + 40));
    }

    private void drawHall(Graphics2D g, Hall hall) {
        // Draw hall background, scaling it to match our cell grid
        if (hallBackground != null) {
            // Draw the background image scaled to fit our hall dimensions
            g.drawImage(hallBackground, 
                       hallX, hallY,             // Destination x,y
                       hallX + hallWidth,        // Destination width
                       hallY + hallHeight,       // Destination height
                       0, 0,                     // Source x,y
                       hallBackground.getWidth(), // Source width
                       hallBackground.getHeight(),// Source height
                       null);
        }

        // Draw grid for debugging (comment out in production)
        g.setColor(new Color(255, 255, 255, 30));  // Semi-transparent white
        for (int x = 0; x <= cellCountX; x++) {
            g.drawLine(hallX + x * cellWidth, hallY, 
                      hallX + x * cellWidth, hallY + hallHeight);
        }
        for (int y = 0; y <= cellCountY; y++) {
            g.drawLine(hallX, hallY + y * cellHeight, 
                      hallX + hallWidth, hallY + y * cellHeight);
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

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Hall hall = playMode.getCurrentHall();
        Hero hero = playMode.getHero();

        // Draw the hall background
        drawHall(g2d, hall);

        // Draw game objects
        drawGameObjects(g2d, hall);

        // Draw UI elements
        drawUI(g2d, hero);
    }

    private void drawGameObjects(Graphics2D g, Hall hall) {
        // Draw hero
        Hero hero = playMode.getHero();
        Position heroPos = hero.getPosition();
        int heroX = hallX + heroPos.getX() * cellWidth;
        int heroY = hallY + heroPos.getY() * cellHeight;
        if (playerImage != null) {
            // Center the hero in the cell
            g.drawImage(playerImage, 
                       heroX + (cellWidth - playerImage.getWidth(null))/2, 
                       heroY + (cellHeight - playerImage.getHeight(null))/2, 
                       cellWidth, cellHeight, null);
        }

        // Draw rune if it exists
        Rune rune = hall.getRune();
        if (rune != null) {
            Position runePos = rune.getPosition();
            int runeX = hallX + runePos.getX() * cellWidth;
            int runeY = hallY + runePos.getY() * cellHeight;
            if (runeImage != null) {
                // Center the rune in the cell
                g.drawImage(runeImage, 
                           runeX + (cellWidth - runeImage.getWidth(null))/2, 
                           runeY + (cellHeight - runeImage.getHeight(null))/2, 
                           cellWidth, cellHeight, null);
            }
        }

        //fighter monsters drawing
        for (FighterMonster monster : playMode.getFighterMonsters()) {
            Position monsterPos = monster.getPosition();
            int monsterX = hallX + monsterPos.getX() * cellWidth;
            int monsterY = hallY + monsterPos.getY() * cellHeight;
            if (fighterMonsterImage != null) {
                g.drawImage(fighterMonsterImage, 
                           monsterX + (cellWidth - fighterMonsterImage.getWidth(null))/2, 
                           monsterY + (cellHeight - fighterMonsterImage.getHeight(null))/2, 
                           cellWidth, cellHeight, null);
            }
        }
        // Draw other game objects here...
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