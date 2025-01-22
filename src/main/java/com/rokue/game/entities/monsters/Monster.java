package com.rokue.game.entities.monsters;

import java.io.Serializable;

import com.rokue.game.behaviour.MonsterBehaviour;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public abstract class Monster implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Position position;
    protected transient MonsterBehaviour behaviour;  // transient because behaviour might not be serializable
    protected String type;

    public Monster(Position position, MonsterBehaviour behaviour, String type) {
        this.position = position;
        this.behaviour = behaviour;
        this.type = type;
    }

    // Custom deserialization to handle the transient behaviour field
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // The behaviour will need to be restored after deserialization
        // This should be handled by the specific monster classes
    }

    public void update(Hero hero, Hall hall) {
        if (behaviour != null) {
            behaviour.act(hero, this);
        }
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public MonsterBehaviour getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(MonsterBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public String getType() {
        return type;
    }
}


