# GeminiDevApi

**GeminiDevApi** é uma API inteligente e de alta performance construída com Java 21 e Spring Boot 3. Ela utiliza o LLM Gemini do Google para fornecer serviços avançados de análise de código, geração de documentação e engenharia de prompt guiada. O sistema foi projetado para escalabilidade e observabilidade, apresentando um sistema de cache multinível, persistência em banco de dados e integração com Prometheus para monitoramento.

## ✨ Funcionalidades Principais

  - **🤖 Análise de Código com IA**: O endpoint `POST /v1/analyze` recebe código-fonte Java e gera automaticamente documentação técnica e esqueletos de testes JUnit 5.
  - **📝 Geração de Prompt Guiada**: Um endpoint amigável `POST /v1/prompt-analyze` permite aos usuários selecionar um perfil profissional (ex: "Engenheiro de Software") e uma tarefa específica para gerar conteúdo especializado e de alta qualidade usando uma abordagem baseada em templates.
  - **⚡️ Sistema de Cache Robusto**: Implementa uma estratégia de cache de dois níveis para minimizar chamadas à API do LLM, reduzir a latência e controlar custos. Utiliza um cache Caffeine em memória (L1) e um cache Redis distribuído (L2) para ambientes de produção.
  - **🛡️ Autenticação Segura**: Possui um sistema de autenticação seguro usando JWT (JSON Web Tokens). O acesso aos endpoints é controlado por papéis (RBAC), distinguindo entre `USER` e `ADMIN`.
  - **⚙️ Painel Administrativo**: Um painel front-end dedicado (`/admin.html`) fornece aos administradores funcionalidades completas de CRUD (Criar, Ler, Atualizar, Deletar) para gerenciar usuários, profissões e funcionalidades disponíveis no sistema de prompt guiado.
  - **🔭 Observabilidade e Monitoramento**: Ambiente local pré-configurado com Prometheus, Grafana e Alertmanager, incluindo um dashboard detalhado para análise de performance da JVM, pool de conexões do banco de dados, requisições HTTP e métricas de negócio customizadas.
  - **🐳 Ambiente Containerizado**: Acompanha um arquivo `docker-compose.yml` pré-configurado para uma configuração de desenvolvimento local transparente, iniciando a aplicação, Redis e Prometheus com um único comando.
  - **🚀 Pipelines de CI/CD Automatizados**: Inclui workflows do GitHub Actions prontos para produção, para integração e implantação contínuas. Os pipelines automatizam testes, análise de qualidade de código (CodeQL, SonarCloud), construção de imagens Docker, envio para o Google Artifact Registry e implantação no Google Cloud Run.

## 🛠️ Arquitetura e Tecnologias

A aplicação segue um padrão de arquitetura em camadas (Controller, Service, Repository) para garantir a separação de responsabilidades e a manutenibilidade.

  - **Backend**: Java 21, Spring Boot 3.5.0, Spring Security (JWT), Spring Data JPA / Hibernate, WebFlux `WebClient`
  - **Banco de Dados**: PostgreSQL (Produção), H2 (Desenvolvimento)
  - **Cache**: Caffeine (L1 Cache), Redis (L2 Cache)
  - **IA & LLM**: Google Gemini
  - **DevOps & Implantação**: Docker, Docker Compose, GitHub Actions, Google Cloud Run, Google Artifact Registry
  - **Observabilidade**: Micrometer, Prometheus, Grafana, Alertmanager.
  - **Testes**: JUnit 5, Mockito, Spring Boot Test
  - **Frontend**: Vanilla JavaScript, HTML5, CSS3

## 🔭 Observabilidade e Monitoramento

