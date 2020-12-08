package com.vitor.cordeiro.teste.quarkus.exception;

public class DynamoDBGeneralException extends TransactionException {
    public DynamoDBGeneralException(String message) {
        super(message);
    }

    public DynamoDBGeneralException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
