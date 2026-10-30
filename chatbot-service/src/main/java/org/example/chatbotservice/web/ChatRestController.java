package org.example.chatbotservice.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatbotservice.agent.BankAssistantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final BankAssistantService bankAssistantService;

    public record ChatRequest(String conversationId, @NotBlank String message) {
    }

    public record ChatResponse(String conversationId, String answer) {
    }

    /** Omit conversationId to start a new conversation; send it back to continue the same one. */
    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        String conversationId = request.conversationId() == null || request.conversationId().isBlank()
                ? UUID.randomUUID().toString()
                : request.conversationId();
        return new ChatResponse(conversationId, bankAssistantService.chat(conversationId, request.message()));
    }

    @GetMapping("/tools")
    public List<String> tools() {
        return bankAssistantService.availableTools();
    }

    /** LLM or MCP failures (invalid API key, quota, unreachable service) are reported as 502. */
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleAgentFailure(RuntimeException e) {
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        log.error("Assistant call failed", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                "Assistant unavailable: " + e.getMessage() + " (" + root.getMessage() + ")");
    }
}
