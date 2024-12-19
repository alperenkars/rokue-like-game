package com.rokue.game;

import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.events.EventManager;
import com.rokue.game.input.GUIInputProvider;
import com.rokue.game.states.GameState;
import com.rokue.game.states.MainMenu;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;
import com.rokue.ui.MainMenuUI;
import com.rokue.ui.PlayModeUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EventManager eventManager = new EventManager();

            MainMenu mainMenu = new MainMenu(eventManager);
            PlayMode playMode = createPlayMode(eventManager);

            MainMenuUI mainMenuUI = new MainMenuUI(mainMenu);
            PlayModeUI playModeUI = new PlayModeUI(playMode);

            GUIInputProvider inputProvider = new GUIInputProvider();

            GameSystem gameSystem = new GameSystem(inputProvider, playModeUI);
            
            // Setup main window
            JFrame frame = new JFrame("Rokue-like Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addKeyListener(inputProvider);
            frame.setSize(872, 662);
            frame.setResizable(false);

            eventManager.subscribe("GAME_COMPLETED", (eventType, data) -> {
                gameSystem.setState(mainMenu);
                frame.getContentPane().removeAll();
                frame.add(mainMenuUI);
                frame.revalidate();
                frame.repaint();
            });

            eventManager.subscribe("START_GAME", (eventType, data) -> {
                // Create a new PlayMode instance
                PlayMode newPlayMode = createPlayMode(eventManager);
                PlayModeUI newPlayModeUI = new PlayModeUI(newPlayMode);
                gameSystem.setRenderer(newPlayModeUI);
                gameSystem.setState(newPlayMode);
                
                frame.getContentPane().removeAll();
                frame.add(newPlayModeUI);
                frame.revalidate();
                frame.repaint();

                for (KeyListener kl : frame.getKeyListeners()) {
                    frame.removeKeyListener(kl);
                }

                frame.addKeyListener(inputProvider);
                frame.setFocusable(true);
                frame.requestFocusInWindow();
            });

            // Set initial state and screen
            gameSystem.setState(mainMenu);
            frame.add(mainMenuUI);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Start game loop
            int delay = 16; // approximately 60 FPS
            new Timer(delay, e -> {
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
                
                // Always render
                gameSystem.render();
            }).start();
        });
    }

    private static PlayMode createPlayMode(EventManager eventManager) {
        Hall hall = new Hall("Test Hall", 20, 20);
        Rune rune = new Rune(new Position(2, 2));
        hall.setRune(rune);

        Hero hero = new Hero(new Position(0, 2), eventManager);
        List<Hall> halls = Arrays.asList(hall);
        
        return new PlayMode(halls, hero, eventManager);
    }
}