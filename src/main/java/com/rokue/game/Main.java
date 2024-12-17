package com.rokue.game;

import java.util.Arrays;
import java.util.List;
import javax.swing.*;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.events.EventManager;
import com.rokue.game.input.GUIInputProvider;
import com.rokue.game.states.PlayMode;
import com.rokue.game.states.MainMenu;
import com.rokue.game.util.Position;
import com.rokue.ui.PlayModeUI;
import com.rokue.ui.MainMenuUI;

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
                gameSystem.setState(playMode);
                frame.getContentPane().removeAll();
                frame.add(playModeUI);
                frame.revalidate();
                frame.repaint();
            });

            // Set initial state and screen
            gameSystem.setState(mainMenu);
            frame.add(mainMenuUI);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Start game loop
            int delay = 16; // approximately 60 FPS
            new Timer(delay, e -> {
                var actions = inputProvider.pollActions();
                if(gameSystem.getCurrentState() instanceof PlayMode) {
                    ((PlayMode)gameSystem.getCurrentState()).handleActions(actions);
                }

                gameSystem.update();
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