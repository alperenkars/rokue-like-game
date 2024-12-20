package com.rokue.game.factories;

import java.util.Random;

import com.rokue.game.entities.enchantments.CloakOfProtection;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.enchantments.ExtraTime;
import com.rokue.game.entities.enchantments.LuringGem;
import com.rokue.game.entities.enchantments.Reveal;
import com.rokue.game.entities.enchantments.ExtraLife;
import com.rokue.game.util.Position;

public class EnchantmentFactory {
    private static Random rand = new Random();

    public static Enchantment createRandomEnchantment() {
        int t = rand.nextInt(5);
        switch(t) {
            case 0:
                return new ExtraTime(new Position(0,0), 5);
            case 1:
                return new Reveal(new Position(0,0));
            case 2:
                return new CloakOfProtection(new Position(0,0));
            case 3:
                return new LuringGem(new Position(0,0));
            case 4:
                return new ExtraLife(new Position(0,0));
            default:
                return new ExtraTime(new Position(0,0), 5);
        }
    }


}