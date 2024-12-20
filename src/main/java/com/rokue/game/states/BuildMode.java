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

    public boolean areAllHallsSatisfied() {
        for (Hall hall : halls) {
            if (!hall.isRequirementMet()) {
                return false;
            }
        }
        return true;
    }

}
