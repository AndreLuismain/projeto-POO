package br.usp.icmc.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe Snake.
 * Validações:
 * - criação da cobra com corpo inicial correto
 * - bloqueio de direção oposta
 * - crescimento e pontuação
 * - atualização de buffs e expiração dos efeitos
 * - colisão com posições do corpo
 */
@DisplayName("Snake Tests")
class SnakeTest {

    private Snake snake;

    @BeforeEach
    void setUp() {
        snake = new Snake(new GridPosition(10, 10), Direction.RIGHT);
    }

    @Test
    @DisplayName("Cobra inicializa com 3 blocos")
    void testInitialBodySize() {
        assertEquals(3, snake.getBody().size());
    }

    @Test
    @DisplayName("Cabeça da cobra está na posição inicial")
    void testInitialHeadPosition() {
        GridPosition head = snake.getHead();
        assertEquals(10, head.x());
        assertEquals(10, head.y());
    }

    @Test
    @DisplayName("Corpo inicial é criado corretamente (para trás da cabeça)")
    void testInitialBodyStructure() {
        List<GridPosition> body = snake.getBody();
        GridPosition head = body.get(0);
        GridPosition segment1 = body.get(1);
        GridPosition segment2 = body.get(2);

        assertEquals(new GridPosition(10, 10), head);
        // Com direção RIGHT, o corpo é criado para trás (LEFT)
        assertEquals(new GridPosition(9, 10), segment1);  // tailX -= 1
        assertEquals(new GridPosition(8, 10), segment2);  // tailX -= 2
    }

    @Test
    @DisplayName("Cobra inicia viva")
    void testInitialAliveStatus() {
        assertTrue(snake.isAlive());
    }

    @Test
    @DisplayName("Cobra inicia com score 0")
    void testInitialScore() {
        assertEquals(0, snake.getScore());
    }

    @Test
    @DisplayName("Direção oposta é bloqueada")
    void testOppositeDirectionBlocked() {
        snake.setDirection(Direction.LEFT); // Oposto de RIGHT
        // Força o próximo passo para verificar que a direção NÃO mudou
        GridPosition nextHead = snake.getNextHeadPosition(40, 40);
        
        // Se a direção tivesse mudado para LEFT, a próxima cabeça teria X=9
        // Como está bloqueada em RIGHT, deve ter X=11
        assertEquals(11, nextHead.x());
    }

    @Test
    @DisplayName("Direção válida é aceita")
    void testValidDirectionChange() {
        snake.setDirection(Direction.UP);
        GridPosition nextHead = snake.getNextHeadPosition(40, 40);
        
        // Com Direction.UP, o Y deve aumentar
        assertEquals(11, nextHead.y());
    }

    @Test
    @DisplayName("Movimento básico (move) reduz o tamanho do corpo")
    void testMoveDoesNotGrow() {
        GridPosition initialHead = snake.getHead();
        GridPosition newHead = new GridPosition(11, 10);
        
        snake.move(newHead);
        
        // Após move, o tamanho deve ser o mesmo (3)
        assertEquals(3, snake.getBody().size());
        assertEquals(newHead, snake.getHead());
    }

    @Test
    @DisplayName("Crescimento (grow) aumenta o tamanho do corpo")
    void testGrowIncreasesSize() {
        int initialSize = snake.getBody().size();
        GridPosition newHead = new GridPosition(11, 10);
        
        snake.grow(newHead);
        
        // Após grow, o tamanho deve aumentar em 1
        assertEquals(initialSize + 1, snake.getBody().size());
    }

    @Test
    @DisplayName("Crescimento aumenta pontuação em 1")
    void testGrowIncreasesScore() {
        GridPosition newHead = new GridPosition(11, 10);
        
        snake.grow(newHead);
        
        assertEquals(1, snake.getScore());
    }

    @Test
    @DisplayName("Crescimento com multiplicador aumenta pontuação em 3")
    void testGrowWithMultiplier() {
        snake.activateMultiplier(50);
        GridPosition newHead = new GridPosition(11, 10);
        
        snake.grow(newHead);
        
        assertEquals(3, snake.getScore());
    }

    @Test
    @DisplayName("Crescimento sem multiplicador após expiração volta a 1 ponto")
    void testGrowAfterMultiplierExpires() {
        snake.activateMultiplier(1);
        snake.updateBuffs(); // Reduz multiplierTicks a 0
        
        GridPosition newHead = new GridPosition(11, 10);
        snake.grow(newHead);
        
        assertEquals(1, snake.getScore());
    }

    @Test
    @DisplayName("Invulnerabilidade é ativada corretamente")
    void testActivateInvulnerability() {
        assertFalse(snake.isInvulnerable());
        
        snake.activateInvulnerability(50);
        
        assertTrue(snake.isInvulnerable());
    }

