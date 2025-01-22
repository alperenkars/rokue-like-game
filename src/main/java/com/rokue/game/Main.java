package com.rokue.game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.rokue.game.events.EventManager;
import com.rokue.game.input.GUIInputProvider;
import com.rokue.game.states.GameState;
import com.rokue.game.states.MainMenu;
import com.rokue.game.states.PlayMode;
import com.rokue.ui.MainMenuUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Setup window
            JFrame gameWindow = new JFrame("Rokue-like Game");
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setSize(900, 800);
            gameWindow.setResizable(false);
            
            // Setup core systems
            EventManager eventManager = new EventManager();
            GameSystem gameSystem = new GameSystem(gameWindow, eventManager);
            
            // Setup input
            GUIInputProvider inputProvider = new GUIInputProvider();
            gameWindow.addKeyListener(inputProvider);
            gameSystem.setInputProvider(inputProvider);

            // Initial state
            MainMenu mainMenu = new MainMenu(eventManager);
            MainMenuUI mainMenuUI = new MainMenuUI(mainMenu);
            gameSystem.transitionTo(mainMenu, mainMenuUI);

            // Show window
            gameWindow.setLocationRelativeTo(null);
            gameWindow.setVisible(true);

            // Start game loop
            new Timer(16, e -> {
                GameState currentState = gameSystem.getCurrentState();
                boolean isPaused = false;

                if (currentState instanceof PlayMode) {
                    PlayMode currentPlayMode = (PlayMode) currentState;
                    isPaused = currentPlayMode.isPaused();

                    // Clear inputs when transitioning to paused state
                    if (isPaused) {
                        inputProvider.clearActions();
                    }
                }

                if (!isPaused) {
                    var actions = inputProvider.pollActions();
                    if (currentState instanceof PlayMode) {
                        ((PlayMode)currentState).handleActions(actions);
                    }
                    gameSystem.update();
                }
                gameSystem.render();
            }).start();
        });
    }
}