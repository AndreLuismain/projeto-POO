package br.usp.icmc.snake;

/**
 * ARQUITETURA (TIPO DE DADO / ENUMERAÇÃO):
 * Usar um Enum (Enumerador) em vez de inteiros (ex: 0, 1, 2, 3) ou Strings (ex: "CIMA", "BAIXO") 
 * é uma prática chamada "Type Safety" (Segurança de Tipo). Isso garante que o compilador do Java 
 * nos impeça de passar um valor inválido (como "DIAGONAL" ou "5") para o movimento da cobra, 
 * evitando bugs e crashes (NullPointerException ou comportamentos inesperados) em tempo de execução.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * REGRA DE NEGÓCIO DA COBRA:
     * Este método é uma regra puramente lógica encapsulada dentro do próprio Enum.
     * Ele verifica se a nova direção que o jogador tentou ir é o exato oposto da direção atual.
     * * Conexão: A classe Snake chama este método na função 'setDirection()'.
     * Motivo: Impede que o jogador vire 180 graus instantaneamente (ex: ir para baixo 
     * enquanto sobe), o que faria a cobra colidir com o próprio "pescoço" e morrer injustamente.
     */
    public boolean isOpposite(Direction other) {
        return this == UP && other == DOWN ||
               this == DOWN && other == UP ||
               this == LEFT && other == RIGHT ||
               this == RIGHT && other == LEFT;
    }
}
