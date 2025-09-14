package com.akatsuki.base55.config;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpConfig {

    @Bean
    public List<McpToolSpec> mcpToolSpecs (List<McpSyncClient> mcpSyncClients) {
        return mcpSyncClients.stream().map(client -> client.listTools().tools().stream().map(tool -> new McpToolSpec(
                tool.name(),
                tool.description(),
                client.getServerInfo().name()
        )).toList()).flatMap(List::stream).toList();
    }
}
