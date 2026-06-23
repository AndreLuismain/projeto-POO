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
import java.util.List;

/**
 * Tela de Exibição dos Melhores Pontos (Top 5).
 * OOP:
 * - Herança e Polimorfismo: Herda de ScreenAdapter, aproveitando a estrutura da libGDX para gerenciamento de telas.
 * - Encapsulamento: Mantém as listas de pontuações e elementos gráficos em estado seguro e private.
 * - Princípio de Responsabilidade Única (SRP): Focada unicamente na apresentação visual dos dados persistidos.
 */
public class HighScoreScreen extends ScreenAdapter {
    private final SnakeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private BitmapFont fontTitle;
    private BitmapFont fontNormal;
    private Texture backgroundTexture;
    private Texture overlayTexture;
    private List<Integer> topScores;

    // Cores padronizadas do projeto
    private final Color corCentro = Color.valueOf("#F5DEB3");
    private final Color corBorda = Color.valueOf("#1A110B");

    public HighScoreScreen(SnakeGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 600, camera);

        // 1. Configuração das Fontes
        this.fontTitle = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));
        this.fontNormal = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));

        this.fontTitle.setUseIntegerPositions(false);
        this.fontNormal.setUseIntegerPositions(false);

        this.fontTitle.getData().setScale(0.8f);
        this.fontNormal.getData().setScale(0.5f);

        addLetterSpacing(this.fontTitle, 4);
        addLetterSpacing(this.fontNormal, 3);

        // 2. Carrega as texturas do fundo
        this.backgroundTexture = new Texture("fundo_menu.png");

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        this.overlayTexture = new Texture(pixmap);
        pixmap.dispose();

        // Acesso Estratégico em "I/O" Parado:
        // A leitura do arquivo físico ocorre uma única vez de forma síncrona na instanciação.
        // Evitando que o custoso acesso ao disco aconteça repetidamente no loop de frame-rate (render).
        // 3. Carrega os dados persistidos do Top 5
        this.topScores = HighScoreManager.loadScores();
    }

    // Polimorfismo e Loop Dinâmico: A engine assume este "render" como loop principal.
    // Lida quase inteiramente com operações gráficas isoladas e inputs básicos, 
    // liberando a engine de preocupações com as mecânicas de física do jogo de fato.
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MainMenuScreen(game));
        }

        Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Fundo e Overlay
        game.batch.draw(backgroundTexture, 0, 0, 800, 600);

        game.batch.setColor(1f, 1f, 1f, 0.85f);
        game.batch.draw(overlayTexture, 0, 0, 800, 600);
        game.batch.setColor(Color.WHITE);

        // Título
        drawOutlinedText(fontTitle, "TOP 5 HIGH SCORES", 180, 520);

        // Desenha a lista de pontuações dinamicamente
        if (topScores.isEmpty()) {
            drawOutlinedText(fontNormal, "Nenhuma partida registrada ainda.", 150, 350);
        } else {
            int yPos = 400;
            for (int i = 0; i < topScores.size(); i++) {
                drawOutlinedText(fontNormal, (i + 1) + " LUGAR: " + topScores.get(i) + " PONTOS", 250, yPos);
                yPos -= 50;
            }
        }

        // Botão de Voltar
        drawOutlinedText(fontNormal, "> Pressione [ESC] para Voltar", 220, 80);

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

    // Encerramento do ciclo e liberação de VRAM:
    // Destrói ativamente as texturas para liberar a memória da placa de vídeo (GPU)
    // ao trocar esta tela, mantendo a performance do jogo ideal ao longo do tempo.
    @Override
    public void dispose() {
        fontTitle.dispose();
        fontNormal.dispose();
        backgroundTexture.dispose();
        overlayTexture.dispose();
    }
}
