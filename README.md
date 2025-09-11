## Base55

Spring Boot service that generates agent workflows and filters MCP tools.

### Prerequisites
- **JDK 21+**
- **Maven 3.9+** (or use `mvnw`/`mvnw.cmd`)
- Optional: **Docker**

### Quick start

Run locally (Maven) or via Docker. The service listens on `http://localhost:4000` by default. See Runtime configuration for the required arguments.

### Runtime configuration (args/env) — required
- **GROQ_API_KEY**: (e.g. `sk-...`)
- **GROQ_BASE_URL**: (e.g. `https://api.groq.com/openai`)
- **GROQ_MODEL**: (e.g. `llama3-groq-70b-8192-tool-use`)
- **GROQ_TEMPERATURE**: (e.g. `0.2`)
- **OPENROUTER_API_KEY**: (e.g. `or-...`)
- **OPENROUTER_BASE_URL**: (e.g. `https://openrouter.ai/api`)
- **OPENROUTER_MODEL**: (e.g. `meta-llama/llama-3.1-70b-instruct`)
- **OPENROUTER_TEMPERATURE**: (e.g. `0.2`)
- **SPRING_PROFILES_ACTIVE**: (e.g. `dev`)

### API (minimal)
Base path: `http://localhost:4000/api/v1/base55`

