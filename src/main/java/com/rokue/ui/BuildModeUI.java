package com.rokue.ui;

import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.Hall;
import com.rokue.game.render.IRenderer;
import com.rokue.game.states.BuildMode;
import com.rokue.game.states.GameState;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.List;

public class BuildModeUI extends JPanel implements IRenderer {
    private BuildMode buildMode;
    private JProgressBar progressBar;

    // Grid constants
    private final int hallX = 20;
    private final int hallY = 20;
    private final int cellWidth = 32;
    private final int cellHeight = 32;
    private final int hallWidth = 640;
    private final int hallHeight = 640;

    // Banner panel constants
    private final int bannerWidth = 200;
    private final int bannerHeight = 400;
    private final int maxObjects = 50;

    public BuildModeUI(BuildMode buildMode) {
        this.buildMode = buildMode;

        setLayout(null);
        setBackground(new Color(43, 27, 44));

        // Add Banner for Objects
        JPanel bannerPanel = createBannerPanel();
        bannerPanel.setBounds(hallX + hallWidth + 20, hallY, bannerWidth, bannerHeight);
        add(bannerPanel);

        // Add Navigation Buttons
        JButton leftButton = createImageButton("src/main/resources/assets/left_arrow.png", e -> {
            buildMode.switchToPreviousHall();
            updateProgressBar();
        });
        leftButton.setBounds(hallX, hallY + hallHeight + 10, 50, 50);
        add(leftButton);

        JButton rightButton = createImageButton("src/main/resources/assets/right_arrow.png", e -> {
            buildMode.switchToNextHall();
            updateProgressBar();
        });
        rightButton.setBounds(hallX + hallWidth - 50, hallY + hallHeight + 10, 50, 50);
        add(rightButton);

        // Add Play Button
        JButton playButton = createImageButton("src/main/resources/assets/playbutton.png", e -> switchToPlayMode());
        playButton.setBounds(hallX + hallWidth / 2 - 25, hallY + hallHeight + 10, 50, 50);
        add(playButton);

        // Add Progress Bar
        progressBar = new JProgressBar(0, maxObjects);
        progressBar.setBounds(hallX, hallY - 20, hallWidth, 20);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.GREEN);
        progressBar.setBackground(new Color(30, 30, 30));
        add(progressBar);

        //Drag and Drop
        setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(dtde.getDropAction());
                    String objectName = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);

                    int[] size = getObjectSize(objectName);
                    DungeonObject object = new DungeonObject(objectName,
                            "src/main/resources/assets/" + objectName + ".png",
                            size[0], size[1]);

                    Point dropPoint = dtde.getLocation();
                    Position gridPosition = getGridPosition(dropPoint);

                    object.setPosition(gridPosition);

                    if (buildMode.addObjectToCurrentHall(object, gridPosition)) {
                        updateProgressBar();
                        repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setPreferredSize(new Dimension(hallX + hallWidth + bannerWidth + 40, hallY + hallHeight + 80));
        updateProgressBar();
    }

    private void updateProgressBar() {
        Hall currentHall = buildMode.getCurrentHall();
        int currentObjectCount = currentHall.getObjects().size();
        int minObjectRequirement = currentHall.getMinObjectRequirement();

        progressBar.setMaximum(minObjectRequirement);
        progressBar.setValue(Math.min(currentObjectCount, minObjectRequirement));
        progressBar.setString(currentObjectCount + " / " + minObjectRequirement + " Objects");

    }

    private JPanel createBannerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 5, 5)); // 4x2 grid with spacing
        panel.setBackground(new Color(30, 30, 30)); // Dark background

        String[] objects = {"pillar", "hole", "box", "crate", "torch", "skull", "chest", "potion"};
        for (String name : objects) {
            int[] size = getObjectSize(name);
            DungeonObject obj = new DungeonObject(name, "src/main/resources/assets/" + name + ".png", size[0], size[1]);
            panel.add(createDraggableLabel(obj));
        }

        return panel;
    }

    private JLabel createDraggableLabel(DungeonObject object) {
        JLabel label = new JLabel(object.getIcon());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        label.setTransferHandler(new TransferHandler("icon") {
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection(object.getName());
            }

            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY;
            }
        });

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent component = (JComponent) e.getSource();
                TransferHandler handler = component.getTransferHandler();
                handler.exportAsDrag(component, e, TransferHandler.COPY);
            }
        });

        return label;
    }

    private int[] getObjectSize(String name) {
        switch (name) {
            case "pillar":
            case "torch":
            case "crate":
                return new int[]{1, 2};
            default:
                return new int[]{1, 1};
        }
    }

    private JButton createImageButton(String imagePath, java.awt.event.ActionListener action) {
        ImageIcon icon = new ImageIcon(new File(imagePath).getAbsolutePath());
        JButton button = new JButton(icon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            action.actionPerformed(e);
            repaint();
        });
        return button;
    }

    private void switchToPlayMode() {
        if (!buildMode.areAllHallsSatisfied()) {
            JOptionPane.showMessageDialog(this, 
                "All halls must meet their minimum object requirements before starting the game.",
                "Cannot Start Game",
                JOptionPane.WARNING_MESSAGE);
        } else {
            buildMode.getEventManager().notify("SWITCH_TO_PLAY_MODE", buildMode.getHalls());
        }
    }

    private Position getGridPosition(Point point) {
        int x = (point.x - hallX) / cellWidth;
        int y = (point.y - hallY) / cellHeight;
        return new Position(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (buildMode == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Hall hall = buildMode.getCurrentHall();
        drawHall(g2d, hall);
    }

    private void drawHall(Graphics2D g, Hall hall) {
        // Draw grid
        for (int x = 0; x < hall.getWidth(); x++) {
            for (int y = 0; y < hall.getHeight(); y++) {
                g.drawRect(hallX + x * cellWidth, hallY + y * cellHeight, cellWidth, cellHeight);
            }
        }

        // Draw objects
        for (DungeonObject obj : hall.getObjects()) {
            Position pos = obj.getPosition();
            int x = hallX + pos.getX() * cellWidth;
            int y = hallY + pos.getY() * cellHeight;
            int width = obj.getWidthInCells() * cellWidth;
            int height = obj.getHeightInCells() * cellHeight;

            ImageIcon icon = (ImageIcon) obj.getIcon();
            g.drawImage(icon.getImage(), x, y, width, height, this);
        }

    }

    @Override
    public void render(GameState state) {
        if (state instanceof BuildMode) {
            this.buildMode = (BuildMode) state;
        }
        repaint();
    }
}
