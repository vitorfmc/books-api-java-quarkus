package com.vitor.cordeiro.teste.quarkus.service;

import com.vitor.cordeiro.teste.quarkus.dto.BookCreateDTO;
import com.vitor.cordeiro.teste.quarkus.dto.BookUpdateDTO;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.DynamoDBGeneralException;
import com.vitor.cordeiro.teste.quarkus.exception.EntityNotFoundException;
import com.vitor.cordeiro.teste.quarkus.exception.GoogleApiGenericException;

import java.io.IOException;

public interface BookService {

    Book save(BookCreateDTO dto) throws GoogleApiGenericException, DataValidationException, DynamoDBGeneralException;

    Book update(String libraryCode, BookUpdateDTO dto) throws GoogleApiGenericException, DataValidationException, DynamoDBGeneralException, EntityNotFoundException;

    void delete(String libraryCode) throws DataValidationException, DynamoDBGeneralException, EntityNotFoundException;

    Book findByLibraryCode(String findByLibraryCode) throws DataValidationException, DynamoDBGeneralException, EntityNotFoundException;

}
