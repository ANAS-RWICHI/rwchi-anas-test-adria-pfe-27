package org.example.accountservice.service;

import lombok.RequiredArgsConstructor;
import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.AccountStatus;
import org.example.accountservice.repository.AccountRepository;
import org.example.accountservice.service.exception.AccountNotFoundException;
import org.example.accountservice.service.exception.AccountOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Account> findByCustomerReference(String customerReference) {
        return accountRepository.findByCustomerReference(customerReference);
    }

    public Account create(String customerReference, String currency, double initialBalance) {
        if (initialBalance < 0) {
            throw new AccountOperationException("Initial balance must not be negative");
        }
        Account account = Account.builder()
                .customerReference(customerReference)
                .currency(currency.toUpperCase())
                .status(AccountStatus.ACTIVE)
                .balance(initialBalance)
                .build();
        return accountRepository.save(account);
    }

    public Account changeStatus(UUID id, AccountStatus status) {
        Account account = findById(id);
        account.setStatus(status);
        return account;
    }

    public Account debit(UUID id, double amount) {
        Account account = findActiveAccount(id, amount);
        if (account.getBalance() < amount) {
            throw new AccountOperationException(
                    "Insufficient balance on account " + id + ": " + account.getBalance() + " < " + amount);
        }
        account.setBalance(account.getBalance() - amount);
        return account;
    }

    public Account credit(UUID id, double amount) {
        Account account = findActiveAccount(id, amount);
        account.setBalance(account.getBalance() + amount);
        return account;
    }

    private Account findActiveAccount(UUID id, double amount) {
        if (amount <= 0) {
            throw new AccountOperationException("Amount must be strictly positive");
        }
        Account account = findById(id);
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountOperationException("Account " + id + " is not active");
        }
        return account;
    }
}
