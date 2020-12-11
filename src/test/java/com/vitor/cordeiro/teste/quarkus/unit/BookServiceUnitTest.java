package com.vitor.cordeiro.teste.quarkus.unit;

import com.vitor.cordeiro.teste.quarkus.client.model.GoogleBooksResponse;
import com.vitor.cordeiro.teste.quarkus.client.model.GoogleBooksResponseItem;
import com.vitor.cordeiro.teste.quarkus.client.model.GoogleBooksVolumeInfo;
import com.vitor.cordeiro.teste.quarkus.dto.BookCreateDTO;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.GoogleApiGenericException;
import com.vitor.cordeiro.teste.quarkus.service.BookServiceImpl;
import com.vitor.cordeiro.teste.quarkus.service.DynamoDBServiceImpl;
import com.vitor.cordeiro.teste.quarkus.service.GoogleBooksService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Arrays;

import static org.mockito.Mockito.any;

@QuarkusTest
public class BookServiceUnitTest {

    @Inject
    BookServiceImpl service;

    @InjectMock
    DynamoDBServiceImpl dynamoDBService;

    @InjectMock
    GoogleBooksService googleService;


    private GoogleBooksResponse googleOkResponse;
    private Book bookOk;

    @BeforeEach
    public void setupBeforeEach() {

        var item1 = new GoogleBooksResponseItem();
        item1.setVolumeInfo(new GoogleBooksVolumeInfo());

        googleOkResponse = new GoogleBooksResponse();
        googleOkResponse.setItems(Arrays.asList(item1));

        bookOk = new Book();
        bookOk.setLibraryCode("TEST");
    }

    @Test
    public void saveOk() throws DataValidationException, GoogleApiGenericException {

        Mockito.when(googleService.getBookByTitle(any())).thenReturn(googleOkResponse);
        Mockito.when(dynamoDBService.findByLibraryCode(any())).thenReturn(null);
        Mockito.when(dynamoDBService.save(any())).thenReturn(bookOk);

        Book resp = service.save(new BookCreateDTO("T", "T", "2020-01-01"));

        Assertions.assertEquals(resp, bookOk);
        Mockito.verify(dynamoDBService, Mockito.times(1)).findByLibraryCode(any());
        Mockito.verify(googleService, Mockito.times(1)).getBookByTitle(any());
    }

    @Test
    public void saveValidationErrorMandatoryData() throws DataValidationException, GoogleApiGenericException {

        DataValidationException error = Assertions.assertThrows(DataValidationException.class, () -> {
            service.save(new BookCreateDTO("", "", ""));
        });

        Assertions.assertEquals(error.getMessages().size(), 3);
        Mockito.verify(dynamoDBService, Mockito.times(0)).findByLibraryCode(any());
        Mockito.verify(googleService, Mockito.times(0)).getBookByTitle(any());
    }

    @Test
    public void saveValidationErrorWrongFormat() throws DataValidationException, GoogleApiGenericException {

        Mockito.when(dynamoDBService.findByLibraryCode(any())).thenReturn(null);

        DataValidationException error = Assertions.assertThrows(DataValidationException.class, () -> {
            service.save(new BookCreateDTO("T", "T", "12"));
        });

        Assertions.assertEquals(error.getMessages().size(), 1);
        Mockito.verify(dynamoDBService, Mockito.times(1)).findByLibraryCode(any());
        Mockito.verify(googleService, Mockito.times(0)).getBookByTitle(any());
    }

    @Test
    public void saveValidationErrorAlreadyExists() throws DataValidationException, GoogleApiGenericException {

        Mockito.when(dynamoDBService.findByLibraryCode(any())).thenReturn(new Book());

        DataValidationException error = Assertions.assertThrows(DataValidationException.class, () -> {
            service.save(new BookCreateDTO("T", "T", "2020-12-12"));
        });

        Assertions.assertEquals(error.getMessages().size(), 1);
        Mockito.verify(dynamoDBService, Mockito.times(1)).findByLibraryCode(any());
        Mockito.verify(googleService, Mockito.times(0)).getBookByTitle(any());
    }

    //TODO WRITE TESTS TO UPDATE, DELETE E FIND

}
