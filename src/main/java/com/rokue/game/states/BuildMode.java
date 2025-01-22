package com.rokue.game.states;

import java.util.ArrayList;
import java.util.List;

import com.rokue.game.GameSystem;
import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.Hall;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;

public class BuildMode implements GameState {

    private List<Hall> halls; // List of halls to design
    private Hall currentHall; // Currently active hall
    private DungeonObject selectedObject; // Object being placed
    private EventManager eventManager;

    public BuildMode(EventManager eventManager) {
        this.eventManager = eventManager;
        this.halls = new ArrayList<>();

        // Adding halls with their distinct names and minimum object requirements
        halls.add(new Hall("Earth Hall", 20, 20, 1));   // Earth Hall: min 6 objects
        halls.add(new Hall("Air Hall", 20, 20, 1));    // Air Hall: min 9 objects
        halls.add(new Hall("Water Hall", 20, 20, 1)); // Water Hall: min 13 objects
        halls.add(new Hall("Fire Hall", 20, 20, 1));  // Fire Hall: min 17 objects

        currentHall = halls.get(0);

    }


    @Override
    public void enter(GameSystem system) {
        System.out.println("Entering Build Mode");
    }

    @Override
    public void update(GameSystem system) {
        // No automatic updates in Build Mode; this would handle state changes as needed
        System.out.println("Updating Build Mode");
    }

    @Override
    public void exit(GameSystem system) {
        System.out.println("Exiting Build Mode");
    }

    public Hall getCurrentHall() {
        return currentHall;
    }

    public void switchToNextHall() {
        int currentIndex = halls.indexOf(currentHall);
        if (currentIndex < halls.size() - 1) {
            currentHall = halls.get(currentIndex + 1);
            System.out.println("Switched to next hall: " + currentHall.getName());
        } else {
            System.out.println("This is the last hall.");
        }
    }

    public void switchToPreviousHall() {
        int currentIndex = halls.indexOf(currentHall);
        if (currentIndex > 0) {
            currentHall = halls.get(currentIndex - 1);
            System.out.println("Switched to previous hall: " + currentHall.getName());
        } else {
            System.out.println("This is the first hall.");
        }
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public boolean addObjectToCurrentHall(DungeonObject object, Position position) {
        Hall hall = getCurrentHall();

        // Check if the cell is occupied
        if (hall.isCellOccupied(position)) {
            return false;
        }

        // Add object to the hall
        object.setPosition(position);
        hall.addObject(object, position);
        return true;
    }

    /**
     * Returns the size of an object based on its name.
     *
     * Requires:
     * - `name` must be a valid string representing the object name.
     *
     * Effects:
     * - Returns an array where the first element is the width in cells
     *   and the second element is the height in cells.
     */
    private int[] getObjectSize(String name) {
        switch (name.toLowerCase()) {
            case "pillar":
            case "torch":
            case "crate":
                return new int[]{1, 2}; // 1 cell wide, 2 cells tall
            default:
                return new int[]{1, 1}; // Default size: 1x1
        }
    }

    public void randomlyFillCurrentHall() {
        Hall hall = getCurrentHall();

        // Clear current objects
        hall.clearObjects();

        int minObjects = hall.getMinObjectRequirement();
        List<String> objectNames = List.of("pillar", "hole", "box", "crate", "torch", "skull", "chest", "potion");

        int attempts = 0; // To prevent infinite loops
        for (int i = 0; i < minObjects; ) {
            String name = objectNames.get((int) (Math.random() * objectNames.size()));
            int[] size = getObjectSize(name);

            Position position = null;
            boolean validPosition = false;

            while (!validPosition && attempts < 10000) {
                attempts++;
                int x = (int) (Math.random() * hall.getWidth());
                int y = (int) (Math.random() * hall.getHeight());
                position = new Position(x, y);

                // Create a temporary object for placement check
                DungeonObject tempObject = new DungeonObject(name, "src/main/resources/assets/" + name + ".png", size[0], size[1]);
                validPosition = hall.canPlaceObject(tempObject, position);
            }

            if (validPosition) {
                DungeonObject object = new DungeonObject(name, "src/main/resources/assets/" + name + ".png", size[0], size[1]);
                hall.addObject(object, position);
                i++; // Increment only when an object is successfully added
            } else {
                System.out.println("Could not place object after several attempts.");
                break;
            }
        }

        System.out.println("Randomly filled " + hall.getName() + " with " + hall.getObjects().size() + " objects.");
    }


    /**
     * Clears all objects from the current hall.
     *
     * Requires:
     * - The current hall must be initialized.
     *
     * Modifies:
     * - Removes all objects from the current hall.
     *
     * Effects:
     * - Leaves the current hall with no objects.
     */
    public void clearCurrentHallObjects() {
        Hall hall = getCurrentHall();
        hall.clearObjects();
    }


    /**
     * Checks if all halls meet their minimum object requirements.
     *
     * Requires:
     * - The `halls` list must not be null.
     * - Each hall in the `halls` list must be properly initialized with a minimum object requirement.
     *
     * Modifies:
     * - Does not modify any fields or objects.
     *
     * Effects:
     * - Returns `true` if all halls in the `halls` list satisfy their minimum object requirements.
     * - Returns `false` if at least one hall does not meet its requirement.
     **/
    public boolean areAllHallsSatisfied() {
        for (Hall hall : halls) {
            if (!hall.isRequirementMet()) {
                return false;
            }
        }
        return true;
    }

}
