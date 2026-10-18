# Módulo Gradle LibGDX — Snake Game (`GDX/`)

Este diretório contém o projeto multi-módulo **Gradle** em **Java 21** e **LibGDX (LWJGL3)** para o jogo **Snake Pixelado Multiplayer**, desenvolvido na disciplina de **Programação Orientada a Objetos (SCC0504 — ICMC-USP)**.

---

## 1. Arquitetura Multi-Módulo (`core` vs `lwjgl3`)

| Submódulo | Pacote Principal | Responsabilidade Técnica |
|---|---|---|
| **[`core/`](./core/)** | `br.usp.icmc.snake` | Contém 100% da lógica de domínio (*Model/Backend* isento de dependências gráficas em `GameWorld`, `Snake`, `GridPosition`, `Direction`, `HighScoreManager`), as telas de apresentação (`MainMenuScreen`, `GameScreen`, `GameOverScreen`, `HighScoreScreen`, `InstructionsScreen`) e a suíte de testes unitários **JUnit 5**. |
| **[`lwjgl3/`](./lwjgl3/)** | `br.usp.icmc.snake.lwjgl3` | Inicializador nativo Desktop (**LWJGL3 / OpenGL**), configuração de janela, FPS e empacotamento de distribuição (`Lwjgl3Launcher`, `StartupHelper`). |
| **[`assets/`](./assets/)** | — | Recursos gráficos (sprites, texturas da cobra e itens), efeitos sonoros (`.wav`/`.mp3`), fontes bitmap (`fonte_jogo.fnt`) e arquivo de persistência `highscores.txt`. |

---

## 2. Destaques de Engenharia no Domínio (`core`)

* **Estrutura de Dados `Deque<GridPosition>` (`Snake.java`):** Movimentação da cobra em complexidade de tempo $O(1)$ através de inserção na cabeça (`addFirst`) e remoção na cauda (`removeLast`), evitando o deslocamento $O(N)$ de listas convencionais.
* **Encapsulamento Defensivo:** `Snake.getBody()` retorna uma cópia imutável (`List.copyOf(body)`), protegendo a integridade da fila interna contra mutações externas.
* **Matemática Modular de *Wrap-Around*:** Travessia contínua de bordas sem condicionais aninhados via aritmética modular `(nextX + worldWidth) % worldWidth`.
* **Desacoplamento Model-View (`GameWorld.java`):** `GameWorld` processa ticks independentes de framerate, colisões, *power-ups* (Escudo de Invulnerabilidade e Multiplicador 3x) e geração procedural de obstáculos sem importar nenhuma classe gráfica do LibGDX.

---

## 3. Como Compilar, Testar e Executar

No terminal, a partir deste diretório (`GDX/`):

```powershell
# Executar a suíte de testes unitários (JUnit 5)
.\gradlew.bat test

# Iniciar o jogo em modo Desktop (LWJGL3)
.\gradlew.bat lwjgl3:run

# Gerar JAR executável em lwjgl3/build/libs/
.\gradlew.bat lwjgl3:jar
```
