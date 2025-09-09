package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.McpToolSpec;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class AgentFactory {

    public AiAgent createAgent(ToolCallbackProvider toolCallbackProvider) {
        return new AiAgent();
    }
}
