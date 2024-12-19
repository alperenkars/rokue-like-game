package com.rokue.game.input;

import com.rokue.game.actions.IAction;
import com.rokue.game.actions.MoveAction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GUIInputProvider extends KeyAdapter implements IInputProvider {
    private List<IAction> actions = new ArrayList<>();
    private boolean isPaused = false;


    @Override
    public void keyPressed(KeyEvent e) {

        if (isPaused) {
            return; // Ignore input while paused
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                actions.add(new MoveAction(MoveAction.Direction.UP));
                break;
            case KeyEvent.VK_DOWN:
                actions.add(new MoveAction(MoveAction.Direction.DOWN));
                break;
            case KeyEvent.VK_LEFT:
                actions.add(new MoveAction(MoveAction.Direction.LEFT));
                break;
            case KeyEvent.VK_RIGHT:
                actions.add(new MoveAction(MoveAction.Direction.RIGHT));
                break;
            default:
                // Şimdilik enchantment inputlarını atlıyorum.
        }
    }

    @Override
    public List<IAction> pollActions() {
        List<IAction> currentActions = new ArrayList<>(actions);
        actions.clear();
        return currentActions;
    }

    //for pause
    public void clearActions() {
        actions.clear();
    }

    public void pause() {
        isPaused = true;
        actions.clear(); // Clear pending actions
    }

    public void resume() {
        isPaused = false;
    }
}