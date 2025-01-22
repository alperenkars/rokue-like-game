package com.rokue.ui;

import com.rokue.game.render.IRenderer;
import com.rokue.game.states.GameState;
import com.rokue.game.states.MainMenu;
import com.rokue.ui.components.ImageBorder;
import com.rokue.game.save.GameSaveManager;
import com.rokue.game.save.GameSaveData;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.net.URL;
import java.util.List;

public class MainMenuUI extends JPanel implements IRenderer {
    private AnimatedBackgroundPanel menuPanel; // Store as a field
    public MainMenu mainMenu;

    public MainMenuUI(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        setLayout(new BorderLayout());

        // first steps to create the main menu panel
        menuPanel = new AnimatedBackgroundPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        
        // load the image
        try {
            ImageIcon originalIcon = new ImageIcon(new File("src/main/resources/assets/logo.png").getAbsolutePath());
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);
            JLabel imageLabel = new JLabel(resizedIcon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // padding ajustment
            menuPanel.add(Box.createVerticalStrut(20));
            menuPanel.add(imageLabel);
            menuPanel.add(Box.createVerticalStrut(50));

            // images for buttons
            ImageIcon playIcon = new ImageIcon(new File("src/main/resources/assets/playbutton.png").getAbsolutePath());
            ImageIcon helpIcon = new ImageIcon(new File("src/main/resources/assets/helpbutton.png").getAbsolutePath());
            ImageIcon exitIcon = new ImageIcon(new File("src/main/resources/assets/quitbutton.png").getAbsolutePath());
            ImageIcon loadIcon = new ImageIcon(new File("src/main/resources/assets/loadbutton.png").getAbsolutePath());

            // create and setup buttons
            JButton playButton = createStyledButton(playIcon);
            JButton loadButton = createStyledButton(loadIcon);
            JButton helpButton = createStyledButton(helpIcon);
            JButton exitButton = createStyledButton(exitIcon);

            playButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    animateButtonClick(playButton, playIcon);
                    mainMenu.getEventManager().notify("START_GAME", null);
                }
            });

            loadButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    animateButtonClick(loadButton, loadIcon);
                    showLoadGameDialog();
                }
            });

            helpButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    animateButtonClick(helpButton, helpIcon);
                    openHelpScreen();
                }
            });

            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    animateButtonClick(exitButton, exitIcon);
                    System.exit(0);
                }
            });

            menuPanel.add(playButton);
            menuPanel.add(Box.createVerticalStrut(20));
            menuPanel.add(loadButton);
            menuPanel.add(Box.createVerticalStrut(20));
            menuPanel.add(helpButton);
            menuPanel.add(Box.createVerticalStrut(20));
            menuPanel.add(exitButton);

        } catch (Exception e) {
            e.printStackTrace();
        }

        add(menuPanel, BorderLayout.CENTER);
    }

    // getter for the animated panel
    public AnimatedBackgroundPanel getAnimatedPanel() {
        return menuPanel;
    }

    public class AnimatedBackgroundPanel extends JPanel {
        private Image backgroundImage;
        private Image animatedImage;
        private int x = 100, y = 100;
        private double xDirection = 1, yDirection = 1;
        private double speed = 2;
        private double rotationAngle = 0;
        private double rotationSpeed = 0.1;
        private ArrayList<Point> trail;
        private final int TRAIL_LENGTH = 20;

        public AnimatedBackgroundPanel() {
            try {
                backgroundImage = ImageIO.read(new File("src/main/resources/assets/background.jpg"));
                animatedImage = ImageIO.read(new File("src/main/resources/assets/animatedmomo.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            trail = new ArrayList<>();
            Timer timer = new Timer(30, e -> {
                updatePosition();
                repaint();
            });
            timer.start();
        }
        public int getTrailSize() {
            return trail.size();
        }
        
        public int getMaxTrailLength() {
            return TRAIL_LENGTH;
        }

        public Point getCurrentPosition() {
            return new Point(x, y);
        }

        public void setCurrentX(int newX) {
            this.x = newX;
        }

        /**
         * updates the position and rotation of the animated image and handles its trail.
         * it should bounce from boundaries and trail tail must follow momo
         * @requires
         * - animatedImage != null
         * - trail != null
         * - TRAIL_LENGTH > 0
         * - speed != 0
         * - getWidth() and getHeight() return valid numbers
         * 
         * @modifies
         * - x, y coordinates
         * - rotationAngle
         * - trail list
         * - xDirection, yDirection
         * 
         * @effects
         * - updates x,y position based on direction and speed
         * - updates rotation angle
         * - adds new position to trail
         * - maintains trail length
         * - reverses direction when hitting boundaries
         */
        public void updatePosition() {
            x += xDirection * speed;
            y += yDirection * speed;
            rotationAngle += rotationSpeed;

            // Add current position to trail
            trail.add(new Point(x, y));
            if (trail.size() > TRAIL_LENGTH) {
                trail.remove(0);
            }

            // Check and correct x boundary
            if (x < 0) {
                x = 0;
                xDirection *= -1;
            } else if (x + animatedImage.getWidth(this) > getWidth()) {
                x = getWidth() - animatedImage.getWidth(this);
                xDirection *= -1;
            }

            // Check and correct y boundary
            if (y < 0) {
                y = 0;
                yDirection *= -1;
            } else if (y + animatedImage.getHeight(this) > getHeight()) {
                y = getHeight() - animatedImage.getHeight(this);
                yDirection *= -1;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Draw background
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }

            // trail dots
            for (int i = 0; i < trail.size(); i++) {
                Point p = trail.get(i);
                float alpha = (float) i / trail.size();
                g2d.setColor(new Color(1f, 1f, 1f, alpha));
                g2d.fillOval(p.x + animatedImage.getWidth(this)/2,
                        p.y + animatedImage.getHeight(this)/2,
                        10, 10);
            }

            // main image
            if (animatedImage != null) {
                AffineTransform oldTransform = g2d.getTransform();
                int centerX = x + animatedImage.getWidth(this) / 2;
                int centerY = y + animatedImage.getHeight(this) / 2;
                g2d.rotate(rotationAngle, centerX, centerY);
                g2d.drawImage(animatedImage, x, y, this);
                g2d.setTransform(oldTransform);
            }
        }
    }

    private JButton createStyledButton(ImageIcon icon) {
        JButton button = new JButton(icon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void openHelpScreen() {
        // parent window of this component
        Window parentWindow = SwingUtilities.getWindowAncestor(this);

        // new frame for the help screen with animated background
        JFrame helpFrame = new JFrame("Help");
        // Set the help frame size to 872x662 to match the desired parent size
        helpFrame.setSize(872, 662);
        helpFrame.setLocationRelativeTo(parentWindow);  // Center relative to parent
        helpFrame.setLayout(new BorderLayout());

        // panel for the help with animated background
        JPanel helpPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon("/assets/background.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        helpPanel.setBackground(new Color(245, 245, 220));
        helpPanel.setLayout(new GridBagLayout());

        
        JPanel textBackgroundPanel = new JPanel();
        textBackgroundPanel.setBackground(new Color(245, 245, 220));
        textBackgroundPanel.setLayout(new BorderLayout());

        //wooden texture border 
        BufferedImage woodenTexture = null;
        try {
            woodenTexture = ImageIO.read(new File("src/main/resources/assets/wooden_texture.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (woodenTexture != null) {
            Border woodenBorder = new ImageBorder(woodenTexture, 30); // Slightly smaller border size
            Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
            Border compoundBorder = new CompoundBorder(woodenBorder, emptyBorder);
            textBackgroundPanel.setBorder(compoundBorder);
        }

       
        JLabel helpLabel = new JLabel();
        helpLabel.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Slightly smaller font to fit better
        helpLabel.setForeground(new Color(139, 69, 19));
        helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            helpLabel.setText(
                "<html>"
                        + "<head>"
                        + "<style>"
                        + "body { "
                        + "    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; "
                        + "    color: #4E342E; "
                        + "    padding: 10px; "
                        + "    background-color: #FFF3E0; "
                        + "}"
                        + "h1 { "
                        + "    text-align: center; "
                        + "    color: #6D4C41; "
                        + "    font-size: 20px; "
                        + "    margin-bottom: 10px; "
                        + "    text-shadow: 1px 1px 2px #BCAAA4;"
                        + "}"
                        + "h2 { "
                        + "    color: #8D6E63; "
                        + "    border-bottom: 2px solid #D7CCC8; "
                        + "    padding-bottom: 3px; "
                        + "    font-size: 16px; "
                        + "    margin-top: 15px; "
                        + "}"
                        + "p { "
                        + "    line-height: 1.3; "
                        + "    font-size: 12px; "
                        + "    margin-bottom: 10px; "
                        + "}"
                        + ".monster, .enchantment { "
                        + "    display: flex; "
                        + "    align-items: center; "
                        + "    margin-bottom: 10px; "
                        + "    background-color: #FFECB3; "
                        + "    padding: 6px; "
                        + "    border-radius: 6px; "
                        + "    box-shadow: 1px 1px 3px rgba(0,0,0,0.2); "
                        + "}"
                        + ".monster img, .enchantment img { "
                        + "    margin-left: 8px; "
                        + "    border-radius: 5px; "
                        + "    box-shadow: 1px 1px 3px rgba(0,0,0,0.3); "
                        + "    width: 32px; "
                        + "    height: 32px; "
                        + "}"
                        + ".monster img:hover, .enchantment img:hover { "
                        + "    transform: scale(1.2); "
                        + "    box-shadow: 4px 4px 10px rgba(0,0,0,0.5); "
                        + "}"
                        + ".section { "
                        + "    background: linear-gradient(135deg, #FFE0B2, #FFCC80); "
                        + "    padding: 20px; "
                        + "    border-radius: 15px; "
                        + "    box-shadow: 4px 4px 12px rgba(0,0,0,0.25); "
                        + "    margin-bottom: 30px; "
                        + "}"
                        + "strong { "
                        + "    color: #D84315; "
                        + "    font-weight: bold; "
                        + "}"
                        + ".highlight { "
                        + "    background-color: #FFF9C4; "
                        + "    padding: 3px 6px; "
                        + "    border-radius: 3px; "
                        + "}"
                        + "ul { "
                        + "    list-style-type: disc; "
                        + "    margin-left: 20px; "
                        + "    margin-bottom: 10px; "
                        + "}"
                        + "li { "
                        + "    margin-bottom: 5px; "
                        + "}"
                        + "</style>"
                        + "</head>"
                        + "<body>"
                        + "<h1>Help Section</h1>"
                        + "<p>Welcome to RoKUe-Like, a rogue-like adventure where you explore, survive, and strategize."
                        + "<br>This section explains the game controls, mechanics, and features to help you succeed.</p>"

                        // Basic Controls Section
                        + "<div class='section'>"
                        + "  <h2>Basic Controls</h2>"
                        + "  <p>"
                        + "    <strong>Arrow Keys:</strong> Move your hero <span class='highlight'>(North, South, East, West)</span>.<br>"
                        + "    <strong>Mouse Left-Click:</strong> Interact with objects and collect enchantments.<br>"
                        + "    <strong>Keyboard Keys:</strong>"
                        + "    <ul>"
                        + "      <li><strong>R:</strong> Use Reveal Enchantment.</li>"
                        + "      <li><strong>P:</strong> Activate Cloak of Protection.</li>"
                        + "      <li><strong>B:</strong> Throw Luring Gem.</li>"
                        + "      <li><strong>A:</strong> Move Left</li>"
                        + "      <li><strong>D:</strong> Move Right</li>"
                        + "      <li><strong>W:</strong> Move Up</li>"
                        + "      <li><strong>S:</strong> Move Down</li>"
                        + "    </ul>"
                        + "    <strong>Pause/Resume:</strong> Use the Pause button to pause and resume.<br>"
                        + "    <strong>Exit:</strong> Use the Exit button to return to the main menu."
                        + "  </p>"
                        + "</div>"

                        // Enchantments Section
                        + "<div class='section'>"
                        + "  <h2>Enchantments</h2>"
                        + "  <div class='enchantment'>"
                        + "    <strong>Reveal Enchantment:</strong> Temporarily reveals hidden areas and traps."
                        + "    <img src='file:" + new File("src/main/resources/assets/reveal.png").getAbsolutePath() + "' alt='Reveal Enchantment'/>"
                        + "  </div>"
                        + "  <div class='enchantment'>"
                        + "    <strong>Cloak of Protection:</strong> Grants temporary invisibility."
                        + "    <img src='" + getClass().getResource("/assets/cloak.png") + "' alt='Cloak of Protection'/>"
                        + "  </div>"
                        + "  <div class='enchantment'>"
                        + "    <strong>Luring Gem:</strong> Distracts Fighter Monsters."
                        + "    <img src='" + getClass().getResource("/assets/lure.png") + "' alt='Luring Gem'/>"
                        + "  </div>"
                        + "</div>"

                        // monsters Section
                        + "<div class='section'>"
                        + "  <h2>Monsters</h2>"
                        + "  <div class='monster'>"
                        + "    <strong>Archer:</strong> Shoots arrows at heroes within 4 squares. Use the Cloak of Protection."
                        + "    <img src='" + getClass().getResource("/assets/archer.png") + "' alt='Archer'/>"
                        + "  </div>"
                        + "  <div class='monster'>"
                        + "    <strong>Fighter:</strong> Moves randomly, attracted to Luring Gems."
                        + "    <img src='" + getClass().getResource("/assets/fighter.png") + "' alt='Fighter'/>"
                        + "  </div>"
                        + "  <div class='monster'>"
                        + "    <strong>Wizard:</strong> Teleports the rune every 5 seconds."
                        + "    <img src='" + getClass().getResource("/assets/wizard.png") + "' alt='Wizard'/>"
                        + "  </div>"
                        + "</div>"

                        // Game Dynamics Section
                        + "<div class='section'>"
                        + "  <h2>Game Dynamics</h2>"
                        + "  <p>"
                        + "    <strong>Lives:</strong> Start with <span class='highlight'>3 lives</span>.<br>"
                        + "    <strong>Timer:</strong> Each hall's timer depends on the number of objects placed.<br>"
                        + "    <strong>Bag:</strong> Stores enchantments for later use."
                        + "  </p>"
                        + "</div>"

                        // Tips for Success Section
                        + "<div class='section'>"
                        + "  <h2>Tips for Success</h2>"
                        + "  <p>"
                        + "    <strong>Plan strategically:</strong> Place objects wisely in Build Mode.<br>"
                        + "    <strong>Use wisely:</strong> Utilize enchantments effectively.<br>"
                        + "    <strong>Manage time:</strong> Keep track of the timer."
                        + "  </p>"
                        + "</div>"
                        + "</body></html>"
        );

      
        ImageIcon backIcon = new ImageIcon(new File("src/main/resources/assets/backbutton.png").getAbsolutePath());
        JButton backButton = new JButton(backIcon);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.addActionListener(actionEvent -> {
            helpFrame.dispose();
            parentWindow.setVisible(true);
        });

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Transparent background
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(helpLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(backButton);
        textPanel.add(Box.createVerticalStrut(10));

       
        JScrollPane scrollPane = new JScrollPane(textPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        textBackgroundPanel.add(scrollPane, BorderLayout.CENTER);

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        helpPanel.add(textBackgroundPanel, gbc);

        helpFrame.add(helpPanel, BorderLayout.CENTER);

        
        helpFrame.setVisible(true);

        // hide the main menu frame
        parentWindow.setVisible(false);
    }

    // method to animate button click
    private static void animateButtonClick(JButton button, ImageIcon originalIcon) {
        ImageIcon clickedIcon = new ImageIcon(new ImageIcon(originalIcon.getImage()).getImage().getScaledInstance(380, 280, Image.SCALE_SMOOTH));
        button.setIcon(clickedIcon);
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setIcon(originalIcon);
                ((Timer) e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showLoadGameDialog() {
        GameSaveManager saveManager = new GameSaveManager(mainMenu.getEventManager());
        List<GameSaveManager.SaveFileInfo> saveFiles = saveManager.getSaveFiles();

        if (saveFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No saved games found.",
                "Load Game",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a custom dialog
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog loadDialog = new JDialog(parentFrame, "Load Game", true);
        loadDialog.setLayout(new BorderLayout());
        loadDialog.setSize(400, 300);
        loadDialog.setLocationRelativeTo(this);

        // Create a list model and JList
        DefaultListModel<GameSaveManager.SaveFileInfo> listModel = new DefaultListModel<>();
        saveFiles.forEach(listModel::addElement);
        JList<GameSaveManager.SaveFileInfo> saveList = new JList<>(listModel);
        saveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saveList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Add list to a scroll pane
        JScrollPane scrollPane = new JScrollPane(saveList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loadButton = new JButton("Load");
        JButton cancelButton = new JButton("Cancel");
        
        // Style buttons
        styleDialogButton(loadButton);
        styleDialogButton(cancelButton);
        
        buttonPanel.add(loadButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Add components to dialog
        loadDialog.add(new JLabel("Select a saved game:", SwingConstants.CENTER), BorderLayout.NORTH);
        loadDialog.add(scrollPane, BorderLayout.CENTER);
        loadDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button actions
        loadButton.addActionListener(e -> {
            GameSaveManager.SaveFileInfo selected = saveList.getSelectedValue();
            if (selected != null) {
                GameSaveData saveData = saveManager.loadGame(selected.getFileName());
                if (saveData != null) {
                    mainMenu.getEventManager().notify("LOAD_GAME", saveData);
                    loadDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(loadDialog,
                        "Error loading save file.",
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(e -> loadDialog.dispose());
        
        // Show dialog
        loadDialog.setVisible(true);
    }

    private void styleDialogButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.GRAY);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.LIGHT_GRAY);
            }
        });
    }

    @Override
    public void render(GameState state) {
        if (state instanceof MainMenu) {
            this.mainMenu = (MainMenu) state;
        }
        repaint();
    }
}