package com.rokue.game.factories;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.monsters.ArcherMonster;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.events.EventManager;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;

class MonsterFactoryTest {
    private Hall testHall;
    private PlayMode mockPlayMode;
    private EventManager mockEventManager;

    @BeforeEach
    void setUp() {
        testHall = new Hall("TestHall", 10, 10, 1);
        mockEventManager = mock(EventManager.class);
        mockPlayMode = mock(PlayMode.class);
        when(mockPlayMode.getEventManager()).thenReturn(mockEventManager);
        when(mockPlayMode.getRemainingTime()).thenReturn(30); // Mock remaining time
    }

    @Test
    void testMonsterHasValidAttributes() {
        Monster monster = MonsterFactory.createRandomMonster(testHall, mockPlayMode);

        assertNotNull(monster, "Created monster should not be null.");
        assertTrue(monster instanceof ArcherMonster || monster instanceof FighterMonster || monster instanceof WizardMonster,
                "Monster should be one of ArcherMonster, FighterMonster, or WizardMonster.");
        assertNotNull(monster.getPosition(), "Monster should have a valid position.");
        assertTrue(testHall.isWithinBounds(monster.getPosition()), "Monster should spawn within the Hall boundaries.");
    }

    @Test
    void testRandomPositionBoundaryValues() {
        for (int i = 0; i < 100; i++) {
            Monster monster = MonsterFactory.createRandomMonster(testHall, mockPlayMode);
            Position position = monster.getPosition();

            assertTrue(position.getX() >= 0 && position.getX() < testHall.getWidth(),
                    "Monster's X position should be within the grid boundary.");
            assertTrue(position.getY() >= 0 && position.getY() < testHall.getHeight(),
                    "Monster's Y position should be within the grid boundary.");
        }
    }

    @Test
    void testMonsterTypeDiversity() {
        Set<Class<?>> monsterTypes = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            Monster monster = MonsterFactory.createRandomMonster(testHall, mockPlayMode);
            monsterTypes.add(monster.getClass());

            if (monsterTypes.contains(ArcherMonster.class) &&
                    monsterTypes.contains(FighterMonster.class) &&
                    monsterTypes.contains(WizardMonster.class)) {
                break;
            }
        }

        assertTrue(monsterTypes.contains(ArcherMonster.class), "ArcherMonster should be generated.");
        assertTrue(monsterTypes.contains(FighterMonster.class), "FighterMonster should be generated.");
        assertTrue(monsterTypes.contains(WizardMonster.class), "WizardMonster should be generated.");
    }

    @Test
    void testNoSpawnCollision() {
        for (int i = 0; i < 10; i++) {
            Monster monster = MonsterFactory.createRandomMonster(testHall, mockPlayMode);
            Position position = monster.getPosition();
            assertNotNull(testHall.getCell(position), "Monster should spawn in a valid cell.");
            assertNull(testHall.getCell(position).getContent(), "Monster should not spawn in an occupied cell.");
        }
    }
}
