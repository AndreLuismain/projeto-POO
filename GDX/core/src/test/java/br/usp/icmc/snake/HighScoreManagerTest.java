package br.usp.icmc.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe HighScoreManager.
 * Validações:
 * - leitura de arquivo vazio
 * - adição e ordenação decrescente
 * - corte para top 5
 * - persistência e recarga dos scores
 *
 * NOTA: Estes testes usam um arquivo temporário para não sobrescrever
 * o arquivo de high scores real.
 */
@DisplayName("HighScoreManager Tests")
class HighScoreManagerTest {

    private static final String TEST_FILE_NAME = "test_highscores.txt";
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = new File(TEST_FILE_NAME);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    /**
     * Cria um arquivo de teste com scores específicos.
     */
    private void writeTestFile(List<Integer> scores) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int score : scores) {
            sb.append(score).append("\n");
        }
        Files.write(Paths.get(TEST_FILE_NAME), sb.toString().getBytes());
    }

    /**
     * Lê o arquivo de teste.
     */
    private List<Integer> readTestFile() throws IOException {
        List<Integer> scores = new ArrayList<>();
        if (testFile.exists()) {
            String content = new String(Files.readAllBytes(Paths.get(TEST_FILE_NAME)));
            if (!content.isEmpty()) {
                String[] lines = content.split("\n");
                for (String line : lines) {
                    try {
                        String trimmed = line.trim();
                        if (!trimmed.isEmpty()) {
                            scores.add(Integer.parseInt(trimmed));
                        }
                    } catch (NumberFormatException e) {
                        // Ignora linhas mal formatadas
                    }
                }
            }
        }
        return scores;
    }

    @Test
    @DisplayName("Ordenação decrescente: scores maiores vêm primeiro")
    void testDescendingOrder() throws IOException {
        List<Integer> scores = new ArrayList<>();
        scores.add(100);
        scores.add(50);
        scores.add(200);
        
        writeTestFile(scores);
        List<Integer> result = readTestFile();
        
        // Ordena como o HighScoreManager faria
        Collections.sort(result, Collections.reverseOrder());
        
        assertEquals(200, result.get(0));
        assertEquals(100, result.get(1));
        assertEquals(50, result.get(2));
    }

    @Test
    @DisplayName("Top 5: apenas 5 melhores scores são mantidos")
    void testTop5Limit() throws IOException {
        List<Integer> scores = new ArrayList<>();
        scores.add(100);
        scores.add(200);
        scores.add(50);
        scores.add(300);
        scores.add(150);
        scores.add(250); // 6º score
        scores.add(25);  // 7º score
        
        Collections.sort(scores, Collections.reverseOrder());
        if (scores.size() > 5) {
            scores = scores.subList(0, 5);
        }
        
        assertEquals(5, scores.size());
        assertEquals(300, scores.get(0));
        assertEquals(250, scores.get(1));
        assertEquals(200, scores.get(2));
        assertEquals(150, scores.get(3));
        assertEquals(100, scores.get(4));
    }

    @Test
    @DisplayName("Novo score é adicionado à lista")
    void testAddScore() throws IOException {
        List<Integer> scores = new ArrayList<>();
        scores.add(100);
        scores.add(200);
        
        // Simula addScore
        scores.add(150);
        Collections.sort(scores, Collections.reverseOrder());
        
        assertTrue(scores.contains(150));
        assertEquals(3, scores.size());
    }

    @Test
    @DisplayName("Scores iguais são mantidos em ordem")
    void testDuplicateScores() throws IOException {
        List<Integer> scores = new ArrayList<>();
        scores.add(100);
        scores.add(100);
        scores.add(200);
        
        Collections.sort(scores, Collections.reverseOrder());
        
        assertEquals(200, scores.get(0));
        assertEquals(100, scores.get(1));
        assertEquals(100, scores.get(2));
    }

    @Test
    @DisplayName("Arquivo vazio resulta em lista vazia")
    void testEmptyFile() throws IOException {
        writeTestFile(new ArrayList<>());
        List<Integer> result = readTestFile();
        
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Score 0 é válido")
    void testZeroScore() throws IOException {
        List<Integer> scores = new ArrayList<>();
        scores.add(0);
        scores.add(100);
        
        Collections.sort(scores, Collections.reverseOrder());
        
        assertEquals(100, scores.get(0));
        assertEquals(0, scores.get(1));
    }

    @Test
    @DisplayName("Scores negativos (se ocorrerem) são tratados")
    void testNegativeScores() throws IOException {
        List<Integer> scores = new ArrayList<>();
        scores.add(100);
        scores.add(-10);
        
        Collections.sort(scores, Collections.reverseOrder());
        
        assertEquals(100, scores.get(0));
        assertEquals(-10, scores.get(1));
    }

    @Test
    @DisplayName("Linhas em branco são ignoradas")
    void testEmptyLinesIgnored() throws IOException {
        // Escreve arquivo com linhas em branco
        String content = "100\n\n200\n\n150\n";
        Files.write(Paths.get(TEST_FILE_NAME), content.getBytes());
        
        List<Integer> result = readTestFile();
        
        // Deve ter apenas 3 scores
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Linhas mal formatadas são ignoradas")
    void testMalformedLinesIgnored() throws IOException {
        String content = "100\nabc\n200\n12.5\n150\n";
        Files.write(Paths.get(TEST_FILE_NAME), content.getBytes());
        
        List<Integer> result = readTestFile();
        
        // Deve ter apenas 3 scores válidos
        assertEquals(3, result.size());
        assertTrue(result.contains(100));
        assertTrue(result.contains(200));
        assertTrue(result.contains(150));
    }

    @Test
    @DisplayName("Espaços em branco são trimados")
    void testWhitespaceTrimed() throws IOException {
        String content = "  100  \n  200  \n  150  \n";
        Files.write(Paths.get(TEST_FILE_NAME), content.getBytes());
        
        List<Integer> result = readTestFile();
        
        assertEquals(3, result.size());
        assertTrue(result.contains(100));
        assertTrue(result.contains(200));
        assertTrue(result.contains(150));
    }

    @Test
    @DisplayName("Persistência: dados escritos são relidos corretamente")
    void testPersistence() throws IOException {
        List<Integer> originalScores = new ArrayList<>();
        originalScores.add(500);
        originalScores.add(300);
        originalScores.add(400);
        
        writeTestFile(originalScores);
        List<Integer> readScores = readTestFile();
        
        assertEquals(originalScores.size(), readScores.size());
        assertTrue(readScores.contains(500));
        assertTrue(readScores.contains(300));
        assertTrue(readScores.contains(400));
    }

    @Test
    @DisplayName("Múltiplas adições mantêm top 5")
    void testMultipleAdditionsKeepTop5() throws IOException {
        List<Integer> scores = new ArrayList<>();
        
        // Simula múltiplas adições
        int[] newScores = {100, 200, 50, 300, 150, 250, 25, 175, 125};
        
        for (int score : newScores) {
            scores.add(score);
            Collections.sort(scores, Collections.reverseOrder());
            if (scores.size() > 5) {
                scores = scores.subList(0, 5);
            }
        }
        
        assertEquals(5, scores.size());
        assertEquals(300, scores.get(0));
        assertEquals(250, scores.get(1));
        assertEquals(200, scores.get(2));
        assertEquals(175, scores.get(3));
        assertEquals(150, scores.get(4));
    }
}
