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
    private static final double LOW_TIME_THRESHOLD = 30.0;
    private static final double HIGH_TIME_THRESHOLD = 70.0;
    private transient final EventManager eventManager;
    private final Hall currentHall;
    private boolean markedForRemoval = false;
    private final int totalTime;
    private transient final PlayMode playMode;

    public WizardMonster(Position startPosition, EventManager eventManager, Hall currentHall, int totalTime, int remainingTime, PlayMode playMode) {
        super(startPosition, null, "WIZARD"); // We'll set the strategy after calculating time percentage
        this.eventManager = eventManager;
        this.currentHall = currentHall;
        this.totalTime = totalTime;
        this.playMode = playMode;
        
        updateStrategy(totalTime, remainingTime); // Set initial strategy
    }

    @Override
    public void update(Hero hero, Hall hall) {
        if (!markedForRemoval) {
            // Check if we need to change strategy based on current time
            updateStrategy(totalTime, playMode.getRemainingTime());
            // Let the behaviour handle the action through MonsterBehaviour interface
            behaviour.act(hero, this);
        }
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
        currentHall.removeMonster(this);
    }

    public EventManager getEventManager() {
        return eventManager;
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

        // Only change strategy if it's a different type
        if (behaviour == null || behaviour.getClass() != newStrategy.getClass()) {
            this.behaviour = newStrategy;
        }
    }
}