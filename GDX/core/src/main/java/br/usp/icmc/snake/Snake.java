package br.usp.icmc.snake;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * ARQUITETURA (ENTIDADE / MODELO):
 * Esta classe representa a entidade do jogador. Repare no forte Encapsulamento:
 * A Snake não sabe o tamanho do mapa, não sabe desenhar a si mesma e não sabe se bateu. 
 * Ela APENAS guarda o seu próprio estado (corpo, direção, pontos) e obedece a ordens de movimento.
 */
public class Snake {
    
    // DECISÃO DE ESTRUTURA DE DADOS: O Deque (Fila Duplamente Terminada).
    // Usar um ArrayList custaria O(N) de processamento para mover todos os blocos do corpo a cada passo.
    // Com o Deque, a complexidade é O(1): nós apenas inserimos uma nova cabeça no início da fila
    // e removemos o último bloco da cauda no final.
    private final Deque<GridPosition> body;
    private Direction currentDirection;
    private boolean isAlive;
    private int score;

    // --- Sistema de Power-ups (Estado Local) ---
    // A cobra guarda em si mesma quantos 'ticks' de poder ela ainda tem.
    private int invulnerableTicks = 0;
    private int multiplierTicks = 0;

    /**
     * CONSTRUTOR:
     * Inicializa a cobra com 3 blocos (cabeça + 2 de corpo).
     * O Switch projeta a cauda matematicamente para o lado oposto da direção inicial.
     */
    public Snake(GridPosition startPosition, Direction startDirection) {
        this.body = new LinkedList<>();
        this.currentDirection = startDirection;
        this.isAlive = true;
        this.score = 0;

        this.body.addFirst(startPosition);

        // Algoritmo de "nascimento": Cria o corpo inicial para trás da cabeça
        for (int i = 1; i <= 2; i++) {
            int tailX = startPosition.x();
            int tailY = startPosition.y();

            switch (startDirection) {
                case UP -> tailY -= i;
                case DOWN -> tailY += i;
                case LEFT -> tailX += i;
                case RIGHT -> tailX -= i;
            }
            this.body.addLast(new GridPosition(tailX, tailY));
        }
    }

    // ENCAPSULAMENTO DEFENSIVO: Retorna uma cópia da lista (`List.copyOf`) 
    // para impedir que outras classes modifiquem o corpo da cobra diretamente.
    public List<GridPosition> getBody() { return List.copyOf(body); }
    public GridPosition getHead() { return body.peekFirst(); }

    /**
     * REGRA DE NEGÓCIO: Controle de Input
     * Impede que o jogador vire a cobra 180 graus (ex: ir para a esquerda enquanto anda para a direita),
     * o que causaria suicídio instantâneo.
     */
    public void setDirection(Direction newDirection) {
        if (!this.currentDirection.isOpposite(newDirection)) {
            this.currentDirection = newDirection;
        }
    }

    /**
     * MATEMÁTICA E LÓGICA (Chamado pelo GameWorld antes de mover):
     * Este método calcula o FUTURO. Ele descobre onde a cabeça vai estar no próximo passo.
     */
    public GridPosition getNextHeadPosition(int worldWidth, int worldHeight) {
        GridPosition head = getHead();
        int nextX = head.x();
        int nextY = head.y();

        // 1. Projeta a coordenada baseada na direção
        switch (currentDirection) {
            case UP -> nextY += 1;
            case DOWN -> nextY -= 1;
            case LEFT -> nextX -= 1;
            case RIGHT -> nextX += 1;
        }

        // 2. MATEMÁTICA DO WRAP-AROUND (O famoso "Atravessa Parede")
        // O operador Módulo (%) divide e pega o resto. 
        // Se nextX for 40 num mapa de largura 40, (40+40)%40 = 0. Ele volta pra esquerda do mapa!
        // Se nextX for -1 (saiu pela esquerda), (-1+40)%40 = 39. Ele aparece na direita!
        // Isso elimina totalmente a necessidade de usar condicionais "if/else".
        nextX = (nextX + worldWidth) % worldWidth;
        nextY = (nextY + worldHeight) % worldHeight;

        return new GridPosition(nextX, nextY);
    }

    /**
     * AÇÃO: Mover normalmente
     * Insere a nova cabeça na frente (addFirst) e apaga a ponta da cauda (removeLast).
     */
    public void move(GridPosition newHead) {
        body.addFirst(newHead);
        body.removeLast();
    }

    /**
     * AÇÃO: Crescer
     * Insere a nova cabeça, MAS NÃO apaga a cauda. O tamanho do Deque aumenta em 1.
     */
    public void grow(GridPosition newHead) {
        body.addFirst(newHead);
        
        // Aplicação do Power Up: Operador Ternário.
        // Se multiplierTicks for maior que 0, soma 3. Senão, soma 1.
        score += (multiplierTicks > 0) ? 3 : 1;
    }

    // --- Gerenciamento de Buffs ---
    // Chamado pelo GameWorld a cada "tick" para desgastar o tempo do poder.
    public void updateBuffs() {
        if (invulnerableTicks > 0) invulnerableTicks--;
        if (multiplierTicks > 0) multiplierTicks--;
    }

    public void activateInvulnerability(int durationInTicks) { this.invulnerableTicks = durationInTicks; }
    public void activateMultiplier(int durationInTicks) { this.multiplierTicks = durationInTicks; }

    public boolean isInvulnerable() { return invulnerableTicks > 0; }
    public boolean hasMultiplier() { return multiplierTicks > 0; }

    // Verifica se uma posição 'pos' faz parte do corpo da cobra (Usado para detectar colisões)
    public boolean checkCollision(GridPosition pos) { return body.contains(pos); }
    
    public int getScore() { return score; }
    public void die() { this.isAlive = false; }
    public boolean isAlive() { return isAlive; }
}
