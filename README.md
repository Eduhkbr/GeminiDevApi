# GeminiDevApi

## Visão Geral

O GeminiDevApi é uma API Java baseada em Spring Boot para análise de código Java, geração automática de documentação e testes utilizando modelos de linguagem (LLM). O sistema utiliza cache, persistência em banco de dados e métricas customizadas para alta performance e observabilidade.

---

## Funcionalidades
- **Análise de código Java**: Recebe classes Java e gera documentação e esqueleto de testes automaticamente via LLM.
- **Cache e Persistência**: Evita chamadas desnecessárias ao LLM utilizando cache (Caffeine/Redis) e banco de dados.
- **Métricas customizadas**: Exposição de métricas via Prometheus para monitoramento de uso de cache e chamadas à IA.
- **Endpoints REST**: Interface HTTP para integração com outros sistemas.

---

## Arquitetura

- **Spring Boot**: Framework principal para REST, DI e configuração.
- **Camada de Serviço**: `JavaClassAnalyzerService` centraliza a lógica de análise, cache e persistência.
- **Repositório**: `GenerationCacheRepository` gerencia o acesso ao banco de dados para cache de resultados.
- **Configurações**: Beans para cache, métricas e integração com Prometheus.
- **Testes**: Cobertura de unidade e integração usando JUnit e Mockito.

---

## Endpoints Principais

- `POST /v1/analyze` — Recebe uma lista de classes Java e retorna documentação e testes gerados.
- `GET /actuator/prometheus` — Exposição de métricas customizadas para Prometheus.

---

## Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.8+
- Docker (opcional, para Redis/Prometheus)

### Build e Execução

```bash
./mvnw clean package
java -jar target/DevApi-0.0.1-SNAPSHOT.jar
```

### Ambiente de Desenvolvimento

- Arquivo de configuração: `src/main/resources/application-dev.yml`
- Para rodar dependências (Redis, Prometheus) via Docker Compose:
- **Crie um arquivo `.env` na raiz do projeto** com as variáveis de ambiente necessárias para o serviço `geminidevapi` (exemplo: `SPRING_PROFILES_ACTIVE=dev`).

```bash
docker-compose up -d
```

---

## Docker e Ecossistema

O projeto já está pronto para rodar em containers Docker, facilitando o setup local e a integração com Redis e Prometheus.

### Build e Execução com Docker Compose

1. **Build e subida dos serviços:**

```bash
docker-compose up --build -d
```

2. **Acessos rápidos:**
   - API: http://localhost:8080
   - Prometheus: http://localhost:9090
   - Redis: localhost:6379

3. **Parar os serviços:**

```bash
docker-compose down
```

### Estrutura dos Containers

- **geminidevapi-app**: Container da aplicação Java (porta 8080)
- **redis-dev**: Cache Redis (porta 6379)
- **prometheus-dev**: Monitoramento Prometheus (porta 9090)

### Observabilidade
- O Prometheus já está configurado para coletar métricas do endpoint `/actuator/prometheus` da aplicação.
- O arquivo `prometheus.yml` pode ser customizado conforme necessidade.

### Dicas
- Para logs da aplicação: `docker logs -f geminidevapi-app`
- Para acessar o shell do container: `docker exec -it geminidevapi-app sh`

---

## Testes

Execute todos os testes automatizados:

```bash
./mvnw test
```

---

## Métricas Disponíveis
- `generation.cache.hits` — Total de hits no cache
- `generation.ia.calls` — Total de chamadas ao LLM

Acesse via `/actuator/prometheus`.

---

## Estrutura do Projeto

```
src/
  main/java/com/eduhkbr/gemini/DevApi/
    web/         # Controllers REST
    service/     # Serviços de negócio
    repository/  # Repositórios de dados
    model/       # Modelos de domínio
    config/      # Configurações Spring
  resources/     # Configurações e templates
  test/java/     # Testes automatizados
```

---

## Contribuição

1. Fork este repositório
2. Crie uma branch (`git checkout -b feature/nome-feature`)
3. Commit suas alterações (`git commit -am 'feat: nova feature'`)
4. Push para o branch (`git push origin feature/nome-feature`)
5. Abra um Pull Request

---

## Licença

Este projeto está sob a licença MIT.

---

## Exemplo de Requisição

Para analisar uma ou mais classes Java, utilize o endpoint `/v1/analyze` conforme o exemplo abaixo:

```bash
curl -X POST http://localhost:8080/v1/analyze \
  -u user:e5fabd54-e456-4aef-9b74-60a058d95ebc \
  -H "Content-Type: application/json" \
  -d '[
    {
      "name": "OrderService.java",
      "sourceCode": "public class OrderService {\n  /\n   * Cria um novo pedido no sistema.\n   /\n  public String createOrder(Order order) {\n    if (order == null || order.getCustomerId() == null) {\n      return null;\n    }\n    return generateOrderId();\n  }\n\n  /\n   * Busca um pedido pelo ID.\n   */\n  public Order getOrder(String orderId) {\n    if (orderId == null) {\n      return null;\n    }\n    Order order = new Order();\n    order.setOrderId(orderId);\n    return order;\n  }\n\n  /*\n   * Cancela um pedido pelo ID.\n   */\n  public boolean cancelOrder(String orderId) {\n    return orderId != null;\n  }\n\n  // Gera um ID simples para o pedido\n  private String generateOrderId() {\n    return \"ORD-\" + System.currentTimeMillis();\n  }\n}"
    }
  ]'
```

- Substitua o usuário e a senha pelo seu token de autenticação, se necessário.
- O corpo da requisição deve ser um array de objetos com os campos `name` e `sourceCode`.

---
