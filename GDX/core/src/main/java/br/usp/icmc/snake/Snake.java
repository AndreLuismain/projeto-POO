package br.usp.icmc.snake;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    private final Deque<GridPosition> body;
    private Direction currentDirection;
    private boolean isAlive;
    private int score;

    public Snake(GridPosition startPosition, br.usp.icmc.snake.Direction startDirection) {
        this.body = new LinkedList<>();
        this.body.addFirst(startPosition);
        this.currentDirection = startDirection;
        this.isAlive = true;
        this.score = 0;
    }

    public Iterable<GridPosition> getBody() {
        return List.copyOf(body);
    }

    public GridPosition getHead() {
        return body.peekFirst();
    }

    public void setDirection(br.usp.icmc.snake.Direction newDirection) {
        if (!this.currentDirection.isOpposite(newDirection)) {
            this.currentDirection = newDirection;
        }
    }

    public GridPosition getNextHeadPosition() {
        GridPosition head = getHead();
        return switch (currentDirection) {
            case UP -> new GridPosition(head.x(), head.y() + 1);
            case DOWN -> new GridPosition(head.x(), head.y() - 1);
            case LEFT -> new GridPosition(head.x() - 1, head.y());
            case RIGHT -> new GridPosition(head.x() + 1, head.y());
        };
    }

    public void move(GridPosition newHead) {
        body.addFirst(newHead);
        body.removeLast();
    }

    public void grow(GridPosition newHead) {
        body.addFirst(newHead);
        score++;
    }

    public boolean checkCollision(GridPosition pos) {
        return body.contains(pos);
    }

    public int getScore() { return score; }
    public void die() { this.isAlive = false; }
    public boolean isAlive() { return isAlive; }
}
