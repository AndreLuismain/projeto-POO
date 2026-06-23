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

/**
 * ARQUITETURA (VISÃO / FRONTEND):
 * Esta classe é puramente responsável por Desenhar e Coletar Input.
 * Repare no Encapsulamento: Ela NÃO calcula regras do jogo. Ela apenas "pergunta" 
 * ao GameWorld onde as coisas estão e as desenha na tela usando o SpriteBatch.
 */
public class GameScreen extends ScreenAdapter {
    // Referências Arquiteturais
    private final SnakeGame game; // Acesso aos recursos globais (SpriteBatch, Fonte principal)
    private final GameWorld world; // O Backend (Modelo) que contém os dados a serem lidos

    // --- Recursos Visuais e Sonoros (Assets) ---
    private Texture chaoClaro;
    private Texture chaoEscuro;
    private Texture comidaTexture;
    private Texture cabeca1Texture;
    private Texture cabeca2Texture;
    private Texture corpo1Texture;
    private Texture corpo2Texture;
    private Texture obstaculoTexture;
    private Texture escudoTexture; 
    private Texture estrelaTexture; 
    
    // TextureRegion permite recortar uma parte específica de uma imagem maior
    private TextureRegion escudoRegion;
    private TextureRegion estrelaRegion;

    private BitmapFont fontScore;
    private Sound eatSound;
    private Sound dieSound;
    private Sound powerUpSound; 

    // Câmera e Viewport: Garantem que o jogo não fique esticado se a janela for redimensionada
    private OrthographicCamera camera;
    private Viewport viewport;

    private float timer = 0;
    
    // cellSize é a ponte matemática entre o Backend (Matriz) e o Frontend (Pixels)
    private final int cellSize; 

