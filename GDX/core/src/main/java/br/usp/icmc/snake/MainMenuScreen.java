package br.usp.icmc.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

public class MainMenuScreen extends ScreenAdapter {
    private final SnakeGame game;

    public MainMenuScreen(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        // Pinta o fundo de azul escuro para diferenciar do jogo
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.font.draw(game.batch, "SNAKE GAME MULTIPLAYER", 150, 400);
        game.font.draw(game.batch, "Pressione ESPACO para Jogar", 150, 300);
        game.font.draw(game.batch, "Pressione ESC para Sair", 150, 250);
        game.batch.end();

        // Roteamento
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
}
