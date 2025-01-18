package com.rokue.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.rokue.game.states.PlayMode;
import com.rokue.game.events.EventListener;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.ArcherMonster;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.enchantments.CloakOfProtection;
import com.rokue.game.entities.enchantments.Reveal;
import com.rokue.game.entities.enchantments.LuringGem;
import com.rokue.game.entities.enchantments.ExtraLife;
import com.rokue.game.entities.enchantments.ExtraTime;

public class PlayModeUI extends JPanel {
    private PlayMode playMode;
    private EventManager eventManager;
    private BufferedImage inventoryBackground;
    private String infoMessage = "";
    private long infoMessageTime = 0;
    private static final long INFO_MESSAGE_DURATION = 3000; // 3 seconds
    
    // Hall rendering constants
    private final int hallX = 20;
    private final int hallY = 20;
    private final int cellWidth = 32;
    private final int cellHeight = 32;
    private final int hallWidth = 640;
    private final int hallHeight = 640;
    private final Color BACKGROUND_COLOR = new Color(43, 27, 44);
    
    // Game images
    private BufferedImage playerImage;
    private BufferedImage runeImage;
    private BufferedImage archerImage;
    private BufferedImage fighterImage;
    private BufferedImage wizardImage;
    private BufferedImage cloakImage;
    private BufferedImage revealImage;
    private BufferedImage luringGemImage;
    private BufferedImage extraLifeImage;
    private BufferedImage extraTimeImage;
    
    // Highlight region variables
    private Position highlightStart = null;
    private Position highlightEnd = null;
    private boolean showHighlight = false;

    public PlayModeUI(PlayMode playMode, EventManager eventManager) {
        this.playMode = playMode;
        this.eventManager = eventManager;
        
        // Subscribe to info message events
        eventManager.subscribe("DISPLAY_INFO", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                if (data instanceof String) {
                    infoMessage = (String) data;
                    infoMessageTime = System.currentTimeMillis();
                }
            }
        });

        // Subscribe to highlight events
        eventManager.subscribe("SHOW_HIGHLIGHT", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                if (data instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Position> highlightData = (Map<String, Position>) data;
                    highlightStart = highlightData.get("start");
                    highlightEnd = highlightData.get("end");
                    showHighlight = true;
                    repaint();
                }
            }
        });

        eventManager.subscribe("HIDE_HIGHLIGHT", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                showHighlight = false;
                repaint();
            }
        });

        try {
            // Load inventory background image
            inventoryBackground = ImageIO.read(getClass().getResourceAsStream("/assets/Inventory.png"));
        } catch (IOException e) {
            System.err.println("Error loading inventory background: " + e.getMessage());
        }
    }

    private void drawInventory(Graphics2D g) {
        // Draw inventory background
        if (inventoryBackground != null) {
            g.drawImage(inventoryBackground, 600, 220, 200, 300, null);
        }
        
        // Draw inventory label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Inventory", 650, 240);

        // Draw info message if active
        if (System.currentTimeMillis() - infoMessageTime < INFO_MESSAGE_DURATION) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(600, 160, 200, 50, 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            drawWrappedText(g, infoMessage, 610, 185, 180);
        }
        
        // Draw inventory grid (3x2)
        if (playMode.getHero() != null) {
            List<String> inventory = playMode.getHero().getInventory();
            int gridX = 635; // Adjusted to align with inventory box spots
            int gridY = 260;
            int itemSize = 50;
            int spacing = 10;
            
            for (int i = 0; i < Math.min(6, inventory.size()); i++) {
                int row = i / 3;
                int col = i % 3;
                int x = gridX + col * (itemSize + spacing);
                int y = gridY + row * (itemSize + spacing);
                
                // Draw item slot background
                g.setColor(new Color(0, 0, 0, 128));
                g.fillRect(x, y, itemSize, itemSize);
                
                // Draw item icon (placeholder for now)
                g.setColor(Color.WHITE);
                g.drawRect(x, y, itemSize, itemSize);
                
                // Draw item name
                String itemName = inventory.get(i);
                FontMetrics fm = g.getFontMetrics();
                int textX = x + (itemSize - fm.stringWidth(itemName)) / 2;
                g.drawString(itemName, textX, y + itemSize + 15);
            }
        }
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = y;

        for (String word : words) {
            if (fm.stringWidth(line + " " + word) < maxWidth) {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            } else {
                g.drawString(line.toString(), x, lineY);
                line = new StringBuilder(word);
                lineY += fm.getHeight();
            }
        }
        if (line.length() > 0) {
            g.drawString(line.toString(), x, lineY);
        }
    }

    public void drawUI(Graphics2D g) {
        // Draw inventory background and items
        drawInventory(g);
        
        // Draw time and lives
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Time: " + playMode.getRemainingTime(), 10, 30);
        g.drawString("Lives: " + playMode.getHero().getLives(), 10, 60);
    }

    private void drawHall(Graphics2D g, Hall hall) {
        // Draw hall background
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(hallX, hallY, hallWidth, hallHeight);

        // Draw grid lines
        g.setColor(Color.GRAY);
        for (int i = 0; i <= hall.getWidth(); i++) {
            g.drawLine(hallX + i * cellWidth, hallY,
                      hallX + i * cellWidth, hallY + hall.getHeight() * cellHeight);
        }
        for (int i = 0; i <= hall.getHeight(); i++) {
            g.drawLine(hallX, hallY + i * cellHeight,
                      hallX + hall.getWidth() * cellWidth, hallY + i * cellHeight);
        }

        // Draw highlight region if active
        if (showHighlight && highlightStart != null && highlightEnd != null) {
            g.setColor(new Color(255, 215, 0, 100)); // Semi-transparent gold
            g.setStroke(new BasicStroke(2.0f)); // Thicker border
            
            int x = hallX + highlightStart.getX() * cellWidth;
            int y = hallY + highlightStart.getY() * cellHeight;
            int width = (highlightEnd.getX() - highlightStart.getX() + 1) * cellWidth;
            int height = (highlightEnd.getY() - highlightStart.getY() + 1) * cellHeight;
            
            // Draw semi-transparent fill
            g.fillRect(x, y, width, height);
            
            // Draw border
            g.setColor(new Color(255, 215, 0)); // Solid gold
            g.drawRect(x, y, width, height);
        }

        // Draw dungeon objects
        for (DungeonObject obj : hall.getObjects()) {
            Position objPos = obj.getPosition();
            g.setColor(Color.GRAY);
            g.fillRect(hallX + objPos.getX() * cellWidth + 2,
                      hallY + objPos.getY() * cellHeight + 2,
                      cellWidth - 4, cellHeight - 4);
        }

        // Draw rune if revealed
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
    }
} 