O projeto adota uma abordagem de "Infraestrutura como Código" para o monitoramento, garantindo que todo o setup seja versionado e replicável. A stack de monitoramento é iniciada junto com a aplicação via `docker-compose`.

  - **Prometheus** ([http://localhost:9090](https://www.google.com/search?q=http://localhost:9090)): Coleta e armazena as métricas.
  - **Grafana** ([http://localhost:3000](https://www.google.com/search?q=http://localhost:3000)): Visualiza as métricas em um dashboard pré-configurado. Login padrão: `admin` / `admin`.
  - **Alertmanager** ([http://localhost:9093](https://www.google.com/search?q=http://localhost:9093)): Gerencia os alertas definidos no Prometheus.

O dashboard padrão, definido em `/dashboards/api_dashboard.json`, inclui painéis para:

  - **KPIs:** Uptime, Uso de CPU, e contadores de negócio.
  - **Requisições HTTP:** Taxa de requisições e latência (p95).
  - **Saúde da JVM:** Uso de memória Heap e Non-Heap, e contagem de Threads.
  - **Pool de Conexões do Banco de Dados:** Conexões ativas, ociosas, pendentes e total de timeouts.

## 🔌 Endpoints da API

A API é protegida com JWT. Um token válido deve ser incluído como um Bearer Token no header `Authorization` para endpoints protegidos.

| Método | Endpoint                    | Descrição                                                                                               | Acesso        |
| :----- | :-------------------------- | :-------------------------------------------------------------------------------------------------------- | :------------ |
| `POST` | `/api/auth/login`           | Autentica um usuário e retorna um JWT.                                                         | Público       |
| `POST` | `/v1/analyze`               | Analisa uma classe Java para gerar documentação e testes.                                      | `USER`, `ADMIN` |
| `POST` | `/v1/prompt-analyze`        | Gera conteúdo com base em uma profissão, funcionalidade e descrição do usuário.                  | `USER`, `ADMIN` |
| `GET`  | `/api/professions`          | Lista todas as profissões disponíveis e suas funcionalidades associadas.                        | `USER`, `ADMIN` |
| `POST` | `/api/professions`          | Cria uma nova profissão.                                                                       | `ADMIN`       |
| `GET`  | `/api/features`             | Lista todas as funcionalidades disponíveis.                                                    | `USER`, `ADMIN` |
| `POST` | `/api/features`             | Cria uma nova funcionalidade e a associa a uma profissão.                                      | `ADMIN`       |
| `GET`  | `/v1/cache`                 | Lista um resumo dos resultados de geração persistidos no cache do banco de dados.                | `USER`, `ADMIN` |
| `GET`  | `/actuator/prometheus`      | Expõe métricas da aplicação e customizadas para o Prometheus.                                   | Público       |
| `GET`  | `/swagger-ui.html`          | Fornece documentação interativa da API (Swagger UI).                                         | Público       |

## 🚀 Como Começar

### Pré-requisitos

  - Java JDK 21
  - Apache Maven 3.9+
  - Docker e Docker Compose

### Configuração do Ambiente Local (Recomendado)

A maneira mais simples de executar toda a stack localmente é usando o Docker Compose.

1.  **Crie um Arquivo de Ambiente**:
    Na raiz do projeto, crie um arquivo chamado `.env`. Este arquivo armazenará sua chave de API do Gemini e outras configurações.

    ```dotenv
    # Arquivo .env
    SPRING_PROFILES_ACTIVE=dev
    GEMINI_API_KEY=SUA_CHAVE_DE_API_GEMINI_AQUI
    BASE_URL_IA=https://generativelanguage.googleapis.com/v1beta
    MODEL_IA_NAME=gemini-1.5-flash
    CORS_ALLOWED_ORIGINS=http://localhost:8080,http://127.0.0.1:8080
    ```

2.  **Build e Execução com Docker Compose**:
    Abra um terminal na raiz do projeto e execute:

    ```bash
    docker-compose up --build -d
    ```

    Este comando irá construir a imagem Docker da aplicação e iniciar três containers: `geminidevapi-app`, `redis-dev` e `prometheus-dev`.

3.  **Acesse os Serviços**:

      - **UI da Aplicação**: `http://localhost:8080/login.html`
      - **Grafana Dashboard**: `http://localhost:3000`
      - **Prometheus UI**: `http://localhost:9090`
      - **Alertmanager UI**: `http://localhost:9093`
      - **Redis (via cliente)**: `localhost:6379`

### Exemplo de Uso

1.  **Login**:
    Acesse a aplicação em [http://localhost:8080/login.html](https://www.google.com/search?q=http://localhost:8080/login.html). Use as credenciais padrão ou as que você adicionou para obter um JWT.

2.  **Analisar Código via `curl`**:
    Use o JWT obtido para fazer uma requisição ao endpoint `/v1/analyze`. O corpo da requisição deve ser um objeto JSON como mostrado em `payload.json`.

    ```bash
    # Substitua SEU_TOKEN_JWT pelo token do passo de login
    TOKEN="SEU_TOKEN_JWT"

    curl -X POST http://localhost:8080/v1/analyze \
       -H "Content-Type: application/json" \
       -H "Authorization: Bearer $TOKEN" \
       -d @payload.json
    ```

## 🔬 Testes

O projeto possui uma suíte de testes abrangente cobrindo testes unitários, de integração e da camada web.

Para executar todos os testes automatizados e gerar um relatório de cobertura, execute:

```bash
./mvnw clean verify
```

O relatório de cobertura do JaCoCo estará disponível em `target/site/jacoco/index.html`.

## 🔄 Pipelines de CI/CD

O projeto utiliza o GitHub Actions para seus workflows de CI/CD:

  - **`pipelinedev.yml`**: Este workflow é acionado em pushes para a branch `develop`. Ele é responsável por:

      - Executar a análise do CodeQL para verificação de segurança.
      - Executar todos os testes unitários e de integração.
      - Gerar um relatório de cobertura de código com JaCoCo.
      - Analisar o código com o SonarCloud para métricas de qualidade.

  - **`pipeline.yml`**: Este workflow é acionado em pushes para a branch `main` e lida com a implantação em produção:

      - Executa o CodeQL e a suíte completa de testes/análises.
      - Constrói uma imagem Docker da aplicação.
      - Envia a imagem Docker para o Google Artifact Registry.
      - Implanta a nova imagem no Google Cloud Run, aplicando todas as variáveis de ambiente necessárias a partir dos GitHub Secrets.

## Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo [LICENSE](https://opensource.org/licenses/MIT) para mais detalhes.
