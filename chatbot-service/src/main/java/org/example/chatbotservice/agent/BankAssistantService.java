package org.example.chatbotservice.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Agent entry point shared by the REST endpoint (and any other channel, e.g. Telegram).
 */
@Service
public class BankAssistantService {

    private final ChatClient chatClient;
    private final List<ToolCallbackProvider> mcpToolProviders;

    public BankAssistantService(ChatClient bankAssistantChatClient, List<ToolCallbackProvider> mcpToolProviders) {
        this.chatClient = bankAssistantChatClient;
        this.mcpToolProviders = mcpToolProviders;
    }

    /** Sends a message in a conversation; previous messages of the same conversation are kept in memory. */
    public String chat(String conversationId, String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    /** Names of the MCP tools discovered on the business microservices. */
    public List<String> availableTools() {
        return mcpToolProviders.stream()
                .flatMap(provider -> Arrays.stream(provider.getToolCallbacks()))
                .map(tool -> tool.getToolDefinition().name())
                .sorted()
                .toList();
    }
}
