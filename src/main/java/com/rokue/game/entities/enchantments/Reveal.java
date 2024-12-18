package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;
import javafx.geometry.Pos;

public class Reveal extends Enchantment {

    public Reveal(Position position) {
        super(position);
    }
    @Override
    public void applyEffect(Hero hero) {
        System.out.println("Reveal: Highlighting rune location.");
        hero.getEventManager().notify("REVEAL_RUNE", null);
    }
}