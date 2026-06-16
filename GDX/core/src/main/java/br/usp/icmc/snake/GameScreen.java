package br.usp.icmc.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private final SnakeGame game;
    private final GameWorld world;

    // Texturas
    private Texture chaoClaro;
    private Texture chaoEscuro;
    private Texture comidaTexture;
    private Texture cabeca1Texture;
    private Texture cabeca2Texture;
    private Texture corpo1Texture;
    private Texture corpo2Texture;
    private Texture obstaculoTexture;
    private Texture escudoTexture; // NOVO: Power Up 1
    private Texture estrelaTexture; // NOVO: Power Up 2
    private TextureRegion escudoRegion;
    private TextureRegion estrelaRegion;

    // UI e Sons
    private BitmapFont fontScore;
    private Sound eatSound;
    private Sound dieSound;
    private Sound powerUpSound; // NOVO

    private OrthographicCamera camera;
    private Viewport viewport;

    private float timer = 0;
    private final int cellSize;

    public GameScreen(SnakeGame game, int gridWidth, int gridHeight, float startSpeed) {
        this.game = game;
        this.world = new GameWorld(gridWidth, gridHeight, startSpeed);
        this.cellSize = 800 / gridWidth;

        this.chaoClaro = new Texture("chaoClaro.png");
        this.chaoEscuro = new Texture("chaoEscuro.png");
        this.comidaTexture = new Texture("comida1.png");
        this.cabeca1Texture = new Texture("cabeca1.png");
        this.cabeca2Texture = new Texture("cabeca2.png");
        this.corpo1Texture = new Texture("corpo1.png");
        this.corpo2Texture = new Texture("corpo2.png");
        this.obstaculoTexture = new Texture("obstaculo.png");

        // Texturas dos Poderes
        this.escudoTexture = new Texture("escudo.png");
        this.estrelaTexture = new Texture("estrela.png");
        // As imagens originais têm muita margem transparente ao redor do ícone;
        // recorta só a área com conteúdo para o desenho ficar maior e bem proporcional.
        this.escudoRegion = new TextureRegion(escudoTexture, 224, 45, 228, 277);
        this.estrelaRegion = new TextureRegion(estrelaTexture, 201, 44, 275, 267);

        this.fontScore = new BitmapFont();
        this.fontScore.getData().setScale(1.2f);

        this.eatSound = Gdx.audio.newSound(Gdx.files.internal("eat.wav"));
        this.dieSound = Gdx.audio.newSound(Gdx.files.internal("die.wav"));
        // Se quiser usar o mesmo som da comida para o poder provisoriamente, tudo bem:
        this.powerUpSound = Gdx.audio.newSound(Gdx.files.internal("eat.wav"));

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 600, camera);
    }

    @Override
    public void render(float delta) {
        handleInput();

        timer += delta;
        if (timer >= world.getCurrentTickTime()) {
            world.update();
            timer = 0;

            if (world.popEatSound()) eatSound.play(0.5f);
            if (world.popDieSound()) dieSound.play(0.8f);
            if (world.popPowerUpSound()) powerUpSound.play(1.0f); // Toca mais alto para destacar
        }

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        draw();

        if (world.isGameOver()) {
            boolean p1Alive = world.getPlayer1().isAlive();
            Snake winner = p1Alive ? world.getPlayer1() : world.getPlayer2();

            game.setScreen(new GameOverScreen(
                game, world.getWinnerMessage(), winner.getScore(), winner.getBody().size()
            ));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) world.getPlayer1().setDirection(Direction.UP);
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) world.getPlayer1().setDirection(Direction.DOWN);
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) world.getPlayer1().setDirection(Direction.LEFT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) world.getPlayer1().setDirection(Direction.RIGHT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) world.getPlayer2().setDirection(Direction.UP);
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) world.getPlayer2().setDirection(Direction.DOWN);
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) world.getPlayer2().setDirection(Direction.LEFT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) world.getPlayer2().setDirection(Direction.RIGHT);
    }

    private void draw() {
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        for (int x = 0; x < 800; x += cellSize) {
            for (int y = 0; y < 600; y += cellSize) {
                int coluna = x / cellSize;
                int linha = y / cellSize;
                if ((coluna + linha) % 2 == 0) {
                    game.batch.draw(chaoClaro, x, y, cellSize, cellSize);
                } else {
                    game.batch.draw(chaoEscuro, x, y, cellSize, cellSize);
                }
            }
        }

        // Desenha Obstáculos
        for (GridPosition obs : world.getObstacles()) {
            game.batch.draw(obstaculoTexture, obs.x() * cellSize, obs.y() * cellSize, cellSize, cellSize);
        }

        // Desenha Power Up (Se existir no mapa)
        if (world.getPowerUpPos() != null) {
            TextureRegion pRegion = (world.getPowerUpType() == 1) ? escudoRegion : estrelaRegion;
            // Desenha maior que uma célula comum para o power-up se destacar no mapa.
            float sizeMultiplier = 1.3f;
            float baseSize = cellSize * sizeMultiplier;
            float aspect = (float) pRegion.getRegionWidth() / pRegion.getRegionHeight();
            float drawWidth = aspect >= 1f ? baseSize : baseSize * aspect;
            float drawHeight = aspect >= 1f ? baseSize / aspect : baseSize;
            float cellCenterX = world.getPowerUpPos().x() * cellSize + cellSize / 2f;
            float cellCenterY = world.getPowerUpPos().y() * cellSize + cellSize / 2f;
            game.batch.draw(pRegion, cellCenterX - drawWidth / 2f, cellCenterY - drawHeight / 2f, drawWidth, drawHeight);
        }

        // Comida
        game.batch.draw(comidaTexture, world.getFood().x() * cellSize, world.getFood().y() * cellSize, cellSize, cellSize);

        // Cobras
        drawSnake(world.getPlayer1(), cabeca1Texture, corpo1Texture);
        drawSnake(world.getPlayer2(), cabeca2Texture, corpo2Texture);

        // Indicadores Visuais no Placar se os Buffs estiverem ativos
        String p2Buffs = world.getPlayer2().isInvulnerable() ? " [ESCUDO]" : (world.getPlayer2().hasMultiplier() ? " [x3]" : "");
        String p1Buffs = world.getPlayer1().isInvulnerable() ? " [ESCUDO]" : (world.getPlayer1().hasMultiplier() ? " [x3]" : "");

        fontScore.draw(game.batch, "P2 (Azul) Score: " + world.getPlayer2().getScore() + p2Buffs, 20, 580);
        fontScore.draw(game.batch, "P1 (Verde) Score: " + world.getPlayer1().getScore() + p1Buffs, 600, 580);

        game.batch.end();
    }

    private void drawSnake(Snake snake, Texture headTex, Texture bodyTex) {
        int index = 0;
        int size = snake.getBody().size();
        float bodyShrink = 4f;
        float tailShrink = 10f;

        // Efeito visual fantasma caso a cobra esteja invulnerável
        float alphaEffect = snake.isInvulnerable() ? 0.3f : 1.0f;

        for (GridPosition pos : snake.getBody()) {
            float posX = pos.x() * cellSize;
            float posY = pos.y() * cellSize;

            if (index == 0) {
                if (bodyTex != null) {
                    game.batch.setColor(1f, 1f, 1f, 0.6f * alphaEffect);
                    game.batch.draw(bodyTex, posX + (bodyShrink / 2), posY + (bodyShrink / 2), cellSize - bodyShrink, cellSize - bodyShrink);
                }
                game.batch.setColor(1f, 1f, 1f, alphaEffect);
                game.batch.draw(headTex, posX, posY, cellSize, cellSize);

            } else if (index == size - 1) {
                if (bodyTex != null) {
                    game.batch.setColor(1f, 1f, 1f, 0.6f * alphaEffect);
                    game.batch.draw(bodyTex, posX + (tailShrink / 2), posY + (tailShrink / 2), cellSize - tailShrink, cellSize - tailShrink);
                }
            } else {
                if (bodyTex != null) {
                    game.batch.setColor(1f, 1f, 1f, 0.6f * alphaEffect);
                    game.batch.draw(bodyTex, posX + (bodyShrink / 2), posY + (bodyShrink / 2), cellSize - bodyShrink, cellSize - bodyShrink);
                }
            }

            game.batch.setColor(1f, 1f, 1f, 1f); // Reseta a cor no fim do loop daquela parte
            index++;
        }
    }

    @Override
    public void dispose() {
        chaoClaro.dispose();
        chaoEscuro.dispose();
        comidaTexture.dispose();
        cabeca1Texture.dispose();
        cabeca2Texture.dispose();
        corpo1Texture.dispose();
        corpo2Texture.dispose();
        obstaculoTexture.dispose();
        escudoTexture.dispose();
        estrelaTexture.dispose();

        fontScore.dispose();
        eatSound.dispose();
        dieSound.dispose();
        powerUpSound.dispose();
    }
}
