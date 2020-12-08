package com.vitor.cordeiro.teste.quarkus.exception;

public class TransactionException extends RuntimeException {
    public TransactionException(String message){
        super(message);
    }

    public TransactionException(String message, Throwable throwable){
        super(message,throwable);
    }
}
