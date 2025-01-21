package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class Reveal extends Enchantment {
    private static final int HIGHLIGHT_SIZE = 4;
    private static final int HIGHLIGHT_DURATION = 10; // seconds

    public Reveal(Position position) {
        super(position);
    }

    @Override
    public void applyEffect(Hero hero) {
        System.out.println("Reveal: Highlighting 4x4 region containing the rune.");
        // Calculate the region that will contain the rune
        // The event handler will handle the actual highlighting
        hero.getEventManager().notify("REVEAL_RUNE", HIGHLIGHT_DURATION);
    }
}