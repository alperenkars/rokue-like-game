package com.rokue.game.states;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.rokue.game.GameSystem;
import com.rokue.game.GameTimer;
import com.rokue.game.actions.IAction;
import com.rokue.game.actions.MoveAction;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.factories.MonsterFactory;
import com.rokue.game.factories.EnchantmentFactory;

public class PlayMode implements GameState {

    private Hall currentHall;
    private List<Hall> halls;
    private Hero hero;
    private GameTimer gameTimer;
    private EventManager eventManager;
    public static final int START_TIME = 60;
    public static final Position START_POSITION = new Position(0, 0);
    private static final int MONSTER_SPAWN_INTERVAL = 480; // (60 FPS * 8)
    private static final int ENCHANTMENT_SPAWN_INTERVAL = 720; // (60 FPS * 12)
    private static final int ENCHANTMENT_DESPAWN_TIME = 360; //  (60 FPS * 6)
    private int monsterSpawnCounter = 0;
    private int enchantmentSpawnCounter = 0;
    private Map<Enchantment, Integer> enchantmentTimers = new HashMap<>();
    private boolean paused = false;

    public PlayMode(List<Hall> halls, Hero hero, EventManager eventManager) {
        this.halls = halls;
        this.currentHall = halls.get(0);
        this.hero = hero;
        this.eventManager = eventManager;
    }

    public void enter(GameSystem system) {
        System.out.println("Entering Play Mode");
        this.gameTimer = new GameTimer(eventManager);
        this.gameTimer.start(PlayMode.START_TIME);

        // Register event handlers for monster interactions
        registerMonsterEventHandlers();
    }

    private void registerMonsterEventHandlers() {
        // Arrow hit event
        eventManager.subscribe("HERO_HIT_BY_ARROW", (eventType, data) -> {
            hero.decreaseLife();
            System.out.println("PlayMode: Hero hit by arrow! Lives remaining: " + hero.getLives());
        });

        // Stab event
        eventManager.subscribe("HERO_STABBED", (eventType, data) -> {
            hero.decreaseLife();
            System.out.println("PlayMode: Hero stabbed by fighter! Lives remaining: " + hero.getLives());
        });

        // Rune collected event
        eventManager.subscribe("RUNE_COLLECTED", (eventType, data) -> {
            onRuneCollected();
        });

        eventManager.subscribe("RUNE_TELEPORTED", (eventType, data) -> {
            Rune rune = currentHall.getRune();
            if (rune != null) {
                rune.moveRandomly(currentHall);
                System.out.println("PlayMode: Rune teleported by wizard!");
            }
        });
    }

    public void update(GameSystem system) {

        if (!paused) {
        currentHall.update(hero);
        
        monsterSpawnCounter++;
        if (monsterSpawnCounter >= MONSTER_SPAWN_INTERVAL) {
            Monster monster = MonsterFactory.createRandomMonster(currentHall);
            currentHall.addMonster(monster);
            monsterSpawnCounter = 0;
        }

        enchantmentSpawnCounter++;
        if (enchantmentSpawnCounter >= ENCHANTMENT_SPAWN_INTERVAL) {
            Enchantment enchantment = EnchantmentFactory.createRandomEnchantment(currentHall);
            currentHall.addEnchantment(enchantment);
            enchantmentTimers.put(enchantment, ENCHANTMENT_DESPAWN_TIME);
            enchantmentSpawnCounter = 0;
        }

        Iterator<Map.Entry<Enchantment, Integer>> it = enchantmentTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Enchantment, Integer> entry = it.next();
            Enchantment enchantment = entry.getKey();
            int timeLeft = entry.getValue() - 1;
            
            if (timeLeft <= 0) {
                currentHall.removeEnchantment(enchantment);
                it.remove();
            } else {
                entry.setValue(timeLeft);
            }
        }
    }
    }

    
public void pause() {
    this.paused = true;
    gameTimer.pause();
    
    // if there is any pause-specific logic to be added, add them later
}


public void resume() {
    this.paused = false;
    gameTimer.resume();
    // if there is any more resume specific logic add later
}


public boolean isPaused() {
    return paused;
}


    public void exit(GameSystem system) {
        System.out.println("Exiting Play Mode");
        if (gameTimer != null) {
            gameTimer.stop();
        }
        eventManager.unsubscribe("RUNE_COLLECTED", null);
    }

    public void handleActions(List<IAction> actions) {
        for (IAction action : actions) {
            if (action instanceof MoveAction) {
                MoveAction moveAction = (MoveAction) action;
                this.hero.move(moveAction.getDirection(), this.currentHall);
            }
        }
    }

    private void onRuneCollected() {
        System.out.println("Rune collected!");

        int nextHallIndex = halls.indexOf(currentHall) + 1;
        if (nextHallIndex < halls.size()) {
            currentHall = halls.get(nextHallIndex);
            System.out.println("Moving to the next hall: " + currentHall.getName());
            gameTimer.start(PlayMode.START_TIME);
            hero.setPosition(PlayMode.START_POSITION);
        } else {
            eventManager.notify("GAME_COMPLETED", null);
            System.out.println("All halls completed. You win!");
        }
    }

    public Hall getCurrentHall() {
        return currentHall;
    }

    public Hero getHero() {
        return hero;
    }

    public int getRemainingTime() {
        return gameTimer.getRemainingTime();
    }
}
