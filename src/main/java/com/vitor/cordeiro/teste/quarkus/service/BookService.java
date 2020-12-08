package com.vitor.cordeiro.teste.quarkus.service;

import com.vitor.cordeiro.teste.quarkus.dto.BookCreateDTO;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.DynamoDBGeneralException;
import com.vitor.cordeiro.teste.quarkus.exception.GoogleApiGenericException;

import java.io.IOException;

public interface BookService {

    Book save(BookCreateDTO dto) throws GoogleApiGenericException, DataValidationException, IOException, DynamoDBGeneralException;

    void delete(String libraryCode) throws DataValidationException, DynamoDBGeneralException;

    Book findByLibraryCode(String findByLibraryCode) throws DataValidationException, DynamoDBGeneralException;

}
