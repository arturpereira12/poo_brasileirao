# Simulação do Campeonato Brasileiro

## Integrantes da Equipe:

*   Artur Coelho Batista Guedes Pereira
*   Davi de Oliveira Gurgel
*   Rafael Torres Nóbrega Gomes

## Local de Armazenamento do Código Fonte:

*   https://github.com/arturpereira12/poo_brasileirao

## Introdução

Este projeto consiste no desenvolvimento de um sistema computacional destinado à simulação do Campeonato Brasileiro de Futebol. O objetivo central reside na criação de um modelo que represente a dinâmica de um torneio de pontos corridos, contemplando turno e returno, em conformidade com o formato oficial da competição.

As funcionalidades implementadas no sistema incluem:
*   Carregamento de dados referentes às equipes participantes, abrangendo informações sobre jogadores e seus respectivos níveis de habilidade.
*   Geração automática do calendário de jogos, com distribuição equilibrada das partidas ao longo das rodadas.
*   Simulação dos resultados das partidas, considerando a força relativa das equipes e incorporando um elemento de aleatoriedade para aumentar o realismo dos desfechos.
*   Cálculo e atualização da tabela de classificação e da lista de artilheiros do campeonato após a simulação de cada rodada.
*   Apresentação dos resultados, da classificação, das partidas por rodada e de estatísticas gerais por meio de uma interface web.

O desenvolvimento do sistema foi fundamentado nos princípios da Programação Orientada a Objetos (POO). Por exemplo, **encapsulamento** foi aplicado na criação de classes como `Time` e `Jogador`, que agrupam atributos (como nome, nível) e comportamentos (como calcular força). A **abstração** permitiu modelar entidades complexas do mundo real (campeonato, partida) em classes que expõem funcionalidades essenciais, ocultando detalhes internos de implementação. O sistema utiliza a linguagem Java e o framework Spring Boot para a implementação da aplicação web, o que possibilita a visualização e interação do usuário com a simulação do campeonato.

## Ferramentas Utilizadas e Estrutura do Projeto

### Ferramentas:

*   **Linguagem de Programação:** Java
*   **Framework:** Spring Boot (Utilizado para a construção da aplicação web e gerenciamento de serviços backend)
*   **Ambiente de Desenvolvimento Integrado (IDE):** Visual Studio Code
*   **Sistema de Controle de Versão:** Git
*   **Gerenciador de Dependências:** Maven
*   **Banco de Dados:** Não aplicável (Persistência de dados realizada em memória ou arquivos, como JSON)

### Estrutura de Pacotes:

O código fonte do projeto, localizado em `src/main/java`, está organizado na seguinte estrutura hierárquica de pacotes:

*   **`br.ufpb.poo.brasileirao`**: Pacote raiz da aplicação.
    *   `Main.java`: Ponto de entrada da aplicação Spring Boot. Contém também a classe interna `TournamentSimulator` para a execução original da simulação via console.
*   **`br.ufpb.poo.brasileirao.model`**: Agrupa as classes que representam as entidades fundamentais do domínio do problema.
    *   `Team.java`: Modela uma equipe participante do campeonato.
    *   `Player.java`: Modela um jogador, associado a uma equipe.
    *   `Position.java`: Enumeração que define as possíveis posições de um jogador.
    *   `Standing.java`: Representação legada das estatísticas de um time na classificação (obsoleta).
*   **`br.ufpb.poo.brasileirao.match`**: Contém as classes relacionadas à representação de partidas.
    *   `Match.java`: Modela uma partida individual entre duas equipes, incluindo resultado e data.
*   **`br.ufpb.poo.brasileirao.tournament`**: Engloba as classes responsáveis pela gestão da estrutura e das estatísticas do torneio.
    *   `LeagueStandings.java`: Gerencia a tabela de classificação geral do campeonato.
    *   `LeagueStandings.TeamStats`: Classe interna que armazena as estatísticas detalhadas de um time específico na classificação.
    *   `TopScorersTable.java`: Gerencia a lista dos principais artilheiros da competição.
