package com.rokue.ui;

import com.rokue.game.render.IRenderer;
import com.rokue.game.states.GameState;
import com.rokue.game.states.PlayMode;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.util.Cell;
import com.rokue.game.util.Position;

import javax.swing.*;
import java.awt.*;

public class PlayModeUI extends JPanel implements IRenderer {
    private PlayMode playMode;

    public PlayModeUI(PlayMode playMode) {
        this.playMode = playMode;
        setBackground(Color.BLACK);
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    @Override
    public void render(GameState state) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (playMode == null) return;

        Hall hall = playMode.getCurrentHall();
        Hero hero = playMode.getHero();
        int remainingTime = playMode.getRemainingTime();

        int width = hall.getWidth();
        int height = hall.getHeight();

        int cellWidth = getWidth() / width;
        int cellHeight = getHeight() / height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = hall.getCell(new Position(x, y));
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);

                if (cell.getContent() instanceof Rune) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(
                            x * cellWidth + cellWidth / 4,
                            y * cellHeight + cellHeight / 4,
                            cellWidth / 2,
                            cellHeight / 2
                    );
                }
            }
        }

        Position heroPos = hero.getPosition();
        g.setColor(Color.BLUE);
        g.fillOval(
                heroPos.getX() * cellWidth + cellWidth / 4,
                heroPos.getY() * cellHeight + cellHeight / 4,
                cellWidth / 2,
                cellHeight / 2
        );

        g.setColor(Color.WHITE);
        g.drawString("Hall: " + hall.getName(), 10, 20);
        g.drawString("Time: " + remainingTime + "s", 10, 40);
        g.drawString("Lives: " + hero.getLives(), 10, 60);
    }
}