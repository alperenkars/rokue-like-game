package com.rokue.test;

import com.rokue.game.entities.Hall;
import com.rokue.game.states.BuildMode;
import com.rokue.ui.BuildModeUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuildModeTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            List<Hall> mockHalls = createMockHalls();
            BuildMode buildMode = new BuildMode(mockHalls);
            BuildModeUI buildModeUI = new BuildModeUI(buildMode);

            JFrame frame = new JFrame("Build Mode Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(buildModeUI, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private static List<Hall> createMockHalls() {
        List<Hall> halls = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Hall hall = new Hall("Test Hall " + (i + 1), 20, 20);
            halls.add(hall);
        }
        return halls;
    }
}
