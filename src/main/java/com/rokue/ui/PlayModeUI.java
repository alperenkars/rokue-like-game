package com.rokue.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.monsters.ArcherMonster;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.render.IRenderer;
import com.rokue.game.states.GameState;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;

public class PlayModeUI extends JPanel implements IRenderer, MouseListener {
    private PlayMode playMode;
    private JFrame gameWindow;

    // Hall rendering constants (matching BuildModeUI)
    private final int hallX = 20;
    private final int hallY = 20;
    private final int cellWidth = 32;
    private final int cellHeight = 32;
    private final int hallWidth = 640;
    private final int hallHeight = 640;
    private final Color BACKGROUND_COLOR = new Color(43, 27, 44); // Match BuildModeUI background

    private boolean isPaused = false;
    private BufferedImage pauseImage;
    private BufferedImage resumeImage;
    private final int BUTTON_SIZE = 40;
    private final int BUTTON_MARGIN = 20;

    // UI Panel constants
    private final int uiPanelWidth = 200;
    private final int uiPanelHeight = 400;

    private BufferedImage playerImage;
    private BufferedImage runeImage;

    // Monster images
    private BufferedImage archerImage;
    private BufferedImage fighterImage;
    private BufferedImage wizardImage;

    // Enchantment images
    private BufferedImage extraTimeImage;
    private BufferedImage revealImage;
    private BufferedImage cloakImage;
    private BufferedImage luringGemImage;
    private BufferedImage extraLifeImage;

    public PlayModeUI(PlayMode playMode, JFrame gameWindow) {
        this.playMode = playMode;
        this.gameWindow = gameWindow;
        this.setPreferredSize(new Dimension(900, 800));
        this.setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        loadImages();
    }

