# Como Executar

# A solução

Nosso problema envolve rotas de avião, **tentando encontrar o menor caminho**

Observando as rotas de avião como Grafo unidirecional, e com a ciência de que valores não são negativos,
podemos utilizar do [algoritmo de Djkstra](https://pt.wikipedia.org/wiki/Algoritmo_de_Dijkstra)

Mas para este algoritmo, há o problema de que precisamos do grafo de adjacência gerado, com uma complexidade de **O(ElogV)**

- E -> Número de arestas / conexões
- V -> Número de vértices / aeroportos

Vamos separar em pequenas milestones sobre o projeto, e realizar a primeira entrega, posteriormente, melhorar.

---

### Milestone 1:

- Apesar de ser o ultimo item, vamos criar nosso servidor REST, para tal, iremos utilizar Kotlin Spring, como dito ao final, o serviço terá:
  - Registro de novas rotas, persistindo no arquivo CSV | DB
    - Esta rota irá precisar consultar se os itens existem, e separar em 2 listas, uma que irá inserir diretamente e outra que irá checar se os itens devem ser atualizados, pois não fará sentido ter
    COG - GUA - 30 e COG - GUA - 40
    Pois a rota de 30 sempre será utilizada
  - Consulta de rota entre dois pontos

### Milestone 2:
- Criar script de importar CSV [Front]
Para tal item, particularmente desgosto de ter manipulação de arquivo no servidor, logo precisamos converter o **CSV para JSON**, e enfim realizar chamada de nosso servidor
  - Caso seja possível utilizar DB, criar exportação das rotas para