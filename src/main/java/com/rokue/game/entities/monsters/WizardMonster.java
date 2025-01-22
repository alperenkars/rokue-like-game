package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.ChallengingWizardStrategy;
import com.rokue.game.behaviour.HelpfulWizardStrategy;
import com.rokue.game.behaviour.IndecisiveWizardStrategy;
import com.rokue.game.behaviour.WizardStrategy;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.events.EventManager;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Position;

public class WizardMonster extends Monster {
    private static final long serialVersionUID = 1L;
    private static final double LOW_TIME_THRESHOLD = 30.0;
    private static final double HIGH_TIME_THRESHOLD = 70.0;
    private transient EventManager eventManager;  // Changed from final to allow setting after deserialization
    private final Hall currentHall;
    private boolean markedForRemoval = false;
    private final int totalTime;
    private transient PlayMode playMode;  // Changed from final to allow setting after deserialization
    private transient int lastRemainingTime;

    public WizardMonster(Position startPosition, EventManager eventManager, Hall currentHall, int totalTime, int remainingTime, PlayMode playMode) {
        super(startPosition, null, "WIZARD");
        this.eventManager = eventManager;
        this.currentHall = currentHall;
        this.totalTime = totalTime;
        this.playMode = playMode;
        this.lastRemainingTime = remainingTime;
        
        updateStrategy(totalTime, remainingTime);
    }

    // Custom deserialization to restore transient fields
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // The eventManager and playMode will be restored by setEventManager and setPlayMode
        updateStrategy(totalTime, lastRemainingTime);
    }

    @Override
    public void update(Hero hero, Hall hall) {
        if (!markedForRemoval && playMode != null) {
            updateStrategy(totalTime, playMode.getRemainingTime());
            if (behaviour != null) {
                behaviour.act(hero, this);
            }
        }
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
        currentHall.removeMonster(this);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public Hall getCurrentHall() {
        return currentHall;
    }

    private void updateStrategy(int totalTime, int remainingTime) {
        double timeRemainingPercentage = (remainingTime * 100.0) / totalTime;
        WizardStrategy newStrategy = null;

        if (timeRemainingPercentage <= LOW_TIME_THRESHOLD) {
            newStrategy = new HelpfulWizardStrategy();
        } else if (timeRemainingPercentage > HIGH_TIME_THRESHOLD) {
            newStrategy = new ChallengingWizardStrategy();
        } else {
            newStrategy = new IndecisiveWizardStrategy();
        }

        if (behaviour == null || behaviour.getClass() != newStrategy.getClass()) {
            this.behaviour = newStrategy;
        }
    }
}