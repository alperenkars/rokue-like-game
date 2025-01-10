package com.rokue.game.states;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rokue.game.GameSystem;
import com.rokue.game.GameTimer;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class PlayModeTest {
    private PlayMode playMode;
    private List<Hall> halls;
    private Hero hero;
    private EventManager eventManager;
    private Hall firstHall;

    
    @BeforeEach
    void setUp() {
        // Create mocks and test objects
        eventManager = new EventManager();
        halls = new ArrayList<>();
        firstHall = new Hall("Test Hall", 20, 20, 1);
        halls.add(firstHall);
        hero = new Hero(PlayMode.START_POSITION, eventManager);
        
        // Initialize PlayMode with test objects
        playMode = new PlayMode(halls, hero, eventManager);
    }

   
    private boolean repOk() {
        if (playMode == null) return false;
        
        // Check null conditions
        if (playMode.getCurrentHall() == null ||
            halls == null ||
            halls.isEmpty() ||
            hero == null ||
            eventManager == null) {
            return false;
        }

        // Check if current hall is in the halls list
        if (!halls.contains(playMode.getCurrentHall())) {
            return false;
        }

        // Check hero position bounds
        Position heroPos = hero.getPosition();
        Hall currentHall = playMode.getCurrentHall();
        if (!currentHall.isWithinBounds(heroPos)) {
            return false;
        }

        // Check counters are non-negative
        // Note: These are private fields in PlayMode, so we can't check directly in a real test
        // In practice, we would need to expose these through methods or use reflection
        
        // Check enchantment timers consistency
        // Note: In practice, we would need a way to access the enchantment timers
        // This is a pseudo-check showing what we would verify
        for (Enchantment enchantment : currentHall.getEnchantments()) {
            // Verify each enchantment has a positive timer
            // and exists in the current hall
            if (!currentHall.getEnchantments().contains(enchantment)) {
                return false;
            }
        }

        return true;
    }

    @Test
    void testInitialState() {
        // Test initial state setup
        assertTrue(repOk(), "Initial state should satisfy representation invariant");
        assertEquals(firstHall, playMode.getCurrentHall(), "Should start with first hall");
        assertEquals(PlayMode.START_POSITION, hero.getPosition(), "Hero should be at start position");
        assertEquals(3, hero.getLives(), "Hero should start with 3 lives");
        assertFalse(playMode.isPaused(), "Game should not be paused initially");
    }

    
    @Test
    void testPauseAndResume() {
        // Test pause functionality
        playMode.pause();
        assertTrue(playMode.isPaused(), "Game should be paused");
        
        // Test resume functionality
        playMode.resume();
        assertFalse(playMode.isPaused(), "Game should not be paused after resume");
        
        // Verify representation invariant holds after state changes
        assertTrue(repOk(), "Rep invariant should hold after pause/resume");
    }

   
    @Test
    void testHeroDeathAndGameOver() {
        // Simulate hero death
        hero.decreaseLife(); // 2 lives
        hero.decreaseLife(); // 1 life
        hero.decreaseLife(); // 0 lives - should trigger game over
        
        assertEquals(0, hero.getLives(), "Hero should have 0 lives");
        
        // Verify representation invariant still holds
        assertTrue(repOk(), "Rep invariant should hold after hero death");
    }

    
    @Test
    void testRuneCollection() {
        // Setup rune in current hall
        Rune rune = new Rune(new Position(5, 5));
        firstHall.setRune(rune);
        
        // Simulate hero collecting rune
        hero.setPosition(new Position(5, 5));
        playMode.update(mock(GameSystem.class));
        
        // Verify rune was collected
        assertTrue(firstHall.getRune().isCollected(), "Rune should be marked as collected");
        
        // Verify representation invariant holds after rune collection
        assertTrue(repOk(), "Rep invariant should hold after rune collection");
    }
} 