    @Test
    @DisplayName("Invulnerabilidade expira após ticks")
    void testInvulnerabilityExpires() {
        snake.activateInvulnerability(2);
        
        assertTrue(snake.isInvulnerable());
        snake.updateBuffs(); // 2 -> 1
        assertTrue(snake.isInvulnerable());
        snake.updateBuffs(); // 1 -> 0
        assertFalse(snake.isInvulnerable());
    }

    @Test
    @DisplayName("Multiplicador é ativado corretamente")
    void testActivateMultiplier() {
        assertFalse(snake.hasMultiplier());
        
        snake.activateMultiplier(50);
        
        assertTrue(snake.hasMultiplier());
    }

    @Test
    @DisplayName("Multiplicador expira após ticks")
    void testMultiplierExpires() {
        snake.activateMultiplier(3);
        
        assertTrue(snake.hasMultiplier());
        snake.updateBuffs(); // 3 -> 2
        assertTrue(snake.hasMultiplier());
        snake.updateBuffs(); // 2 -> 1
        assertTrue(snake.hasMultiplier());
        snake.updateBuffs(); // 1 -> 0
        assertFalse(snake.hasMultiplier());
    }

    @Test
    @DisplayName("Cobra detecta colisão com sua própria cauda")
    void testCollisionWithOwnBody() {
        GridPosition tailPosition = snake.getBody().get(snake.getBody().size() - 1);
        
        assertTrue(snake.checkCollision(tailPosition));
    }

    @Test
    @DisplayName("Cobra detecta colisão com sua própria cabeça")
    void testCollisionWithOwnHead() {
        GridPosition head = snake.getHead();
        
        assertTrue(snake.checkCollision(head));
    }

    @Test
    @DisplayName("Cobra não detecta colisão com posição vazia")
    void testNoCollisionWithEmpty() {
        GridPosition emptyPos = new GridPosition(0, 0);
        
        assertFalse(snake.checkCollision(emptyPos));
    }

    @Test
    @DisplayName("Cobra morre corretamente")
    void testDie() {
        assertTrue(snake.isAlive());
        
        snake.die();
        
        assertFalse(snake.isAlive());
    }

    @Test
    @DisplayName("Wrap-around funciona corretamente (saída pela direita)")
    void testWrapAroundRight() {
        GridPosition head = new GridPosition(39, 10);
        Snake snakeAtEdge = new Snake(head, Direction.RIGHT);
        
        GridPosition nextHead = snakeAtEdge.getNextHeadPosition(40, 40);
        
        // Deve voltar para x=0 (wrap-around)
        assertEquals(0, nextHead.x());
    }

    @Test
    @DisplayName("Wrap-around funciona corretamente (saída pela esquerda)")
    void testWrapAroundLeft() {
        GridPosition head = new GridPosition(0, 10);
        Snake snakeAtEdge = new Snake(head, Direction.LEFT);
        
        GridPosition nextHead = snakeAtEdge.getNextHeadPosition(40, 40);
        
        // Deve voltar para x=39 (wrap-around)
        assertEquals(39, nextHead.x());
    }

    @Test
    @DisplayName("Wrap-around funciona corretamente (saída por cima)")
    void testWrapAroundUp() {
        GridPosition head = new GridPosition(10, 39);
        Snake snakeAtEdge = new Snake(head, Direction.UP);
        
        GridPosition nextHead = snakeAtEdge.getNextHeadPosition(40, 40);
        
        // Deve voltar para y=0 (wrap-around)
        assertEquals(0, nextHead.y());
    }

    @Test
    @DisplayName("Wrap-around funciona corretamente (saída por baixo)")
    void testWrapAroundDown() {
        GridPosition head = new GridPosition(10, 0);
        Snake snakeAtEdge = new Snake(head, Direction.DOWN);
        
        GridPosition nextHead = snakeAtEdge.getNextHeadPosition(40, 40);
        
        // Deve voltar para y=39 (wrap-around)
        assertEquals(39, nextHead.y());
    }

    @Test
    @DisplayName("Múltiplos crescimentos acumulam pontuação")
    void testMultipleGrowths() {
        snake.grow(new GridPosition(11, 10));
        snake.grow(new GridPosition(12, 10));
        snake.grow(new GridPosition(13, 10));
        
        assertEquals(3, snake.getScore());
    }

    @Test
    @DisplayName("Crescimento com e sem multiplicador")
    void testGrowthMixedWithMultiplier() {
        snake.grow(new GridPosition(11, 10)); // +1
        snake.activateMultiplier(50);
        snake.grow(new GridPosition(12, 10)); // +3
        snake.grow(new GridPosition(13, 10)); // +3
        
        assertEquals(7, snake.getScore());
    }
}
