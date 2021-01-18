package com.vitor.cordeiro.teste.quarkus.unit;

import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.EntityNotFoundException;
import com.vitor.cordeiro.teste.quarkus.exception.GoogleApiGenericException;
import com.vitor.cordeiro.teste.quarkus.service.BookServiceImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BookResourceUnitTest {

    @InjectMock
    BookServiceImpl service;

    /**
     * testPost201
     */
    @Test
    void testPost201() throws IOException, DataValidationException, GoogleApiGenericException {

        Book book = new Book();
        book.setLibraryCode("TEST");
        Mockito.when(service.save(Mockito.any())).thenReturn(book);

        File creationRequestFile = new File("src/test/java/resources/book_post_201.json");
        String body = Files.readString(creationRequestFile.toPath());

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when().post("/book")
                .then()
                    .body("requestId", notNullValue())
                    .body("errors", nullValue())
                    .body("data.libraryCode", is(book.getLibraryCode()))
                    .statusCode(201);
    }

    /**
     * testPost400DataValidationException
     */
    @Test
    void testPost400DataValidationException() throws DataValidationException, GoogleApiGenericException {

        Mockito.when(service.save(Mockito.any())).thenThrow(new DataValidationException(Arrays.asList("1","2","3")));

        given()
                .header("Content-Type", "application/json")
                .body("{}")
                .when().post("/book")
                .then()
                    .body("requestId", notNullValue())
                    .body("errors.size()", is(3))
                    .body("data", nullValue())
                    .statusCode(400);
    }

    /**
     * testPut200
     */
    @Test
    void testPut200() throws IOException, DataValidationException, GoogleApiGenericException, EntityNotFoundException {

        String libraryCode = "TEST";
        Book book = new Book();
        book.setLibraryCode(libraryCode);
        Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(book);

        File creationRequestFile = new File("src/test/java/resources/book_put_200.json");
        String body = Files.readString(creationRequestFile.toPath());

        given()
                .header("Content-Type", "application/json")
                .pathParam("libraryCode", libraryCode)
                .body(body)
                .when().put("/book/{libraryCode}")
                .then()
                    .body("requestId", notNullValue())
                    .body("errors", nullValue())
                    .body("data.libraryCode", is(book.getLibraryCode()))
                    .statusCode(200);
    }

    /**
     * testPut400DataValidationException
     */
    @Test
    void testPut400DataValidationException() throws DataValidationException, GoogleApiGenericException, EntityNotFoundException {

        Mockito.when(service.update(Mockito.any(), Mockito.any())).thenThrow(new DataValidationException(Arrays.asList("1","2")));

        given()
                .header("Content-Type", "application/json")
                .pathParam("libraryCode", "TEST")
                .body("{}")
                .when().put("/book/{libraryCode}")
                .then()
                    .body("requestId", notNullValue())
                    .body("errors.size()", is(2))
                    .body("data", nullValue())
                    .statusCode(400);
    }

    /**
     * testPut404
     */
    @Test
    void testPut404() throws DataValidationException, GoogleApiGenericException, EntityNotFoundException {

        Mockito.when(service.update(Mockito.any(), Mockito.any())).thenThrow(EntityNotFoundException.class);

        given()
                .header("Content-Type", "application/json")
                .pathParam("libraryCode", "TEST")
                .body("{}")
                .when().put("/book/{libraryCode}")
                .then()
                .body("requestId", notNullValue())
                .body("data", nullValue())
                .statusCode(404);
    }

    /**
     * testDelete204
     */
    @Test
    void testDelete204() {
        given()
                .pathParam("libraryCode", "TEST")
                .when().delete("/book/{libraryCode}")
                .then()
                .statusCode(204);
    }

    /**
     * testGet200
     */
    @Test
    void testGet200() throws EntityNotFoundException {

        String libraryCode = "TEST";
        Book book = new Book();
        book.setLibraryCode(libraryCode);
        Mockito.when(service.findByLibraryCode(Mockito.any())).thenReturn(book);

        given()
                .header("Content-Type", "application/json")
                .queryParam("libraryCode", libraryCode)
                .when().get("/book")
                .then()
                .body("requestId", notNullValue())
                .body("errors", nullValue())
                .body("data.libraryCode", is(book.getLibraryCode()))
                .statusCode(200);
    }

    /**
     * testGet404
     */
    @Test
    void testGet404() throws EntityNotFoundException {

        Mockito.when(service.findByLibraryCode(Mockito.any())).thenThrow(EntityNotFoundException.class);

        given()
                .header("Content-Type", "application/json")
                .queryParam("libraryCode", "TEST")
                .when().get("/book")
                .then()
                .body("requestId", notNullValue())
                .body("errors", notNullValue())
                .body("data.libraryCode", nullValue())
                .statusCode(404);
    }

}
