package org.example.transactionservice.mcp;

import lombok.RequiredArgsConstructor;
import org.example.transactionservice.domain.Transaction;
import org.example.transactionservice.service.TransactionService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * MCP adapter: exposes the transaction business operations as tools for AI agents.
 */
@Component
@RequiredArgsConstructor
public class TransactionTools {

    private final TransactionService transactionService;

    @Tool(description = "Get a transaction by its UUID")
    public Transaction getTransaction(@ToolParam(description = "Transaction UUID") String transactionId) {
        return transactionService.findById(UUID.fromString(transactionId));
    }

    @Tool(description = "List the transactions of a bank account, most recent first")
    public List<Transaction> listTransactionsByAccount(@ToolParam(description = "Account UUID") String accountId) {
        return transactionService.findByAccountId(UUID.fromString(accountId));
    }

    @Tool(description = "Record a payment from a bank account. The amount must be positive, not exceed the "
            + "configured ceiling, match the account currency and be covered by the balance.")
    public Transaction createTransaction(@ToolParam(description = "Account UUID") String accountId,
                                         @ToolParam(description = "Amount, strictly positive") double amount,
                                         @ToolParam(description = "ISO currency code of the account, e.g. EUR") String currency,
                                         @ToolParam(description = "Merchant category, e.g. GROCERY, TRAVEL") String merchantCategory,
                                         @ToolParam(description = "ISO country code, e.g. MA, FR") String country) {
        return transactionService.create(UUID.fromString(accountId), BigDecimal.valueOf(amount), currency,
                merchantCategory, country);
    }

    @Tool(description = "Transfer money between two active accounts with the same currency")
    public List<Transaction> transfer(@ToolParam(description = "Source account UUID") String fromAccountId,
                                      @ToolParam(description = "Target account UUID") String toAccountId,
                                      @ToolParam(description = "Amount, strictly positive") double amount) {
        return transactionService.transfer(UUID.fromString(fromAccountId), UUID.fromString(toAccountId),
                BigDecimal.valueOf(amount));
    }
}
