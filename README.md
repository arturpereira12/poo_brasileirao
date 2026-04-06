# Simulação do Campeonato Brasileiro

Este projeto consiste no desenvolvimento de um sistema computacional destinado à simulação do Campeonato Brasileiro de Futebol. O objetivo central reside na criação de um modelo que represente a dinâmica de um torneio de pontos corridos, contemplando turno e returno, em conformidade com o formato oficial da competição.

As funcionalidades implementadas no sistema incluem:
*   Carregamento de dados referentes às equipes participantes, abrangendo informações sobre jogadores e seus respectivos níveis de habilidade.
*   Geração automática do calendário de jogos, com distribuição equilibrada das partidas ao longo das rodadas.
*   Simulação dos resultados das partidas, considerando a força relativa das equipes e incorporando um elemento de aleatoriedade para aumentar o realismo dos desfechos.
*   Cálculo e atualização da tabela de classificação e da lista de artilheiros do campeonato após a simulação de cada rodada.
*   Apresentação dos resultados, da classificação, das partidas por rodada e de estatísticas gerais por meio de uma interface web.

O desenvolvimento do sistema foi fundamentado nos princípios da Programação Orientada a Objetos (POO). Por exemplo, **encapsulamento** foi aplicado na criação de classes como `Team` e `Player`, que agrupam atributos (como nome, nível) e comportamentos (como calcular força). A **abstração** permitiu modelar entidades complexas do mundo real (campeonato, partida) em classes que expõem funcionalidades essenciais, ocultando detalhes internos de implementação. O sistema utiliza a linguagem Java e o framework Spring Boot para a implementação da aplicação web, o que possibilita a visualização e interação do usuário com a simulação do campeonato.

## Integrantes da Equipe

- **Artur Coelho Batista Guedes Pereira**
- **Davi de Oliveira Gurgel**
- **Rafael Torres Nóbrega Gomes**

---

## Funcionalidades

**Carregamento de dados**  
   - Equipes, jogadores e níveis de habilidade  
   - Leitura de arquivos JSON com estrutura de times e jogadores
   
**Simulação realista**  
   - Geração automática de calendário (turno e returno)  
   - Resultados baseados em força das equipes + aleatoriedade
   - Vantagem do fator "casa" incorporada no algoritmo
   - Distribuição de gols por posição seguindo estatísticas reais

**Estatísticas em tempo real**  
   - Tabela de classificação dinâmica  
   - Lista de artilheiros atualizada
   - Análise de desempenho por rodadas
   - Estatísticas avançadas (média de gols, vitórias em casa/fora)

**Interface Web Interativa**  
   - Desenvolvida com HTML5, CSS3, Bootstrap e Thymeleaf  
   - Visualização responsiva de resultados e estatísticas
   - Gráficos interativos para análise visual de dados
   - Navegação simples e intuitiva entre diferentes funcionalidades

---

## Tecnologias

| Ferramenta           | Descrição                                  |
|----------------------|--------------------------------------------|
| **Java 17**          | Linguagem principal do projeto            |
| **Spring Boot**      | Framework para backend e gestão de serviços|
| **Maven**            | Gerenciamento de dependências             |
| **HTML5/CSS3**       | Desenvolvimento do Frontend               |
| **Bootstrap 5**      | Framework de componentes responsivos      |
| **Thymeleaf**        | Template engine para integração Java-HTML |
| **Chart.js**         | Biblioteca para visualização de dados     |
| **Lombok**           | Redução de boilerplate em classes Java (getters, setters, construtores, etc.) |

---

## Estrutura de Pacotes

O código fonte do projeto (`src/main/java`) está organizado na seguinte estrutura:

```bash
br.ufpb.poo.brasileirao/
├── Main.java                    # Ponto de entrada Spring Boot + simulador console
│
├── controller/                  # Controladores Web
│   ├── HomeController.java       # Página inicial
│   ├── ChampionshipController.java # Classificação/simulação
│   ├── MatchesController.java    # Gerenciamento de partidas
│   ├── StatsController.java      # Estatísticas/artilharia
│   └── TeamController.java       # Visualização e dados dos times
│
├── exception/                   # Exceções personalizadas
│   └── InvalidStrengthException.java # Exceção para força inválida de jogador/time
│
├── match/                       # Lógica de partidas
│   └── Match.java               # Modelo de partida (resultado + data)
│
├── model/                       # Entidades do domínio
│   ├── Team.java                # Modelo de equipe
│   ├── Player.java              # Modelo de jogador abstrato
│   ├── Forward.java             # Implementação específica de atacante
│   ├── Midfielder.java          # Implementação específica de meio-campista
│   ├── Defender.java            # Implementação específica de defensor
│   ├── Goalkeeper.java          # Implementação específica de goleiro
│   ├── Position.java            # Enum de posições
│   ├── GoalProbabilityCalculator.java        # Calculador de probabilidade de gols
│   ├── GoalProbabilityCalculatorFactory.java # Fábrica de calculadores de probabilidade
│   └── Standing.java            # Estatísticas de time (legado)
│
├── service/                     # Lógica de negócio
│   ├── TournamentManager.java   # Orquestração principal
│   ├── TeamService.java         # Operações com equipes
│   ├── TournamentService.java   # Gerenciamento alternativo (legado)
│   └── strategy/                # Implementação do padrão Strategy
│       ├── StrengthCalculationStrategy.java # Interface de estratégia
│       └── AverageStrengthStrategy.java     # Implementação de cálculo de força média
│
├── tournament/                  # Gerenciamento do torneio
│   ├── LeagueStandings.java     # Tabela de classificação
│   │   └── TeamStats            # Estatísticas por time (classe interna)
│   └── TopScorersTable.java     # Lista de artilheiros
```

