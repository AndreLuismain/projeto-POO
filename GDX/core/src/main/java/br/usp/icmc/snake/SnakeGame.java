package br.usp.icmc.snake;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SnakeGame extends Game {
    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        // Aumenta a escala da fonte para não ficar minúscula
        font.getData().setScale(2f);

        // O jogo agora começa no Menu Principal, não mais direto no jogo
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
