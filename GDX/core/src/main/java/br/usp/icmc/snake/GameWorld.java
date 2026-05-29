package br.usp.icmc.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {
    private final int width;
    private final int height;

    private final Snake player1;
    private final Snake player2;
    private GridPosition food;

    private final List<GridPosition> obstacles;
    private int ticksSinceLastObstacle = 0;
    private final int OBSTACLE_SPAWN_TICKS = 40;

    // --- Sistema de Power-ups (Mundo) ---
    private GridPosition powerUpPos = null;
    private int powerUpType = 0; // 1 = Escudo (Invulnerabilidade), 2 = Estrela (Multiplicador)
    private int ticksSinceLastPowerUp = 0;
    private final int POWERUP_SPAWN_TICKS = 60; // Tenta spawnar um poder a cada 60 frames
    private final int BUFF_DURATION = 50; // O poder dura 50 frames no jogador

    private boolean gameOver;
    private String winnerMessage;

    private float currentTickTime;
    private final float MIN_TICK_TIME = 0.05f;
    private final float SPEED_DECREMENT = 0.002f;

    private boolean soundEatRequested = false;
    private boolean soundDieRequested = false;
    private boolean soundPowerUpRequested = false; // Novo som

    private final Random random = new Random();

    public GameWorld(int width, int height, float startingSpeed) {
        this.width = width;
        this.height = height;
        this.currentTickTime = startingSpeed;
        this.obstacles = new ArrayList<>();

        int startX_P1 = width / 4;
        int startX_P2 = width - (width / 4);
        int midY = height / 2;

        this.player1 = new Snake(new GridPosition(startX_P1, midY), Direction.RIGHT);
        this.player2 = new Snake(new GridPosition(startX_P2, midY), Direction.LEFT);

        this.gameOver = false;
        this.winnerMessage = "";

        spawnFood();
    }

    public void update() {
        if (gameOver) return;

        soundEatRequested = false;
        soundPowerUpRequested = false;

        // Atualiza os contadores de buff das cobras
        player1.updateBuffs();
        player2.updateBuffs();

        // Timer de Obstáculos
        ticksSinceLastObstacle++;
        if (ticksSinceLastObstacle >= OBSTACLE_SPAWN_TICKS) {
            spawnObstacle();
            ticksSinceLastObstacle = 0;
        }

        // Timer de Power-Ups
        if (powerUpPos == null) {
            ticksSinceLastPowerUp++;
            if (ticksSinceLastPowerUp >= POWERUP_SPAWN_TICKS) {
                spawnPowerUp();
                ticksSinceLastPowerUp = 0;
            }
        }

        GridPosition nextHead1 = player1.getNextHeadPosition(width, height);
        GridPosition nextHead2 = player2.getNextHeadPosition(width, height);

        // ==========================================
        // 1. SISTEMA DE COLISÕES (Agora com Invulnerabilidade)
        // ==========================================
        if (nextHead1.equals(nextHead2)) {
            if (!player1.isInvulnerable()) player1.die();
            if (!player2.isInvulnerable()) player2.die();
        } else {
            if (!player1.isInvulnerable() && (player1.checkCollision(nextHead1) || player2.checkCollision(nextHead1) || obstacles.contains(nextHead1))) {
                player1.die();
            }
            if (!player2.isInvulnerable() && (player2.checkCollision(nextHead2) || player1.checkCollision(nextHead2) || obstacles.contains(nextHead2))) {
                player2.die();
            }
        }

        if (!player1.isAlive() || !player2.isAlive()) {
            gameOver = true;
            soundDieRequested = true;
            if (!player1.isAlive() && !player2.isAlive()) winnerMessage = "Empate! Colisao letal.";
            else if (!player1.isAlive()) winnerMessage = "Player 2 Venceu!";
            else winnerMessage = "Player 1 Venceu!";
            return;
        }

        // ==========================================
        // 2. MOVIMENTO, ALIMENTAÇÃO E POWER-UPS
        // ==========================================
        processMovementAndEvents(player1, nextHead1);
        processMovementAndEvents(player2, nextHead2);
    }

    private void processMovementAndEvents(Snake snake, GridPosition nextHead) {
        // Verifica se comeu a maçã normal
        if (nextHead.equals(food)) {
            snake.grow(nextHead);
            soundEatRequested = true;
            spawnFood();
            if (currentTickTime > MIN_TICK_TIME) currentTickTime -= SPEED_DECREMENT;
        }
        // Verifica se pegou o Power Up
        else if (powerUpPos != null && nextHead.equals(powerUpPos)) {
            if (powerUpType == 1) snake.activateInvulnerability(BUFF_DURATION);
            else if (powerUpType == 2) snake.activateMultiplier(BUFF_DURATION);

            snake.move(nextHead); // Move normalmente, não cresce
            powerUpPos = null; // Apaga o item do mapa
            soundPowerUpRequested = true;
        }
        // Movimento normal
        else {
            snake.move(nextHead);
        }
    }

    private void spawnFood() {
        boolean validPosition = false;
        while (!validPosition) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            GridPosition newFood = new GridPosition(x, y);
            if (!player1.checkCollision(newFood) && !player2.checkCollision(newFood) && !obstacles.contains(newFood)) {
                food = newFood;
                validPosition = true;
            }
        }
    }

    private void spawnObstacle() {
        boolean validPosition = false;
        while (!validPosition) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            GridPosition newObs = new GridPosition(x, y);
            if (!player1.checkCollision(newObs) && !player2.checkCollision(newObs) &&
                !newObs.equals(food) && !obstacles.contains(newObs)) {
                obstacles.add(newObs);
                validPosition = true;
            }
        }
    }

    private void spawnPowerUp() {
        boolean validPosition = false;
        while (!validPosition) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            GridPosition newPower = new GridPosition(x, y);
            if (!player1.checkCollision(newPower) && !player2.checkCollision(newPower) &&
                !newPower.equals(food) && !obstacles.contains(newPower)) {
                powerUpPos = newPower;
                powerUpType = random.nextInt(2) + 1; // Gera 1 ou 2 aleatoriamente
                validPosition = true;
            }
        }
    }

    // Getters
    public Snake getPlayer1() { return player1; }
    public Snake getPlayer2() { return player2; }
    public GridPosition getFood() { return food; }
    public List<GridPosition> getObstacles() { return obstacles; }
    public GridPosition getPowerUpPos() { return powerUpPos; }
    public int getPowerUpType() { return powerUpType; }

    public boolean isGameOver() { return gameOver; }
    public String getWinnerMessage() { return winnerMessage; }
    public float getCurrentTickTime() { return currentTickTime; }

    public boolean popEatSound() {
        if (soundEatRequested) { soundEatRequested = false; return true; } return false;
    }
    public boolean popDieSound() {
        if (soundDieRequested) { soundDieRequested = false; return true; } return false;
    }
    public boolean popPowerUpSound() {
        if (soundPowerUpRequested) { soundPowerUpRequested = false; return true; } return false;
    }
}