Os arquivos de recursos estão em `src/main/resources/`, incluindo:
- `data/teams_and_players.json` — Dados dos times e jogadores
- `templates/` — Templates Thymeleaf para as páginas web
- `static/images/crests/` — Imagens dos escudos dos times

## Padrões de Projeto e Design Arquitetural

### Padrão MVC
O sistema implementa o padrão Model-View-Controller, separando claramente:
- **Model**: Classes em `model/`, `match/`, `tournament/`
- **View**: Templates Thymeleaf em `resources/templates/`
- **Controller**: Classes em `controller/`

### Injeção de Dependências
Utilizamos o Spring Framework para gerenciar as dependências entre componentes através de anotações como `@Service` e `@Autowired`.

### Strategy Pattern
O sistema utiliza explicitamente o padrão Strategy em diferentes contextos:
1. **Cálculo de Força**: No pacote `service/strategy` com interface `StrengthCalculationStrategy` e implementação concreta `AverageStrengthStrategy`
2. **Probabilidade de Gols**: Através do `GoalProbabilityCalculator` e sua factory.

```java
// Exemplo do cálculo de probabilidades com estratégia ponderada
Position scorerPosition = GoalProbabilityCalculatorFactory.getPositionForGoal(randomValue);
```

### Factory Method Pattern
O sistema implementa o padrão Factory Method para a criação de objetos relacionados à probabilidade de gols:
```java
// Factory para criar calculadores de probabilidade
GoalProbabilityCalculatorFactory.getPositionForGoal(randomValue);
```

### Singleton Pattern
Gerenciadores principais como `TournamentManager` são implementados como singletons gerenciados pelo Spring.

### Adapter Pattern
Utilização de adapters para compatibilidade entre modelos de dados legados e atuais:
```java
// Adaptador para compatibilidade com template legacy
public static class StandingAdapter {
    // Conversão entre formatos
}
```

### Herança e Polimorfismo
O modelo de jogadores utiliza herança e polimorfismo:
```
Player (classe abstrata)
├── Forward
├── Midfielder  
├── Defender
└── Goalkeeper
```
### UML
![UML](/src/main/resources/static/images/crests/UML.png)

## Resultados e Considerações

### Conquistas
- **Sistema completo** de simulação do Brasileirão  
- **Carregamento dinâmico** de dados (JSON)  
- **Algoritmo eficiente** para geração de calendário  
- **Simulação realista** com força das equipes + aleatoriedade  
- **Interface web integrada** (Spring Boot + Thymeleaf)  
- **Persistência de resultados** em arquivos JSON

### Desafios Superados
| Desafio                      | Solução Implementada               |
|------------------------------|-------------------------------------|
| Modelagem de classes         | Diagramas UML + revisões iterativas|
| Geração de calendário        | Algoritmo round-robin adaptado     |
| Integração frontend/backend  | API REST + Thymeleaf templates     |
| Gerenciamento de estado      | Padrão Singleton + session attributes |
| Simulação probabilística     | Algoritmo ponderado por força de jogadores |
| Visualização de dados        | Integração com Chart.js para gráficos |

### Aprendizados

#### Java & Spring Boot
- Domínio avançado de Collections Framework  
- Configuração automática com Spring Boot  
- Injeção de dependências  
- Padrão MVC na prática  
- Serialização/deserialização JSON

#### POO Aplicada
- **Encapsulamento**: Modelagem de entidades com acesso controlado  
- **Herança e Composição**: Reutilização de código entre componentes relacionados
- **Abstração**: Interfaces para serviços e controladores  
- **Coesão**: Divisão clara de responsabilidades  
- **Baixo acoplamento**: Comunicação via interfaces  
- **Polimorfismo**: Tratamento uniforme de diferentes implementações

### Sugestões para a Disciplina
- Maior ênfase em testes unitários  
- Workshops de integração frontend/backend  
- Casos de estudo com sistemas legados  
- Práticas de refatoração e melhoria de código legado

### Feedback e Sugestões para a Disciplina

* Nosso grupo apreciou particularmente a oportunidade de ganhar mais experiência prática com desenvolvimento web utilizando Spring Boot e de aprimorar as habilidades de colaboração e trabalho em equipe.

### Home Page
![Home Page](/src/main/resources/static/images/crests/ADR01.png)
![Home Page](/src/main/resources/static/images/crests/ADR02.png)
![Home Page](/src/main/resources/static/images/crests/ADR03.png)
![Home Page](/src/main/resources/static/images/crests/ADR1.png)



### Games
![Games](/src/main/resources/static/images/crests/ADR04.png)

### Stats
![Stats](/src/main/resources/static/images/crests/ADR05.png)
![Stats](/src/main/resources/static/images/crests/ADR06.png)
![Stats](/src/main/resources/static/images/crests/ADR2.png)

### Teams
![Teams](/src/main/resources/static/images/crests/ADR07.png)

---

## Melhorias Futuras

- Permitir salvar e carregar simulações anteriores, possibilitando o acompanhamento de diferentes campeonatos.
- Adicionar estatísticas avançadas: assistências, cartões amarelos/vermelhos, lesões, faltas, passes, etc.
- Implementar modos de jogo adicionais, como um modo "Football Manager" (gestão de elenco, transferências, escalação, táticas e finanças).
- Suporte a múltiplos campeonatos, temporadas e histórico de desempenho dos times.
- Exportação de dados e relatórios em formatos como PDF ou CSV.
- Interface para personalização de times, jogadores e regras do campeonato.
- Integração com APIs externas de futebol para importar dados reais de jogadores, times e partidas.
- Modo multiplayer/local para simulação entre diferentes usuários.
- Sistema de conquistas, rankings e desafios para aumentar o engajamento.
