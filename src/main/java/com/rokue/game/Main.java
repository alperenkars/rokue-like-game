package com.rokue.game;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.Rune;
import com.rokue.game.events.EventManager;
import com.rokue.game.input.GUIInputProvider;
import com.rokue.game.render.IRenderer;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;
import com.rokue.ui.PlayModeUI;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Rokue-like Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);

            Hall hall = new Hall("Test Hall", 5,5);
            Rune rune = new Rune(new Position(2,2));
            hall.setRune(rune);

            EventManager eventManager = new EventManager();
            Hero hero = new Hero(new Position(0,0), eventManager);
            List<Hall> halls = Arrays.asList(hall);
            PlayMode playMode = new PlayMode(halls, hero, eventManager);

            PlayModeUI playModeUI = new PlayModeUI(playMode);
            frame.add(playModeUI);

            GUIInputProvider inputProvider = new GUIInputProvider();

            GameSystem gameSystem = new GameSystem(inputProvider, playModeUI);
            gameSystem.setState(playMode);

            frame.setVisible(true);

            int delay = 100;
            new Timer(delay, e -> {
                gameSystem.update();
                gameSystem.render();
            }).start();
        });
    }
}