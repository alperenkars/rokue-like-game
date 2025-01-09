package com.rokue.game.behaviour;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;

class ShootArrowTest {
    
    private ShootArrow shootArrow;
    
    @Mock
    private Hero hero;
    
    @Mock
    private Monster monster;
    
    @Mock
    private EventManager eventManager;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        shootArrow = new ShootArrow();
        
        when(hero.getEventManager()).thenReturn(eventManager);
    }
    
    /**
     * Test Case 1: Arrow hits hero when in range
     * Tests that when the hero is within range (distance <= 4.0):
     * - The HERO_HIT_BY_ARROW event is triggered immediately
     * - The attack lasts for 1 second
     * - After attack ends, 3-second cooldown starts
     */
    @Test
    void testArrowHitsHeroInRange() throws InterruptedException {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(2, 2);  // Distance ≈ 2.8 units
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);
        
        // Act - First shot
        shootArrow.act(hero, monster);
        
        // Assert - Event triggered immediately
        verify(hero.getEventManager()).notify("HERO_HIT_BY_ARROW", null);
        
        // Still in attack duration (500ms)
        Thread.sleep(500);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Attack finished, in cooldown (1.5s total)
        Thread.sleep(1000);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Cooldown finished (4.5s total, with extra buffer)
        Thread.sleep(3000);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(2)).notify("HERO_HIT_BY_ARROW", null);
    }
    
    /**
     * Test Case 2: Arrow misses hero when out of range
     * Tests that when the hero is out of range (distance > 4.0):
     * - No event is triggered
     * - No attack state or cooldown is activated
     */
    @Test
    void testArrowMissesHeroOutOfRange() {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(5, 5);  // Distance = 7.07 units
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);
        
        // Act
        shootArrow.act(hero, monster);
        
        // Assert
        verify(hero.getEventManager(), never()).notify(anyString(), any());
        
        // Verify no attack state or cooldown (can act immediately again)
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), never()).notify(anyString(), any());
    }
    
    /**
     * Test Case 3: Attack and Cooldown behavior
     * Tests the complete attack and cooldown cycle:
     * - Initial hit triggers event
     * - Attack lasts 1 second
     * - Cooldown lasts 3 seconds after attack ends
     */
    @Test
    void testAttackAndCooldownBehavior() throws InterruptedException {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(2, 2);
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);
        
        // Initial attack
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // During attack (500ms)
        Thread.sleep(500);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Attack just ended, cooldown started (1.5s)
        Thread.sleep(1000);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Middle of cooldown (2.5s)
        Thread.sleep(1000);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Cooldown just ended (4.5s, with extra buffer)
        Thread.sleep(2000);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(2)).notify("HERO_HIT_BY_ARROW", null);
    }
} 