package org.example.transactionservice.client;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.service.exception.AccountNotFoundException;
import org.example.transactionservice.service.exception.AccountServiceUnavailableException;
import org.example.transactionservice.service.exception.TransactionRejectedException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@Component
public class AccountRestClientFallbackFactory implements FallbackFactory<AccountRestClient> {

    @Override
    public AccountRestClient create(Throwable cause) {
        return new AccountRestClient() {
            @Override
            public AccountDTO getAccount(UUID id) {
                throw translate(id, cause);
            }

            @Override
            public AccountDTO debit(UUID id, AmountRequest request) {
                throw translate(id, cause);
            }

            @Override
            public AccountDTO credit(UUID id, AmountRequest request) {
                throw translate(id, cause);
            }
        };
    }

    private RuntimeException translate(UUID accountId, Throwable cause) {
        if (cause instanceof FeignException.NotFound) {
            return new AccountNotFoundException(accountId);
        }
        if (cause instanceof FeignException.FeignClientException clientError) {
            return new TransactionRejectedException("Refused by account-service: " + clientError.contentUTF8());
        }
        log.warn("account-service unavailable for account {}: {}", accountId, cause.toString());
        return new AccountServiceUnavailableException(
                "account-service is unavailable, the operation was not performed", cause);
    }
}
