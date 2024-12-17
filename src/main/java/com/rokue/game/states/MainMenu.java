package com.rokue.game.states;

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

public class MainMenu {

    public MainMenu() {
        // Create the main frame
        JFrame frame = new JFrame("RoKUe-Like Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.getContentPane().setBackground(new Color(139, 69, 19)); // Set frame background to brown
        frame.setLayout(new BorderLayout());

        // Create a panel for the menu with animated background
        AnimatedBackgroundPanel menuPanel = new AnimatedBackgroundPanel("/Users/sachr/Desktop/background.jpg", "/Users/sachr/Desktop/animatedmomo.png");
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // Load and resize the image
        ImageIcon originalIcon = new ImageIcon(MainMenu.class.getResource("/assets/logo.png"));
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JLabel imageLabel = new JLabel(resizedIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add padding
        menuPanel.add(Box.createVerticalStrut(-10)); // Add vertical strut to position the image 50 pixels above
        menuPanel.add(imageLabel);
        menuPanel.add(Box.createVerticalStrut(100));

        // Load button images
        ImageIcon playIcon = new ImageIcon(MainMenu.class.getResource("/assets/playbutton.png")); // Replace with your image path
        ImageIcon helpIcon = new ImageIcon(MainMenu.class.getResource("/assets/helpbutton.png")); // Replace with your image path
        ImageIcon exitIcon = new ImageIcon(MainMenu.class.getResource("/assets/quitbutton.png")); // Replace with your image path

        // Create buttons for play, help, and exit
        JButton playButton = new JButton(playIcon);
        JButton helpButton = new JButton(helpIcon);
        JButton exitButton = new JButton(exitIcon);

        // button properties
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        helpButton.setContentAreaFilled(false);
        helpButton.setBorderPainted(false);
        helpButton.setFocusPainted(false);
        helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // mouse listeners for click events
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                animateButtonClick(playButton, playIcon);
                frame.dispose(); // Close the main menu
                // BuildMode.main(null); // Open Build Mode
            }
        });

        helpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                animateButtonClick(helpButton, helpIcon);
                openHelpScreen(frame);
            }
        });

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                animateButtonClick(exitButton, exitIcon);
                System.exit(0); // Exit the application
            }
        });

        // Add buttons to the panel
        menuPanel.add(playButton);
        menuPanel.add(Box.createVerticalStrut(20)); // Decreased vertical distance
        menuPanel.add(helpButton);
        menuPanel.add(Box.createVerticalStrut(20)); // Decreased vertical distance
        menuPanel.add(exitButton);

        // Add the panel to the frame
        frame.add(menuPanel, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);
    }

    private static void openHelpScreen(JFrame frame) {
        System.out.println("Opening help screen"); // Debugging statement

        // Create a new frame for the help screen with animated background
        JFrame helpFrame = new JFrame("Help");
        helpFrame.setSize(1920, 1080); // Set the size to 1920x1080
        helpFrame.setLayout(new BorderLayout());

        // Create a panel for the help with animated background
        JPanel helpPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon(MainMenu.class.getResource("/assets/background.jpg")).getImage(); // Replace with your image path

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        helpPanel.setBackground(new Color(245, 245, 220)); // Set help panel background to cream
        helpPanel.setLayout(new GridBagLayout());

        // Create a cream background panel for the text
        JPanel textBackgroundPanel = new JPanel();
        textBackgroundPanel.setBackground(new Color(245, 245, 220)); // Set background to cream
        textBackgroundPanel.setPreferredSize(new Dimension(1400, 800)); // Adjusted size
        textBackgroundPanel.setLayout(new BorderLayout());

        // Add a wooden texture border to the text background panel
        // Load the wooden texture image from resources
        BufferedImage woodenTexture = null;
        try {
            woodenTexture = ImageIO.read(MainMenu.class.getResource("/assets/wooden_texture.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (woodenTexture != null) {
            Border woodenBorder = new ImageBorder(woodenTexture, 50);
            Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
            Border compoundBorder = new CompoundBorder(woodenBorder, emptyBorder);
            textBackgroundPanel.setBorder(compoundBorder);
        }

        // Add some help options (for example purposes)
        JLabel helpLabel = new JLabel();
        helpLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        helpLabel.setForeground(new Color(139, 69, 19));
        helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

// Update the helpLabel text with the Enchantments section included
helpLabel.setText(
    "<html>"
    + "<head>"
    + "<style>"
    + "body { "
    + "    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; "
    + "    color: #4E342E; "
    + "    padding: 20px; "
    + "    background-color: #FFF3E0; "
    + "}"
    + "h1 { "
    + "    text-align: center; "
    + "    color: #6D4C41; "
    + "    font-size: 42px; "
    + "    margin-bottom: 30px; "
    + "    text-shadow: 2px 2px 4px #BCAAA4;"
    + "}"
    + "h2 { "
    + "    color: #8D6E63; "
    + "    border-bottom: 3px solid #D7CCC8; "
    + "    padding-bottom: 10px; "
    + "    font-size: 32px; "
    + "    margin-top: 50px; "
    + "}"
    + "p { "
    + "    line-height: 1.8; "
    + "    font-size: 20px; "
    + "    margin-bottom: 30px; "
    + "}"
    + ".monster, .enchantment { "
    + "    display: flex; "
    + "    align-items: center; "
    + "    margin-bottom: 25px; "
    + "    background-color: #FFECB3; "
    + "    padding: 10px; "
    + "    border-radius: 10px; "
    + "    box-shadow: 2px 2px 5px rgba(0,0,0,0.2); "
    + "}"
    + ".monster img, .enchantment img { "
    + "    margin-left: 20px; "
    + "    border-radius: 10px; "
    + "    box-shadow: 2px 2px 5px rgba(0,0,0,0.3); "
    + "    transition: transform 0.3s, box-shadow 0.3s; "
    + "    width: 70px; "
    + "    height: 70px; "
    + "}"
    + ".monster img:hover, .enchantment img:hover { "
    + "    transform: scale(1.2); "
    + "    box-shadow: 4px 4px 10px rgba(0,0,0,0.5); "
    + "}"
    + ".section { "
    + "    background: linear-gradient(135deg, #FFE0B2, #FFCC80); "
    + "    padding: 25px; "
    + "    border-radius: 20px; "
    + "    box-shadow: 4px 4px 12px rgba(0,0,0,0.25); "
    + "    margin-bottom: 40px; "
    + "}"
    + "strong { "
    + "    color: #D84315; "
    + "    font-weight: bold; "
    + "}"
    + ".highlight { "
    + "    background-color: #FFF9C4; "
    + "    padding: 5px 10px; "
    + "    border-radius: 5px; "
    + "}"
    + "ul { "
    + "    list-style-type: disc; "
    + "    margin-left: 20px; "
    + "}"
    + "li { "
    + "    margin-bottom: 10px; "
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
    + "    <strong>Mouse Left-Click:</strong> Interact with nearby objects and collect enchantments.<br>"
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
    + "    <strong>Pause/Resume:</strong> Use the Pause button to pause and resume the game.<br>"
    + "    <strong>Exit:</strong> Use the Exit button to quit the game and return to the main menu."
    + "  </p>"
    + "</div>"
    
    // Enchantments Section
    + "<div class='section'>"
    + "  <h2>Enchantments</h2>"
    + "  <div class='enchantment'>"
    + "    <strong>Reveal Enchantment:</strong> Temporarily reveals hidden areas and traps."
    + "    <img src='file:/Users/sachr/Desktop/reveal.png' alt='Reveal Enchantment'/>"
    + "  </div>"
    + "  <div class='enchantment'>"
    + "    <strong>Cloak of Protection:</strong> Grants temporary invisibility to avoid detection by Archers."
    + "    <img src='file:/Users/sachr/Desktop/cloak.png' alt='Cloak of Protection'/>"
    + "  </div>"
    + "  <div class='enchantment'>"
    + "    <strong>Luring Gem:</strong> Distracts Fighter Monsters, guiding them away from your path."
    + "    <img src='file:/Users/sachr/Desktop/luringgem.png' alt='Luring Gem'/>"
    + "  </div>"
    + "</div>"
    
    // Monsters Section
    + "<div class='section'>"
    + "  <h2>Monsters</h2>"
    + "  <div class='monster'>"
    + "    <strong>Archer:</strong> Shoots arrows at heroes within 4 squares. Use Cloak of Protection to avoid detection."
    + "    <img src='file:/Users/sachr/Desktop/archer.png' alt='Archer'/>"
    + "  </div>"
    + "  <div class='monster'>"
    + "    <strong>Fighter:</strong> Moves randomly but is attracted to Luring Gems. Avoid close contact."
    + "    <img src='file:/Users/sachr/Desktop/fighter.png' alt='Fighter'/>"
    + "  </div>"
    + "  <div class='monster'>"
    + "    <strong>Wizard:</strong> Teleports the rune to a random location every 5 seconds."
    + "    <img src='file:/Users/sachr/Desktop/wizard.png' alt='Wizard'/>"
    + "  </div>"
    + "</div>"
    
    // Game Dynamics Section
    + "<div class='section'>"
    + "  <h2>Game Dynamics</h2>"
    + "  <p>"
    + "    <strong>Lives:</strong> Start with <span class='highlight'>3 lives</span>; lose a life if hit by a monster or if time runs out.<br>"
    + "    <strong>Timer:</strong> Each hall’s timer depends on the number of objects placed.<br>"
    + "    <strong>Bag:</strong> Stores enchantments for later use."
    + "  </p>"
    + "</div>"
    
    // Tips for Success Section
    + "<div class='section'>"
    + "  <h2>Tips for Success</h2>"
    + "  <p>"
    + "    <strong>Plan strategically:</strong> Place objects strategically in Build Mode to optimize gameplay.<br>"
    + "    <strong>Use wisely:</strong> Utilize enchantments wisely to navigate tough situations.<br>"
    + "    <strong>Manage time:</strong> Keep track of the timer and manage your moves efficiently."
    + "  </p>"
    + "</div>"
    + "</body></html>"
);
        // Create the image button
        ImageIcon backIcon = new ImageIcon(MainMenu.class.getResource("/assets/backbutton.png")); // Replace with your image path
        JButton backButton = new JButton(backIcon);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.addActionListener(e -> {
            helpFrame.dispose(); // Close the help screen
            frame.setVisible(true); // Show the main menu again
        });

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Make the text panel transparent
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(helpLabel);
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(backButton);

        // Add the text panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(textPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        textBackgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // Center the text background panel in the help panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        helpPanel.add(textBackgroundPanel, gbc);

        helpFrame.add(helpPanel, BorderLayout.CENTER);

        // Make the help frame visible
        helpFrame.setVisible(true);

        // Hide the main menu frame
        frame.setVisible(false);
    }

    // Custom JPanel class for animated background
    static class AnimatedBackgroundPanel extends JPanel {
        private Image backgroundImage;
        private Image animatedImage;
        private int x = 0;
        private int y = 0;
        private int xDirection = 1;
        private int yDirection = 1;
        private int speed = 5;
        private double rotationAngle = 0;
        private double rotationSpeed = 0.1;
        private ArrayList<Point> trail;
        private final int TRAIL_LENGTH = 10; // Reduced trail length

        public AnimatedBackgroundPanel(String backgroundImagePath, String animatedImagePath) {
            backgroundImage = new ImageIcon(backgroundImagePath).getImage();
            animatedImage = new ImageIcon(animatedImagePath).getImage();
            trail = new ArrayList<>();
            Timer timer = new Timer(30, e -> {
                updatePosition();
                repaint();
            });
            timer.start();
        }

        private void updatePosition() {
            x += xDirection * speed;
            y += yDirection * speed;
            rotationAngle += rotationSpeed;

            // Add current position to trail
            trail.add(new Point(x, y));
            if (trail.size() > TRAIL_LENGTH) {
                trail.remove(0);
            }

            if (x < 0 || x + animatedImage.getWidth(this) > getWidth()) {
                xDirection *= -1;
            }
            if (y < 0 || y + animatedImage.getHeight(this) > getHeight()) {
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

            // Draw trail dots
            for (int i = 0; i < trail.size(); i++) {
                Point p = trail.get(i);
                float alpha = (float) i / trail.size();
                g2d.setColor(new Color(1f, 1f, 1f, alpha));
                g2d.fillOval(p.x + animatedImage.getWidth(this)/2, 
                            p.y + animatedImage.getHeight(this)/2, 
                            10, 10);
            }

            // Draw main image
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

    // Method to animate button click
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
}