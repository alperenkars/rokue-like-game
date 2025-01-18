package com.rokue.game.factories;

import java.util.Random;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.enchantments.CloakOfProtection;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.enchantments.ExtraTime;
import com.rokue.game.entities.enchantments.LuringGem;
import com.rokue.game.entities.enchantments.Reveal;
import com.rokue.game.entities.enchantments.ExtraLife;
import com.rokue.game.util.Position;

public class EnchantmentFactory {
    private static Random rand = new Random();

    public static Enchantment createRandomEnchantment(Hall hall) {
        int t = rand.nextInt(5);
        while (true) {
            Position spawnPos = new Position(rand.nextInt(hall.getWidth()), rand.nextInt(hall.getHeight()));
            if (hall.getCell(spawnPos).getContent() == null) {
                return new Reveal(new Position(spawnPos.getX(), spawnPos.getY()));
//                switch (t) {
//                    case 0:
//                        return new ExtraTime(new Position(spawnPos.getX(), spawnPos.getY()), 5);
//                    case 1:
//                        return new Reveal(new Position(spawnPos.getX(), spawnPos.getY()));
//                    case 2:
//                        return new CloakOfProtection(new Position(spawnPos.getX(), spawnPos.getY()));
//                    case 3:
//                        return new LuringGem(new Position(spawnPos.getX(), spawnPos.getY()));
//                    case 4:
//                        return new ExtraLife(new Position(spawnPos.getX(), spawnPos.getY()));
//                }
            }
        }
    }


}
