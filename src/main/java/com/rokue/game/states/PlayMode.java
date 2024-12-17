package com.rokue.game.states;

import com.rokue.game.GameSystem;
import com.rokue.game.actions.IAction;
import com.rokue.game.actions.MoveAction;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.GameTimer;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;

import java.util.List;

public class PlayMode implements GameState {

    private Hall currentHall;
    private List<Hall> halls;
    private Hero hero;
    private GameTimer gameTimer;
    private EventManager eventManager;
    public static final int START_TIME = 60;
    public static final Position START_POSITION = new Position(0, 0);


    public PlayMode(List<Hall> halls, Hero hero, EventManager eventManager) {
        this.halls = halls;
        this.currentHall = halls.get(0);
        this.hero = hero;
        this.eventManager = eventManager;
    }

    public void enter(GameSystem system) {
        System.out.println("Entering Play Mode");
        this.gameTimer = new GameTimer(eventManager);
        this.gameTimer.start(PlayMode.START_TIME);

        eventManager.subscribe("TIMER_TICK", (eventType, data) -> onTimerTick((int) data));
        eventManager.subscribe("TIME_EXPIRED", (eventType, data) -> onTimeExpired());
        eventManager.subscribe("RUNE_COLLECTED", (eventType, data) -> onRuneCollected());
    }

    public void update(GameSystem system) {
        currentHall.update(hero);
    }

    public void exit(GameSystem system) {
        System.out.println("Exiting Play Mode");
        if (gameTimer != null) {
            gameTimer.stop();
        }
        eventManager.unsubscribe("TIMER_TICK", null);
        eventManager.unsubscribe("TIME_EXPIRED", null);
        eventManager.unsubscribe("RUNE_COLLECTED", null);
    }

    public void handleActions(List<IAction> actions) {
        for (IAction action : actions) {
            if (action instanceof MoveAction) {
                MoveAction moveAction = (MoveAction) action;
                this.hero.move(moveAction.getDirection(), this.currentHall);
            }
        }
    }

    private void onTimerTick(int remainingTime) {
        System.out.println("Time remaining: " + remainingTime + " seconds");
    }

    private void onTimeExpired() {
        System.out.println("Time's up! Game Over!");
    }

    private void onRuneCollected() {
        System.out.println("Rune collected!");

        int nextHallIndex = halls.indexOf(currentHall) + 1;
        if (nextHallIndex < halls.size()) {
            currentHall = halls.get(nextHallIndex);
            System.out.println("Moving to the next hall: " + currentHall.getName());
            gameTimer.start(PlayMode.START_TIME);
            hero.setPosition(PlayMode.START_POSITION);
        } else {
            eventManager.notify("GAME_COMPLETED", null);
            System.out.println("All halls completed. You win!");
        }
    }

    public Hall getCurrentHall() {
        return currentHall;
    }

    public Hero getHero() {
        return hero;
    }

    public int getRemainingTime() {
        return gameTimer.getRemainingTime();
    }
}
