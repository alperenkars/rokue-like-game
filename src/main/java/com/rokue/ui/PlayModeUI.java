package com.rokue.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.*;

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
        this.addMouseListener(this);
        this.gameWindow = gameWindow;
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(hallX + hallWidth + uiPanelWidth + 40, 
                                     hallY + hallHeight + 80));

        // Make the panel focusable to receive keyboard events
        setFocusable(true);
        
        // Add key listener from the game window
        for (KeyListener listener : gameWindow.getKeyListeners()) {
            addKeyListener(listener);
        }
        
        // Request focus when created
        requestFocusInWindow();

        try {
            playerImage = ImageIO.read(new File("src/main/resources/assets/player.png"));
            runeImage = ImageIO.read(new File("src/main/resources/assets/rune.png"));

            pauseImage = ImageIO.read(new File("src/main/resources/assets/pausebutton.png"));
            resumeImage = ImageIO.read(new File("src/main/resources/assets/resumebutton.png"));

            // Load monster images
            archerImage = ImageIO.read(new File("src/main/resources/assets/archer.png"));
            fighterImage = ImageIO.read(new File("src/main/resources/assets/fighter.png"));
            wizardImage = ImageIO.read(new File("src/main/resources/assets/wizard.png"));

//            // Load enchantment images
//            extraTimeImage = ImageIO.read(new File("src/main/resources/assets/extratime.png"));
//            revealImage = ImageIO.read(new File("src/main/resources/assets/reveal.png"));
//            cloakImage = ImageIO.read(new File("src/main/resources/assets/cloak.png"));
//            luringGemImage = ImageIO.read(new File("src/main/resources/assets/luringgem.png"));
//            extraLifeImage = ImageIO.read(new File("src/main/resources/assets/extralife.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawHall(Graphics2D g, Hall hall) {
        // Draw grid (matching BuildModeUI's grid)
        g.setColor(new Color(255, 255, 255, 30));  // Match BuildModeUI grid color
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

        // Draw rune
        Rune rune = hall.getRune();
        if (rune != null && runeImage != null) {
            Position runePos = rune.getPosition();
            int runeX = hallX + runePos.getX() * cellWidth;
            int runeY = hallY + runePos.getY() * cellHeight;
            g.drawImage(runeImage,
                       runeX,
                       runeY,
                       cellWidth,  // Use full cell width
                       cellHeight, // Use full cell height
                       null);
        }

        // Draw hero with consistent size
        Hero hero = playMode.getHero();
        Position heroPos = hero.getPosition();
        int heroX = hallX + heroPos.getX() * cellWidth;
        int heroY = hallY + heroPos.getY() * cellHeight;
        if (playerImage != null) {
            g.drawImage(playerImage,
                       heroX, 
                       heroY, 
                       cellWidth,  // Use full cell width
                       cellHeight, // Use full cell height
                       null);
        }

        // Draw monsters with consistent size
        for (Monster monster : playMode.getCurrentHall().getMonsters()) {
            Position monsterPos = monster.getPosition();
            int monsterX = hallX + monsterPos.getX() * cellWidth;
            int monsterY = hallY + monsterPos.getY() * cellHeight;
            
            BufferedImage monsterImage = null;
            if (monster instanceof ArcherMonster) monsterImage = archerImage;
            else if (monster instanceof FighterMonster) monsterImage = fighterImage;
            else if (monster instanceof WizardMonster) monsterImage = wizardImage;
            
            if (monsterImage != null) {
                g.drawImage(monsterImage,
                           monsterX,
                           monsterY,
                           cellWidth,  // Use full cell width
                           cellHeight, // Use full cell height
                           null);
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
    public void mouseClicked(MouseEvent e) {
        checkPauseButtonClick(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

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
}