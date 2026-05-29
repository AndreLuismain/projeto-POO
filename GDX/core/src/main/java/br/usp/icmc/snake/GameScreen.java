package br.usp.icmc.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameScreen extends ScreenAdapter {
    private final SnakeGame game;
    private final GameWorld world;

    private float timer = 0;
    private final float tickTime = 0.15f; // Velocidade do jogo (0.15 segundos por movimento)
    private final int cellSize = 20; // Tamanho de cada "quadrado" na tela

    public GameScreen(SnakeGame game) {
        this.game = game;
        // Cria um mundo de 40x30 quadrados
        this.world = new GameWorld(40, 30);
    }

    @Override
    public void render(float delta) {
        handleInput();

        // Só atualiza a lógica do jogo quando o timer atinge o tickTime
        timer += delta;
        if (timer >= tickTime) {
            world.update();
            timer = 0;
        }

        draw();
        // Faz o roteamento passando a mensagem de vitória do Model para a nova View
        if (world.isGameOver()) {
            game.setScreen(new GameOverScreen(game, world.getWinnerMessage()));
        }

    }

    private void handleInput() {
        // Player 1 (Setas)
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) world.getPlayer1().setDirection(Direction.UP);
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) world.getPlayer1().setDirection(Direction.DOWN);
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) world.getPlayer1().setDirection(Direction.LEFT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) world.getPlayer1().setDirection(Direction.RIGHT);

        // Player 2 (WASD)
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) world.getPlayer2().setDirection(Direction.UP);
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) world.getPlayer2().setDirection(Direction.DOWN);
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) world.getPlayer2().setDirection(Direction.LEFT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) world.getPlayer2().setDirection(Direction.RIGHT);
    }

    private void draw() {
        // 1. Fundo grafite elegante (nada de preto absoluto)
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 2. Desenhando um Grid de fundo (Linhas finas)
        game.shapeRenderer.setColor(0.2f, 0.2f, 0.25f, 1f);
        for (int x = 0; x < Gdx.graphics.getWidth(); x += cellSize) {
            game.shapeRenderer.rectLine(x, 0, x, Gdx.graphics.getHeight(), 1);
        }
        for (int y = 0; y < Gdx.graphics.getHeight(); y += cellSize) {
            game.shapeRenderer.rectLine(0, y, Gdx.graphics.getWidth(), y, 1);
        }

        // 3. Desenhando a Comida como um Círculo (centralizado no quadrado)
        game.shapeRenderer.setColor(Color.CORAL);
        game.shapeRenderer.circle(
            world.getFood().x() * cellSize + (cellSize / 2f),
            world.getFood().y() * cellSize + (cellSize / 2f),
            cellSize / 2.5f
        );

        // 4. Desenhando as Cobras com nosso novo método estético
        drawSnake(world.getPlayer1(), Color.LIME, Color.FOREST); // Player 1 (Verde claro/escuro)
        drawSnake(world.getPlayer2(), Color.CYAN, Color.TEAL);   // Player 2 (Azul claro/escuro)

        game.shapeRenderer.end();
    }

    // Metodo auxiliar para estilizar a cobra
    private void drawSnake(Snake snake, Color headColor, Color bodyColor) {
        boolean isHead = true;
        for (GridPosition pos : snake.getBody()) {
            if (isHead) {
                game.shapeRenderer.setColor(headColor);
                isHead = false;
            } else {
                game.shapeRenderer.setColor(bodyColor);
            }

            // O truque da textura: desenhamos um retângulo 2 pixels menor que a célula.
            // Isso cria a ilusão de "escamas" em vez de um tubo sólido.
            game.shapeRenderer.rect(
                pos.x() * cellSize + 1,
                pos.y() * cellSize + 1,
                cellSize - 2,
                cellSize - 2
            );
        }
    }
}
