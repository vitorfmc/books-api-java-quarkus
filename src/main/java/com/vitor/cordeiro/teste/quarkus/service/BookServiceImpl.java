package com.vitor.cordeiro.teste.quarkus.service;

import com.vitor.cordeiro.teste.quarkus.client.model.GoogleBooksResponse;
import com.vitor.cordeiro.teste.quarkus.dto.BookCreateDTO;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.entity.Image;
import com.vitor.cordeiro.teste.quarkus.enums.BookStatusEnum;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.DynamoDBGeneralException;
import com.vitor.cordeiro.teste.quarkus.exception.GoogleApiGenericException;
import com.vitor.cordeiro.teste.quarkus.util.ImageUtil;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class BookServiceImpl implements BookService {

    private static final Logger LOG = Logger.getLogger(BookServiceImpl.class);

    @Inject
    GoogleBooksService googleService;

    @Inject
    DynamoDBService dynamoDBService;


    @Override
    public Book save(BookCreateDTO dto)
            throws GoogleApiGenericException, DataValidationException, IOException, DynamoDBGeneralException {

        LOG.info("[BOOK-SAVE] START");

        Book book = null;

        try{
            validate(dto);

            GoogleBooksResponse googleResponse = googleService.getBookByTitle(dto.getTitle());

            if(googleResponse == null || googleResponse.getItems() == null || googleResponse.getItems().isEmpty())
                throw new GoogleApiGenericException("Book not found in Google API");

            var x = googleResponse.getItems().get(0);

            OffsetDateTime published = OffsetDateTime.parse(x.getVolumeInfo().getPublishedDate() + "T00:00:00-00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            OffsetDateTime catalogingDateFormatted = OffsetDateTime.parse(dto.getCatalogingDate() + "T00:00:00-00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            var image = x.getVolumeInfo().getImageLinks() != null ? new Image(
                    x.getVolumeInfo().getImageLinks().getThumbnail(),
                    "data:image/jpeg;base64," +
                            ImageUtil.urlToBase64(x.getVolumeInfo().getImageLinks().getThumbnail() + ".jpeg")
            ) : null;

            book = dynamoDBService.save(new Book( dto.getLibraryCode(),
                                            BookStatusEnum.NEW,
                                            catalogingDateFormatted,
                                            dto.getTitle(),
                                            x.getVolumeInfo().getTitle(),
                                            x.getVolumeInfo().getAuthors(),
                                            x.getVolumeInfo().getCategories(),
                                            x.getVolumeInfo().getPublisher(),
                                            published,
                                            x.getVolumeInfo().getDescription(),
                                            x.getVolumeInfo().getPageCount(),
                                            image));

            LOG.info("[BOOK-SAVE] END");

            return book;

        }catch (Exception e){
            LOG.error("[BOOK-SAVE] Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String libraryCode) throws DataValidationException, DynamoDBGeneralException {

        LOG.info("[BOOK-DELETE] START");

        if(libraryCode == null || libraryCode.trim().isEmpty())
            throw new DataValidationException(Arrays.asList("libraryCode is mandatory"));

        dynamoDBService.delete(libraryCode);

        LOG.info("[BOOK-DELETE] END");
    }

    @Override
    public Book findByLibraryCode(String findByLibraryCode) throws DynamoDBGeneralException {

        LOG.info("[BOOK-FIND] START");

        var book = dynamoDBService.findByLibraryCode(findByLibraryCode);
        if(book == null)
            throw new DynamoDBGeneralException("Book not found");

        LOG.info("[BOOK-FIND] END");

        return book;
    }

    private void validate(BookCreateDTO dto) throws DataValidationException{

        List<String> errors = new ArrayList<>();

        ValidatorFactory factory= Validation.buildDefaultValidatorFactory();
        Validator validator=factory.getValidator();
        List<ConstraintViolation<Object>> violations = new ArrayList<>(validator.validate(dto));

        for(ConstraintViolation<Object> currentError : violations){
            errors.add(currentError.getMessage());
        }

        if(dto.getLibraryCode() != null && dynamoDBService.findByLibraryCode(dto.getLibraryCode()) != null){
            errors.add("book already exists");
        }

        if (!errors.isEmpty()) throw new DataValidationException(errors);
    }
}
