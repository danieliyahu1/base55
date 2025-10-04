package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.LlmEvaluationResult;
import com.akatsuki.base55.domain.SubTask;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.akatsuki.base55.constant.AgentConstants.*;

@Component
public class TaskDecomposer {

    ChatClient chatClient;
    ChatMemory chatMemory;

    public TaskDecomposer(@Qualifier("huggingFaceChatModel") ChatModel huggingFaceChatModel
            , JdbcChatMemoryRepository chatMemoryRepository) {
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(25)
                .build();

        this.chatClient = ChatClient.builder(huggingFaceChatModel).defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    public SubTask getNextSubTask(SubTask lastSubTask, String systemPrompt, String conversationId, LlmEvaluationResult llmEvaluationResult) {
        return this.chatClient.prompt()
                .system(s -> s.text(
                        SYSTEM_PROMPT_TASK_DECOMPOSER
                                + "\n"
                                + systemPrompt
                ))
                .user(u -> u.text(NEXT_SUB_TASK_TASK)
                        .param("sub-task", lastSubTask.description())
                        .param("evaluation", llmEvaluationResult))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .entity(SubTask.class);
    }

    public SubTask firstDecomposition(String systemPrompt, String conversationId) {
        return this.chatClient.prompt()
                .system(s -> s.text(
                        SYSTEM_PROMPT_TASK_DECOMPOSER
                                + "\n"
                                + systemPrompt
                ))
                .user(u -> u.text(FIRST_DECOMPOSITION))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .entity(SubTask.class);
    }

    private void clearChatMemoryConversation(String conversationId) {
        this.chatMemory.clear(conversationId);
    }
}
