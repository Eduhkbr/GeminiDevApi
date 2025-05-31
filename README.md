# DevApi

API Java Spring Boot para análise e documentação de classes Java utilizando LLM (Gemini).

## Sumário
- [Descrição](#descrição)
- [Requisitos](#requisitos)
- [Configuração](#configuração)
- [Execução](#execução)
- [Endpoints](#endpoints)
- [Testes](#testes)
- [Documentação da API](#documentação-da-api)

## Descrição
Esta API permite enviar classes Java para análise e retorna documentação técnica gerada automaticamente por LLM.

## Requisitos
- Java 21+
- Maven 3.8+
- Chave de API Gemini (Google)

## Configuração
1. Defina as variáveis de ambiente ou edite `src/main/resources/application.yml`:
   ```yml
   gemini:
     api:
       base-url: https://generativelanguage.googleapis.com/v1beta/models
       key: ${GEMINI_API_KEY}
       model-name: gemini-2.5-flash-preview-05-20
   ```
2. Exporte a variável de ambiente:
   ```bash
   export GEMINI_API_KEY=seu_token
   ```

## Execução
```bash
./mvnw spring-boot:run
```

## Endpoints
- `POST /v1/analyze` — Recebe uma lista de classes Java (JSON) e retorna documentação gerada.

### Exemplo de payload
```json
[
  {
    "name": "MinhaClasse",
    "sourceCode": "public class MinhaClasse { ... }"
  }
]
```

## Testes
Execute:
```bash
./mvnw test
```

## Documentação da API
Após subir a aplicação, acesse:
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Licença
MIT
