package org.example.transactionservice.web.rest;

import org.example.transactionservice.service.exception.AccountNotFoundException;
import org.example.transactionservice.service.exception.AccountServiceUnavailableException;
import org.example.transactionservice.service.exception.TransactionNotFoundException;
import org.example.transactionservice.service.exception.TransactionRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = TransactionRestController.class)
public class RestExceptionHandler {

    @ExceptionHandler({TransactionNotFoundException.class, AccountNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(TransactionRejectedException.class)
    public ProblemDetail handleRejected(TransactionRejectedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), e.getMessage());
    }

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ProblemDetail handleUnavailable(AccountServiceUnavailableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }
}
