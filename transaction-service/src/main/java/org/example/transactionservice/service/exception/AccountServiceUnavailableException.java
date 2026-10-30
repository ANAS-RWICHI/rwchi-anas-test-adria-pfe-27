package org.example.transactionservice.service.exception;

/**
 * Raised by the Resilience4j fallback when account-service cannot be reached or the circuit is open.
 */
public class AccountServiceUnavailableException extends RuntimeException {

    public AccountServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
