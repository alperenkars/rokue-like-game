package com.rokue.game.states;

import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.Hall;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuildModeTest {
    private BuildMode buildMode;

    @BeforeEach
    public void setUp() {
        EventManager eventManager = new EventManager();
        buildMode = new BuildMode(eventManager);
    }

    @Test
    public void testAllHallsSatisfied() {
        // Satisfy all halls
        for (Hall hall : buildMode.getHalls()) {
            for (int i = 0; i < hall.getMinObjectRequirement(); i++) {
                DungeonObject obj = new DungeonObject("testObject", "testPath", 1, 1);
                hall.addObject(obj, new Position(i, 0));
            }
        }
        assertTrue(buildMode.areAllHallsSatisfied(), "All halls should be satisfied.");
    }

    @Test
    public void testNotAllHallsSatisfied() {
        // Satisfy only some halls
        List<Hall> halls = buildMode.getHalls();
        Hall firstHall = halls.get(0);
        for (int i = 0; i < firstHall.getMinObjectRequirement(); i++) {
            DungeonObject obj = new DungeonObject("testObject", "testPath", 1, 1);
            firstHall.addObject(obj, new Position(i, 0));
        }

        assertFalse(buildMode.areAllHallsSatisfied(), "Not all halls are satisfied.");
    }

    @Test
    public void testNoHallsSatisfied() {
        assertFalse(buildMode.areAllHallsSatisfied(), "No halls should be satisfied initially.");
    }
}
