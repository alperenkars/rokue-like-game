package com.rokue.ui;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rokue.game.states.PlayMode;

public class PlayModeUITest {
    private PlayModeUI playModeUI;
    private PlayMode playMode;
    private JFrame gameWindow;

    @BeforeEach
    public void setUp() {
        playMode = mock(PlayMode.class);
        gameWindow = mock(JFrame.class);
        
        // Mock EventManager
        com.rokue.game.events.EventManager eventManager = mock(com.rokue.game.events.EventManager.class);
        when(playMode.getEventManager()).thenReturn(eventManager);
        
        when(gameWindow.getKeyListeners()).thenReturn(new KeyListener[0]);
        
        playModeUI = new PlayModeUI(playMode, gameWindow);
    }

    /**
     * Test 1: Single button click pauses the game correctly.
     *
     * Requires:
     * - The PlayModeUI instance must be properly initialized.
     * - The pause button coordinates must be correctly calculated.
     *
     * Modifies:
     * - The state of the PlayMode instance.
     *
     * Effects:
     * - Simulates a mouse click on the pause button.
     * - Verifies that the game is paused and resumed correctly.
     */

    @Test
    public void testSingleButtonClickPausesGame() {
        int buttonX = playModeUI.getWidth() - 40 - 20;
        int buttonY = 20;

        MouseEvent pressEvent = new MouseEvent(playModeUI, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, buttonX + 1, buttonY + 1, 1, false);
        playModeUI.mousePressed(pressEvent);

        verify(playMode).pause();
        assertTrue(playModeUI.isPaused());

        playModeUI.mousePressed(pressEvent);

        verify(playMode).resume();
        assertFalse(playModeUI.isPaused());
    }

    /**
     * Test 2: Multiple button clicks in quick succession.
     *
     * Requires:
     * - The PlayModeUI instance must be properly initialized.
     * - The pause button coordinates must be correctly calculated.
     *
     * Modifies:
     * - The state of the PlayMode instance.
     *
     * Effects:
     * - Simulates multiple mouse clicks on the pause button.
     * - Verifies that the game is paused and resumed correctly.
     */

    @Test
    public void testMultipleButtonClicks() {
        int buttonX = playModeUI.getWidth() - 40 - 20;
        int buttonY = 20;

        MouseEvent pressEvent = new MouseEvent(playModeUI, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, buttonX + 1, buttonY + 1, 1, false);
        playModeUI.mousePressed(pressEvent);
        playModeUI.mousePressed(pressEvent);
        playModeUI.mousePressed(pressEvent);

        verify(playMode, times(2)).pause();
        verify(playMode, times(1)).resume();
    }

    /**
     * Test 3: Clicking when the game is already paused or in an invalid state.
     *
     * Requires:
     * - The PlayModeUI instance must be properly initialized.
     * - The pause button coordinates must be correctly calculated.
     *
     * Modifies:
     * - The state of the PlayMode instance.
     *
     * Effects:
     * - Simulates mouse clicks on the pause button when the game is already paused.
     * - Verifies that the game handles these scenarios correctly.
     */
    @Test
    public void testClickWhenGameAlreadyPaused() {
        int buttonX = playModeUI.getWidth() - 40 - 20;
        int buttonY = 20;

        MouseEvent pressEvent = new MouseEvent(playModeUI, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, buttonX + 1, buttonY + 1, 1, false);
        playModeUI.mousePressed(pressEvent);

        verify(playMode).pause();
        assertTrue(playModeUI.isPaused());

        playModeUI.mousePressed(pressEvent);

        verify(playMode).resume();
        assertFalse(playModeUI.isPaused());

        playModeUI.mousePressed(pressEvent);

        verify(playMode, times(2)).pause();
        assertTrue(playModeUI.isPaused());
    }
}