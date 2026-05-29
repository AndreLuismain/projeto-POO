# 🐍 Jogo da Cobrinha Pixelado (Snake Game)

Um clone moderno e expandido do clássico jogo da cobrinha (Snake), construído do zero com foco em arquitetura Orientada a Objetos, separação de responsabilidades (Backend/Frontend) e persistência de dados local.

Desenvolvido em **Java 21** e **LibGDX** como projeto prático para a disciplina de Programação Orientada a Objetos (SCC504) do Instituto de Ciências Matemáticas e de Computação (ICMC-USP).

## Tecnologias Utilizadas
* **Linguagem:** Java 21
* **Framework Gráfico:** LibGDX (Desktop/LWJGL3)
* **Build System:** Gradle
* **Arquitetura:** Design Patterns aplicados à Game Loop, separação estrita de regras de negócio (GameWorld/Snake) e apresentação (Screens).

## Funcionalidades e Mecânicas

### Core Gameplay
* **Multiplayer Local:** Player 1 (Setas Direcionais) e Player 2 (WASD).
* **Wrap-around:** O cenário não tem bordas sólidas, as cobras realizam *teletransporte* de uma extremidade à outra da matriz.
* **Sistema de Colisões Letal:** Morte ao colidir contra a outra cobra, o próprio corpo ou os obstáculos do mapa.

### Sistemas Dinâmicos
* **Geração de Obstáculos:** Rochas surgem no cenário dinamicamente ao longo do tempo (baseado em ticks), limitando o espaço e aumentando a dificuldade.
* **Resolução Dinâmica:** O jogador pode escolher o tamanho da grade (Pequeno, Normal, Épico) e a velocidade inicial diretamente pelo Menu Principal.

### Itens Especiais (Power-ups)
* 🍎 **Comida Normal:** Cresce a cobra e aumenta ligeiramente a velocidade geral do jogo.
* ⭐ **Estrela (Multiplicador):** Multiplica a pontuação ganha por 3 temporariamente.
* 🛡️ **Escudo (Invulnerabilidade):** Concede o efeito fantasma (alpha visual e invulnerabilidade), permitindo atravessar a outra cobra, obstáculos e o próprio corpo por um curto período.

### UI & Persistência de Dados
* **I/O de Arquivos:** Sistema de High Scores isolado (`HighScoreManager`) que lê e grava os 5 melhores pontuadores em um arquivo `.txt` local.
* **Interface Personalizada:** Fontes em Bitmap modificadas nativamente via código para ajustes de *spacing* e contornos estilizados (Tinting/Overlay).
* **Áudio:** Efeitos sonoros para coleta de itens, power-ups e mortes gerenciados por *flags* no ciclo de renderização.

## Autores
* **André Luís** & **Renan Soriano** *Estudantes de Sistemas de Informação - ICMC/USP.*
