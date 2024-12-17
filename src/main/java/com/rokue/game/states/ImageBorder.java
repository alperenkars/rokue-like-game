package com.rokue.game.states;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageBorder implements Border {
    private BufferedImage borderImage;
    private int thickness;

    public ImageBorder(BufferedImage image, int thickness) {
        this.borderImage = image;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (borderImage != null) {
            // Draw the top border
            for (int i = 0; i < width; i += thickness) {
                g.drawImage(borderImage, i, 0, thickness, thickness, null);
            }

            // Draw the bottom border
            for (int i = 0; i < width; i += thickness) {
                g.drawImage(borderImage, i, height - thickness, thickness, thickness, null);
            }

            // Draw the left border
            for (int i = 0; i < height; i += thickness) {
                g.drawImage(borderImage, 0, i, thickness, thickness, null);
            }

            // Draw the right border
            for (int i = 0; i < height; i += thickness) {
                g.drawImage(borderImage, width - thickness, i, thickness, thickness, null);
            }
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}