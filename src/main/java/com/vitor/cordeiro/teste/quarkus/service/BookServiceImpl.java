package com.vitor.cordeiro.teste.quarkus.service;

import com.vitor.cordeiro.teste.quarkus.client.model.GoogleBooksResponse;
import com.vitor.cordeiro.teste.quarkus.client.model.GoogleBooksResponseItem;
import com.vitor.cordeiro.teste.quarkus.dto.BookCreateDTO;
import com.vitor.cordeiro.teste.quarkus.dto.BookUpdateDTO;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.entity.Image;
import com.vitor.cordeiro.teste.quarkus.enums.BookStatusEnum;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.DynamoDBGeneralException;
import com.vitor.cordeiro.teste.quarkus.exception.EntityNotFoundException;
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

        Book book;

        try{
            List<String> errors = validate(dto);
            if(dto.getLibraryCode() != null && dynamoDBService.findByLibraryCode(dto.getLibraryCode()) != null){
                errors.add("book already exists");
            }
            if (!errors.isEmpty()) throw new DataValidationException(errors);

            var googleResponse = getGoogleResponse(dto.getTitle());

            book = dynamoDBService.save(construct(googleResponse, dto.getLibraryCode(),
                    dto.getCatalogingDate(), dto.getTitle()));

            LOG.info("[BOOK-SAVE] END");

            return book;

        }catch (Exception e){
            LOG.error("[BOOK-SAVE] Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Book update(String libraryCode, BookUpdateDTO dto)
            throws GoogleApiGenericException, DataValidationException, IOException, DynamoDBGeneralException, EntityNotFoundException {

        LOG.info("[BOOK-UPDATE] START");

        Book book;

        try{
            List<String> errors = validate(dto);
            if (!errors.isEmpty()) throw new DataValidationException(errors);

            if (dynamoDBService.findByLibraryCode(libraryCode) == null)
                throw new EntityNotFoundException("Book not found");

            var googleResponse = getGoogleResponse(dto.getTitle());

            book = dynamoDBService.save(construct(googleResponse, libraryCode,
                    dto.getCatalogingDate(), dto.getTitle()));

            LOG.info("[BOOK-UPDATE] END");

            return book;

        }catch (Exception e){
            LOG.error("[BOOK-UPDATE] Error: " + e.getMessage());
            throw e;
        }
    }


    private Book construct(GoogleBooksResponseItem googleResponseItem, String libraryCode, String catalogingDate, String title) throws IOException {
        OffsetDateTime published = OffsetDateTime.parse(googleResponseItem.getVolumeInfo().getPublishedDate() + "T00:00:00-00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        OffsetDateTime catalogingDateFormatted = OffsetDateTime.parse(catalogingDate + "T00:00:00-00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        var image = googleResponseItem.getVolumeInfo().getImageLinks() != null ? new Image(
                googleResponseItem.getVolumeInfo().getImageLinks().getThumbnail(),
                "data:image/jpeg;base64," +
                        ImageUtil.urlToBase64(googleResponseItem.getVolumeInfo().getImageLinks().getThumbnail() + ".jpeg")
        ) : null;

        return new Book(libraryCode,
                            BookStatusEnum.NEW,
                            catalogingDateFormatted,
                            title,
                            googleResponseItem.getVolumeInfo().getTitle(),
                            googleResponseItem.getVolumeInfo().getAuthors(),
                            googleResponseItem.getVolumeInfo().getCategories(),
                            googleResponseItem.getVolumeInfo().getPublisher(),
                            published,
                            googleResponseItem.getVolumeInfo().getDescription(),
                            googleResponseItem.getVolumeInfo().getPageCount(),
                            image);
    }

    private GoogleBooksResponseItem getGoogleResponse(String title) throws GoogleApiGenericException, DataValidationException {
        GoogleBooksResponse googleResponse = googleService.getBookByTitle(title);

        if(googleResponse == null || googleResponse.getItems() == null || googleResponse.getItems().isEmpty())
            throw new GoogleApiGenericException("Book not found in Google API");

        return googleResponse.getItems().get(0);
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

    private List<String> validate(Object obj) {

        List<String> errors = new ArrayList<>();

        ValidatorFactory factory= Validation.buildDefaultValidatorFactory();
        Validator validator=factory.getValidator();
        List<ConstraintViolation<Object>> violations = new ArrayList<>(validator.validate(obj));

        for(ConstraintViolation<Object> currentError : violations){
            errors.add(currentError.getMessage());
        }

        return errors;
    }
}
