package br.usp.icmc.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

public class GameOverScreen extends ScreenAdapter {
    private final SnakeGame game;
    private final String winnerMessage;

    public GameOverScreen(SnakeGame game, String winnerMessage) {
        this.game = game;
        this.winnerMessage = winnerMessage;
    }

    @Override
    public void render(float delta) {
        // Pinta o fundo de vermelho escuro para dar sensação de Game Over
        Gdx.gl.glClearColor(0.3f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.font.draw(game.batch, "GAME OVER", 300, 450);
        game.font.draw(game.batch, winnerMessage, 150, 350);
        game.font.draw(game.batch, "Pressione ESPACO para tentar novamente", 100, 250);
        game.font.draw(game.batch, "Pressione M para voltar ao Menu", 150, 200);
        game.batch.end();

        // Roteamento
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
