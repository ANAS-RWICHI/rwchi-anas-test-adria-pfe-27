package org.example.transactionservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.client.AccountDTO;
import org.example.transactionservice.client.AccountRestClient;
import org.example.transactionservice.client.AmountRequest;
import org.example.transactionservice.domain.Transaction;
import org.example.transactionservice.domain.TransactionType;
import org.example.transactionservice.repository.TransactionRepository;
import org.example.transactionservice.service.exception.TransactionNotFoundException;
import org.example.transactionservice.service.exception.TransactionRejectedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Single business entry point, shared by the REST and MCP adapters.
 */
@Slf4j
@Service
@RefreshScope
@Transactional
public class TransactionService {

    private static final String TRANSFER_CATEGORY = "TRANSFER";

    private final TransactionRepository transactionRepository;
    private final AccountRestClient accountRestClient;
    private final BigDecimal maxAmount;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRestClient accountRestClient,
                              @Value("${bank.transaction.max-amount:1000}") BigDecimal maxAmount) {
        this.transactionRepository = transactionRepository;
        this.accountRestClient = accountRestClient;
        this.maxAmount = maxAmount;
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Transaction findById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByAccountId(UUID accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    /**
     * Records a payment: checks the rules against the current account state, saves the transaction,
     * then debits the account. A failed debit rolls the local insert back.
     */
    public Transaction create(UUID accountId, BigDecimal amount, String currency,
                              String merchantCategory, String country) {
        checkAmount(amount);
        AccountDTO account = accountRestClient.getAccount(accountId);
        checkAccount(account, currency);
        checkBalance(account, amount);

        Transaction transaction = transactionRepository.save(newTransaction(
                accountId, amount, account.currency(), merchantCategory, country, TransactionType.DEBIT));
        accountRestClient.debit(accountId, new AmountRequest(amount.doubleValue()));
        return transaction;
    }

    /**
     * Transfers money between two accounts of the same currency: one DEBIT and one CREDIT transaction.
     * If the credit fails after the debit succeeded, the source account is credited back.
     */
    public List<Transaction> transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new TransactionRejectedException("Source and target accounts must be different");
        }
        checkAmount(amount);
        AccountDTO from = accountRestClient.getAccount(fromAccountId);
        AccountDTO to = accountRestClient.getAccount(toAccountId);
        checkAccount(from, from.currency());
        checkAccount(to, from.currency());
        checkBalance(from, amount);

        Transaction debit = transactionRepository.save(newTransaction(
                fromAccountId, amount, from.currency(), TRANSFER_CATEGORY, null, TransactionType.DEBIT));
        Transaction credit = transactionRepository.save(newTransaction(
                toAccountId, amount, from.currency(), TRANSFER_CATEGORY, null, TransactionType.CREDIT));

        AmountRequest request = new AmountRequest(amount.doubleValue());
        accountRestClient.debit(fromAccountId, request);
        try {
            accountRestClient.credit(toAccountId, request);
        } catch (RuntimeException e) {
            log.warn("Credit of {} failed, compensating debit of {}", toAccountId, fromAccountId);
            accountRestClient.credit(fromAccountId, request);
            throw e;
        }
        return List.of(debit, credit);
    }

    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new TransactionRejectedException("Amount must be strictly positive");
        }
        if (amount.compareTo(maxAmount) > 0) {
            throw new TransactionRejectedException("Amount " + amount + " exceeds the ceiling of " + maxAmount);
        }
    }

    private void checkAccount(AccountDTO account, String currency) {
        if (!account.isActive()) {
            throw new TransactionRejectedException("Account " + account.id() + " is not active");
        }
        if (!account.currency().equalsIgnoreCase(currency)) {
            throw new TransactionRejectedException("Currency " + currency + " does not match account currency "
                    + account.currency());
        }
    }

    private void checkBalance(AccountDTO account, BigDecimal amount) {
        if (BigDecimal.valueOf(account.balance()).compareTo(amount) < 0) {
            throw new TransactionRejectedException("Insufficient balance on account " + account.id()
                    + ": " + account.balance() + " < " + amount);
        }
    }

    private Transaction newTransaction(UUID accountId, BigDecimal amount, String currency,
                                       String merchantCategory, String country, TransactionType type) {
        return Transaction.builder()
                .accountId(accountId)
                .amount(amount)
                .currency(currency.toUpperCase())
                .merchantCategory(merchantCategory)
                .country(country)
                .createdAt(Instant.now())
                .type(type)
                .build();
    }
}