    private void drawHall(Graphics2D g, Hall hall) {
        // Draw grid
        g.setColor(new Color(255, 255, 255, 30));
        for (int x = 0; x <= hall.getWidth(); x++) {
            g.drawLine(hallX + x * cellWidth, hallY, 
                      hallX + x * cellWidth, hallY + hallHeight);
        }
        for (int y = 0; y <= hall.getHeight(); y++) {
            g.drawLine(hallX, hallY + y * cellHeight, 
                      hallX + hallWidth, hallY + y * cellHeight);
        }

        // Draw objects
        for (DungeonObject obj : hall.getObjects()) {
            Position pos = obj.getPosition();
            int x = hallX + pos.getX() * cellWidth;
            int y = hallY + pos.getY() * cellHeight;
            int width = obj.getWidthInCells() * cellWidth;
            int height = obj.getHeightInCells() * cellHeight;

            ImageIcon icon = (ImageIcon) obj.getIcon();
            if (icon != null) {
                g.drawImage(icon.getImage(), x, y, width, height, this);
            }
        }

        // Draw rune only if revealed
        Rune rune = hall.getRune();
        if (rune != null && rune.isRevealed() && !rune.isCollected()) {
            Position runePos = rune.getPosition();
            g.drawImage(runeImage, 
                       hallX + runePos.getX() * cellWidth, 
                       hallY + runePos.getY() * cellHeight, 
                       cellWidth, cellHeight, this);
        }

        // Draw monsters
        for (Monster monster : hall.getMonsters()) {
            Position monsterPos = monster.getPosition();
            BufferedImage monsterImage = null;
            
            if (monster instanceof ArcherMonster) {
                monsterImage = archerImage;
            } else if (monster instanceof FighterMonster) {
                monsterImage = fighterImage;
            } else if (monster instanceof WizardMonster) {
                monsterImage = wizardImage;
            }
            
            if (monsterImage != null) {
                g.drawImage(monsterImage, 
                           hallX + monsterPos.getX() * cellWidth, 
                           hallY + monsterPos.getY() * cellHeight, 
                           cellWidth, cellHeight, this);
            }
        }

        // Draw hero
        Hero hero = hall.getHero();
        if (hero != null) {
            Position heroPos = hero.getPosition();
            g.drawImage(playerImage, 
                       hallX + heroPos.getX() * cellWidth, 
                       hallY + heroPos.getY() * cellHeight, 
                       cellWidth, cellHeight, this);
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
    public void mousePressed(MouseEvent e) {
        if (!playMode.isPaused()) {
            // Convert mouse coordinates to grid position
            int gridX = (e.getX() - hallX) / cellWidth;
            int gridY = (e.getY() - hallY) / cellHeight;
            Position clickPos = new Position(gridX, gridY);

            Hall currentHall = playMode.getCurrentHall();
            if (currentHall != null && currentHall.isWithinBounds(clickPos)) {
                DungeonObject clickedObject = currentHall.getObjectAt(clickPos);
                if (clickedObject != null) {
                    Hero hero = playMode.getHero();
                    // Check if hero is adjacent to the clicked object
                    if (isAdjacentToHero(clickPos, hero.getPosition())) {
                        // Check if there's a rune under the clicked object
                        if (hero.checkForRune(hero.getPosition(), currentHall, clickedObject)) {
                            // Remove the object only if a rune was found
                            currentHall.removeObject(clickPos);
                            repaint(); // Force immediate repaint
                        }
                    }
                }
            }
        }
        checkPauseButtonClick(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Empty implementation - using mousePressed instead
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Empty implementation
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public void checkPauseButtonClick(int mouseX, int mouseY) {
        int buttonX = getWidth() - BUTTON_SIZE - BUTTON_MARGIN;
        int buttonY = BUTTON_MARGIN;

        if (mouseX >= buttonX && mouseX <= buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY <= buttonY + BUTTON_SIZE) {
            isPaused = !isPaused;
            if (isPaused) {
                // Pause game logic
                playMode.pause();
            } else {
                // Resume game logic
                playMode.resume();
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (playMode == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Hall hall = playMode.getCurrentHall();
        drawHall(g2d, hall);
        drawUI(g2d, playMode.getHero());

        BufferedImage currentImage = isPaused ? resumeImage : pauseImage;
        int x = getWidth() - BUTTON_SIZE - BUTTON_MARGIN;
        int y = BUTTON_MARGIN;
        g2d.drawImage(currentImage, x, y, BUTTON_SIZE, BUTTON_SIZE, null);
    }

    private void drawUI(Graphics2D g, Hero hero) {
        g.setColor(new Color(43, 27, 44));
        g.fillRect(hallX + hallWidth + 20, 100, 200, 400);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time: " + playMode.getRemainingTime() + " seconds", 
                    hallX + hallWidth + 20 + 10, 100 + 30);

        int heartSize = 20;
        int heartX = hallX + hallWidth + 20 + 10;
        int heartY = 100 + 50;
        for (int i = 0; i < hero.getLives(); i++) {
            g.setColor(Color.RED);
            g.fillOval(heartX + (i * (heartSize + 5)), heartY, heartSize, heartSize);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void loadImages() {
        try {
            playerImage = ImageIO.read(new File("src/main/resources/assets/player.png"));
            runeImage = ImageIO.read(new File("src/main/resources/assets/rune.png"));
            pauseImage = ImageIO.read(new File("src/main/resources/assets/pausebutton.png"));
            resumeImage = ImageIO.read(new File("src/main/resources/assets/resumebutton.png"));

            // Load monster images
            archerImage = ImageIO.read(new File("src/main/resources/assets/archer.png"));
            fighterImage = ImageIO.read(new File("src/main/resources/assets/fighter.png"));
            wizardImage = ImageIO.read(new File("src/main/resources/assets/wizard.png"));

            // Make the panel focusable to receive keyboard events
            setFocusable(true);
            
            // Add key listener from the game window
            for (KeyListener listener : gameWindow.getKeyListeners()) {
                addKeyListener(listener);
            }
            
            // Request focus when created
            requestFocusInWindow();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load game assets: " + e.getMessage());
        }
    }

    private boolean isAdjacentToHero(Position objectPos, Position heroPos) {
        int dx = Math.abs(objectPos.getX() - heroPos.getX());
        int dy = Math.abs(objectPos.getY() - heroPos.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }
}