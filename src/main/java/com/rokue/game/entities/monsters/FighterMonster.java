package com.rokue.game.entities.monsters;

import com.rokue.game.behaviour.StabDagger;
import com.rokue.game.entities.Hall;
import com.rokue.game.entities.Hero;
import com.rokue.game.util.Position;

public class FighterMonster extends Monster {
    private Hero targetHero;
    private StabDagger weapon;
    private Position luringGemPosition;
    private boolean reactingToLuringGem;
    private long lastMoveTime;
    private static final long MOVE_INTERVAL = 1000;

    public FighterMonster(Position startPosition, Hero targetHero) {
        super(startPosition, new StabDagger());
        this.targetHero = targetHero;
        this.weapon = new StabDagger();
        this.reactingToLuringGem = false;
        this.lastMoveTime = System.currentTimeMillis();
    }

    @Override
    public void move(Hall hall) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime >= MOVE_INTERVAL) {
            if (luringGemPosition != null) {
                moveTowards(luringGemPosition);
            } else {
                moveRandomly(hall);
            }
            lastMoveTime = currentTime;
        }
    }
    
    private void moveRandomly(Hall hall) {
        Position currentPos = this.getPosition();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int[] direction = directions[(int) (Math.random() * directions.length)];

        Position newPos = new Position(
            currentPos.getX() + direction[0],
            currentPos.getY() + direction[1]
        );

        if (hall.isWithinBounds(newPos) && hall.getCell(newPos).getContent() == null) {
            this.setPosition(newPos);
            System.out.println("FighterMonster moved to: " + newPos);
        }
    }

    private void moveTowards(Position targetPosition) {
        Position currentPos = this.getPosition();

        int xStep = Integer.compare(targetPosition.getX(), currentPos.getX());
        int yStep = Integer.compare(targetPosition.getY(), currentPos.getY());

        Position newPos = new Position(
            currentPos.getX() + xStep,
            currentPos.getY() + yStep
        );

        this.setPosition(newPos);
        System.out.println("FighterMonster moving towards Luring Gem: " + newPos);
    }

    public boolean isHeroNearby() {
        Position heroPos = targetHero.getPosition();
        Position monsterPos = this.getPosition();

        return Math.abs(heroPos.getX() - monsterPos.getX()) <= 1 &&
               Math.abs(heroPos.getY() - monsterPos.getY()) <= 1;
    }

    public void attackHero() {
        if (isHeroNearby()) {
            System.out.println("FighterMonster is attacking the hero!");
            if (!targetHero.hasCloakOfProtection()) {
                targetHero.decreaseLife();
            } else {
                System.out.println("Cloak of Protection does not protect the hero from the dagger stab of fighter monsters!");
                targetHero.decreaseLife();
            }
        }
    }

    public void reactToLuringGem(Position gemPosition) {
        System.out.println("FighterMonster is reacting to Luring Gem!");
        this.luringGemPosition = gemPosition;
        this.reactingToLuringGem = true;
    }

    @Override
    public void update(Hero hero, Hall hall) {
        if (isHeroNearby()) {
            attackHero();
        } else {
            move(hall);
        }
    }

    public boolean isReactingToLuringGem() {
        return reactingToLuringGem;
    }

    public void move(){
        return; //This monster does not use, but the Fighter Monster does.
    }
}