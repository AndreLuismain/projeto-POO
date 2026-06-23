package br.usp.icmc.snake;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a enumeração Direction.
 * Validação da regra de negócio: isOpposite() para todos os pares relevantes.
 */
@DisplayName("Direction Tests")
class DirectionTest {

    @Test
    @DisplayName("UP é oposto de DOWN")
    void testUpIsOppositeOfDown() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN));
    }

    @Test
    @DisplayName("DOWN é oposto de UP")
    void testDownIsOppositeOfUp() {
        assertTrue(Direction.DOWN.isOpposite(Direction.UP));
    }

    @Test
    @DisplayName("LEFT é oposto de RIGHT")
    void testLeftIsOppositeOfRight() {
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT));
    }

    @Test
    @DisplayName("RIGHT é oposto de LEFT")
    void testRightIsOppositeOfLeft() {
        assertTrue(Direction.RIGHT.isOpposite(Direction.LEFT));
    }

    @Test
    @DisplayName("UP NÃO é oposto de LEFT")
    void testUpIsNotOppositeOfLeft() {
        assertFalse(Direction.UP.isOpposite(Direction.LEFT));
    }

    @Test
    @DisplayName("UP NÃO é oposto de RIGHT")
    void testUpIsNotOppositeOfRight() {
        assertFalse(Direction.UP.isOpposite(Direction.RIGHT));
    }

    @Test
    @DisplayName("DOWN NÃO é oposto de LEFT")
    void testDownIsNotOppositeOfLeft() {
        assertFalse(Direction.DOWN.isOpposite(Direction.LEFT));
    }

    @Test
    @DisplayName("DOWN NÃO é oposto de RIGHT")
    void testDownIsNotOppositeOfRight() {
        assertFalse(Direction.DOWN.isOpposite(Direction.RIGHT));
    }

    @Test
    @DisplayName("LEFT NÃO é oposto de UP")
    void testLeftIsNotOppositeOfUp() {
        assertFalse(Direction.LEFT.isOpposite(Direction.UP));
    }

    @Test
    @DisplayName("LEFT NÃO é oposto de DOWN")
    void testLeftIsNotOppositeOfDown() {
        assertFalse(Direction.LEFT.isOpposite(Direction.DOWN));
    }

    @Test
    @DisplayName("RIGHT NÃO é oposto de UP")
    void testRightIsNotOppositeOfUp() {
        assertFalse(Direction.RIGHT.isOpposite(Direction.UP));
    }

    @Test
    @DisplayName("RIGHT NÃO é oposto de DOWN")
    void testRightIsNotOppositeOfDown() {
        assertFalse(Direction.RIGHT.isOpposite(Direction.DOWN));
    }
}
