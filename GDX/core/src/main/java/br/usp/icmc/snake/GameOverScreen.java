package br.usp.icmc.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Tela de Fim de Jogo.
 * OOP:
 * - Herança: Herda de ScreenAdapter para ser gerenciada pelo ciclo de vida de telas da libGDX (Polimorfismo).
 * - Encapsulamento: Mantém variáveis de estado (pontuações, texturas) como privativas, blindando sua exibição.
 * - Princípio de Responsabilidade Única (SRP): Isola totalmente as lógicas gráficas de término do jogo (da simulação passível de colisão gerenciada pela GameScreen).
 */
public class GameOverScreen extends ScreenAdapter {
    private final SnakeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private String winnerMessage;
    private int p1Score;
    private int p1Size;
    private int p2Score;
    private int p2Size;

    private BitmapFont fontTitle;
    private BitmapFont fontNormal;
    private Texture backgroundTexture;
    private Texture overlayTexture;

    private final Color corCentro = Color.valueOf("#F5DEB3");
    private final Color corBorda = Color.valueOf("#1A110B");

    public GameOverScreen(SnakeGame game, String winnerMessage, int p1Score, int p1Size, int p2Score, int p2Size) {
        this.game = game;
        this.winnerMessage = winnerMessage;
        this.p1Score = p1Score;
        this.p1Size = p1Size;
        this.p2Score = p2Score;
        this.p2Size = p2Size;

        // Acesso Estratégico em "I/O" Parado: 
        // O salvamento chamando HighScoreManager ocorre no construtor para que o 
        // acesso ao disco local (operação lenta) aconteça de forma síncrona uma única vez, 
        // fora do método "render", tirando travamentos da engine no loop de Gameplay (GameScreen).
        // Salva a melhor pontuação da partida no arquivo local (Top 5).
        // Não usa a pontuação de quem "venceu" porque quem morre primeiro
        // pode ter feito mais pontos que o sobrevivente.
        HighScoreManager.addScore(Math.max(p1Score, p2Score));

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 600, camera);

        // 1. Configuração das Fontes
        this.fontTitle = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));
        this.fontNormal = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));

        this.fontTitle.setUseIntegerPositions(false);
        this.fontNormal.setUseIntegerPositions(false);

        this.fontTitle.getData().setScale(0.9f);
        this.fontNormal.getData().setScale(0.5f);

        addLetterSpacing(this.fontTitle, 4);
        addLetterSpacing(this.fontNormal, 3);

        // 2. Texturas de Fundo
        this.backgroundTexture = new Texture("fundo_menu.png");

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        this.overlayTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    // Polimorfismo e Loop Dinâmico: O framework libGDX agora direciona seu loop 
    // principal (60x/seg) para este método, monitorando eventos visuais estáticos e 
    // lendo inputs isolados das regras de negócio do jogo (SnakeGame/GameScreen).
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Fundo e Véu Transparente
        game.batch.draw(backgroundTexture, 0, 0, 800, 600);

        game.batch.setColor(1f, 1f, 1f, 0.85f);
        game.batch.draw(overlayTexture, 0, 0, 800, 600);
        game.batch.setColor(Color.WHITE);

        // Título e Resultado
        drawOutlinedText(fontTitle, "FIM DE JOGO", 230, 500);
        drawOutlinedText(fontNormal, winnerMessage, 230, 420);

        // Estatísticas (separadas por player para deixar claro quem fez quantos pontos)
        drawOutlinedText(fontNormal, "PLAYER 1: " + p1Score + " PONTOS (TAMANHO " + p1Size + ")", 230, 320);
        drawOutlinedText(fontNormal, "PLAYER 2: " + p2Score + " PONTOS (TAMANHO " + p2Size + ")", 230, 270);

        // Instrução para voltar
        drawOutlinedText(fontNormal, "> Pressione [ENTER] para Voltar", 180, 150);

        game.batch.end();
    }

    private void drawOutlinedText(BitmapFont font, String text, float x, float y) {
        font.setColor(corBorda);
        font.draw(game.batch, text, x - 1, y);
        font.draw(game.batch, text, x + 1, y);
        font.draw(game.batch, text, x, y - 1);
        font.draw(game.batch, text, x, y + 1);

        font.setColor(corCentro);
        font.draw(game.batch, text, x, y);

        font.setColor(Color.WHITE);
    }

    private void addLetterSpacing(BitmapFont font, int spacingPixels) {
        for (BitmapFont.Glyph[] page : font.getData().glyphs) {
            if (page != null) {
                for (BitmapFont.Glyph glyph : page) {
                    if (glyph != null) {
                        glyph.xadvance += spacingPixels;
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    // Encerramento do ciclo e controle de hardware: 
    // Libera os recursos pesados estáticos de RAM e VRAM da classe
    // para evitar memory-leaks ao destruir essa transição.
    @Override
    public void dispose() {
        fontTitle.dispose();
        fontNormal.dispose();
        backgroundTexture.dispose();
        overlayTexture.dispose();
    }
}
