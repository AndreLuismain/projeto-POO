package br.usp.icmc.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ARQUITETURA (MODELO - BACKEND): 
 * Esta classe é o "cérebro" do jogo. Repare que NÃO há imports gráficos do LibGDX aqui 
 * (como Texture ou SpriteBatch). Ela apenas gerencia dados, matemática e as regras de negócio, 
 * garantindo o princípio da Responsabilidade Única (SOLID).
 */
public class GameWorld {
    private final int width;
    private final int height;

    // Entidades do jogo encapsuladas
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
    private final int POWERUP_SPAWN_TICKS = 60; // Tenta spawnar um poder a cada 60 frames (ticks)
    private final int BUFF_DURATION = 50; // O poder dura 50 frames no jogador

    private boolean gameOver;
    private String winnerMessage;

    // --- Sistema de Ticks (Independência de Framerate) ---
    // A velocidade do jogo é baseada neste timer, não nos frames do monitor do usuário.
    private float currentTickTime;
    private final float MIN_TICK_TIME = 0.05f;
    private final float SPEED_DECREMENT = 0.002f;

    // Flags de comunicação de Áudio (A tela lê essas flags e toca o som sem quebrar o isolamento)
    private boolean soundEatRequested = false;
    private boolean soundDieRequested = false;
    private boolean soundPowerUpRequested = false; 

    private final Random random = new Random();

    /**
     * CONSTRUTOR:
     * Injetado com as configurações escolhidas no MainMenuScreen.
     * Posiciona os jogadores em lados opostos e inicia o estado do mundo.
     */
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

    /**
     * LÓGICA PRINCIPAL (Chamada pela GameScreen quando o tickTime acumula o suficiente):
     * Concentra toda a física, spawn de itens e detecção de colisão do jogo.
     */
    public void update() {
        if (gameOver) return;

        soundEatRequested = false;
        soundPowerUpRequested = false;

        // Atualiza os contadores de tempo dos power-ups ativos em cada cobra
        player1.updateBuffs();
        player2.updateBuffs();

        // Timer Procedural de Obstáculos
        ticksSinceLastObstacle++;
        if (ticksSinceLastObstacle >= OBSTACLE_SPAWN_TICKS) {
            spawnObstacle();
            ticksSinceLastObstacle = 0;
        }

        // Timer Procedural de Power-Ups (Só spawna se não houver nenhum no mapa)
        if (powerUpPos == null) {
            ticksSinceLastPowerUp++;
            if (ticksSinceLastPowerUp >= POWERUP_SPAWN_TICKS) {
                spawnPowerUp();
                ticksSinceLastPowerUp = 0;
            }
        }

        // Pede para as entidades Snake calcularem qual será o próximo bloco que vão ocupar.
        // É dentro deste método getNextHeadPosition() que ocorre a matemática modular do Wrap-around.
        GridPosition nextHead1 = player1.getNextHeadPosition(width, height);
        GridPosition nextHead2 = player2.getNextHeadPosition(width, height);

        // ==========================================
        // 1. SISTEMA DE COLISÕES (Com bypass de Invulnerabilidade)
        // ==========================================
        // Se bateram cabeça com cabeça:
        if (nextHead1.equals(nextHead2)) {
            if (!player1.isInvulnerable()) player1.die();
            if (!player2.isInvulnerable()) player2.die();
        } else {
            // Checagem Player 1: Bateu em si mesmo? Bateu no Player 2? Bateu numa rocha?
            if (!player1.isInvulnerable() && (player1.checkCollision(nextHead1) || player2.checkCollision(nextHead1) || obstacles.contains(nextHead1))) {
                player1.die();
            }
            // Checagem Player 2: Bateu em si mesmo? Bateu no Player 1? Bateu numa rocha?
            if (!player2.isInvulnerable() && (player2.checkCollision(nextHead2) || player1.checkCollision(nextHead2) || obstacles.contains(nextHead2))) {
                player2.die();
            }
        }

        // Se algum morreu, paralisa o mundo e delega a mensagem de vitória.
        // O critério de vitória é a pontuação, não quem sobreviveu: quem morre
        // primeiro ainda pode vencer se tiver feito mais pontos.
        if (!player1.isAlive() || !player2.isAlive()) {
            gameOver = true;
            soundDieRequested = true;
            int p1Score = player1.getScore();
            int p2Score = player2.getScore();
            if (p1Score == p2Score) winnerMessage = "Empate! Mesma pontuacao.";
            else if (p1Score > p2Score) winnerMessage = "Player 1 Venceu!";
            else winnerMessage = "Player 2 Venceu!";
            return;
        }

        // ==========================================
        // 2. MOVIMENTO, ALIMENTAÇÃO E POWER-UPS
        // ==========================================
        processMovementAndEvents(player1, nextHead1);
        processMovementAndEvents(player2, nextHead2);
    }

    /**
     * MÁQUINA DE ESTADOS DO MOVIMENTO:
     * Verifica se o próximo passo é uma maçã, um poder ou um espaço vazio.
     * Conexão: Invoca os métodos da classe Snake (grow ou move) manipulando a estrutura Deque.
     */
    private void processMovementAndEvents(Snake snake, GridPosition nextHead) {
        // Cenário A: Comeu a maçã (Cresce e acelera o jogo manipulando o Tick Time)
        if (nextHead.equals(food)) {
            snake.grow(nextHead); // O Deque ganha uma nova cabeça, mas não perde o rabo.
            soundEatRequested = true;
            spawnFood();
            if (currentTickTime > MIN_TICK_TIME) currentTickTime -= SPEED_DECREMENT;
        }
        // Cenário B: Pegou o Power Up (Ativa o efeito e anda normalmente)
        else if (powerUpPos != null && nextHead.equals(powerUpPos)) {
            if (powerUpType == 1) snake.activateInvulnerability(BUFF_DURATION);
            else if (powerUpType == 2) snake.activateMultiplier(BUFF_DURATION);

            snake.move(nextHead); // Anda normal (Deque adiciona cabeça e remove rabo)
            powerUpPos = null; // Remove o item do mapa logicamente
            soundPowerUpRequested = true;
        }
        // Cenário C: Movimento normal por espaço vazio
        else {
            snake.move(nextHead);
        }
    }

    /**
     * GERAÇÃO PROCEDURAL SEGURO:
     * Este laço while(true) garante que o item (Maçã, Rocha ou Poder) 
     * não seja criado em cima do corpo de nenhuma das cobras ou de outros itens.
     */
    private void spawnFood() {
        boolean validPosition = false;
        while (!validPosition) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            GridPosition newFood = new GridPosition(x, y);
            
            // Regra de Validação Lógica
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
                powerUpType = random.nextInt(2) + 1; // 1 (Escudo) ou 2 (Estrela)
                validPosition = true;
            }
        }
    }

    // ==========================================
    // GETTERS (Ponte com a Interface Gráfica)
    // A GameScreen usa esses métodos apenas para ler posições e desenhar.
    // ==========================================
    public Snake getPlayer1() { return player1; }
    public Snake getPlayer2() { return player2; }
    public GridPosition getFood() { return food; }
    public List<GridPosition> getObstacles() { return obstacles; }
    public GridPosition getPowerUpPos() { return powerUpPos; }
    public int getPowerUpType() { return powerUpType; }

    public boolean isGameOver() { return gameOver; }
    public String getWinnerMessage() { return winnerMessage; }
    public float getCurrentTickTime() { return currentTickTime; }

    // Flags consumidas pela GameScreen para tocar efeitos sonoros pontuais
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
