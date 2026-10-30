package org.example.transactionservice.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.transactionservice.domain.Transaction;
import org.example.transactionservice.service.TransactionService;
import org.example.transactionservice.web.rest.dto.CreateTransactionRequest;
import org.example.transactionservice.web.rest.dto.TransferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    @GetMapping
    public List<Transaction> findAll(@RequestParam(required = false) UUID accountId) {
        return accountId == null
                ? transactionService.findAll()
                : transactionService.findByAccountId(accountId);
    }

    @GetMapping("/{id}")
    public Transaction findById(@PathVariable UUID id) {
        return transactionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.create(request.accountId(), request.amount(), request.currency(),
                request.merchantCategory(), request.country());
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Transaction> transfer(@Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request.fromAccountId(), request.toAccountId(), request.amount());
    }
}