    /**
     * CONSTRUTOR:
     * Instanciação controlada de memória. Todas as texturas são carregadas para a VRAM 
     * uma única vez no início, evitando criar novas imagens dentro do laço de renderização (Memory Leak).
     */
    public GameScreen(SnakeGame game, int gridWidth, int gridHeight, float startSpeed) {
        this.game = game;
        // Injeção de dependência e inicialização do Mundo Lógico
        this.world = new GameWorld(gridWidth, gridHeight, startSpeed);
        
        // Descobre quantos pixels vale cada "bloco" da matriz lógica
        // Ex: Tela de 800px / Matriz de 40 blocos = 20 pixels por bloco.
        this.cellSize = 800 / gridWidth;

        this.chaoClaro = new Texture("chaoClaro.png");
        this.chaoEscuro = new Texture("chaoEscuro.png");
        this.comidaTexture = new Texture("comida1.png");
        this.cabeca1Texture = new Texture("cabeca1.png");
        this.cabeca2Texture = new Texture("cabeca2.png");
        this.corpo1Texture = new Texture("corpo1.png");
        this.corpo2Texture = new Texture("corpo2.png");
        this.obstaculoTexture = new Texture("obstaculo.png");

        this.escudoTexture = new Texture("escudo.png");
        this.estrelaTexture = new Texture("estrela.png");
        
        // HACK GRÁFICO: As imagens originais tinham bordas transparentes enormes.
        // Em vez de editar a imagem externa, usamos TextureRegion para fazer o "crop" via código.
        this.escudoRegion = new TextureRegion(escudoTexture, 224, 45, 228, 277);
        this.estrelaRegion = new TextureRegion(estrelaTexture, 201, 44, 275, 267);

        this.fontScore = new BitmapFont();
        this.fontScore.getData().setScale(1.2f);

        this.eatSound = Gdx.audio.newSound(Gdx.files.internal("eat.wav"));
        this.dieSound = Gdx.audio.newSound(Gdx.files.internal("die.wav"));
        this.powerUpSound = Gdx.audio.newSound(Gdx.files.internal("eat.wav"));

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 600, camera);
    }

    /**
     * O GAME LOOP (Executado dezenas/centenas de vezes por segundo).
     * Mantém a ordem perfeita: 1. Lê inputs -> 2. Atualiza regras -> 3. Desenha -> 4. Checa Fim de Jogo.
     */
    @Override
    public void render(float delta) {
        // 1. Controller: Captura eventos de teclado
        handleInput();

        // 2. Modelo: O acumulador de tempo que desvincula a velocidade do jogo do Framerate do monitor.
        timer += delta;
        if (timer >= world.getCurrentTickTime()) {
            world.update(); // Manda o backend processar um "passo" no jogo
            timer = 0;

            // Padrão Observer simplificado (Polling): Lemos os eventos ocorridos no backend 
            // para reproduzir os efeitos sonoros no frontend sem acoplar bibliotecas de som na lógica.
            if (world.popEatSound()) eatSound.play(0.5f);
            if (world.popDieSound()) dieSound.play(0.8f);
            if (world.popPowerUpSound()) powerUpSound.play(1.0f); 
        }

        camera.update();
        game.batch.setProjectionMatrix(camera.combined); // Diz ao "pincel" para usar a perspectiva da câmera

        // 3. View: Chama o método dedicado para colocar os pixels na tela
        draw();

        // 4. Mudança de Estado: O Backend avisou que deu GameOver? 
        if (world.isGameOver()) {
            Snake p1 = world.getPlayer1();
            Snake p2 = world.getPlayer2();

            // Polimorfismo: Delega o controle do fluxo para a tela de encerramento
            game.setScreen(new GameOverScreen(
                game, world.getWinnerMessage(),
                p1.getScore(), p1.getBody().size(),
                p2.getScore(), p2.getBody().size()
            ));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * CONTROLLER:
     * Traduz o pressionamento de teclas em intenções de movimento injetadas nos objetos Snake.
     */
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

    /**
     * RENDERIZAÇÃO E TRADUÇÃO DE COORDENADAS:
     * Toda a mágica de pegar um "GridPosition(5, 5)" lógico e transformá-lo em algo desenhado na tela ocorre aqui.
     */
    private void draw() {
        // Limpa a tela com uma cor de fundo escura
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Abre o "lote" de envios para a Placa de Vídeo (Otimização)
        game.batch.begin();

        // 1. Desenho do Chão (Laço duplo calculando Par ou Ímpar para criar efeito tabuleiro de xadrez)
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

        // 2. Tradução Posicional Dinâmica: Matriz * cellSize
        for (GridPosition obs : world.getObstacles()) {
            game.batch.draw(obstaculoTexture, obs.x() * cellSize, obs.y() * cellSize, cellSize, cellSize);
        }

        // Desenho de Power Up com ajuste fino de Aspect Ratio (Proporção) e tamanho inflado
        if (world.getPowerUpPos() != null) {
            TextureRegion pRegion = (world.getPowerUpType() == 1) ? escudoRegion : estrelaRegion;
            float sizeMultiplier = 1.3f; // Destaca o power-up fazendo-o 30% maior que as maçãs comuns
            float baseSize = cellSize * sizeMultiplier;
            
            // Mantém a imagem não distorcida com base na sua proporção nativa
            float aspect = (float) pRegion.getRegionWidth() / pRegion.getRegionHeight();
            float drawWidth = aspect >= 1f ? baseSize : baseSize * aspect;
            float drawHeight = aspect >= 1f ? baseSize / aspect : baseSize;
            
            float cellCenterX = world.getPowerUpPos().x() * cellSize + cellSize / 2f;
            float cellCenterY = world.getPowerUpPos().y() * cellSize + cellSize / 2f;
            game.batch.draw(pRegion, cellCenterX - drawWidth / 2f, cellCenterY - drawHeight / 2f, drawWidth, drawHeight);
        }

        game.batch.draw(comidaTexture, world.getFood().x() * cellSize, world.getFood().y() * cellSize, cellSize, cellSize);

        // 3. Renderiza as Entidades principais repassando suas respectivas texturas
        drawSnake(world.getPlayer1(), cabeca1Texture, corpo1Texture);
        drawSnake(world.getPlayer2(), cabeca2Texture, corpo2Texture);

        // 4. UI Textual (Hud Dinâmico via Fontes)
        String p2Buffs = world.getPlayer2().isInvulnerable() ? " [ESCUDO]" : (world.getPlayer2().hasMultiplier() ? " [x3]" : "");
        String p1Buffs = world.getPlayer1().isInvulnerable() ? " [ESCUDO]" : (world.getPlayer1().hasMultiplier() ? " [x3]" : "");

        fontScore.draw(game.batch, "P2 (Azul) Score: " + world.getPlayer2().getScore() + p2Buffs, 20, 580);
        fontScore.draw(game.batch, "P1 (Verde) Score: " + world.getPlayer1().getScore() + p1Buffs, 600, 580);

        game.batch.end();
    }

    /**
     * POLIMENTO VISUAL INDIVIDUAL:
     * Varre a estrutura Deque da cobra aplicando transformações visuais.
     */
    private void drawSnake(Snake snake, Texture headTex, Texture bodyTex) {
        int index = 0;
        int size = snake.getBody().size();
        
        // Variáveis de recuo para dar o efeito que o corpo e o rabo são menores que a cabeça
        float bodyShrink = 4f;
        float tailShrink = 10f;

        // Feedback Visual de Buff: Reduz a opacidade da cobra para 30% se estiver invulnerável (Efeito Fantasma)
        float alphaEffect = snake.isInvulnerable() ? 0.3f : 1.0f;

        for (GridPosition pos : snake.getBody()) {
            float posX = pos.x() * cellSize;
            float posY = pos.y() * cellSize;

            // Manipulação de Canal Alpha da cor do SpriteBatch
            if (index == 0) { // CABEÇA
                if (bodyTex != null) {
                    game.batch.setColor(1f, 1f, 1f, 0.6f * alphaEffect);
                    game.batch.draw(bodyTex, posX + (bodyShrink / 2), posY + (bodyShrink / 2), cellSize - bodyShrink, cellSize - bodyShrink);
                }
                game.batch.setColor(1f, 1f, 1f, alphaEffect);
                game.batch.draw(headTex, posX, posY, cellSize, cellSize);

            } else if (index == size - 1) { // CAUDA (Desenha consideravelmente menor)
                if (bodyTex != null) {
                    game.batch.setColor(1f, 1f, 1f, 0.6f * alphaEffect);
                    game.batch.draw(bodyTex, posX + (tailShrink / 2), posY + (tailShrink / 2), cellSize - tailShrink, cellSize - tailShrink);
                }
            } else { // CORPO GERAL
                if (bodyTex != null) {
                    game.batch.setColor(1f, 1f, 1f, 0.6f * alphaEffect);
                    game.batch.draw(bodyTex, posX + (bodyShrink / 2), posY + (bodyShrink / 2), cellSize - bodyShrink, cellSize - bodyShrink);
                }
            }

            game.batch.setColor(1f, 1f, 1f, 1f); // Reseta a cor global para não afetar as próximas texturas
            index++;
        }
    }

    /**
     * GERENCIAMENTO DE MEMÓRIA (C/C++ nativo):
     * Diferente de arrays padrão que o Garbage Collector do Java limpa, 
     * assets visuais/sonoros no LibGDX alocam memória direta em C/C++ (Placa de vídeo e som).
     * Este método GARANTE que quando sairmos da tela, não causemos Memory Leaks no computador do usuário.
     */
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
