package com.rokue.game.states;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import com.rokue.game.behaviour.TeleportHero;
import com.rokue.game.behaviour.TeleportRune;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.events.EventListener;

import com.rokue.game.GameSystem;
import com.rokue.game.GameTimer;
import com.rokue.game.actions.IAction;
import com.rokue.game.actions.MoveAction;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.events.EventManager;
import com.rokue.game.factories.EnchantmentFactory;
import com.rokue.game.factories.MonsterFactory;
import com.rokue.game.util.Position;

/**
 * PlayMode represents the active gameplay state in the RoKUe-Like game.
 * It manages the game's core mechanics including hall transitions, monster spawning,
 * enchantment management, and game events.
 * 
 * @requires
 *   - halls != null and not empty
 *   - hero != null
 *   - eventManager != null
 *   - All halls in the list are properly initialized
 *   - Hero has valid position and lives > 0
 * 
 * @modifies
 *   - currentHall state (monsters, enchantments, rune)
 *   - hero state (position, lives)
 *   - game timer state
 *   - monster and enchantment spawn counters
 * 
 * @effects
 *   - Maintains game state according to game rules
 *   - Spawns monsters and enchantments at regular intervals
 *   - Handles hall transitions when runes are collected
 *   - Manages hero state and game over conditions
 */
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
    private Random rand = new Random();
    private volatile boolean paused = false;
    private final ReentrantLock updateLock = new ReentrantLock();
    private final ReentrantLock hallTransitionLock = new ReentrantLock();
    private final Object enchantmentLock = new Object();


    /**
     * Creates a new PlayMode instance with the given halls, hero, and event manager.
     * 
     * @requires
     *   - halls != null and not empty
     *   - hero != null
     *   - eventManager != null
     * 
     * @modifies
     *   - this.halls
     *   - this.currentHall
     *   - this.hero
     *   - this.eventManager
     * 
     * @effects
     *   - Initializes PlayMode with the first hall as current
     *   - Sets up the hero in the current hall
     *   - Prepares event management system
     */
    public PlayMode(List<Hall> halls, Hero hero, EventManager eventManager) {
        this.halls = halls;
        this.currentHall = halls.get(0);
        this.hero = hero;
        this.eventManager = eventManager;
        this.currentHall.setHero(hero);
    }

    public void enter(GameSystem system) {
        System.out.println("Entering Play Mode");
        this.gameTimer = new GameTimer(eventManager);
        this.gameTimer.start(PlayMode.START_TIME);

        Rune rune = new Rune(new Position(rand.nextInt(currentHall.getWidth()), rand.nextInt(currentHall.getHeight())));
        currentHall.setRune(rune);

        registerEventHandlers();
    }

    private void registerEventHandlers() {
        // Arrow hit event
        eventManager.subscribe("HERO_HIT_BY_ARROW", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (hero) {
                    if (!isPaused() && hero.getLives() > 0) {
                        hero.decreaseLife();
                        System.out.println("PlayMode: Hero hit by arrow! Lives remaining: " + hero.getLives());
                    }
                }
            }
        });

        // Stab event
        eventManager.subscribe("HERO_STABBED", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (hero) {
                    if (!isPaused() && hero.getLives() > 0) {
                        hero.decreaseLife();
                        System.out.println("PlayMode: Hero stabbed by fighter! Lives remaining: " + hero.getLives());
                    }
                }
            }
        });

        // Rune collected event
        eventManager.subscribe("RUNE_COLLECTED", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (PlayMode.this) {
                    if (!isPaused()) {
                        onRuneCollected();
                    }
                }
            }
        });

        eventManager.subscribe("RUNE_TELEPORTED", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (currentHall) {
                    if (!isPaused()) {
                        Rune rune = currentHall.getRune();
                        if (rune != null) {
                            rune.moveToRandomObject(currentHall);
                            currentHall.setRune(rune);
                            System.out.println("PlayMode: Rune teleported by wizard!");
                        }
                    }
                }
            }
        });

        eventManager.subscribe("HERO_DEAD", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (PlayMode.this) {
                    if (!isPaused()) {
                        gameTimer.stop();
                        System.out.println("PlayMode: Hero is dead. Game Over!");
                        eventManager.notify("GAME_OVER", null);
                    }
                }
            }
        });
    }

    /**
     * Updates the game state for one frame.
     * 
     * @requires
     *   - system != null
     *   - currentHall != null
     *   - hero != null
     *   - Not in a transitioning state
     * 
     * @modifies
     *   - currentHall state (monsters, enchantments)
     *   - monsterSpawnCounter
     *   - enchantmentSpawnCounter
     *   - enchantmentTimers
     * 
     * @effects
     *   - Updates hall state
     *   - Spawns monsters if interval reached
     *   - Spawns enchantments if interval reached
     *   - Updates enchantment timers and removes expired enchantments
     */
    public void update(GameSystem system) {
        if (!paused) {
            updateLock.lock();
            try {
                currentHall.update(hero);

                //updated to %30 of the time wizard monster behaviour
                boolean wizardExists = false;
                for (Monster monster : currentHall.getMonsters()) {
                    if (monster instanceof WizardMonster && !((WizardMonster) monster).isRemoved()) {
                        wizardExists = true;
                        break;
                    }
                }

                if (wizardExists) {
                    Iterator<Monster> monsterIterator = currentHall.getMonsters().iterator();
                    while (monsterIterator.hasNext()) {
                        Monster monster = monsterIterator.next();
                        if (monster instanceof WizardMonster) {
                            WizardMonster wizard = (WizardMonster) monster;

                            if (wizard.isRemoved()) {
                                continue;
                            }

                            double totalTime = PlayMode.START_TIME;
                            double remainingTime = this.getRemainingTime();
                            double percentage = remainingTime / totalTime;

                            if (percentage < 0.30) {
                                TeleportHero teleportHero = new TeleportHero(currentHall);
                                teleportHero.act(hero, wizard);
                                System.out.println("WizardMonster: Hero teleported!");
                                monsterIterator.remove();
                            } else if (percentage > 0.70) {
                                if (!(wizard.getBehaviour() instanceof TeleportRune)) {
                                    wizard.setBehaviour(new TeleportRune());
                                }
                                wizard.getBehaviour().act(hero, wizard);
                            } else {

                                    monsterIterator.remove();

                            }
                        } else {
                            monster.update(hero, currentHall);
                        }
                    }
                } else {
                    System.out.println("No active wizard on the map. TeleportHero will not be triggered.");
                    for (Monster monster : currentHall.getMonsters()) {
                        monster.update(hero, currentHall);
                    }
                }

                
                monsterSpawnCounter++;
                if (monsterSpawnCounter >= MONSTER_SPAWN_INTERVAL) {
                    Monster monster = MonsterFactory.createRandomMonster(currentHall);
                    synchronized(currentHall) {
                        currentHall.addMonster(monster);
                    }
                    monsterSpawnCounter = 0;
                }

                enchantmentSpawnCounter++;
                if (enchantmentSpawnCounter >= ENCHANTMENT_SPAWN_INTERVAL) {
                    Enchantment enchantment = EnchantmentFactory.createRandomEnchantment(currentHall);
                    synchronized(enchantmentLock) {
                        currentHall.addEnchantment(enchantment);
                        enchantmentTimers.put(enchantment, ENCHANTMENT_DESPAWN_TIME);
                    }
                    enchantmentSpawnCounter = 0;
                }

                synchronized(enchantmentLock) {
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
            } finally {
                updateLock.unlock();
            }
        }
    }


    /**
     * Pauses the game.
     * 
     * @requires
     *   - Game is not already paused
     * 
     * @modifies
     *   - this.paused
     *   - gameTimer state
     * 
     * @effects
     *   - Sets game to paused state
     *   - Pauses the game timer if it exists
     */
    public synchronized void pause() {
        if (!paused) {
            this.paused = true;
            if (gameTimer != null) {
                gameTimer.pause();
            }
        }
    }


    /**
     * Resumes the game from a paused state.
     * 
     * @requires
     *   - Game is currently paused
     * 
     * @modifies
     *   - this.paused
     *   - gameTimer state
     * 
     * @effects
     *   - Sets game to unpaused state
     *   - Resumes the game timer if it exists
     */
    public synchronized void resume() {
        if (paused) {
            this.paused = false;
            if (gameTimer != null) {
                gameTimer.resume();
            }
        }
    }


    public boolean isPaused() {
        return paused;
    }


    public void exit(GameSystem system) {
        System.out.println("Exiting Play Mode");
        updateLock.lock();
        try {
            if (gameTimer != null) {
                gameTimer.stop();
            }
            // Unsubscribe from all events
            eventManager.unsubscribe("RUNE_COLLECTED", null);
            eventManager.unsubscribe("HERO_HIT_BY_ARROW", null);
            eventManager.unsubscribe("HERO_STABBED", null);
            eventManager.unsubscribe("RUNE_TELEPORTED", null);
            eventManager.unsubscribe("HERO_DEAD", null);

            // Reset all game state
            resetGameState();
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Resets the game state.
     * 
     * @requires
     *   - halls != null
     *   - All halls are properly initialized
     * 
     * @modifies
     *   - All halls' state
     *   - currentHall
     *   - hero position
     *   - monster and enchantment states
     *   - spawn counters
     * 
     * @effects
     *   - Clears all monsters and enchantments
     *   - Resets hero to starting position
     *   - Resets all game counters
     *   - Returns to first hall
     */
    private void resetGameState() {
        hallTransitionLock.lock();
        try {
            // Reset all counters and collections
            monsterSpawnCounter = 0;
            enchantmentSpawnCounter = 0;
            synchronized(enchantmentLock) {
                enchantmentTimers.clear();
            }

            // Reset all halls
            for (Hall hall : halls) {
                synchronized(hall) {
                    hall.clearMonsters();
                    hall.clearEnchantments();
                    hall.setRune(null);
                    hall.setHero(null);
                }
            }

            // Reset current hall to first hall and initialize it
            synchronized(this) {
                currentHall = halls.get(0);
                // Reset hero position and state
                if (hero != null) {
                    hero.setPosition(START_POSITION);
                }
                currentHall.setHero(hero);
                
                // Create and set initial rune for the first hall
                Position runePos = new Position(rand.nextInt(currentHall.getWidth()), 
                                             rand.nextInt(currentHall.getHeight()));
                Rune rune = new Rune(runePos);
                currentHall.setRune(rune);
            }

            // Reset pause state
            paused = false;
        } finally {
            hallTransitionLock.unlock();
        }
    }

    private void resetState() {
        updateLock.lock();
        try {
            monsterSpawnCounter = 0;
            enchantmentSpawnCounter = 0;
            synchronized(enchantmentLock) {
                enchantmentTimers.clear();
            }
            synchronized(currentHall) {
                currentHall.clearMonsters();
                currentHall.clearEnchantments();
            }
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Handles rune collection and hall transition.
     * 
     * @requires
     *   - Game is not paused
     *   - currentHall != null
     *   - halls list is properly initialized
     * 
     * @modifies
     *   - currentHall
     *   - hero position
     *   - game timer
     *   - monster and enchantment states
     * 
     * @effects
     *   - Transitions to next hall if available
     *   - Resets game state for new hall
     *   - Triggers game completion if last hall
     */
    private void onRuneCollected() {
        hallTransitionLock.lock();
        try {
            System.out.println("Rune collected!");

            int nextHallIndex = halls.indexOf(currentHall) + 1;
            if (nextHallIndex < halls.size()) {
                resetState(); // Reset state before changing hall
                
                // Safely transition to next hall
                synchronized(this) {
                    // Clear current hall's rune and hero
                    currentHall.setRune(null);
                    currentHall.setHero(null);
                    
                    // Set up next hall
                    currentHall = halls.get(nextHallIndex);
                    System.out.println("Moving to the next hall: " + currentHall.getName());
                    
                    if (gameTimer != null) {
                        gameTimer.stop(); // Ensure old timer is stopped
                    }
                    gameTimer.start(PlayMode.START_TIME);
                    
                    // Set up hero and rune in new hall
                    hero.setPosition(START_POSITION);
                    currentHall.setHero(hero);
                    
                    Position runePos = new Position(rand.nextInt(currentHall.getWidth()), 
                                                  rand.nextInt(currentHall.getHeight()));
                    Rune rune = new Rune(runePos);
                    currentHall.setRune(rune);
                }
            } else {
                synchronized(this) {
                    if (gameTimer != null) {
                        gameTimer.stop();
                    }
                    // Clear final hall state
                    currentHall.setRune(null);
                    currentHall.setHero(null);
                    
                    eventManager.notify("GAME_COMPLETED", null);
                    System.out.println("All halls completed. You win!");
                }
            }
        } finally {
            hallTransitionLock.unlock();
        }
    }

    public synchronized Hall getCurrentHall() {
        return currentHall;
    }

    public synchronized Hero getHero() {
        return hero;
    }

    public int getRemainingTime() {
        GameTimer timer = gameTimer; // Local reference for thread safety
        return timer != null ? timer.getRemainingTime() : 0;
    }

    public void handleActions(List<IAction> actions) {
        for (IAction action : actions) {
            if (action instanceof MoveAction) {
                MoveAction moveAction = (MoveAction) action;
                this.hero.move(moveAction.getDirection(), this.currentHall);
            }
        }
    }
}
