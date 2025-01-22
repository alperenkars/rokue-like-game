package com.rokue.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.rokue.ui.components.ImagePanel;
import com.rokue.game.save.GameSaveManager;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.awt.AlphaComposite;
import javax.swing.Timer;

import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.enchantments.CloakOfProtection;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.enchantments.ExtraLife;
import com.rokue.game.entities.enchantments.ExtraTime;
import com.rokue.game.entities.enchantments.LuringGem;
import com.rokue.game.entities.enchantments.Reveal;
import com.rokue.game.entities.monsters.ArcherMonster;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.render.IRenderer;
import com.rokue.game.states.GameState;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

public class PlayModeUI extends ImagePanel implements IRenderer, MouseListener {
    private PlayMode playMode;
    private JFrame gameWindow;
    private Clip backgroundClip; // To manage the background music

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
    private BufferedImage saveImage;
    private BufferedImage returnToMainImage;
    private final int BUTTON_SIZE = 40;
    private final int BUTTON_MARGIN = 20;


    // UI Panel constants
    private final int uiPanelWidth = 200;
    private final int uiPanelHeight = 400;
    private final int inventoryStartY = 320; // Moved down from 220
    private final int inventoryItemSize = 32;
    private final int inventorySpacing = 5;
    private final int INVENTORY_GRID_SIZE = 3; // 3x2 grid
    private String infoMessage = ""; // For displaying game info
    private long infoMessageTime = 0;
    private static final long INFO_MESSAGE_DURATION = 5000; // 3 seconds

    private BufferedImage playerImage;
    private BufferedImage runeImage;
    private BufferedImage inventoryBgImage;
    private Image wallTexture; // Load your wall texture image

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
    private BufferedImage heartImage;
    private BufferedImage remainingTimeImage;

    private Position highlightStart = null;
    private Position highlightEnd = null;
    private final Color HIGHLIGHT_COLOR = new Color(255, 255, 0, 50); // Semi-transparent yellow
    private final int wallThickness = 10; // Define wall thickness

    private boolean isTransitioning = false;
    private float opacity = 1.0f;
    private Timer transitionTimer;  
    

