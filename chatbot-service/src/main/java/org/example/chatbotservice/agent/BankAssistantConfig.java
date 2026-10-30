package org.example.chatbotservice.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BankAssistantConfig {

    private static final String SYSTEM_PROMPT = """
            You are the assistant of a bank. You help users consult bank accounts, record payments \
            and make transfers by calling the available tools, which are connected to the bank's \
            account-service and transaction-service.
            Rules:
            - Always use the tools to read data; never invent accounts, balances, ids or transactions.
            - Account and transaction ids are UUIDs. When the user names a customer reference \
            (e.g. CUST-001) or a currency, look the account up with the tools first.
            - Before creating a transaction or a transfer, summarise it (accounts, amount, currency) \
            and ask the user to confirm. Only call the tool after an explicit confirmation.
            - When a tool returns an error (ceiling exceeded, blocked account, currency mismatch, \
            insufficient balance, service unavailable), explain it plainly; do not retry blindly.
            - Answer in the user's language, briefly; format amounts with their currency.
            """;

    @Bean
    public ChatClient bankAssistantChatClient(ChatClient.Builder builder,
                                              List<ToolCallbackProvider> mcpToolProviders,
                                              ChatMemory chatMemory) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultToolCallbacks(mcpToolProviders.toArray(ToolCallbackProvider[]::new))
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
