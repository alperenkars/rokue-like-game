package com.rokue.game.states;

import java.util.ArrayList;
import java.util.List;

import com.rokue.game.GameSystem;
import com.rokue.game.entities.DungeonObject;
import com.rokue.game.entities.Hall;
import com.rokue.game.util.Position;

public class BuildMode implements GameState {

    private List<Hall> halls; // List of halls to design
    private Hall currentHall; // Currently active hall
    private DungeonObject selectedObject; // Object being placed

    public BuildMode(List<Hall> halls) {
        this.halls = halls;
        this.currentHall = halls.get(0); // Start with the first hall
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

}
