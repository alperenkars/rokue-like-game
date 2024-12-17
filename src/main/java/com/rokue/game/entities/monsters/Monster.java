package com.rokue.game.entities.monsters;

import com.rokue.game.entities.Hero;
import com.rokue.game.events.EventManager;
import com.rokue.game.util.Position;


public abstract class Monster {
    protected Position position;
    protected EventManager eventManager;

    public Monster(Position position, EventManager eventManager) {
        this.position = position;
        this.eventManager = eventManager;
    }
    public Position getPosition() {
        return position;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public abstract void update(Hero hero);

}
