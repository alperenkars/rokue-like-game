package com.rokue.game.entities.enchantments;

import com.rokue.game.entities.Hero;

public class CloakOfProtection extends Enchantment {
    public static final int DURATION_SECONDS = 20;
    private long activationEndTime;

    @Override
    public void applyEffect(Hero hero) {
        collect();
        hero.addToBag(this);
    }

    @Override
    public void use(Hero hero) {
        if (this.isCollected()) {
            hero.activateEnchantment(this);
            this.activationEndTime = System.currentTimeMillis() + DURATION_SECONDS * 1000L;
            hero.getEventManager().notify("CLOAK_USED", DURATION_SECONDS);
            System.out.println("Hero: Cloak of Protection activated for " + DURATION_SECONDS + " seconds!");
        }
    }


    @Override
    public boolean update(Hero hero) {
        if (System.currentTimeMillis() > activationEndTime) {
            hero.deactivateEnchantment(this);
            hero.getEventManager().notify("CLOAK_EXPIRED", null);
            System.out.println("Hero: Cloak of Protection expired.");
            return false;
        }
        return true;
    }
}
