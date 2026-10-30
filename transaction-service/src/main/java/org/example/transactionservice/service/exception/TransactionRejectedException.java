package org.example.transactionservice.service.exception;

/**
 * Business rule violation: invalid amount, ceiling exceeded, inactive account, currency mismatch, insufficient balance.
 */
public class TransactionRejectedException extends RuntimeException {

    public TransactionRejectedException(String message) {
        super(message);
    }
}
