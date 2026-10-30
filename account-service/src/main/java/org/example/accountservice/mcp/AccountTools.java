package org.example.accountservice.mcp;

import lombok.RequiredArgsConstructor;
import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.AccountStatus;
import org.example.accountservice.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * MCP adapter: exposes the account business operations as tools for AI agents.
 */
@Component
@RequiredArgsConstructor
public class AccountTools {

    private final AccountService accountService;

    @Tool(description = "List all bank accounts with their status, currency and balance")
    public List<Account> listAccounts() {
        return accountService.findAll();
    }

    @Tool(description = "Get a bank account by its UUID")
    public Account getAccount(@ToolParam(description = "Account UUID") String accountId) {
        return accountService.findById(UUID.fromString(accountId));
    }

    @Tool(description = "List the bank accounts owned by a customer")
    public List<Account> getAccountsByCustomer(@ToolParam(description = "Customer reference") String customerReference) {
        return accountService.findByCustomerReference(customerReference);
    }

    @Tool(description = "Open a new ACTIVE bank account")
    public Account createAccount(@ToolParam(description = "Customer reference") String customerReference,
                                 @ToolParam(description = "ISO currency code, e.g. EUR or MAD") String currency,
                                 @ToolParam(description = "Initial balance, zero or positive") double initialBalance) {
        return accountService.create(customerReference, currency, initialBalance);
    }

    @Tool(description = "Block or activate a bank account")
    public Account changeAccountStatus(@ToolParam(description = "Account UUID") String accountId,
                                       @ToolParam(description = "ACTIVE or BLOCKED") AccountStatus status) {
        return accountService.changeStatus(UUID.fromString(accountId), status);
    }
}
