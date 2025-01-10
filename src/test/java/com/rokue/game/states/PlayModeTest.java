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
        eventManager = new EventManager();
        halls = new ArrayList<>();
        firstHall = new Hall("Test Hall", 20, 20, 1);
        halls.add(firstHall);
        hero = new Hero(PlayMode.START_POSITION, eventManager);
        
        playMode = new PlayMode(halls, hero, eventManager);
    }

   
    private boolean repOk() {
        if (playMode == null) return false;
        
        if (playMode.getCurrentHall() == null ||
            halls == null ||
            halls.isEmpty() ||
            hero == null ||
            eventManager == null) {
            return false;
        }

        if (!halls.contains(playMode.getCurrentHall())) {
            return false;
        }

        Position heroPos = hero.getPosition();
        Hall currentHall = playMode.getCurrentHall();
        if (!currentHall.isWithinBounds(heroPos)) {
            return false;
        }

        for (Enchantment enchantment : currentHall.getEnchantments()) {
            if (!currentHall.getEnchantments().contains(enchantment)) {
                return false;
            }
        }

        return true;
    }

    @Test
    void testInitialState() {
        assertTrue(repOk(), "Initial state should satisfy representation invariant");
        assertEquals(firstHall, playMode.getCurrentHall(), "Should start with first hall");
        assertEquals(PlayMode.START_POSITION, hero.getPosition(), "Hero should be at start position");
        assertEquals(3, hero.getLives(), "Hero should start with 3 lives");
        assertFalse(playMode.isPaused(), "Game should not be paused initially");
    }

    
    @Test
    void testPauseAndResume() {
        playMode.pause();
        assertTrue(playMode.isPaused(), "Game should be paused");
        
        playMode.resume();
        assertFalse(playMode.isPaused(), "Game should not be paused after resume");
        
        assertTrue(repOk(), "Rep invariant should hold after pause/resume");
    }

   
    @Test
    void testHeroDeathAndGameOver() {
        hero.decreaseLife(); // 2 lives
        hero.decreaseLife(); // 1 life
        hero.decreaseLife(); // 0 lives - should trigger game over
        
        assertEquals(0, hero.getLives(), "Hero should have 0 lives");
        
        assertTrue(repOk(), "Rep invariant should hold after hero death");
    }

    
    @Test
    void testRuneCollection() {
        Rune rune = new Rune(new Position(5, 5));
        firstHall.setRune(rune);
        
        hero.setPosition(new Position(5, 5));
        playMode.update(mock(GameSystem.class));
        
        assertTrue(firstHall.getRune().isCollected(), "Rune should be marked as collected");
        
        assertTrue(repOk(), "Rep invariant should hold after rune collection");
    }
} 