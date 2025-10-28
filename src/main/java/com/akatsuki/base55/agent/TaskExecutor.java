package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.agent.TaskExecutorResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.List;
import java.util.UUID;

import static com.akatsuki.base55.constant.AgentConstants.SYSTEM_PROMPT_TASK_EXECUTOR;
import static com.akatsuki.base55.constant.AgentConstants.USER_PROMPT_SUB_TASK_EXECUTOR;

public class TaskExecutor {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final String agentSystemPrompt;

    public TaskExecutor (ChatModel chatModel,
                            ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory,
                            String agentSystemPrompt){
        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
        this.chatMemory = chatMemory;
        this.agentSystemPrompt = agentSystemPrompt;
    }

    public TaskExecutorResponse executeTask(String task, String conversationId) {
        return this.chatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_TASK_EXECUTOR)
                        .param("{ai_agent}", agentSystemPrompt))
                .user(u -> u.text(USER_PROMPT_SUB_TASK_EXECUTOR)
                        .param("sub-task", task))
                .advisors(a -> a.param("conversationId", conversationId))
                .call()
                .entity(TaskExecutorResponse.class);
    }

    public void clearChatMemory(String conversationId) {
        chatMemory.clear(conversationId);
    }

    public List<Message> getConversationHistory(UUID agentId) {
        return chatMemory.get(agentId.toString());
    }
}
