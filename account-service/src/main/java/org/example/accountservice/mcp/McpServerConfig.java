package org.example.accountservice.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider accountToolCallbackProvider(AccountTools accountTools) {
        return MethodToolCallbackProvider.builder().toolObjects(accountTools).build();
    }
}
