<div align="center">

# ⚔️ Servidor Bridge

### Minecraft Minigame Plugin · Java · Spigot 1.8.8

Um plugin de Minecraft com partidas **The Bridge 1v1**, gerenciamento de mapas, sistemas administrativos e mecânicas personalizadas para servidores.

<br>

![Java](https://img.shields.io/badge/Java-8-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Minecraft](https://img.shields.io/badge/Minecraft-1.8.8-62A34B?style=for-the-badge\&logo=minecraft\&logoColor=white)
![Spigot](https://img.shields.io/badge/Platform-Spigot-orange?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge)

</div>

---

## 📌 Sobre o projeto

O **Servidor Bridge** é um plugin desenvolvido em Java para servidores Minecraft, com foco no minigame The Bridge e em ferramentas complementares de gerenciamento.

O projeto reúne mecânicas de partidas competitivas, controle de equipes, pontuação, gerenciamento de mundos, interfaces interativas e funcionalidades administrativas.

## 🎮 The Bridge

<table>
<tr>
<td width="50%">

### ⚔️ Sistema de partidas

* Partidas 1v1
* Filas de jogadores
* Equipes Azul e Vermelha
* Seleção de mapas
* Controle de estados da partida
* Sistema de pontuação até 5 pontos
* Detecção de regiões de pontuação
* Vitória, empate e encerramento

</td>
<td width="50%">

### 🗺️ Gerenciamento de mapas

* Criação de cópias dos mundos
* Mundos separados para partidas
* Configuração de spawns
* Regiões de pontuação
* Limites de construção
* Tentativa de limpeza dos mundos ao finalizar

</td>
</tr>
</table>

## ✨ Funcionalidades

<details>
<summary><strong>🛡️ Kits e inventários</strong></summary>

* Distribuição de equipamentos para as partidas.
* Espadas, picaretas e blocos.
* Arcos e flechas.
* Maçãs douradas.
* Gerenciamento de inventário dos jogadores.

</details>

<details>
<summary><strong>🖥️ Interface e experiência do jogador</strong></summary>

* Scoreboard personalizado.
* Títulos e efeitos sonoros.
* Cabeçalho e rodapé da lista de jogadores.
* Menus interativos.
* Estrutura para espectadores.

</details>

<details>
<summary><strong>👥 Grupos e permissões</strong></summary>

O projeto contém uma estrutura de grupos e permissões com as seguintes categorias:

| Grupo         | Identificador |
| :------------ | :------------ |
| Administrador | `ADMIN`       |
| Moderador     | `MOD`         |
| Ajudante      | `HELPER`      |
| VIP           | `VIP`         |
| Membro        | `MEMBRO`      |

</details>

<details>
<summary><strong>🔨 Sistema de punições</strong></summary>

* Banimentos permanentes e temporários.
* Silenciamentos permanentes e temporários.
* Expulsão de jogadores.
* Menus para gerenciamento de punições.

</details>

<details>
<summary><strong>🔍 Verificação de Reach</strong></summary>

O plugin implementa uma verificação própria de alcance de ataques, considerando elementos como distância, posições dos jogadores, movimentação e latência.

O sistema pode cancelar ataques considerados irregulares e aplicar ações contra jogadores detectados. Seus limites precisam de testes para avaliar a precisão e reduzir falsos positivos.

</details>

## 🧰 Tecnologias

<div align="center">

| Tecnologia         | Utilização                    |
| :----------------- | :---------------------------- |
| ☕ Java 8           | Linguagem principal           |
| 🟩 Spigot 1.8.8    | Plataforma do servidor        |
| 🔌 Bukkit API      | Eventos, comandos e mecânicas |
| 🏷️ TitleAPI 1.8.1 | Recursos de títulos           |
| ⚙️ YAML            | Configuração                  |
| 💻 Eclipse         | Ambiente de desenvolvimento   |

</div>

## 🗂️ Organização do código

| Pacote              | Responsabilidade                      |
| :------------------ | :------------------------------------ |
| `Main`              | Inicialização do plugin               |
| `TheBridge`         | Lógica das partidas                   |
| `Comandos`          | Comandos do servidor                  |
| `Eventos`           | Eventos do Minecraft                  |
| `InventariosBridge` | Kits e inventários                    |
| `MapasMenu`         | Menus de mapas                        |
| `Permissao`         | Grupos e permissões                   |
| `Punicao`           | Sistemas de punição                   |
| `Anticheat`         | Verificação de alcance                |
| `Scoreboard`        | Placar e informações visuais          |
| `Clima`             | Funcionalidades relacionadas ao clima |

## ⚙️ Configuração

### Requisitos

* JDK 8
* Spigot 1.8.8
* Bukkit/Spigot API compatível
* TitleAPI 1.8.1
* Bibliotecas necessárias configuradas no ambiente de desenvolvimento

### Preparação

1. Importe o projeto para o Eclipse.
2. Configure o JDK 8.
3. Ajuste as referências às bibliotecas utilizadas.
4. Configure as dependências da API do servidor.
5. Preencha o arquivo `config.yml` com os parâmetros necessários para mapas, spawns, regiões e limites.
6. Compile o plugin e teste-o em um servidor de desenvolvimento compatível.

> **⚠️ Atenção:** o `config.yml` presente na versão analisada está vazio. As configurações esperadas pelo código precisam ser preenchidas antes de validar o funcionamento completo.

O projeto analisado não contém `pom.xml` nem `build.gradle`; a compilação pode exigir configuração manual das bibliotecas locais.

## 🚧 Pontos de atenção

| Área                    | Situação identificada                                                   |
| :---------------------- | :---------------------------------------------------------------------- |
| Configuração            | O arquivo `config.yml` está vazio                                       |
| Comando `/build`        | O executor não parece ser registrado na inicialização                   |
| Kit da equipe Azul      | Há um caminho de reaparecimento que entrega o kit Vermelho              |
| Espectadores            | O acompanhamento contínuo do alvo precisa ser verificado                |
| Eventos de blocos       | O código pode alterar o cancelamento definido por outros plugins        |
| Gerenciamento de mundos | Operações de cópia e remoção podem bloquear a thread principal          |
| Reach                   | Os critérios de detecção precisam de testes contra falsos positivos     |
| Compilação              | As referências locais dificultam reproduzir o build em outros ambientes |

## 🎯 Objetivos técnicos

Este projeto reúne conceitos e práticas de desenvolvimento de plugins para Minecraft:

* Programação orientada a objetos com Java.
* Desenvolvimento baseado em eventos.
* Gerenciamento de partidas e estados.
* Manipulação de mundos e posições.
* Interfaces por inventários.
* Sistemas administrativos e permissões.
* Implementação de verificações personalizadas.
* Organização modular do código.

## 📍 Estado do projeto

O repositório contém uma implementação de várias mecânicas do The Bridge e funcionalidades administrativas complementares.

Alguns recursos e configurações ainda precisam de revisão e testes. A documentação descreve a implementação identificada no código e não pressupõe que o plugin esteja pronto para produção.

---

<div align="center">

### 👨‍💻 Desenvolvido por Ryan Rodrigues Alvim

[![GitHub](https://img.shields.io/badge/GitHub-RyanAlvim-181717?style=for-the-badge\&logo=github)](https://github.com/RyanAlvim)

*Java · Minecraft Plugins · Backend Development*

</div>
