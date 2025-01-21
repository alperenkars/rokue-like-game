package com.rokue.ui;

import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.Hall;
import com.rokue.game.render.IRenderer;
import com.rokue.game.states.BuildMode;
import com.rokue.game.states.GameState;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;
import com.rokue.ui.components.ImagePanel;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.HierarchyEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;

public class BuildModeUI extends ImagePanel implements IRenderer {
    private BuildMode buildMode;
    private JProgressBar progressBar;
    private SoundManager soundManager;
    private float alpha = 1.0f; // Opacity level (1.0f = fully opaque)
    private boolean isAnimating = false;
    private Timer fadeTimer;
    private final int ANIMATION_DURATION = 500; // Total animation time in milliseconds
    private final int TIMER_DELAY = 40; // Timer delay in milliseconds (~25 FPS)
    private boolean fadeOut = true; // Direction of fade
    private Runnable postFadeAction; // Action to perform after fade
    private Image wallTexture; // Load your wall texture image


    // Grid constants
    private final int hallX = 20;
    private final int hallY = 20;
    private final int cellWidth = 32;
    private final int cellHeight = 32;
    private final int hallWidth = 640;
    private final int hallHeight = 640;
    private final int wallThickness = 20; // Define wall thickness

    // Banner panel constants
    private final int bannerWidth = 200;
    private final int bannerHeight = 400;
    private final int maxObjects = 50;

    public BuildModeUI(BuildMode buildMode) {
         super("src/main/resources/assets/background.png");
        this.buildMode = buildMode;

        // Load the wall texture image
        try {
            wallTexture = ImageIO.read(getClass().getResource("/assets/surround_brick.png"));
        } catch (IOException e) {
            System.err.println("Error loading wall texture.");
            e.printStackTrace();
        }

        // Initialize SoundManager
        soundManager = new SoundManager();

        setLayout(null);
        setBackground(new Color(43, 27, 44));

        // Add Banner for Objects
        JPanel bannerPanel = createBannerPanel();
        bannerPanel.setBounds(hallX + hallWidth + 20, hallY, bannerWidth, bannerHeight);
        add(bannerPanel);

        // Add Fill Button
        JButton fillButton = createImageButton("src/main/resources/assets/fill_button.png", e -> {
            buildMode.randomlyFillCurrentHall();
            updateProgressBar();
        });
        fillButton.setBounds(hallX + hallWidth + 20, hallY + bannerHeight + 20, 190, 50);
        add(fillButton);

        // Add Clear Button
        JButton clearButton = createImageButton("src/main/resources/assets/clear_button.png", e -> {
            buildMode.clearCurrentHallObjects();
            updateProgressBar();
        });
        clearButton.setBounds(hallX + hallWidth + 20, hallY + bannerHeight + 80, 190, 50);
        add(clearButton);


        // Add Navigation Buttons
        JButton leftButton = createImageButton("src/main/resources/assets/left_arrow.png", e -> {
            startFadeAnimation(() -> {
                buildMode.switchToPreviousHall();
                updateProgressBar();
            });
        });
        leftButton.setBounds(hallX, hallY + hallHeight + 30, 50, 50);
        add(leftButton);

        JButton rightButton = createImageButton("src/main/resources/assets/right_arrow.png", e -> {
            startFadeAnimation(() -> {
                buildMode.switchToNextHall();
                updateProgressBar();
                soundManager.playNextHallSound();
            });
        });
        rightButton.setBounds(hallX + hallWidth - 50, hallY + hallHeight + 30, 50, 50);
        add(rightButton);

        // Add Play Button
        JButton playButton = createImageButton("src/main/resources/assets/playbutton.png", e -> switchToPlayMode());
        playButton.setBounds(hallX + hallWidth / 2 - 85, hallY + hallHeight + 10, 190, 90);
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
                        soundManager.playDragSound();
                        updateProgressBar();
                        repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Hall hall = buildMode.getCurrentHall();
        // Adjust panel size to accommodate walls
        int totalWidth = hallX + (hall.getWidth() * cellWidth) + bannerWidth + 40 + (2 * wallThickness);
        int totalHeight = hallY + (hall.getHeight() * cellHeight) + 100 + (2 * wallThickness);
        setPreferredSize(new Dimension(totalWidth, totalHeight));
        updateProgressBar();

       

        // Ensure resources are freed when the panel is no longer needed
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && !isDisplayable()) {
                soundManager.close();
            }
        });
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
        JPanel panel = new ImagePanel("src/main/resources/assets/banner.png");
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
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
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

    // Apply the current alpha transparency
        super.paintComponent(g);    
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
       
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        super.paintComponent(g2d);

        if (buildMode == null) return;

        
       
        Hall hall = buildMode.getCurrentHall();
        drawSurroundingWalls(g2d);
        drawHall(g2d, hall);

        g2d.dispose();
    }

    /* Draws the surrounding wall textures around the hall.
    */
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

    private void drawHall(Graphics2D g, Hall hall) {
        
        // Draw grid
        for (int x = 0; x < hall.getWidth(); x++) {
            for (int y = 0; y < hall.getHeight(); y++) {
               // g.drawRect(hallX + x * cellWidth, hallY + y * cellHeight, cellWidth, cellHeight);
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


private void startFadeAnimation(Runnable action) {
    if (isAnimating) return; // Prevent overlapping animations

    isAnimating = true;
    fadeOut = true; // Start with fade-out
    postFadeAction = action;

    fadeTimer = new Timer(TIMER_DELAY, null);
    int totalSteps = ANIMATION_DURATION / TIMER_DELAY;
    final int[] currentStep = {0};

    fadeTimer.addActionListener(e -> {
        if (fadeOut) {
            alpha -= 1.0f / totalSteps;
            if (alpha <= 0f) {
                alpha = 0f;
                fadeOut = false;
                fadeTimer.stop();
                if (postFadeAction != null) {
                    postFadeAction.run(); // Execute hall switch
                }
                // Start fade-in
                startFadeIn();
            }
        }
        repaint();
        currentStep[0]++;
    });

    fadeTimer.start();
}

private void startFadeIn() {
    fadeTimer = new Timer(TIMER_DELAY, null);
    int totalSteps = ANIMATION_DURATION / TIMER_DELAY;
    final int[] currentStep = {0};

    fadeTimer.addActionListener(e -> {
        alpha += 1.0f / totalSteps;
        if (alpha >= 1f) {
            alpha = 1f;
            fadeTimer.stop();
            isAnimating = false;
        }
        repaint();
        currentStep[0]++;
    });

    fadeTimer.start();
}

// SoundManager class as defined earlier
class SoundManager {
    private Clip dragClip;
    private Clip nextHallClip;


    public SoundManager() {
        dragClip = loadSound("/assets/drop.wav");
        nextHallClip= loadSound("/assets/next_hall.wav");
      

    }

    private Clip loadSound(String path) {
        try {
            URL soundURL = getClass().getResource(path);
            if (soundURL == null) {
                System.err.println("Sound file not found: " + path);
                return null;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void playDragSound() {
        if (dragClip != null) {
            dragClip.setFramePosition(0);
            dragClip.start();
        }
    }

    public void playNextHallSound() {
        if (nextHallClip != null) {
            nextHallClip.setFramePosition(0);
            nextHallClip.start();
        }
    }



    public void close() {
        if (dragClip != null) dragClip.close();
        if (nextHallClip != null) nextHallClip.close();

    }

} 
}
