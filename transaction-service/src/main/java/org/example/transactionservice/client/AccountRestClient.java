package org.example.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;


@FeignClient(name = "account-service", url = "${ACCOUNT_SERVICE_URL:http://localhost:8081/}", path = "/api/accounts",
        fallbackFactory = AccountRestClientFallbackFactory.class)
public interface AccountRestClient {

    @GetMapping("/{id}")
    AccountDTO getAccount(@PathVariable UUID id);

    @PutMapping("/{id}/debit")
    AccountDTO debit(@PathVariable UUID id, @RequestBody AmountRequest request);

    @PutMapping("/{id}/credit")
    AccountDTO credit(@PathVariable UUID id, @RequestBody AmountRequest request);
}
