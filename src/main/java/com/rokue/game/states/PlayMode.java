package com.rokue.game.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Font;

import com.rokue.game.GameSystem;
import com.rokue.game.GameTimer;
import com.rokue.game.actions.IAction;
import com.rokue.game.actions.MoveAction;
import com.rokue.game.behaviour.ShootArrow;
import com.rokue.game.behaviour.StabDagger;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.ArcherMonster;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.events.EventListener;
import com.rokue.game.events.EventManager;
import com.rokue.game.factories.EnchantmentFactory;
import com.rokue.game.factories.MonsterFactory;
import com.rokue.game.save.GameSaveData;
import com.rokue.game.util.Position;
import com.rokue.ui.MainMenuUI;



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
    private static final long MONSTER_SPAWN_INTERVAL_MS = 8000; // 8 seconds
    private static final long ENCHANTMENT_SPAWN_INTERVAL_MS = 12000; // 12 seconds
    private static final long ENCHANTMENT_DESPAWN_TIME_MS = 6000; // 6 seconds
    private long lastMonsterSpawnTime = 0;
    private long lastEnchantmentSpawnTime = 0;
    private Map<Enchantment, Long> enchantmentDespawnTimes = new HashMap<>();
    private Random rand = new Random();
    private volatile boolean paused = false;
    private final ReentrantLock updateLock = new ReentrantLock();
    private final ReentrantLock hallTransitionLock = new ReentrantLock();
    private final Object enchantmentLock = new Object();
    private long currentTime = System.currentTimeMillis();
    private int initialTime;
    private int completedHallsCount = 0;

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
        this(halls, hero, eventManager, START_TIME);
    }

    public PlayMode(List<Hall> halls, Hero hero, EventManager eventManager, int initialTime) {
        this.halls = halls;
        this.currentHall = halls.get(0);
        this.hero = hero;
        this.eventManager = eventManager;
        this.initialTime = initialTime;
        this.currentHall.setHero(hero);
    }

    public void enter(GameSystem system) {
        System.out.println("Entering Play Mode");
        gameTimer = new GameTimer(eventManager);
        gameTimer.start(initialTime);

        spawnInitialRune();
        registerEventHandlers();
    }
    private void spawnInitialRune() {
        Position runePos = new Position(rand.nextInt(currentHall.getWidth()), rand.nextInt(currentHall.getHeight()));
        Rune rune = new Rune(runePos);
        currentHall.setRune(rune);
    }

    private void registerEventHandlers() {
        // Arrow hit event
        eventManager.subscribe("HERO_HIT_BY_ARROW", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (hero) {
                    if (!isPaused() && hero.getLives() > 0) {
                        hero.decreaseLife();
                        eventManager.notify("LOG_MESSAGE", "Hero hit by arrow!");
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
                        eventManager.notify("LOG_MESSAGE", "Hero stabbed by fighter!");
                    }
                }
            }
        });

        // Info message event
        eventManager.subscribe("SHOW_INFO", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                if (data instanceof String) {
                    eventManager.notify("LOG_MESSAGE", data);
                    eventManager.notify("DISPLAY_INFO", data);
                }
            }
        });

        // Add lives event
        eventManager.subscribe("ADD_LIVES", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (hero) {
                    if (!isPaused()) {
                        int currentLives = hero.getLives();
                        currentLives++;
                       // eventManager.notify("LOG_MESSAGE", "PlayMode: Added 1 life. Lives: " + currentLives);
                    }
                }
            }
        });

        // Add time event
        eventManager.subscribe("ADD_TIME", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                if (!isPaused() && data instanceof Integer) {
                    int seconds = (Integer) data;
                    gameTimer.addTime(seconds);
                    eventManager.notify("LOG_MESSAGE", "Added " + seconds + " seconds");
                }
            }
        });

        // Invisibility event
        eventManager.subscribe("INVISIBILITY", new EventListener() {
            private long invisibilityEndTime = 0;
            private boolean isInvisible = false;

            @Override
            public void onEvent(String eventType, Object data) {
                if (!isPaused() && data instanceof Integer) {
                    int duration = (Integer) data;
                    // Set invisibility flag for archer monsters
                    for (Monster monster : currentHall.getMonsters()) {
                        if (monster instanceof ArcherMonster) {
                            ((ShootArrow)monster.getBehaviour()).setHeroInvisible(true);
                        }
                    }
                    
                    isInvisible = true;
                    invisibilityEndTime = System.currentTimeMillis() + (duration * 1000);
                    eventManager.notify("LOG_MESSAGE", "Hero invisible now.");

                    // Start a timer to check invisibility status periodically
                    new Thread(() -> {
                        while (isInvisible && System.currentTimeMillis() < invisibilityEndTime) {
                            if (isPaused()) {
                                // If game is paused, extend the end time by the pause duration
                                invisibilityEndTime += 100; // Extend by the sleep duration
                            }
                            try {
                                Thread.sleep(100); // Check every 100ms
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                        
                        // Only remove invisibility if it hasn't been removed already
                        if (isInvisible) {
                            isInvisible = false;
                            if (!isPaused()) {
                                for (Monster monster : currentHall.getMonsters()) {
                                    if (monster instanceof ArcherMonster) {
                                        ((ShootArrow)monster.getBehaviour()).setHeroInvisible(false);
                                    }
                                }
                                eventManager.notify("LOG_MESSAGE", "Hero visibility restored");
                            }
                        }
                    }).start();
                }
            }
        });

        // Reveal rune event
        eventManager.subscribe("REVEAL_RUNE", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                if (!isPaused() && data instanceof Integer) {
                    int duration = (Integer) data;
                    Rune rune = currentHall.getRune();
                    if (rune != null && rune.getHiddenUnder() != null) {
                        Position runePos = rune.getHiddenUnder().getPosition();
                        // Calculate the 4x4 region that will contain the rune
                        // Ensure the region stays within hall bounds
                        int startX = Math.max(0, runePos.getX() - 1);
                        int startY = Math.max(0, runePos.getY() - 1);
                        int endX = Math.min(currentHall.getWidth() - 1, startX + 3);
                        int endY = Math.min(currentHall.getHeight() - 1, startY + 3);
                        
                        // Adjust start positions if region would exceed bounds
                        if (endX - startX < 3) startX = Math.max(0, endX - 3);
                        if (endY - startY < 3) startY = Math.max(0, endY - 3);

                        Position highlightStart = new Position(startX, startY);
                        Position highlightEnd = new Position(endX, endY);
                        
                        // Notify UI to show highlight
                        Map<String, Position> highlightData = new HashMap<>();
                        highlightData.put("start", highlightStart);
                        highlightData.put("end", highlightEnd);
                        eventManager.notify("SHOW_HIGHLIGHT", highlightData);
                        
                        // Schedule highlight removal
                        new Thread(() -> {
                            try {
                                Thread.sleep(duration * 1000);
                                if (!isPaused()) {
                                    eventManager.notify("HIDE_HIGHLIGHT", null);
                                   // eventManager.notify("LOG_MESSAGE", "PlayMode: Rune highlight removed");
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                        
                        eventManager.notify("LOG_MESSAGE", "Rune highlighted.");
                    }
                }
            }
        });

        // Distraction event
        eventManager.subscribe("DISTRACTION", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                if (!isPaused() && data instanceof Position) {
                    Position targetPos = (Position) data;
                    // Update fighter monsters' target position
                    for (Monster monster : currentHall.getMonsters()) {
                        if (monster instanceof FighterMonster) {
                            ((StabDagger)monster.getBehaviour()).setTargetPosition(targetPos);
                        }
                    }
                    eventManager.notify("LOG_MESSAGE", "Fighter monsters distracted." );
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
                        }
                    }
                }
            }
        });

        eventManager.subscribe("ADD_LIVES", new EventListener() {
            @Override
            public void onEvent(String eventType, Object data) {
                synchronized (hero) {
                    if (!isPaused()) {
                        hero.increaseLife();
                      //  eventManager.notify("LOG_MESSAGE", "PlayMode: Added 1 life. Lives: " + hero.getLives());
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
                        eventManager.notify("LOG_MESSAGE", "Hero is dead. Game Over!");
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
                
                long currentTime = System.currentTimeMillis();
                
                // Monster spawning
                if (currentTime - lastMonsterSpawnTime >= MONSTER_SPAWN_INTERVAL_MS) {
                    Monster monster = MonsterFactory.createRandomMonster(currentHall, this);
                    synchronized(currentHall) {
                        currentHall.addMonster(monster);
                    }
                    lastMonsterSpawnTime = currentTime;
                }

                // Enchantment spawning
                if (currentTime - lastEnchantmentSpawnTime >= ENCHANTMENT_SPAWN_INTERVAL_MS) {
                    Enchantment enchantment = EnchantmentFactory.createRandomEnchantment(currentHall);
                    synchronized(enchantmentLock) {
                        currentHall.addEnchantment(enchantment);
                        enchantmentDespawnTimes.put(enchantment, currentTime + ENCHANTMENT_DESPAWN_TIME_MS);
                    }
                    lastEnchantmentSpawnTime = currentTime;
                }

                // Enchantment despawning
                synchronized(enchantmentLock) {
                    Iterator<Map.Entry<Enchantment, Long>> it = enchantmentDespawnTimes.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Enchantment, Long> entry = it.next();
                        Enchantment enchantment = entry.getKey();
                        long despawnTime = entry.getValue();
                        
                        if (currentTime >= despawnTime) {
                            currentHall.removeEnchantment(enchantment);
                            it.remove();
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
            lastMonsterSpawnTime = System.currentTimeMillis();
            lastEnchantmentSpawnTime = System.currentTimeMillis();
            synchronized(enchantmentLock) {
                enchantmentDespawnTimes.clear();
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

   
private void handleGameEnd() {
    
    resetState();

    // Notify the UI to show the congratulations screen
    eventManager.notify("GAME_COMPLETED", null);
    eventManager.notify("EXIT_PLAY_MODE", null);
}

// Then call handleGameEnd() wherever you detect the game is finished:
    private void checkGameOver() {
        int nextHallIndex = halls.indexOf(currentHall) + 1;
        if (nextHallIndex >= halls.size()) {
            handleGameEnd();
        }
    }

    private void resetState() {
        updateLock.lock();
        try {
            lastMonsterSpawnTime = System.currentTimeMillis();
            lastEnchantmentSpawnTime = System.currentTimeMillis();
            synchronized(enchantmentLock) {
                enchantmentDespawnTimes.clear();
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

            // Clear any existing highlight
            eventManager.notify("HIDE_HIGHLIGHT", null);

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
                    gameTimer.start(initialTime);
                    
                    // Set up hero and rune in new hall
                    hero.setPosition(START_POSITION);
                    currentHall.setHero(hero);
                    
                    Position runePos = new Position(rand.nextInt(currentHall.getWidth()), 
                                                  rand.nextInt(currentHall.getHeight()));
                    Rune rune = new Rune(runePos);
                    currentHall.setRune(rune);
                }
                onHallCompleted();
            } else {
                synchronized(this) {
                    if (gameTimer != null) {
                        gameTimer.stop();
                    }
                    // Clear final hall state
                    currentHall.setRune(null);
                    currentHall.setHero(null);
                    checkGameOver();
                    System.out.println("All halls completed. You win!");
                }
            }
        } finally {
            hallTransitionLock.unlock();
        }
    }

    private void onHallCompleted() {
        completedHallsCount++;
        getEventManager().notify("HALL_COMPLETED", completedHallsCount);
    }

    public synchronized Hall getCurrentHall() {
        return currentHall;
    }

    public synchronized List<Hall> getHalls() {
        return halls;
    }

    public synchronized Hero getHero() {
        return hero;
    }

    public int getRemainingTime() {
        return gameTimer != null ? gameTimer.getRemainingTime() : 0;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void handleActions(List<IAction> actions) {
        for (IAction action : actions) {
            if (action instanceof MoveAction) {
                MoveAction moveAction = (MoveAction) action;
                this.hero.move(moveAction.getDirection(), this.currentHall);
            }
        }
    }

    public void loadFromSaveData(GameSaveData saveData) {
        hallTransitionLock.lock();
        try {
            // Load all halls
            this.halls = saveData.getHalls();
            
            // Set current hall
            this.currentHall = halls.get(saveData.getCurrentHallIndex());
            
            // Set up hero
            this.hero = saveData.getHero();
            this.currentHall.setHero(hero);
            
            // Initialize timer if needed
            if (gameTimer == null) {
                gameTimer = new GameTimer(eventManager);
            }
            
            // Restart timer with saved time
            gameTimer.stop();
            gameTimer.start(saveData.getRemainingTime());
            
            // Make sure all halls have their event managers and play mode set
            for (Hall hall : halls) {
                for (Monster monster : hall.getMonsters()) {
                    if (monster instanceof WizardMonster) {
                        WizardMonster wizard = (WizardMonster) monster;
                        wizard.setEventManager(eventManager);
                        wizard.setPlayMode(this);
                    }
                }
            }
        } finally {
            hallTransitionLock.unlock();
        }
    }
}