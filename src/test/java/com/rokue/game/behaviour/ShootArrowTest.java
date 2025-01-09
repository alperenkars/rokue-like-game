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
     * - The HERO_HIT_BY_ARROW event is triggered
     * - The cooldown is activated
     */
    @Test
    void testArrowHitsHeroInRange() {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(2, 2);  // Distance ≈ 2.8 units
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);
        
        // Act
        shootArrow.act(hero, monster);
        
        // Assert
        verify(hero.getEventManager()).notify("HERO_HIT_BY_ARROW", null);
        
        // Verify cooldown is active
        shootArrow.act(hero, monster);  // Second shot shouldn't trigger event
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
    }
    
    /**
     * Test Case 2: Arrow misses hero when out of range
     * Tests that when the hero is out of range (distance > 4.0):
     * - No event is triggered
     * - No cooldown is activated
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
        
        // Verify no cooldown (can shoot immediately again)
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), never()).notify(anyString(), any());
    }
    
    /**
     * Test Case 3: Cooldown behavior
     * Tests that the cooldown mechanism works correctly:
     * - After a hit, cooldown prevents shooting for 90 frames
     * - After cooldown expires, can shoot again
     */
    @Test
    void testCooldownBehavior() {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(2, 2);
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);
        
        // Act & Assert
        // First shot should hit
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Simulate 90 frames (complete cooldown)
        for (int i = 0; i < 90; i++) {
            shootArrow.act(hero, monster);
        }
        
        // Next shot after cooldown - should hit again
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(2)).notify("HERO_HIT_BY_ARROW", null);
    }
} 