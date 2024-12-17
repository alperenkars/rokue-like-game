package com.rokue.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.rokue.game.events.EventManager;
import com.rokue.game.states.MainMenu;

public class MainMenuUI extends JPanel {
    private MainMenu mainMenu;

    public MainMenuUI(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        
        setBackground(new Color(43, 27, 44));  // Dark purple background
        
        // Add mouse listener to the panel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainMenu.getEventManager().notify("START_GAME", null);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Add some text to indicate clickable area
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String message = "Click anywhere to start";
        
        // Center the text
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        
        g2d.drawString(message, x, y);
    }
}