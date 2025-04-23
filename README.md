# 🏆 Simulação do Campeonato Brasileiro

Este projeto consiste no desenvolvimento de um sistema computacional destinado à simulação do Campeonato Brasileiro de Futebol. O objetivo central reside na criação de um modelo que represente a dinâmica de um torneio de pontos corridos, contemplando turno e returno, em conformidade com o formato oficial da competição.

As funcionalidades implementadas no sistema incluem:
*   Carregamento de dados referentes às equipes participantes, abrangendo informações sobre jogadores e seus respectivos níveis de habilidade.
*   Geração automática do calendário de jogos, com distribuição equilibrada das partidas ao longo das rodadas.
*   Simulação dos resultados das partidas, considerando a força relativa das equipes e incorporando um elemento de aleatoriedade para aumentar o realismo dos desfechos.
*   Cálculo e atualização da tabela de classificação e da lista de artilheiros do campeonato após a simulação de cada rodada.
*   Apresentação dos resultados, da classificação, das partidas por rodada e de estatísticas gerais por meio de uma interface web.

O desenvolvimento do sistema foi fundamentado nos princípios da Programação Orientada a Objetos (POO). Por exemplo, **encapsulamento** foi aplicado na criação de classes como `Time` e `Jogador`, que agrupam atributos (como nome, nível) e comportamentos (como calcular força). A **abstração** permitiu modelar entidades complexas do mundo real (campeonato, partida) em classes que expõem funcionalidades essenciais, ocultando detalhes internos de implementação. O sistema utiliza a linguagem Java e o framework Spring Boot para a implementação da aplicação web, o que possibilita a visualização e interação do usuário com a simulação do campeonato.

## 👥 Integrantes da Equipe

- **Artur Coelho Batista Guedes Pereira**
- **Davi de Oliveira Gurgel**
- **Rafael Torres Nóbrega Gomes**

---

## 🚀 Funcionalidades

✔️ **Carregamento de dados**  
   - Equipes, jogadores e níveis de habilidade  
   
🎮 **Simulação realista**  
   - Geração automática de calendário (turno e returno)  
   - Resultados baseados em força das equipes + aleatoriedade  

📊 **Estatísticas em tempo real**  
   - Tabela de classificação dinâmica  
   - Lista de artilheiros atualizada  

🌐 **Interface Web Interativa**  
   - Desenvolvida com HTML5, CSS3 e Thymeleaf  
   - Visualização responsiva de resultados e estatísticas  

---

## 🛠️ Tecnologias

| Ferramenta           | Descrição                                  |
|----------------------|--------------------------------------------|
| **Java 17**          | Linguagem principal do projeto            |
| **Spring Boot**      | Framework para backend e gestão de serviços|
| **Maven**            | Gerenciamento de dependências             |
| **HTML5 CSS3**              | Desenvolvimento do Frontend                      |

---

## 🗃️ Estrutura de Pacotes

O código fonte do projeto (`src/main/java`) está organizado na seguinte estrutura:

```bash
br.ufpb.poo.brasileirao/
├── Main.java                    # Ponto de entrada Spring Boot + simulador console
│
├── model/                       # Entidades do domínio
│   ├── Team.java                # Modelo de equipe
│   ├── Player.java              # Modelo de jogador
│   ├── Position.java            # Enum de posições
│   └── Standing.java            # Estatísticas de time (legado)
│
├── match/                       # Lógica de partidas
│   └── Match.java               # Modelo de partida (resultado + data)
│
├── tournament/                  # Gerenciamento do torneio
│   ├── LeagueStandings.java      # Tabela de classificação
│   │   └── TeamStats            # Estatísticas por time
│   └── TopScorersTable.java      # Lista de artilheiros
│
├── service/                     # Lógica de negócio
│   ├── TournamentManager.java    # Orquestração principal
│   ├── TeamService.java          # Operações com equipes
│   └── TournamentService.java    # Gerenciamento alternativo
│
├── controller/                  # Controladores Web
│   ├── HomeController.java       # Página inicial
│   ├── ChampionshipController.java # Classificação/simulação
│   ├── MatchesController.java    # Gerenciamento de partidas
│   └── StatsController.java      # Estatísticas/artilharia
│
└── controladores/               # Controladores legados
    ├── TeamController.java       # Carregamento de dados (legado)
    └── TournamentController.java # Gerenciamento (legado)

```
## 📊 Resultados e Considerações

### ✅ Conquistas
- **Sistema completo** de simulação do Brasileirão  
- **Carregamento dinâmico** de dados (JSON)  
- **Algoritmo eficiente** para geração de calendário  
- **Simulação realista** com força das equipes + aleatoriedade  
- **Interface web integrada** (Spring Boot + Thymeleaf)  

### �️ Desafios Superados
| Desafio                      | Solução Implementada               |
|------------------------------|-------------------------------------|
| Modelagem de classes         | Diagramas UML + revisões iterativas|
| Geração de calendário        | Algoritmo round-robin adaptado     |
| Integração frontend/backend  | API REST + Thymeleaf templates     |
| Gerenciamento de estado      | Padrão Singleton + session attributes |

### 🎓 Aprendizados

#### Java & Spring Boot
- Domínio avançado de Collections Framework  
- Configuração automática com Spring Boot  
- Injeção de dependências  
- Padrão MVC na prática  

#### POO Aplicada
- **Encapsulamento**: Modelagem de entidades com acesso controlado  
- **Abstração**: Interfaces para serviços e controladores  
- **Coesão**: Divisão clara de responsabilidades  
- **Baixo acoplamento**: Comunicação via interfaces  

### 💡 Sugestões para a Disciplina
- Maior ênfase em testes unitários  
- Workshops de integração frontend/backend  
- Casos de estudo com sistemas legados  

### Feedback e Sugestões para a Disciplina

* Nosso grupo apreciou particularmente a oportunidade de ganhar mais experiência prática com desenvolvimento web utilizando Spring Boot e de aprimorar as habilidades de colaboração e trabalho em equipe.
