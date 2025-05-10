# Como Executar

Será necessário criar um `.env`
Como não há dados sensíveis, será apenas para o uso de DEV.
Eis seus valores:

`SERVICE_PORT=3000`
`MONGO_URI=mongodb://wildclown:senha123@localhost:27017/ebury_db?authSource=admin`

O `MONGO_URI` idealmente deve ser passado através de secret provider, como Azure vault.
Outros valores necessários podem ser passados também pelo Key vault, rotas de serviços internos, por exemplo, que serão utilizados na malha interna (VPN)

Foi utilizado Docker no sistema, logo esta ferramenta deve estar presente e executando na máquina.

Como utilizaremos Mongo, deixei um `docker-compose.yml` que irá disponibilizar o DB na porta `27017`. **Caso precise realizar alterações em sua configuração, é necessário também mudar a variável em .env**.

Não houve sucesso em escrever um docker-compose genérico que sobe o Banco de dados e o serviço do sistema em sequência. Logo, é solicitado que execute o serviço individualmente.
Em ambiente de testes foi utilizado a IDE `Intellij`.
O container de serviço conseguiu subir em testes, mas a sua comunicação com o banco era falha, retornando `500` em suas requisições.

## Passo a passo:

- Execute o Docker
- Vá até a pasta `.\ebury-challenge\ebury-challenge` e execute o comando de terminal `docker-compose up`
  - Nota: Se fechar o Docker Desktop, no ambiente de testes, ao reabrir, o container ainda estará em execução, mas sem acesso.
  Caso isto ocorra, feche e execute o comando novamente.
- Abra o projeto com sua IDE de preferência
- Crie o `.env` fornecido acima
- Execute o serviço, a sua função main estará em `FlightManagementApplication.kt`
- Espere o serviço estar ativo, Se a mensagem abaixo surgir, o serviço estará disponível para receber requisições:

`(...) Tomcat started on port(s): 3000 (http) with context path ''`
`(...) Started FlightManagementApplicationKt in 2.794 seconds (process running for 3.132)`

- As rotas do serviço podem ser visualizadas com `Ebury.openapi.json`, mas caso não possua visualizador OpenAPI, disponibilizem em `csv-to-json-converter/index.html` uma área no Header para mudar o conteúdo da página ao `Redoc` onde há também as especificações de rotas do sistema.
- Para melhor comunicação de Back e Front, foi preferido o processamento de dados unicamente em JSON, mas a fim de manter especificação do uso de CSV, foi disponibilizado em `index.html` um conversor de CSV, simulando o comportamento do Front em sanitizar dados antes de enviar ao serviço.
- Agora é só aproveitar o serviço!
Leia a seção `A solução` para entender restrições de chamada.

---

# A solução

Nosso problema envolve rotas de avião, **tentando encontrar o menor caminho**

Importante tomar em conta que no mundo existem cerca de 49.024 aeroportos.

Observando rotas de avião como Grafo unidirecional, e com a ciência de que valores não serão negativos, podemos utilizar o [algoritmo de Dijkstra](https://pt.wikipedia.org/wiki/Algoritmo_de_Dijkstra)

Mas para este algoritmo, há o problema de que precisamos do grafo de adjacência gerado, com uma complexidade de **O(AlogV)**

- A -> Número de arestas / conexões
- V -> Número de vértices / aeroportos

Tomando o input fornecido como a lista de rotas de aviões, fora determinado algumas validações
- A lista não poderá ter a mesma rota repetida, mas com valores diferentes
- A lista não poderá ter viagens com preço menor que 0
- O nome do aeroporto não pode ser ``null`` ou vazio `""`

Caso seja inserido novos valores ou atualizadas rotas, a cache será apagada.

Ao inserir uma lista de dados, caso uma rota já exista, será mantido o menor valor de ambas, caso seja necessário alterar o valor, é necessário utilizar uma rota a parte não desenvolvida.
Caso queiram manter o comportamento de CSV em que somente o que está presente nele inicialmente seja os dados do banco, remova o comentário na camada de serviço para que se apague todos os itens do banco ao inserir um novo batch.
No cenário ideal, esta remoção iria quebrar os testes.
Os testes seguirão o [seguinte modelo](https://www.linkedin.com/pulse/test-structure-continuous-integration-teixeira-soares-de-almeida-heaqf/)

Visando melhor otimização de complexidade e redução de chamadas ao Banco de dados, foi realizado um modelo de Cache de serviço, reduzindo em alguns cenários a complexidade a **O(1)**, mas incrementando a complexidade de memória a **O(V*A)**, para seu sucesso, é necessário determinar exatamente como lidar com esta cache.

### PS:

O código de front não é o ideal, mas foi o ágil necessário.