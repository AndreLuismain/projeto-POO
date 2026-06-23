package br.usp.icmc.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends ScreenAdapter {
    private final SnakeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private BitmapFont fontNormal;
    private BitmapFont fontFooter;
    private Texture backgroundTexture;

    private int selectedWidth = 40;
    private int selectedHeight = 30;
    private String sizeLabel = "NORMAL (40x30)";

    private float selectedSpeed = 0.15f;
    private String speedLabel = "NORMAL";

    private final Color corCreme = Color.valueOf("#FFD966");
    private final Color corDourada = Color.valueOf("#000000");

    public MainMenuScreen(SnakeGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 600, camera);

        this.fontNormal = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));
        this.fontFooter = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));

        //Elas evitam que a fonte fique "quebrada" ou amassada ao mudar a escala.
        this.fontNormal.setUseIntegerPositions(false);
        this.fontFooter.setUseIntegerPositions(false);

        // Escalas reduzidas drasticamente para caber na tela sem virar um borrão
        this.fontNormal.getData().setScale(0.5f);
        this.fontFooter.getData().setScale(0.35f);

        // Como o texto normal é maior, 3 pixels devem ficar bons. Pro rodapé, 1 ou 2 pixels.
        addLetterSpacing(this.fontNormal, 3);
        addLetterSpacing(this.fontFooter, 2);

        this.backgroundTexture = new Texture("fundo_menu.png");
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        game.batch.draw(backgroundTexture, 0, 0, 800, 600);

        // O título em texto foi removido para valorizar a placa do seu background!

        // --- INSTRUÇÕES E CONFIGURAÇÕES NO MEIO ---
        // Posições X e Y ajustadas para encaixar no meio do cenário
        drawOutlinedText(fontNormal, "<- MAPA -> : " + sizeLabel, 240, 350);
        drawOutlinedText(fontNormal, "VELOCIDADE: " + speedLabel, 240, 310);

        // --- BOTÕES ---
        drawOutlinedText(fontNormal, "> Pressione [ENTER] para START", 220, 270);
        drawOutlinedText(fontNormal, "> Pressione [I] para INSTRUCOES", 220, 230);
        drawOutlinedText(fontNormal, "> Pressione [H] para HIGH SCORES", 220, 190);
        drawOutlinedText(fontNormal, "> Pressione [ESC] para FECHAR", 220, 150);

        // --- RODAPÉ CENTRALIZADO ---
        // Em vez de espremer nos cantos, colocamos centralizado em duas linhas
        GlyphLayout layout = new GlyphLayout();

        String txtEsquerda = "Desenvolvido por Andre Luis, Renan Soriano e Andre Luiz - ICMC USP";
        layout.setText(fontFooter, txtEsquerda);
        drawOutlinedText(fontFooter, txtEsquerda, (800 - layout.width) / 2, 50);

        String txtDireita = "Projeto SCC504 - Programacao Orientada a Objetos";
        layout.setText(fontFooter, txtDireita);
        drawOutlinedText(fontFooter, txtDireita, (800 - layout.width) / 2, 25);

        game.batch.end();
    }

    private void drawOutlinedText(BitmapFont font, String text, float x, float y) {
        // Contorno em 8 direções para garantir leitura sobre o fundo de pedra.
        font.setColor(corDourada);
        font.draw(game.batch, text, x - 1, y - 1);
        font.draw(game.batch, text, x - 1, y);
        font.draw(game.batch, text, x - 1, y + 1);
        font.draw(game.batch, text, x + 1, y - 1);
        font.draw(game.batch, text, x + 1, y);
        font.draw(game.batch, text, x + 1, y + 1);
        font.draw(game.batch, text, x, y - 1);
        font.draw(game.batch, text, x, y + 1);

        font.setColor(corCreme);
        font.draw(game.batch, text, x, y);

        font.setColor(Color.WHITE);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectedWidth == 40) {
                selectedWidth = 20; selectedHeight = 15; sizeLabel = "PEQUENO (20x15)";
            } else if (selectedWidth == 20) {
                selectedWidth = 80; selectedHeight = 60; sizeLabel = "EPICO (80x60)";
            } else {
                selectedWidth = 40; selectedHeight = 30; sizeLabel = "NORMAL (40x30)";
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectedSpeed == 0.15f) {
                selectedSpeed = 0.08f; speedLabel = "RAPIDA";
            } else if (selectedSpeed == 0.08f) {
                selectedSpeed = 0.25f; speedLabel = "LENTA";
            } else {
                selectedSpeed = 0.15f; speedLabel = "NORMAL";
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game, selectedWidth, selectedHeight, selectedSpeed));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            game.setScreen(new HighScoreScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            game.setScreen(new InstructionsScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    // Hack para aumentar o espaçamento das letras direto no código
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
    public void dispose() {
        fontNormal.dispose();
        fontFooter.dispose();
        backgroundTexture.dispose();
    }
}
