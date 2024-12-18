package com.rokue.game.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rokue.game.GameSystem;
import com.rokue.game.GameTimer;
import com.rokue.game.actions.IAction;
import com.rokue.game.actions.MoveAction;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.entities.monsters.FighterMonster;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;

public class PlayMode implements GameState {

    private Hall currentHall;
    private List<Hall> halls;
    private Hero hero;
    private GameTimer gameTimer;
    private EventManager eventManager;
    private List<FighterMonster> fighterMonsters;
    public static final int START_TIME = 60;
    public static final Position START_POSITION = new Position(0, 0);


    public PlayMode(List<Hall> halls, Hero hero, EventManager eventManager) {
        this.halls = halls;
        this.currentHall = halls.get(0);
        this.hero = hero;
        this.eventManager = eventManager;
        this.fighterMonsters = new ArrayList<>();
        initializeMonsters();
    }

    private void initializeMonsters() {
        Random random = new Random();
        int x = random.nextInt(currentHall.getWidth());
        int y = random.nextInt(currentHall.getHeight());
        fighterMonsters.add(new FighterMonster(new Position(x, y), hero));
    }
    
    public void enter(GameSystem system) {
        System.out.println("Entering Play Mode");
        this.gameTimer = new GameTimer(eventManager);
        this.gameTimer.start(PlayMode.START_TIME);

        eventManager.subscribe("RUNE_COLLECTED", (eventType, data) -> onRuneCollected());
        eventManager.subscribe("DISTRACTION", (eventType, data) -> onDistraction((Position) data));
    }

    public void update(GameSystem system) {
        currentHall.update(hero);
        for (FighterMonster monster : fighterMonsters) {
            monster.update(hero, currentHall);
        }
    }

    public void exit(GameSystem system) {
        System.out.println("Exiting Play Mode");
        if (gameTimer != null) {
            gameTimer.stop();
        }
        eventManager.unsubscribe("RUNE_COLLECTED", null);
        eventManager.unsubscribe("DISTRACTION", null);
    }

    public void handleActions(List<IAction> actions) {
        for (IAction action : actions) {
            if (action instanceof MoveAction) {
                MoveAction moveAction = (MoveAction) action;
                this.hero.move(moveAction.getDirection(), this.currentHall);
            }
        }
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

    private void onDistraction(Position gemPosition) {
        for (FighterMonster monster : fighterMonsters) {
            monster.reactToLuringGem(gemPosition);
        }
    }

    public Hall getCurrentHall() {
        return currentHall;
    }

    public Hero getHero() {
        return hero;
    }

    public List<FighterMonster> getFighterMonsters() {
        return fighterMonsters;
    }

    public int getRemainingTime() {
        return gameTimer.getRemainingTime();
    }
}
