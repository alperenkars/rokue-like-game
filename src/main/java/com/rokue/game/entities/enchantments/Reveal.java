package com.rokue.game.entities.enchantments;

import java.util.Random;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class Reveal extends Enchantment {

    private Position position;

    public Reveal(Position position) {
        super();
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public void applyEffect(Hero hero) {
        Hall currentHall = hero.getCurrentHall();
        if (currentHall.getRune() != null && currentHall != null  ) {
            Position runePosition = currentHall.getRune().getPosition();
            System.out.println("Reveal: You can find the rune in this area..: " + runePosition);
            revealRuneRegion(runePosition, currentHall);
        } else {
            System.out.println("Reveal: No rune found in the current hall!");
        }
    }



    private void revealRuneRegion(Position runePosition, Hall hall) {
    int hallWidth = hall.getWidth();
    int hallHeight = hall.getHeight();

    //  valid top-left corner for the 4x4 grid with rune inside of it
    int minX = Math.max(0, runePosition.getX() - 3);
    int minY = Math.max(0, runePosition.getY() - 3);
    int maxX = Math.min(hallWidth - 4, runePosition.getX());
    int maxY = Math.min(hallHeight - 4, runePosition.getY());  // ensuring its in the hall boundaries


    // to chose random left corner
    Random random = new Random();
    int leftX = random.nextInt(maxX - minX + 1) + minX; 
    int leftY = random.nextInt(maxY - minY + 1) + minY; 

    // Display or process the 4x4 grid
    System.out.println("Reveal: Here's your clue.. Highlighting rune area starting at (" + leftX + ", " + leftY + ")");

    for (int x = leftX; x < leftX + 4; x++) {
        for (int y = leftY; y < leftY + 4; y++) {
            Position position = new Position(x, y);
            System.out.println("Revealed cell: " + position);
            // Add rendering or highlighting logic here if necessary
        }
    }
}

}
