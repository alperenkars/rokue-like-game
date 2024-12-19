package com.rokue.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.ImageIcon;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.enchantments.*;
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

    private final int uiX = hallX + hallWidth + 20;
    private final int uiTopY = 100;

    // Pause button
    private boolean isPaused = false;
    private BufferedImage pauseImage;
    private BufferedImage resumeImage;
    private final int BUTTON_SIZE = 40;
    private final int BUTTON_MARGIN = 20;
    

    private BufferedImage hallBackground;

    // This is for purely dynamic rendering, which might be implemented later.
//    private TileLoader tileLoader;
//    private BufferedImage floorTile;
//    private BufferedImage wallTile;

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

    public PlayModeUI(PlayMode playMode) {
        this.playMode = playMode;
        this.addMouseListener(this);
        setBackground(new Color(43, 27, 44));

        try {
            hallBackground = ImageIO.read(getClass().getResource("/assets/test_hall.png"));
            playerImage = ImageIO.read(getClass().getResource("/assets/player.png"));
            runeImage = ImageIO.read(getClass().getResource("/assets/cloakreveallure.png"));

            //load pause resume images
            pauseImage = ImageIO.read(getClass().getResourceAsStream("/assets/pausebutton.png"));
            resumeImage = ImageIO.read(getClass().getResourceAsStream("/assets/resumebutton.png"));

            // Load monster images
            archerImage = ImageIO.read(getClass().getResource("/assets/archer.png"));
            fighterImage = ImageIO.read(getClass().getResource("/assets/fighter.png"));
            wizardImage = ImageIO.read(getClass().getResource("/assets/wizard.png"));

//            // Load enchantment images
//            extraTimeImage = ImageIO.read(getClass().getResource("/assets/extratime.png"));
//            revealImage = ImageIO.read(getClass().getResource("/assets/reveal.png"));
//            cloakImage = ImageIO.read(getClass().getResource("/assets/cloak.png"));
//            luringGemImage = ImageIO.read(getClass().getResource("/assets/luringgem.png"));
//            extraLifeImage = ImageIO.read(getClass().getResource("/assets/extralife.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPreferredSize(new Dimension(hallX + hallWidth + 200, hallY + hallHeight + 40));
    }

    private void drawHall(Graphics2D g, Hall hall) {

        if (hallBackground != null) {

            g.drawImage(hallBackground, 
                       hallX, hallY,
                       hallX + hallWidth,
                       hallY + hallHeight,
                       0, 0,
                       hallBackground.getWidth(),
                       hallBackground.getHeight(),
                       null);
        }

        g.setColor(new Color(255, 255, 255, 30));
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

        drawHall(g2d, hall);

        drawGameObjects(g2d, hall);

        drawUI(g2d, hero);

        BufferedImage currentImage = isPaused ? resumeImage : pauseImage;
        int x = getWidth() - BUTTON_SIZE - BUTTON_MARGIN;
        int y = BUTTON_MARGIN;
        g2d.drawImage(currentImage, x, y, BUTTON_SIZE, BUTTON_SIZE, null);
    }

    private void drawGameObjects(Graphics2D g, Hall hall) {
        // Draw Hero
        Hero hero = playMode.getHero();
        Position heroPos = hero.getPosition();
        int heroX = hallX + heroPos.getX() * cellWidth;
        int heroY = hallY + heroPos.getY() * cellHeight;
        if (playerImage != null) {
            g.drawImage(playerImage,
                       heroX + (cellWidth - playerImage.getWidth(null))/2, 
                       heroY + (cellHeight - playerImage.getHeight(null))/2, 
                       cellWidth, cellHeight, null);
        }

        

        // Draw Rune
        Rune rune = hall.getRune();
        if (rune != null) {
            Position runePos = rune.getPosition();
            int runeX = hallX + runePos.getX() * cellWidth;
            int runeY = hallY + runePos.getY() * cellHeight;
            if (runeImage != null) {
                g.drawImage(runeImage,
                           runeX + (cellWidth - runeImage.getWidth(null))/2, 
                           runeY + (cellHeight - runeImage.getHeight(null))/2, 
                           cellWidth, cellHeight, null);
            }
        }

        // Draw Monsters using the monster list
        for (Monster monster : hall.getMonsters()) {
            Position monsterPos = monster.getPosition();
            int monsterX = hallX + monsterPos.getX() * cellWidth;
            int monsterY = hallY + monsterPos.getY() * cellHeight;
            
            BufferedImage monsterImage = null;
            if (monster instanceof ArcherMonster && archerImage != null) {
                monsterImage = archerImage;
            } else if (monster instanceof FighterMonster && fighterImage != null) {
                monsterImage = fighterImage;
            } else if (monster instanceof WizardMonster && wizardImage != null) {
                monsterImage = wizardImage;
            }

            if (monsterImage != null) {
                g.drawImage(monsterImage,
                          monsterX + (cellWidth - monsterImage.getWidth(null))/2,
                          monsterY + (cellHeight - monsterImage.getHeight(null))/2,
                          cellWidth, cellHeight, null);
            }
        }

        // Draw Enchantments using the enchantment list
        for (Enchantment enchantment : hall.getEnchantments()) {
            Position enchantPos = enchantment.getPosition();
            int enchantX = hallX + enchantPos.getX() * cellWidth;
            int enchantY = hallY + enchantPos.getY() * cellHeight;
            
            BufferedImage enchantmentImage = null;
            if (enchantment instanceof ExtraTime && extraTimeImage != null) {
                enchantmentImage = extraTimeImage;
            } else if (enchantment instanceof Reveal && revealImage != null) {
                enchantmentImage = revealImage;
            } else if (enchantment instanceof CloakOfProtection && cloakImage != null) {
                enchantmentImage = cloakImage;
            } else if (enchantment instanceof LuringGem && luringGemImage != null) {
                enchantmentImage = luringGemImage;
            } else if (enchantment instanceof ExtraLife && extraLifeImage != null) {
                enchantmentImage = extraLifeImage;
            }

            if (enchantmentImage != null) {
                g.drawImage(enchantmentImage,
                          enchantX + (cellWidth - enchantmentImage.getWidth(null))/2,
                          enchantY + (cellHeight - enchantmentImage.getHeight(null))/2,
                          cellWidth, cellHeight, null);
            }
        }
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

    private void drawUI(Graphics2D g, Hero hero) {
        g.setColor(new Color(43, 27, 44));
        g.fillRect(uiX, uiTopY, 200, 400);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time: " + playMode.getRemainingTime() + " seconds", 
                    uiX + 10, uiTopY + 30);

        int heartSize = 20;
        int heartX = uiX + 10;
        int heartY = uiTopY + 50;
        for (int i = 0; i < hero.getLives(); i++) {
            g.setColor(Color.RED);
            g.fillOval(heartX + (i * (heartSize + 5)), heartY, heartSize, heartSize);
        }
    }
}