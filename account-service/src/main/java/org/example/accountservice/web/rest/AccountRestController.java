package org.example.accountservice.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.AccountStatus;
import org.example.accountservice.service.AccountService;
import org.example.accountservice.web.rest.dto.AmountRequest;
import org.example.accountservice.web.rest.dto.CreateAccountRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping
    public List<Account> findAll(@RequestParam(required = false) String customerReference) {
        return customerReference == null
                ? accountService.findAll()
                : accountService.findByCustomerReference(customerReference);
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable UUID id) {
        return accountService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.create(request.customerReference(), request.currency(), request.initialBalance());
    }

    @PutMapping("/{id}/status/{status}")
    public Account changeStatus(@PathVariable UUID id, @PathVariable AccountStatus status) {
        return accountService.changeStatus(id, status);
    }

    @PutMapping("/{id}/debit")
    public Account debit(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return accountService.debit(id, request.amount());
    }

    @PutMapping("/{id}/credit")
    public Account credit(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return accountService.credit(id, request.amount());
    }
}
