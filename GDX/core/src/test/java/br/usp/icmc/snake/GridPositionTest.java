package br.usp.icmc.snake;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o record GridPosition.
 * Validação: igualdade e uso como valor de coordenada.
 */
@DisplayName("GridPosition Tests")
class GridPositionTest {

    @Test
    @DisplayName("Duas posições iguais devem ser iguais")
    void testEqualPositions() {
        GridPosition pos1 = new GridPosition(5, 10);
        GridPosition pos2 = new GridPosition(5, 10);
        assertEquals(pos1, pos2);
    }

    @Test
    @DisplayName("Duas posições diferentes devem ser diferentes")
    void testDifferentPositions() {
        GridPosition pos1 = new GridPosition(5, 10);
        GridPosition pos2 = new GridPosition(5, 11);
        assertNotEquals(pos1, pos2);
    }

    @Test
    @DisplayName("Posições com X diferente são diferentes")
    void testDifferentX() {
        GridPosition pos1 = new GridPosition(5, 10);
        GridPosition pos2 = new GridPosition(6, 10);
        assertNotEquals(pos1, pos2);
    }

    @Test
    @DisplayName("Posições com Y diferente são diferentes")
    void testDifferentY() {
        GridPosition pos1 = new GridPosition(5, 10);
        GridPosition pos2 = new GridPosition(5, 11);
        assertNotEquals(pos1, pos2);
    }

    @Test
    @DisplayName("Acessar coordenada X")
    void testAccessX() {
        GridPosition pos = new GridPosition(7, 3);
        assertEquals(7, pos.x());
    }

    @Test
    @DisplayName("Acessar coordenada Y")
    void testAccessY() {
        GridPosition pos = new GridPosition(7, 3);
        assertEquals(3, pos.y());
    }

    @Test
    @DisplayName("Posições com valor negativo")
    void testNegativeCoordinates() {
        GridPosition pos1 = new GridPosition(-5, -10);
        GridPosition pos2 = new GridPosition(-5, -10);
        assertEquals(pos1, pos2);
    }

    @Test
    @DisplayName("Posição com zero")
    void testZeroCoordinates() {
        GridPosition pos = new GridPosition(0, 0);
        assertEquals(0, pos.x());
        assertEquals(0, pos.y());
    }

    @Test
    @DisplayName("GridPosition é imutável (record)")
    void testImmutability() {
        GridPosition pos1 = new GridPosition(5, 10);
        GridPosition pos2 = new GridPosition(5, 10);
        
        // Records devem ser imutáveis, então criar uma nova com mesmos valores
        // deve resultar em duas instâncias distintas porém iguais
        assertNotSame(pos1, pos2);
        assertEquals(pos1, pos2);
    }

    @Test
    @DisplayName("Hash code é consistente para posições iguais")
    void testHashCodeConsistency() {
        GridPosition pos1 = new GridPosition(5, 10);
        GridPosition pos2 = new GridPosition(5, 10);
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }

    @Test
    @DisplayName("GridPosition funciona em coleções")
    void testInCollections() {
        GridPosition pos1 = new GridPosition(5, 10);
        GridPosition pos2 = new GridPosition(5, 10);
        GridPosition pos3 = new GridPosition(3, 7);

        var positions = new java.util.ArrayList<GridPosition>();
        positions.add(pos1);
        positions.add(pos3);

        assertTrue(positions.contains(pos2)); // pos2 deve ser igual a pos1
        assertTrue(positions.contains(pos3));
    }
}
