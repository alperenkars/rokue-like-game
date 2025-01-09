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
     * Test Case 1: Successful hit followed by 3-second cooldown
     * Tests that when the hero is within range:
     * - The HERO_HIT_BY_ARROW event is triggered
     * - A 3-second cooldown is enforced
     */
    @Test
    void testSuccessfulHitCooldown() throws InterruptedException {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(2, 2);  // Distance ≈ 2.8 units
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);
        
        // Act - First shot (hit)
        shootArrow.act(hero, monster);
        
        // Assert - Event triggered
        verify(hero.getEventManager()).notify("HERO_HIT_BY_ARROW", null);
        
        // Try to shoot again after 2 seconds (should fail due to 3s cooldown)
        Thread.sleep(2000);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Wait another 1.5 seconds (total 3.5s, should allow next shot)
        Thread.sleep(1500);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(2)).notify("HERO_HIT_BY_ARROW", null);
    }
    
    /**
     * Test Case 2: Miss followed by 1-second cooldown
     * Tests that when the hero is out of range:
     * - No event is triggered
     * - Only 1-second cooldown is applied
     */
    @Test
    void testMissCooldown() throws InterruptedException {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(5, 5);  // Distance = 7.07 units
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);
        
        // Act - First shot (miss)
        shootArrow.act(hero, monster);
        
        // Assert - No event triggered
        verify(hero.getEventManager(), never()).notify(anyString(), any());
        
        // Try to shoot again after 0.5 seconds (should fail due to 1s cooldown)
        Thread.sleep(500);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), never()).notify(anyString(), any());
        
        // Wait another 0.7 seconds (total 1.2s, should allow next attempt)
        Thread.sleep(700);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), never()).notify(anyString(), any());
    }
    
    /**
     * Test Case 3: Hit-Miss-Hit sequence
     * Tests the transition between hit and miss cooldowns:
     * - Hit (3s cooldown)
     * - Miss (1s cooldown)
     * - Hit again
     */
    @Test
    void testHitMissHitSequence() throws InterruptedException {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position closePos = new Position(2, 2);  // Distance ≈ 2.8 units
        Position farPos = new Position(5, 5);    // Distance = 7.07 units
        when(hero.getPosition()).thenReturn(heroPos);
        
        // First shot - Hit
        when(monster.getPosition()).thenReturn(closePos);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Wait 3.5 seconds (clear hit cooldown)
        Thread.sleep(3500);
        
        // Second shot - Miss
        when(monster.getPosition()).thenReturn(farPos);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_HIT_BY_ARROW", null);
        
        // Wait 1.2 seconds (clear miss cooldown)
        Thread.sleep(1200);
        
        // Third shot - Hit
        when(monster.getPosition()).thenReturn(closePos);
        shootArrow.act(hero, monster);
        verify(hero.getEventManager(), times(2)).notify("HERO_HIT_BY_ARROW", null);
    }
} 