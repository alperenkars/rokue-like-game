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
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
    private final int inventoryStartY = 220; // Moved further down
    private final int inventoryItemSize = 32;
    private final int inventorySpacing = 5;
    private final int INVENTORY_GRID_SIZE = 3; // 3x2 grid
    private String infoMessage = ""; // For displaying game info
    private long infoMessageTime = 0;
    private static final long INFO_MESSAGE_DURATION = 5000; // 3 seconds

    private BufferedImage playerImage;
    private BufferedImage runeImage;
    private BufferedImage inventoryBgImage;

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

    private Position highlightStart = null;
    private Position highlightEnd = null;
    private final Color HIGHLIGHT_COLOR = new Color(255, 255, 0, 50); // Semi-transparent yellow

    public PlayModeUI(PlayMode playMode, JFrame gameWindow) {
        this.playMode = playMode;
        this.gameWindow = gameWindow;
        this.setPreferredSize(new Dimension(900, 800));
        this.setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        loadImages();

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

        playMode.getEventManager().subscribe("HIDE_HIGHLIGHT", (eventType, data) -> {
            highlightStart = null;
            highlightEnd = null;
            repaint();
        });
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
                        hero.interactWithRune(clickedCell, currentHall);
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
        // Draw UI background
        g.setColor(new Color(43, 27, 44));
        g.fillRect(hallX + hallWidth + 20, 100, uiPanelWidth, uiPanelHeight);

        // Draw time
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time: " + playMode.getRemainingTime() + " seconds", 
                    hallX + hallWidth + 30, 130);

        // Draw lives
        int heartSize = 20;
        int heartX = hallX + hallWidth + 30;
        int heartY = 150;
        for (int i = 0; i < hero.getLives(); i++) {
            g.setColor(Color.RED);
            g.fillOval(heartX + (i * (heartSize + 5)), heartY, heartSize, heartSize);
        }

        // Draw inventory
        drawInventory(g, hero);
    }

    private void drawInventory(Graphics2D g, Hero hero) {
        int inventoryX = hallX + hallWidth + 30;
        
        // Draw inventory background
        g.drawImage(inventoryBgImage, inventoryX - 10, inventoryStartY, uiPanelWidth - 20, 150, null);

        // Calculate grid positions
        int gridStartX = inventoryX + 25;
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
            inventoryBgImage = ImageIO.read(getClass().getResource("/assets/Inventory.png"));

            // Load enchantment images
            cloakImage = ImageIO.read(getClass().getResource("/assets/cloak.png"));
            revealImage = ImageIO.read(getClass().getResource("/assets/reveal.png"));
            luringGemImage = ImageIO.read(getClass().getResource("/assets/lure.png"));
            extraLifeImage = ImageIO.read(getClass().getResource("/assets/extra_life.png"));
            extraTimeImage = ImageIO.read(getClass().getResource("/assets/clock.png"));

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
        Hero hero = playMode.getHero();
        if (hero == null) return;

        int gridStartX = hallX + hallWidth + 45;
        int gridStartY = inventoryStartY + 40;
        
        int col = (x - gridStartX) / (inventoryItemSize + inventorySpacing);
        int row = (y - gridStartY) / (inventoryItemSize + inventorySpacing);
        
        if (col >= 0 && col < INVENTORY_GRID_SIZE && row >= 0 && row < 2) {
            int index = row * INVENTORY_GRID_SIZE + col;
            List<String> inventory = hero.getInventory();
            if (index >= 0 && index < inventory.size()) {
                String item = inventory.get(index);
                hero.useEnchantment(item);
                showInfoMessage(getEnchantmentMessage(item));
                repaint();
            }
        }
    }

    private String getEnchantmentMessage(String type) {
        switch (type) {
            case "CLOAK":
                return "Activated Cloak of Protection! You are invisible to archers for 20 seconds.";
            case "REVEAL":
                return "Used Reveal enchantment! The rune's location is temporarily revealed.";
            case "LURE":
                return "Activated Luring Gem! Fighter monsters are now distracted.";
            default:
                return "";
        }
    }

    public void showInfoMessage(String message) {
        this.infoMessage = message;
        this.infoMessageTime = System.currentTimeMillis();
        repaint();
    }
}