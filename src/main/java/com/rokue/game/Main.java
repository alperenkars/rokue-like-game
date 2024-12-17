package com.rokue.game;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.events.EventManager;
import com.rokue.game.input.GUIInputProvider;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;
import com.rokue.ui.PlayModeUI;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create hall and rune
            Hall hall = new Hall("Test Hall",5,5);
            Rune rune = new Rune(new Position(2,2));
            hall.setRune(rune);

            EventManager eventManager = new EventManager();
            Hero hero = new Hero(new Position(2,2), eventManager); // center of 5x5

            List<Hall> halls = Arrays.asList(hall);
            PlayMode playMode = new PlayMode(halls, hero, eventManager);

            PlayModeUI playModeUI = new PlayModeUI(playMode);

            GUIInputProvider inputProvider = new GUIInputProvider();

            GameSystem gameSystem = new GameSystem(inputProvider, playModeUI);
            gameSystem.setState(playMode);

            JFrame frame = new JFrame("Rokue-like Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addKeyListener(inputProvider);

            frame.setSize(872, 662);
            frame.setResizable(false);

            frame.add(playModeUI);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Swing timer for game loop
            int delay=1000; // 1 second per update
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
}