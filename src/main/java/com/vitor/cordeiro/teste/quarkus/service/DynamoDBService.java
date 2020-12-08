package com.vitor.cordeiro.teste.quarkus.service;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.exception.DynamoDBGeneralException;

import java.util.List;

public interface DynamoDBService {

    Book save(Book book) throws DynamoDBGeneralException;
    void delete(String libraryCode) throws DynamoDBGeneralException;
    Book findByLibraryCode(String libraryCode) throws DynamoDBGeneralException;
}
