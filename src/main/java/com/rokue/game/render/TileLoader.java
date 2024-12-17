package com.rokue.game.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TileLoader {
    private BufferedImage tileset;
    private int tileWidth;
    private int tileHeight;
    private int scaleFactor;

    public TileLoader(String resourcePath, int tileWidth, int tileHeight, int scaleFactor) {
        try {
            this.tileset = ImageIO.read(getClass().getResource(resourcePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.scaleFactor = scaleFactor;
    }

    public BufferedImage getTile(int tileX, int tileY) {
        BufferedImage sub = tileset.getSubimage(
                tileX * tileWidth,
                tileY * tileHeight,
                tileWidth,
                tileHeight);

        BufferedImage scaled = new BufferedImage(tileWidth * scaleFactor, tileHeight * scaleFactor, BufferedImage.TYPE_INT_ARGB);
        scaled.getGraphics().drawImage(sub, 0, 0, tileWidth*scaleFactor, tileHeight*scaleFactor, null);
        return scaled;
    }
}