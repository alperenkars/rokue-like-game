package com.rokue.game;

import com.rokue.game.events.EventManager;
import java.util.Timer;
import java.util.TimerTask;

public class GameTimer {

    private static final int TICK_INTERVAL = 1000; // Timer ticks every second
    private int remainingTime;
    private boolean isPaused=false;
    private Timer timer;
    private EventManager eventManager;

    public GameTimer(EventManager eventManager) {
        this.eventManager = eventManager;
        this.remainingTime = 0;
        this.isPaused = false;
        this.timer = new Timer();

        eventManager.subscribe("ADD_TIME", (eventType, data) -> {
            int timeToAdd = (int) data;
            addTime(timeToAdd);
            System.out.println("Added " + timeToAdd + " seconds to the timer.");
        });
    }

    public void start(int initialTime) {
        this.remainingTime = initialTime;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    tick();
                }
            }
        }, 0, TICK_INTERVAL);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void addTime(int seconds) {
        remainingTime += seconds;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    private void tick() {
        if (!isPaused &&remainingTime > 0) {
            remainingTime--;
            eventManager.notify("TIMER_TICK", remainingTime);
        } else if (!isPaused && remainingTime <= 0) {
            stop();
            eventManager.notify("TIME_EXPIRED", null);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void stop() {
        timer.cancel();
    }
}
