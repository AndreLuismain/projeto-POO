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

public class InstructionsScreen extends ScreenAdapter {
    private final SnakeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private BitmapFont fontTitle;
    private BitmapFont fontNormal;
    private Texture backgroundTexture;
    private Texture overlayTexture; // Nossa textura semi-transparente gerada via código

    // Mesmas cores do menu para manter a consistência visual
    private final Color corCentro = Color.valueOf("#F5DEB3");
    private final Color corBorda = Color.valueOf("#1A110B");

    public InstructionsScreen(SnakeGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 600, camera);

        // 1. Configurando as fontes idênticas ao Menu Principal
        this.fontTitle = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));
        this.fontNormal = new BitmapFont(Gdx.files.internal("fonte_jogo.fnt"));

        this.fontTitle.setUseIntegerPositions(false);
        this.fontNormal.setUseIntegerPositions(false);

        this.fontTitle.getData().setScale(0.8f);
        this.fontNormal.getData().setScale(0.45f);

        addLetterSpacing(this.fontTitle, 4);
        addLetterSpacing(this.fontNormal, 2);

        this.backgroundTexture = new Texture("fundo_menu.png");

        // 2. Criando o Overlay Preto Dinâmico (1x1 pixel esticado depois)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        this.overlayTexture = new Texture(pixmap);
        pixmap.dispose(); // Limpa da memória RAM, já foi pra Placa de Vídeo
    }

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

        // 1. Desenha o fundo original
        game.batch.draw(backgroundTexture, 0, 0, 800, 600);

        // 2. Aplica a transparência no pincel e desenha o véu preto
        game.batch.setColor(1f, 1f, 1f, 0.85f); // 0.85f = 85% opaco, 15% transparente
        game.batch.draw(overlayTexture, 0, 0, 800, 600);
        game.batch.setColor(Color.WHITE); // Reseta a cor do pincel para não afetar as letras

        // 3. Desenha os Textos com Contorno
        drawOutlinedText(fontTitle, "COMO JOGAR", 260, 550);

        int textX = 100;

        drawOutlinedText(fontNormal, "CONTROLES:", textX, 470);
        drawOutlinedText(fontNormal, "- Player 1 (Verde): Setas Direcionais", textX + 20, 430);
        drawOutlinedText(fontNormal, "- Player 2 (Azul): Teclas W A S D", textX + 20, 390);

        drawOutlinedText(fontNormal, "REGRAS:", textX, 330);
        drawOutlinedText(fontNormal, "- Coma a maca para crescer e fazer pontos.", textX + 20, 290);
        drawOutlinedText(fontNormal, "- Cuidado! O jogo fica mais rapido a cada maca.", textX + 20, 260);
        drawOutlinedText(fontNormal, "- Bater na outra cobra, no corpo ou nas rochas causa GAME OVER.", textX + 20, 230);

        drawOutlinedText(fontNormal, "ITENS ESPECIAIS:", textX, 170);
        drawOutlinedText(fontNormal, "- ESTRELA: Multiplica os pontos por 3 temporariamente.", textX + 20, 130);
        drawOutlinedText(fontNormal, "- ESCUDO: Fica invulneravel e atravessa tudo temporariamente.", textX + 20, 100);

        drawOutlinedText(fontNormal, "> Pressione [ESC] para Voltar", 260, 40);

        game.batch.end();
    }

    // Método de contorno idêntico ao do Menu
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

    // Método de espaçamento idêntico ao do Menu
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

    @Override
    public void dispose() {
        fontTitle.dispose();
        fontNormal.dispose();
        backgroundTexture.dispose();
        overlayTexture.dispose(); // Não esqueça de descartar!
    }
}
