package com.rokue.ui;
import com.rokue.game.states.MainMenu;
import static org.junit.jupiter.api.Assertions.*;
import com.rokue.game.events.EventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * tests for animated background, rotating momo and trailing tail
 */
class MainMenuUITest {

    private MainMenuUI mainMenuUI;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    @BeforeEach
    void setUp() throws InterruptedException {
        // to start swing operations before test
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            // instance of mainmenu to test
            mainMenuUI = new TestableMainMenuUI(new MainMenu(new EventManager()));
            mainMenuUI.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            latch.countDown();
        });
        latch.await(3, TimeUnit.SECONDS);
    }

    /**
     * testable version with minor modifications
     */
    private class TestableMainMenuUI extends MainMenuUI {
        // Provide a constructor that forwards to the parent constructor
        public TestableMainMenuUI(MainMenu mainMenu) {
            super(mainMenu); 
        
        }

        public AnimatedBackgroundPanel getAnimatedPanel() {
            return super.getAnimatedPanel(); 
        }
    }

    @Test
    void testAnimation() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            //should be interactable
            MainMenuUI.AnimatedBackgroundPanel panel = ((TestableMainMenuUI) mainMenuUI).getAnimatedPanel();

            // get initial position
            Point initialPos = panel.getCurrentPosition(); 
            panel.updatePosition(); // Step the animation
            Point afterPos = panel.getCurrentPosition(); 

            // position change test
            assertNotEquals(initialPos, afterPos, "position should change after update");

            latch.countDown();
        });
        latch.await(3, TimeUnit.SECONDS);
    }

    @Test
    void testTrailLength() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            MainMenuUI.AnimatedBackgroundPanel panel = ((TestableMainMenuUI) mainMenuUI).getAnimatedPanel();
            int maxLength = panel.getMaxTrailLength();

            // test max trail length and if it follows momo
            for (int i = 0; i < maxLength + 5; i++) {
                panel.updatePosition();
            }

            int actualTrailSize = panel.getTrailSize();
            assertEquals(maxLength, actualTrailSize, "trail should maintain max length");

            latch.countDown();
        });
        latch.await(3, TimeUnit.SECONDS);
    }

    @Test
    void testBoundaryBehavior() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            MainMenuUI.AnimatedBackgroundPanel panel = ((TestableMainMenuUI) mainMenuUI).getAnimatedPanel();

            // force the panel to surpass the right boundary
            
            panel.setCurrentX(WINDOW_WIDTH + 10);

            panel.updatePosition();
            Point currentPos = panel.getCurrentPosition();

            // it should bounce or reverse direction, so x should be within window
            assertTrue(currentPos.x < WINDOW_WIDTH, "should bounce from the window boundary");

            latch.countDown();
        });
        latch.await(3, TimeUnit.SECONDS);
    }
}