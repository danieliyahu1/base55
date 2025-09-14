package com.akatsuki.base55.registry;

import com.akatsuki.base55.domain.ToolRegistryKey;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ToolRegistry {

    private final Map<ToolRegistryKey, ToolCallback> registry;

     public ToolRegistry(List<McpToolSpec> mcpToolSpecs, ToolCallbackProvider toolCallbackProvider) {
            this.registry = mcpToolSpecs.stream()
                    .map(spec -> {
                        // Try to find matching ToolCallback
                        Optional<ToolCallback> matching = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                                .filter(tool -> tool.getToolDefinition().name().contains(spec.name())
                                        && tool.getToolDefinition().description().equals(spec.description()))
                                .findFirst();
                        // Convert to Map.Entry if found
                        return matching.map(toolCallback -> Map.entry(
                                new ToolRegistryKey(spec.serverName(), spec.name()),
                                toolCallback
                        ));
                    })
                    // Flatten Optional<Map.Entry> to Stream<Map.Entry>
                    .flatMap(Optional::stream)
                    // Collect into map
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            log.info("Mcp tool registry initialized with {} tools", registry.size());
     }

     public Optional<ToolCallback> getToolCallback(String serverName, String toolName) {
         return Optional.ofNullable(registry.get(new ToolRegistryKey(serverName, toolName)));
     }

     public List<ToolCallback> getMatchingToolCallBacks(List<McpToolSpec> mcpToolSpecs) {
         return mcpToolSpecs.stream()
                 .map(spec -> getToolCallback(spec.serverName(), spec.name()))
                 .flatMap(Optional::stream)
                 .toList();
     }
}