    private boolean waitingForLuringDirection = false;


    
    public PlayModeUI(PlayMode playMode, JFrame gameWindow) {
        super("src/main/resources/assets/background.png");
        this.playMode = playMode;
        this.gameWindow = gameWindow;
        this.setPreferredSize(new Dimension(900, 800));
        this.setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        loadImages();

        try {
            wallTexture = ImageIO.read(getClass().getResource("/assets/surround_brick.png"));
        } catch (IOException e) {
            System.err.println("Error loading wall texture.");
            e.printStackTrace();
        }

        // Subscribe to highlight events
        playMode.getEventManager().subscribe("SHOW_HIGHLIGHT", (eventType, data) -> {
            if (data instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Position> highlightData = (Map<String, Position>) data;
                highlightStart = highlightData.get("start");
                highlightEnd = highlightData.get("end");
                repaint();
            }
        });

        // Inside the PlayModeUI constructor or an initialization method
        playMode.getEventManager().subscribe("SHOW_CONGRATS_SCREEN", (eventType, data) -> {
            showImprovedCongratulationsScreen();
});

        playMode.getEventManager().subscribe("HIDE_HIGHLIGHT", (eventType, data) -> {
            highlightStart = null;
            highlightEnd = null;
            repaint();
        });
        

        // Add key listener for enchantment shortcuts
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (!playMode.isPaused()) {
                    Hero hero = playMode.getHero();
                    char key = Character.toUpperCase(e.getKeyChar());
                    
                    if (waitingForLuringDirection) {
                        Position heroPos = hero.getPosition();
                        Position targetPos = null;
                        
                        switch (key) {
                            case 'W':
                                targetPos = new Position(heroPos.getX(), heroPos.getY() - 1);
                                break;
                            case 'S':
                                targetPos = new Position(heroPos.getX(), heroPos.getY() + 1);
                                break;
                            case 'A':
                                targetPos = new Position(heroPos.getX() - 1, heroPos.getY());
                                break;
                            case 'D':
                                targetPos = new Position(heroPos.getX() + 1, heroPos.getY());
                                break;
                            default:
                                return; // Ignore other keys when waiting for direction
                        }
                        
                        if (targetPos != null) {
                            hero.removeFromInventory("LURE");
                            playMode.getEventManager().notify("DISTRACTION", targetPos);
                            showInfoMessage(getEnchantmentMessage("LURE"));
                        }
                        waitingForLuringDirection = false;
                    } else {
                        switch (key) {
                            case 'R':
                                if (hero.hasItem("REVEAL")) {
                                    hero.useEnchantment("REVEAL");
                                    showInfoMessage(getEnchantmentMessage("REVEAL"));
                                }
                                break;
                            case 'P':
                                if (hero.hasItem("CLOAK")) {
                                    hero.useEnchantment("CLOAK");
                                    showInfoMessage(getEnchantmentMessage("CLOAK"));
                                }
                                break;
                            case 'B':
                                if (hero.hasItem("LURE")) {
                                    waitingForLuringDirection = true;
                                    showInfoMessage("Press WASD to choose the direction for the Luring Gem");
                                }
                                break;
                        }
                    }
                }
            }
            
        }
        
        );
        
        // Make panel focusable to receive keyboard events
        this.setFocusable(true);
        this.requestFocusInWindow();
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

        // Draw enchantments
        for (Enchantment enchantment : hall.getEnchantments()) {
            if (!enchantment.isCollected()) {
                Position enchantPos = enchantment.getPosition();
                BufferedImage enchantmentImage = null;

                if (enchantment instanceof CloakOfProtection) {
                    enchantmentImage = cloakImage;
                } else if (enchantment instanceof Reveal) {
                    enchantmentImage = revealImage;
                } else if (enchantment instanceof LuringGem) {
                    enchantmentImage = luringGemImage;
                } else if (enchantment instanceof ExtraLife) {
                    enchantmentImage = extraLifeImage;
                } else if (enchantment instanceof ExtraTime) {
                    enchantmentImage = extraTimeImage;
                }

                if (enchantmentImage != null) {
                    g.drawImage(enchantmentImage,
                               hallX + enchantPos.getX() * cellWidth,
                               hallY + enchantPos.getY() * cellHeight,
                               cellWidth, cellHeight, this);
                }
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

        // Draw highlight area if active
        if (highlightStart != null && highlightEnd != null) {
            g.setColor(HIGHLIGHT_COLOR);
            int x = hallX + highlightStart.getX() * cellWidth;
            int y = hallY + highlightStart.getY() * cellHeight;
            int width = (highlightEnd.getX() - highlightStart.getX() + 1) * cellWidth;
            int height = (highlightEnd.getY() - highlightStart.getY() + 1) * cellHeight;
            g.fillRect(x, y, width, height);
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
            // Check inventory clicks
            if (isInventoryClick(e.getX(), e.getY())) {
                handleInventoryClick(e.getX(), e.getY());
                return;
            }

            // Convert mouse coordinates to grid position
            int gridX = (e.getX() - hallX) / cellWidth;
            int gridY = (e.getY() - hallY) / cellHeight;
            Position clickPos = new Position(gridX, gridY);

            Hall currentHall = playMode.getCurrentHall();
            if (currentHall != null && currentHall.isWithinBounds(clickPos)) {
                Cell clickedCell = currentHall.getCell(clickPos);
                if (clickedCell != null) {
                    // Check if clicked on an enchantment
                    if (clickedCell.getContent() instanceof Enchantment) {
                        Hero hero = playMode.getHero();
                        hero.interactWithObject(clickedCell, currentHall);
                        repaint();
                        return;
                    }

                    // Check for rune under objects (existing logic)
                    DungeonObject clickedObject = currentHall.getObjectAt(clickPos);
                    if (clickedObject != null) {
                        Hero hero = playMode.getHero();
                        if (isAdjacentToHero(clickPos, hero.getPosition())) {
                            if (hero.checkForRune(hero.getPosition(), currentHall, clickedObject)) {
                                currentHall.removeObject(clickPos);
                                repaint();
                            }
                        }
                    }
                }
            }
        }
        checkPauseButtonClick(e.getX(), e.getY());
        if (isPaused) {
            checkSaveButtonClick(e.getX(), e.getY());
            checkReturnToMainButtonClick(e.getX(), e.getY());
        }
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

    private void checkSaveButtonClick(int mouseX, int mouseY) {
        int buttonX = getWidth() - BUTTON_SIZE - BUTTON_MARGIN;
        int buttonY = BUTTON_MARGIN + BUTTON_SIZE + BUTTON_MARGIN; // Position for save button

        if (mouseX >= buttonX && mouseX <= buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY <= buttonY + BUTTON_SIZE) {
            GameSaveManager saveManager = new GameSaveManager(playMode.getEventManager());
            saveManager.setCurrentPlayMode(playMode);  // Set the current PlayMode for WizardMonsters
            saveManager.saveGame(playMode);
            showInfoMessage("Game saved successfully!");
        }
    }

    private void checkReturnToMainButtonClick(int mouseX, int mouseY) {
        int buttonX = getWidth() - BUTTON_SIZE - BUTTON_MARGIN;
        int buttonY = BUTTON_MARGIN + (2 * BUTTON_SIZE) + (2 * BUTTON_MARGIN); // Position for return button

        if (mouseX >= buttonX && mouseX <= buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY <= buttonY + BUTTON_SIZE) {
            stopBackgroundMusic();
            playMode.getEventManager().notify("SHOW_MAIN_MENU", null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (playMode == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Hall hall = playMode.getCurrentHall();
        drawSurroundingWalls(g2d);
        drawHall(g2d, hall);
        drawUI(g2d, playMode.getHero());

        BufferedImage currentImage = isPaused ? resumeImage : pauseImage;
        int x = getWidth() - BUTTON_SIZE - BUTTON_MARGIN;
        int y = BUTTON_MARGIN;
        g2d.drawImage(currentImage, x, y, BUTTON_SIZE, BUTTON_SIZE, null);

        // Draw save button when paused
        if (isPaused) {
            if (saveImage != null) {
                int saveY = y + BUTTON_SIZE + BUTTON_MARGIN;
                g2d.drawImage(saveImage, x, saveY, BUTTON_SIZE, BUTTON_SIZE, null);
                
                // Draw return to main button below save button
                int returnY = saveY + BUTTON_SIZE + BUTTON_MARGIN;
                g2d.drawImage(returnToMainImage, x, returnY, BUTTON_SIZE, BUTTON_SIZE, null);
            }
        }
    }

    private void drawSurroundingWalls(Graphics2D g) {
        if (wallTexture == null) {
            // Fallback: Draw colored walls if texture isn't available
            g.setColor(new Color(139, 69, 19)); // Brown color for walls
        }
    
        // **Top Wall**
        if (wallTexture != null) {
            for (int x = hallX - wallThickness; x < hallX + hallWidth + wallThickness; x += wallThickness) {
                g.drawImage(wallTexture, x, hallY - wallThickness, wallThickness, wallThickness, this);
            }
        } else {
            g.fillRect(hallX - wallThickness, hallY - wallThickness,
                      hallWidth + (2 * wallThickness), wallThickness);
        }
    
        // **Bottom Wall**
        if (wallTexture != null) {
            for (int x = hallX - wallThickness; x < hallX + hallWidth + wallThickness; x += wallThickness) {
                g.drawImage(wallTexture, x, hallY + hallHeight, wallThickness, wallThickness, this);
            }
        } else {
            g.fillRect(hallX - wallThickness, hallY + hallHeight,
                      hallWidth + (2 * wallThickness), wallThickness);
        }
    
        // **Left Wall**
        if (wallTexture != null) {
            for (int y = hallY; y < hallY + hallHeight; y += wallThickness) {
                g.drawImage(wallTexture, hallX - wallThickness, y, wallThickness, wallThickness, this);
            }
        } else {
            g.fillRect(hallX - wallThickness, hallY,
                      wallThickness, hallHeight);
        }
    
        // **Right Wall**
        if (wallTexture != null) {
            for (int y = hallY; y < hallY + hallHeight; y += wallThickness) {
                g.drawImage(wallTexture, hallX + hallWidth, y, wallThickness, wallThickness, this);
            }
        } else {
            g.fillRect(hallX + hallWidth, hallY,
                      wallThickness, hallHeight);
        }
    }
    
 

    private void drawUI(Graphics2D g, Hero hero) {
        // Draw UI background
        g.setColor(new Color(43, 27, 44));
        g.fillRect(hallX + hallWidth + 20, 200, uiPanelWidth, uiPanelHeight);  // Moved from 180 to 200

        drawTime(g);
        // Draw lives using heart icon
        int heartSize = 40;
        int heartX = hallX + hallWidth + 30;
        int heartY = 280;  // Moved from 230 to 280
        for (int i = 0; i < hero.getLives(); i++) {
            if (heartImage != null) {
                g.drawImage(heartImage, 
                            heartX + (i * (heartSize + 5)), 
                            heartY, 
                            heartSize, 
                            heartSize, 
                            this);
            } else {
                // Fallback: Draw filled oval if heart image is not loaded
                g.setColor(Color.RED);
                g.fillOval(heartX + (i * (heartSize + 5)), heartY, heartSize, heartSize);
            }
        }

        // Draw inventory
        drawInventory(g, hero);
    }

    private void drawTime(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
    
        // Text and icon configuration
        String timeText = "Time: " + playMode.getRemainingTime() + " seconds";
        int iconSize = 50;
        int padding = -10;
    
        // Positioning
        int timeX = hallX + hallWidth + 30;
        int timeY = 250;  // Moved from 210 to 250
    
        // Load remaining time icon
        BufferedImage clockIcon = remainingTimeImage;
    
        // Custom Font
        Font timeFont = new Font("Verdana", Font.BOLD, 14);
        g2d.setFont(timeFont);
        FontMetrics fm = g2d.getFontMetrics();
    
        // Calculate text dimensions
        int textWidth = fm.stringWidth(timeText);
        int textHeight = fm.getHeight();
    
        // Background Rectangle dimensions
    /*     int bgWidth = iconSize + padding + textWidth + padding;
        int bgHeight = Math.max(iconSize, textHeight) + 60;
    
        // Draw semi-transparent background rectangle
        g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black
        g2d.fillRoundRect(timeX - 5, timeY - textHeight + 10, bgWidth, bgHeight, 15, 15);*/
    
        // Draw remaining time icon
        if (clockIcon != null) {
            int adjustedY = (timeY - iconSize + 5) + 15;
            g2d.drawImage(clockIcon, timeX, adjustedY, iconSize, iconSize, this);
        } else {
            System.err.println("Remaining time icon not loaded.");
        }
    
        // Text Shadow
        g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black for shadow
        g2d.drawString(timeText, timeX + iconSize + padding + 1, timeY + 1);
    
        // Main Text
        g2d.setColor(Color.WHITE);
        g2d.drawString(timeText, timeX + iconSize + padding, timeY);
    }

    private void drawInventory(Graphics2D g, Hero hero) {
        int inventoryX = hallX + hallWidth + 40;  // Changed from 30 to 40
        
        // Draw inventory background
        g.drawImage(inventoryBgImage, inventoryX - 10, inventoryStartY, uiPanelWidth - 20, 150, null);

        // Calculate grid positions
        int gridStartX = inventoryX + 28;  // Changed from 25 to 35
        int gridStartY = inventoryStartY + 40;
        List<String> inventory = hero.getInventory();
        
        // Draw items in a 3x2 grid
        for (int i = 0; i < Math.min(6, inventory.size()); i++) {
            int row = i / INVENTORY_GRID_SIZE;
            int col = i % INVENTORY_GRID_SIZE;
            int x = gridStartX + col * (inventoryItemSize + inventorySpacing);
            int y = gridStartY + row * (inventoryItemSize + inventorySpacing);
            
            String item = inventory.get(i);
            BufferedImage itemImage = null;
            switch (item) {
                case "CLOAK":
                    itemImage = cloakImage;
                    break;
                case "REVEAL":
                    itemImage = revealImage;
                    break;
                case "LURE":
                    itemImage = luringGemImage;
                    break;
            }
            
            if (itemImage != null) {
                g.drawImage(itemImage, x, y, inventoryItemSize, inventoryItemSize, null);
            }
        }

        // Draw info message if active
        if (System.currentTimeMillis() - infoMessageTime < INFO_MESSAGE_DURATION) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            drawWrappedText(g, infoMessage, inventoryX, inventoryStartY + 200, uiPanelWidth - 40);
        }
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = fm.getHeight();

        for (String word : words) {
            if (fm.stringWidth(line + word) < maxWidth) {
                line.append(word).append(" ");
            } else {
                g.drawString(line.toString(), x, y);
                y += lineHeight;
                line = new StringBuilder(word + " ");
            }
        }
        g.drawString(line.toString(), x, y);
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void loadImages() {
        try {
            playerImage = ImageIO.read(getClass().getResource("/assets/player.png"));
            runeImage = ImageIO.read(getClass().getResource("/assets/rune.png"));
            archerImage = ImageIO.read(getClass().getResource("/assets/archer.png"));
            fighterImage = ImageIO.read(getClass().getResource("/assets/fighter.png"));
            wizardImage = ImageIO.read(getClass().getResource("/assets/wizard.png"));
            pauseImage = ImageIO.read(getClass().getResource("/assets/pausebutton.png"));
            resumeImage = ImageIO.read(getClass().getResource("/assets/resumebutton.png"));
            saveImage = ImageIO.read(getClass().getResource("/assets/savebutton.png"));
            returnToMainImage = ImageIO.read(getClass().getResource("/assets/returnToMain.png"));
            inventoryBgImage = ImageIO.read(getClass().getResource("/assets/Inventory.png"));
    
            // Load enchantment images
            cloakImage = ImageIO.read(getClass().getClassLoader().getResource("assets/cloak.png"));
            revealImage = ImageIO.read(getClass().getClassLoader().getResource("assets/reveal.png"));
            luringGemImage = ImageIO.read(getClass().getClassLoader().getResource("assets/lure.png"));
            extraLifeImage = ImageIO.read(getClass().getClassLoader().getResource("assets/extra_life.png"));
            extraTimeImage = ImageIO.read(getClass().getClassLoader().getResource("assets/clock.png"));
            heartImage = ImageIO.read(getClass().getClassLoader().getResource("assets/heart.png")); // Load heart image
            remainingTimeImage = ImageIO.read(getClass().getClassLoader().getResource("assets/rclock.png")); // Load heart image
    
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

    private boolean isInventoryClick(int x, int y) {
        int inventoryX = hallX + hallWidth + 45; // Adjusted for grid start
        int inventoryY = inventoryStartY + 40;
        int gridWidth = INVENTORY_GRID_SIZE * (inventoryItemSize + inventorySpacing);
        int gridHeight = 2 * (inventoryItemSize + inventorySpacing);
        
        return x >= inventoryX && x <= inventoryX + gridWidth &&
               y >= inventoryY && y <= inventoryY + gridHeight;
    }

    private void handleInventoryClick(int x, int y) {
        // Inventory clicks are no longer used for enchantments
        // This method is kept for potential future inventory interactions
    }

    private String getEnchantmentMessage(String type) {
        switch (type) {
            case "CLOAK":
                return "Activated Cloak of Protection (P)! You are invisible to archers for 20 seconds.";
            case "REVEAL":
                return "Used Reveal enchantment (R)! The rune's location is temporarily revealed.";
            case "LURE":
                return "Luring Gem activated! Fighter monsters will move towards that direction.";
            default:
                return "";
        }
    }

    public void showInfoMessage(String message) {
        this.infoMessage = message;
        this.infoMessageTime = System.currentTimeMillis();
        repaint();
    }


      /**
     * A custom JPanel that supports fading by adjusting its alpha transparency.
     */
    private class FadingPanel extends JPanel {
        private float alpha = 0f;

        /**
         * Sets the alpha value for transparency.
         *
         * @param value The new alpha value (0.0f - 1.0f).
         */
        public void setAlpha(float value) {
            alpha = Math.min(1f, Math.max(0f, value));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            // Set the alpha composite for transparency
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2d);
            g2d.dispose();
        }
    }
    
 /**
     * Displays the improved Congratulations screen with enhanced visuals and a fade-in effect.
     */
    private void showImprovedCongratulationsScreen() {
        SwingUtilities.invokeLater(() -> {
            // Create a new JFrame for the end-game screen
            JFrame endFrame = new JFrame("Game Complete");
            endFrame.setSize(800, 600);
            endFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            endFrame.setResizable(false);

            // Create a panel with a background image
            JPanel panel = new JPanel() {
                BufferedImage bgImage;

                {
                    try {
                        // Replace "/assets/final_bg.png" with the actual path to your background image in the resources
                        bgImage = ImageIO.read(getClass().getResource("/assets/final_bg.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (bgImage != null) {
                        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };
            panel.setLayout(new BorderLayout());

            // Create a FadingPanel for the overlay content
            FadingPanel overlayPanel = new FadingPanel();
            overlayPanel.setOpaque(false);
            overlayPanel.setLayout(new BorderLayout());

             // Congratulations Label at the Top
             JLabel congratsLabel = new JLabel("🎉 Congratulations! You Completed the Game! 🎉", SwingConstants.CENTER);
             congratsLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
             congratsLabel.setForeground(Color.BLACK); // Black text for visibility against white background
             congratsLabel.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10)); // Top padding
             overlayPanel.add(congratsLabel, BorderLayout.NORTH);
 
             // Button Panel at the Bottom
             JPanel buttonPanel = new JPanel();
             buttonPanel.setOpaque(false);
             buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50)); // Centered with padding
 
             JButton backButton = new JButton("Go Back to Main Screen");
             styleButton(backButton);
 
             backButton.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     // Stop the background music
                     stopBackgroundMusic();

                     // Close the end-game frame
                     endFrame.dispose();
 
                     // Notify GameSystem to show the main menu
                     playMode.getEventManager().notify("SHOW_MAIN_MENU", null);
                 }
             });
 
             buttonPanel.add(backButton);
             overlayPanel.add(buttonPanel, BorderLayout.SOUTH);
 
             // Add the overlay panel to the main panel
             panel.add(overlayPanel, BorderLayout.CENTER);
 
             endFrame.add(panel);
             endFrame.setLocationRelativeTo(null);
             endFrame.setVisible(true);
 
             // Start the fade-in effect using a Swing Timer
             Timer timer = new Timer(50, null); // 50ms delay
             timer.addActionListener(new ActionListener() {
                 float alpha = 0f;

                 @Override
                 public void actionPerformed(ActionEvent e) {
                     alpha += 0.05f; // Increment alpha
                     if (alpha >= 1f) {
                         alpha = 1f;
                         timer.stop();
                     }
                     overlayPanel.setAlpha(alpha);
                 }
             });
             timer.start();

             // Start playing background music
             playBackgroundMusic("/assets/end_music.wav");
         });
     }
 

    /**
     * Loads and plays the background music in a loop.
     *
     * @param audioPath The path to the audio file in the resources.
     */
    private void playBackgroundMusic(String audioPath) {
        try {
            // Obtain the audio input stream from the resource
            URL soundURL = getClass().getResource(audioPath);
            if (soundURL == null) {
                System.err.println("Audio file not found: " + audioPath);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioIn);
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops and closes the background music clip.
     */
    private void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }

      /**
     * Styles the JButton to have a black background, white text, rounded corners, and hover effects.
     *
     * @param button The JButton to style.
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(Color.BLACK); // Changed to Black text
        button.setBackground(Color.LIGHT_GRAY); // Changed background to Light Gray for contrast
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.LIGHT_GRAY);
            }
        });
    }

 

}