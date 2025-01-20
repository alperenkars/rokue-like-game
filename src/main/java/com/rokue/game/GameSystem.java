package com.rokue.game;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.events.EventManager;
import com.rokue.game.input.IInputProvider;
import com.rokue.game.render.IRenderer;
import com.rokue.game.states.BuildMode;
import com.rokue.game.states.GameSaveData;
import com.rokue.game.states.GameState;
import com.rokue.game.states.MainMenu;
import com.rokue.game.states.PlayMode;
import com.rokue.ui.BuildModeUI;
import com.rokue.ui.MainMenuUI;
import com.rokue.ui.PlayModeUI;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameSystem {
    private GameState currentState;
    private IRenderer renderer;
    private boolean isRunning;
    private IInputProvider inputProvider;
    private EventManager eventManager;
    private JFrame gameWindow;

    public GameSystem(JFrame gameWindow, EventManager eventManager) {
        this.isRunning = true;
        this.gameWindow = gameWindow;
        this.eventManager = eventManager;
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        eventManager.subscribe("START_GAME", (eventType, data) -> {
            BuildMode buildMode = new BuildMode(eventManager);
            BuildModeUI buildModeUI = new BuildModeUI(buildMode);
            transitionTo(buildMode, buildModeUI);
        });

        eventManager.subscribe("SWITCH_TO_PLAY_MODE", (eventType, data) -> {
            List<Hall> halls = (List<Hall>) data;
            Hero hero = new Hero(PlayMode.START_POSITION, eventManager, new ArrayList<>());
            PlayMode playMode = new PlayMode(halls, hero, eventManager);
            PlayModeUI playModeUI = new PlayModeUI(playMode, gameWindow);
            transitionTo(playMode, playModeUI);
        });

        eventManager.subscribe("GAME_COMPLETED", (eventType, data) -> {
            MainMenu mainMenu = new MainMenu(eventManager);
            MainMenuUI mainMenuUI = new MainMenuUI(mainMenu);
            transitionTo(mainMenu, mainMenuUI);
        });

        eventManager.subscribe("TIME_EXPIRED", (eventType, data) -> {
            MainMenu mainMenu = new MainMenu(eventManager);
            MainMenuUI mainMenuUI = new MainMenuUI(mainMenu);
            transitionTo(mainMenu, mainMenuUI);
        });

        eventManager.subscribe("GAME_OVER", (eventType, data) -> {
            MainMenu mainMenu = new MainMenu(eventManager);
            MainMenuUI mainMenuUI = new MainMenuUI(mainMenu);
            transitionTo(mainMenu, mainMenuUI);
        });
    }

    public void transitionTo(GameState newState, JPanel newUI) {
        if (currentState != null) {
            currentState.exit(this);
        }
        
        currentState = newState;
        renderer = (IRenderer) newUI;
        currentState.enter(this);

        gameWindow.getContentPane().removeAll();
        gameWindow.add(newUI);
        gameWindow.revalidate();
        gameWindow.repaint();
        
        // Request focus for the new UI panel
        newUI.requestFocusInWindow();
        // Make the panel focusable
        newUI.setFocusable(true);
    }

    public void update() {
        if (isRunning &&  currentState != null) {
            currentState.update(this);
        }
    }

    public void setState(GameState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = newState;
        if (currentState != null) {
            currentState.enter(this);
        }
    }

    public void render() {
        if (currentState != null && renderer != null) {
            renderer.render(currentState);
        }
    }

    public void setInputProvider(IInputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    public void stop() {
        isRunning = false;
        System.out.println("Game Stopped");
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void saveGame(String filePath) {
    if (currentState instanceof PlayMode playMode) {
        // Create a GameSaveData object
        GameSaveData saveData = new GameSaveData(
            playMode.getHero(),
            playMode.getHalls(),
            playMode.getCurrentHallIndex(),
            (int) playMode.getRemainingTime()
        );

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(saveData);
            System.out.println("Game saved successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    } else {
        System.out.println("Cannot save game: Current state is not PlayMode.");
    }
}


public void loadGame(String filePath) {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
        // Read the saved data
        GameSaveData saveData = (GameSaveData) ois.readObject();

        // Restore PlayMode from GameSaveData
        PlayMode playMode = new PlayMode(
            saveData.getHalls(),
            saveData.getHero(),
            eventManager
        );
        playMode.setCurrentHall(saveData.getHalls().get(saveData.getCurrentHallIndex()));
        playMode.getGameTimer().start(saveData.getRemainingTime());

        // Transition to restored PlayMode
        PlayModeUI playModeUI = new PlayModeUI(playMode, gameWindow);
        transitionTo(playMode, playModeUI);

        System.out.println("Game loaded successfully from " + filePath);
    } catch (IOException | ClassNotFoundException e) {
        System.err.println("Error loading game: " + e.getMessage());
    }
}

    


}