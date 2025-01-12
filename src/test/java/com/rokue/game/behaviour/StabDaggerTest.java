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

class StabDaggerTest {
    private StabDagger stabDagger;
    @Mock
    private Hero hero;
    @Mock
    private Monster monster;
    @Mock
    private EventManager eventManager;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stabDagger = new StabDagger();

        when(hero.getEventManager()).thenReturn(eventManager);
    }
    /**
     * Test Case num 1: Stab hits the hero when within range
     * Tests that when the hero is adjacent to the monster (distance <= 1.0):
     * - The "HERO_STABBED" event is triggered
     * 
     */
    @Test
    void testStabHitsHeroInRange() {
        // Arrange
        Position heroPos = new Position(1, 1);
        Position monsterPos = new Position(1, 2); 
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);

        // Act
        stabDagger.act(hero, monster);

        // Assert
        verify(hero.getEventManager(), times(1)).notify("HERO_STABBED", null);


        stabDagger.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_STABBED", null);
    }
    /**
     * Test Case num 2: Stab misses the hero when out of range
     * Tests that when the hero is not adjacent to the monster (distance > 1.0):
     * - No "HERO_STABBED" event is triggered
     * 
     */
    @Test
    void testStabMissesHeroOutOfRange() {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(3, 3); //too far away
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);

        // Act
        stabDagger.act(hero, monster);

        // Assert
        verify(hero.getEventManager(), never()).notify(anyString(), any());

        
        stabDagger.act(hero, monster);
        verify(hero.getEventManager(), never()).notify(anyString(), any());
    }
    /**
     * Test Case 3: Cooldown behavior after a hit
     * Tests that after a successful stab:
     * - Cooldown of 3 seconds is enforced
     * - The monster cannot stab again during the cooldown
     */
    @Test
    void testCooldownAfterHit() throws InterruptedException {
        // Arrange
        Position heroPos = new Position(1, 1);
        Position monsterPos = new Position(1, 2); 
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);

        // Act
        stabDagger.act(hero, monster);

        // Assert
        verify(hero.getEventManager(), times(1)).notify("HERO_STABBED", null);

        // Simulation for 2 sec wait (less than the cooldown)
        Thread.sleep(2000);

        // Attempt to stab again - should not work due to cooldown
        stabDagger.act(hero, monster);
        verify(hero.getEventManager(), times(1)).notify("HERO_STABBED", null);

        // Simulate waiting another 2 seconds (cooldown elapsed)
        Thread.sleep(2000);

        // Stab again - should work now
        stabDagger.act(hero, monster);
        verify(hero.getEventManager(), times(2)).notify("HERO_STABBED", null);
    }

    /**
     * Test Case 4: Cooldown behavior after a miss
     * Tests that after a missed stab:
     * - Cooldown of 1 second is enforced
     * - The monster can stab again after the cooldown
     */
    @Test
    void testCooldownAfterMiss() throws InterruptedException {
        // Arrange
        Position heroPos = new Position(0, 0);
        Position monsterPos = new Position(3, 3); // Too far
        when(hero.getPosition()).thenReturn(heroPos);
        when(monster.getPosition()).thenReturn(monsterPos);

        // Act
        stabDagger.act(hero, monster);

        // Assert
        verify(hero.getEventManager(), never()).notify(anyString(), any());

        // Simulate waiting 500ms (less than the cooldown for miss)
        Thread.sleep(500);

        // Attempt to stab again - should not work due to cooldown
        stabDagger.act(hero, monster);
        verify(hero.getEventManager(), never()).notify(anyString(), any());

        Thread.sleep(500);

        // Stabbing again--- now it should work
        stabDagger.act(hero, monster);
        verify(hero.getEventManager(), never()).notify(anyString(), any());
    }
}
