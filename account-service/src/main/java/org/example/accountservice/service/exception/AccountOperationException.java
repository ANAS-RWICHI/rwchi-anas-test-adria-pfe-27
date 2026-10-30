package org.example.accountservice.service.exception;

/**
 * Business rule violation on an existing account (blocked account, insufficient balance, invalid amount).
 */
public class AccountOperationException extends RuntimeException {

    public AccountOperationException(String message) {
        super(message);
    }
}