*   **`br.ufpb.poo.brasileirao.service`**: Contém as classes de serviço que implementam a lógica de negócio da aplicação.
    *   `TournamentManager.java`: Serviço central que orquestra a simulação do torneio, incluindo geração de calendário, simulação de partidas e atualização de estatísticas para a interface web.
    *   `TeamService.java`: Serviço dedicado a operações relacionadas às equipes, como o carregamento de dados a partir de fontes externas.
    *   `TournamentService.java`: Implementação alternativa ou legada do serviço de gerenciamento do torneio.
*   **`br.ufpb.poo.brasileirao.controller`**: Agrupa os controladores Spring MVC, responsáveis por receber requisições HTTP e retornar respostas para a interface web.
    *   `HomeController.java`: Controlador para a página inicial da aplicação.
    *   `ChampionshipController.java`: Controlador para endpoints relacionados à classificação e à simulação de rodadas.
    *   `MatchesController.java`: Controlador para endpoints que exibem informações sobre as partidas.
    *   `StatsController.java`: Controlador para endpoints relacionados à exibição de estatísticas, como a artilharia.
*   **`br.ufpb.poo.brasileirao.controladores`**: Pacote contendo implementações de controladores mais antigas ou alternativas, possivelmente associadas à versão de console da simulação.
    *   `TeamController.java`: Controlador legado para o carregamento de dados de equipes.
    *   `TournamentController.java`: Controlador legado para a gestão do torneio.

## Resultados e Considerações Finais

### Resultados Alcançados

O projeto resultou em um sistema funcional capaz de simular o Campeonato Brasileiro de Futebol no formato de pontos corridos (turno e returno). A solução implementa com sucesso as funcionalidades propostas, incluindo:

*   Carregamento dinâmico de equipes e jogadores a partir de fontes de dados (ex: arquivos JSON).
*   Geração automática e correta do calendário de jogos, garantindo que todas as equipes se enfrentem duas vezes.
*   Simulação de partidas com resultados baseados na força das equipes e com um componente de aleatoriedade.
*   Cálculo preciso e atualização em tempo real da tabela de classificação e da artilharia após cada rodada simulada.
*   Interface web interativa desenvolvida com Spring Boot e Thymeleaf (ou outra tecnologia de front-end), permitindo ao usuário visualizar a classificação, os jogos por rodada, os artilheiros e iniciar/avançar a simulação.

O sistema demonstra a aplicação prática dos conceitos de Orientação a Objetos na modelagem de um problema complexo do mundo real.

### Dificuldades Encontradas

Durante o desenvolvimento, a equipe enfrentou alguns desafios, tais como:

*   **Modelagem Inicial:** Definir a estrutura de classes e relacionamentos mais adequada para representar a complexidade do campeonato (times, jogadores, partidas, classificação, artilharia) exigiu discussões e refinamentos.
*   **Algoritmo de Geração de Calendário:** Implementar um algoritmo eficiente e justo para gerar o calendário de jogos (turno e returno) de forma que evitasse confrontos repetidos na mesma rodada ou sequências desbalanceadas de jogos em casa/fora.
*   **Integração Front-end e Back-end:** Conectar a lógica de simulação do backend (Java/Spring Boot) com a interface web (HTML/CSS/JavaScript/Thymeleaf), garantindo a atualização dinâmica dos dados e a interatividade do usuário.
*   **Gerenciamento de Estado da Simulação:** Manter o estado correto da simulação (rodada atual, partidas jogadas, classificação) entre as requisições web.

### Reflexão sobre a Aprendizagem (Java e POO)

O desenvolvimento deste projeto proporcionou uma experiência prática valiosa no aprendizado e aplicação da linguagem Java e dos paradigmas da Programação Orientada a Objetos (POO).

*   **Java:** A equipe pôde aprofundar seus conhecimentos na sintaxe, bibliotecas padrão (Collections, I/O, Date/Time API) e recursos da linguagem. O uso do Spring Boot também introduziu conceitos importantes como injeção de dependência, anotações e o padrão MVC.
*   **POO:** Os princípios de POO foram fundamentais para a organização e modularidade do código.

### Feedback e Sugestões para a Disciplina

* Nosso grupo apreciou particularmente a oportunidade de ganhar mais experiência prática com desenvolvimento web utilizando Spring Boot e de aprimorar as habilidades de colaboração e trabalho em equipe.
