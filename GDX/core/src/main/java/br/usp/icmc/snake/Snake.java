package br.usp.icmc.snake;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    private final Deque<GridPosition> body;
    private Direction currentDirection;
    private boolean isAlive;
    private int score;

    // --- Sistema de Power-ups ---
    private int invulnerableTicks = 0;
    private int multiplierTicks = 0;

    public Snake(GridPosition startPosition, Direction startDirection) {
        this.body = new LinkedList<>();
        this.currentDirection = startDirection;
        this.isAlive = true;
        this.score = 0;

        this.body.addFirst(startPosition);

        for (int i = 1; i <= 2; i++) {
            int tailX = startPosition.x();
            int tailY = startPosition.y();

            switch (startDirection) {
                case UP -> tailY -= i;
                case DOWN -> tailY += i;
                case LEFT -> tailX += i;
                case RIGHT -> tailX -= i;
            }
            this.body.addLast(new GridPosition(tailX, tailY));
        }
    }

    public List<GridPosition> getBody() { return List.copyOf(body); }
    public GridPosition getHead() { return body.peekFirst(); }

    public void setDirection(Direction newDirection) {
        if (!this.currentDirection.isOpposite(newDirection)) {
            this.currentDirection = newDirection;
        }
    }

    public GridPosition getNextHeadPosition(int worldWidth, int worldHeight) {
        GridPosition head = getHead();
        int nextX = head.x();
        int nextY = head.y();

        switch (currentDirection) {
            case UP -> nextY += 1;
            case DOWN -> nextY -= 1;
            case LEFT -> nextX -= 1;
            case RIGHT -> nextX += 1;
        }

        nextX = (nextX + worldWidth) % worldWidth;
        nextY = (nextY + worldHeight) % worldHeight;

        return new GridPosition(nextX, nextY);
    }

    public void move(GridPosition newHead) {
        body.addFirst(newHead);
        body.removeLast();
    }

    public void grow(GridPosition newHead) {
        body.addFirst(newHead);
        // Se o multiplicador estiver ativo, ganha 3 pontos, senão 1.
        score += (multiplierTicks > 0) ? 3 : 1;
    }

    // --- Gerenciamento de Buffs ---
    public void updateBuffs() {
        if (invulnerableTicks > 0) invulnerableTicks--;
        if (multiplierTicks > 0) multiplierTicks--;
    }

    public void activateInvulnerability(int durationInTicks) { this.invulnerableTicks = durationInTicks; }
    public void activateMultiplier(int durationInTicks) { this.multiplierTicks = durationInTicks; }

    public boolean isInvulnerable() { return invulnerableTicks > 0; }
    public boolean hasMultiplier() { return multiplierTicks > 0; }

    public boolean checkCollision(GridPosition pos) { return body.contains(pos); }
    public int getScore() { return score; }
    public void die() { this.isAlive = false; }
    public boolean isAlive() { return isAlive; }
}
