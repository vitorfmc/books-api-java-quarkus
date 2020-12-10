package com.vitor.cordeiro.teste.quarkus.service;

import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.exception.DynamoDBGeneralException;
import com.vitor.cordeiro.teste.quarkus.exception.EntityNotFoundException;

public interface DynamoDBService {

    Book save(Book book) throws DynamoDBGeneralException;
    void delete(Book book) throws DynamoDBGeneralException;
    Book findByLibraryCode(String libraryCode) throws DynamoDBGeneralException;
}
