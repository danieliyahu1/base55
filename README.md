## Base55

Spring Boot service for generating agent workflows and filtering MCP tools. This README explains local setup, environment variables, running (Maven/Docker), and API usage.

### Prerequisites
- **JDK 21+**
- **Maven 3.9+** (or use the included `mvnw`/`mvnw.cmd`)
- Optional: **Docker** (to run via container)

### Quick start (local)
1. Export required environment variables (see Environment variables below).
2. Run the app:
   - Windows PowerShell:
     ```powershell
     ./mvnw.cmd spring-boot:run
     ```
   - macOS/Linux:
     ```bash
     ./mvnw spring-boot:run
     ```
3. The API will listen on `http://localhost:4000`.

### Build a jar
```bash
./mvnw clean package -DskipTests
java -jar target/base55-0.0.1-SNAPSHOT.jar
```

### Run with Docker
Build and run:
```bash
docker build -t base55:local .
docker run --rm -p 4000:4000 \
  -e GROQ_API_KEY=$GROQ_API_KEY \
  -e GROQ_BASE_URL=${GROQ_BASE_URL:-https://api.groq.com/openai} \
  -e GROQ_TEMPERATURE=${GROQ_TEMPERATURE:-0.2} \
  -e GROQ_MODEL=${GROQ_MODEL:-llama3-groq-70b-8192-tool-use} \
  -e OPENROUTER_API_KEY=$OPENROUTER_API_KEY \
  -e OPENROUTER_BASE_URL=${OPENROUTER_BASE_URL:-https://openrouter.ai/api} \
  -e OPENROUTER_MODEL=${OPENROUTER_MODEL:-meta-llama/llama-3.1-70b-instruct} \
  -e OPENROUTER_TEMPERATURE=${OPENROUTER_TEMPERATURE:-0.2} \
  base55:local
```

### Environment variables
These are injected via `src/main/resources/application.properties` using `${VAR_NAME}` placeholders. Set them in your shell, CI, or container runtime.

- **GROQ_API_KEY**: API key for Groq-compatible OpenAI endpoint.
- **GROQ_BASE_URL**: Base URL for Groq OpenAI-compatible API.
- **GROQ_TEMPERATURE**: Chat generation temperature for Groq client.
- **GROQ_MODEL**: Model name for Groq client.
- **OPENROUTER_API_KEY**: API key for OpenRouter (used by `AiConfig#openRouterChatModel`).
- **OPENROUTER_BASE_URL**: Base URL for OpenRouter OpenAI-compatible API.
- **OPENROUTER_MODEL**: Model id for OpenRouter client.
- **OPENROUTER_TEMPERATURE**: Chat temperature for OpenRouter client.

Other notable properties (already defaulted in `application.properties`):
- `server.port=4000`
- MCP client enabled; stdio server config from `src/main/resources/mcp-stdio-servers.json`

#### Example .env (Unix/macOS bash/zsh)
```bash
export GROQ_API_KEY=sk-... 
export GROQ_BASE_URL=https://api.groq.com/openai
export GROQ_TEMPERATURE=0.2
export GROQ_MODEL=llama3-groq-70b-8192-tool-use

export OPENROUTER_API_KEY=or_...
export OPENROUTER_BASE_URL=https://openrouter.ai/api
export OPENROUTER_MODEL=meta-llama/llama-3.1-70b-instruct
export OPENROUTER_TEMPERATURE=0.2
```
Then `source .env` before starting the app.

#### Windows PowerShell (current session)
```powershell
$env:GROQ_API_KEY="sk-..."
$env:GROQ_BASE_URL="https://api.groq.com/openai"
$env:GROQ_TEMPERATURE="0.2"
$env:GROQ_MODEL="llama3-groq-70b-8192-tool-use"

$env:OPENROUTER_API_KEY="or_..."
$env:OPENROUTER_BASE_URL="https://openrouter.ai/api"
$env:OPENROUTER_MODEL="meta-llama/llama-3.1-70b-instruct"
$env:OPENROUTER_TEMPERATURE="0.2"
```

#### Activate dev profile (optional)
There is a `src/main/resources/application-dev.properties`. To enable it:
- Bash/zsh:
  ```bash
  export SPRING_PROFILES_ACTIVE=dev
  ```
- PowerShell:
  ```powershell
  $env:SPRING_PROFILES_ACTIVE="dev"
  ```

### API
Base path: `http://localhost:4000/api/v1/base55`

- POST `/generate-tasks`
  - Body:
    ```json
    { "prompt": "Plan a data ingestion pipeline for CSV files" }
    ```
  - Response: a `Workflow` object describing steps (JSON).

- POST `/filter-tools`
  - Body:
    ```json
    { "prompt": "Search a codebase and summarize README changes" }
    ```
  - Response: `Map<String, List<McpToolSpec>>` keyed by tool provider.

### MCP stdio servers
Configure `src/main/resources/mcp-stdio-servers.json` as needed. The app loads it via `spring.ai.mcp.client.stdio.servers-configuration=classpath:mcp-stdio-servers.json`.

### Troubleshooting
- 401/403 from providers: verify API keys and base URLs.
- Empty/slow responses: reduce temperature, confirm model names.
- Port already in use: set `server.port` or run `-Dserver.port=4001`.


