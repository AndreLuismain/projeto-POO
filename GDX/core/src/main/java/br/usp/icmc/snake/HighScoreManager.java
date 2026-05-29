package br.usp.icmc.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private static final String FILE_NAME = "highscores.txt";

    /**
     * Adiciona uma nova pontuação à lista, ordena de forma decrescente
     * e mantém apenas o Top 5 guardado no ficheiro local.
     */
    public static void addScore(int score) {
        List<Integer> scores = loadScores();
        scores.add(score);

        // Ordena do maior para o menor
        Collections.sort(scores, Collections.reverseOrder());

        // Mantém apenas as 5 melhores pontuações
        if (scores.size() > 5) {
            scores = scores.subList(0, 5);
        }

        saveScores(scores);
    }

    /**
     * Lê o ficheiro local e devolve a lista com o Top 5 atual.
     */
    public static List<Integer> loadScores() {
        List<Integer> scores = new ArrayList<>();
        FileHandle file = Gdx.files.local(FILE_NAME);

        if (file.exists()) {
            String text = file.readString();
            if (!text.isEmpty()) {
                String[] lines = text.split("\n");
                for (String line : lines) {
                    try {
                        scores.add(Integer.parseInt(line.trim()));
                    } catch (NumberFormatException e) {
                        // Ignora linhas mal formatadas ou corrompidas
                    }
                }
            }
        }
        return scores;
    }

    /**
     * Grava a lista de pontuações no ficheiro de texto local.
     */
    private static void saveScores(List<Integer> scores) {
        FileHandle file = Gdx.files.local(FILE_NAME);
        StringBuilder sb = new StringBuilder();
        for (int score : scores) {
            sb.append(score).append("\n");
        }
        file.writeString(sb.toString(), false); // false para sobrescrever o ficheiro antigo
    }
}
