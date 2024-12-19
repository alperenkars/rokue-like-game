package com.rokue.game;

import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.rokue.game.states.BuildMode;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.states.PlayMode;
import com.rokue.game.entities.Rune;
import com.rokue.game.events.EventManager;
import com.rokue.game.input.GUIInputProvider;
import com.rokue.game.states.MainMenu;
import com.rokue.game.util.Position;
import com.rokue.ui.BuildModeUI;
import com.rokue.ui.MainMenuUI;
import com.rokue.ui.PlayModeUI;

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
                var actions = inputProvider.pollActions();
                if (gameSystem.getCurrentState() instanceof PlayMode) {
                    ((PlayMode)gameSystem.getCurrentState()).handleActions(actions);
                }
                gameSystem.update();
                gameSystem.render();
            }).start();
        });
    }
}