package br.usp.icmc.snake;

import java.util.Random;

public class GameWorld {
    private final int width;
    private final int height;
    private final br.usp.icmc.snake.Snake player1;
    private final br.usp.icmc.snake.Snake player2;
    private GridPosition food;
    private boolean isGameOver;
    private String winnerMessage;
    private final Random random = new Random();

    public GameWorld(int width, int height) {
        this.width = width;
        this.height = height;
        this.player1 = new br.usp.icmc.snake.Snake(new GridPosition(5, height / 2), br.usp.icmc.snake.Direction.RIGHT);
        this.player2 = new br.usp.icmc.snake.Snake(new GridPosition(width - 6, height / 2), br.usp.icmc.snake.Direction.LEFT);
        spawnFood();
        this.isGameOver = false;
        this.winnerMessage = "";
    }

    public void update() {
        if (isGameOver) return;

        GridPosition nextHead1 = applyWrapAround(player1.getNextHeadPosition());
        GridPosition nextHead2 = applyWrapAround(player2.getNextHeadPosition());

        if (nextHead1.equals(nextHead2)) {
            player1.die();
            player2.die();
            determineWinner();
            isGameOver = true;
            return;
        }

        boolean p1Morte = player1.checkCollision(nextHead1) || player2.checkCollision(nextHead1);
        boolean p2Morte = player2.checkCollision(nextHead2) || player1.checkCollision(nextHead2);

        if (p1Morte || p2Morte) {
            if (p1Morte) player1.die();
            if (p2Morte) player2.die();
            determineWinner();
            isGameOver = true;
            return;
        }

        if (nextHead1.equals(food)) {
            player1.grow(nextHead1);
            spawnFood();
        } else {
            player1.move(nextHead1);
        }

        if (nextHead2.equals(food)) {
            player2.grow(nextHead2);
            spawnFood();
        } else {
            player2.move(nextHead2);
        }
    }

    private GridPosition applyWrapAround(GridPosition pos) {
        int newX = pos.x();
        int newY = pos.y();
        if (newX < 0) newX = width - 1;
        else if (newX >= width) newX = 0;
        if (newY < 0) newY = height - 1;
        else if (newY >= height) newY = 0;
        return new GridPosition(newX, newY);
    }

    private void spawnFood() {
        GridPosition newFoodPos;
        boolean isOnSnake;
        do {
            newFoodPos = new GridPosition(random.nextInt(width), random.nextInt(height));
            isOnSnake = player1.checkCollision(newFoodPos) || player2.checkCollision(newFoodPos);
        } while (isOnSnake);
        this.food = newFoodPos;
    }

    private void determineWinner() {
        if (!player1.isAlive() && !player2.isAlive()) {
            winnerMessage = "Empate Técnico! Ambas colidiram.";
        } else if (!player1.isAlive()) {
            winnerMessage = "Jogador 2 Ganhou!";
        } else {
            winnerMessage = "Jogador 1 Ganhou!";
        }
    }

    public br.usp.icmc.snake.Snake getPlayer1() { return player1; }
    public br.usp.icmc.snake.Snake getPlayer2() { return player2; }
    public GridPosition getFood() { return food; }
    public boolean isGameOver() { return isGameOver; }
    public String getWinnerMessage() { return winnerMessage; }
}
