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
            List<String> errors = new ArrayList<>();

            /*
                NOTE: CloudEvent is not able to build the ValidatorFactory as it requires a specific bootstrap for native images.

                ValidatorFactory factory= Validation.buildDefaultValidatorFactory();
                Validator validator=factory.getValidator();
                List<ConstraintViolation<Object>> violations = new ArrayList<>(validator.validate(obj));

                for(ConstraintViolation<Object> currentError : violations){
                    errors.add(currentError.getMessage());
            }*/

            if(dto.getCatalogingDate() == null || dto.getTitle().isEmpty()){
                errors.add("catalogingDate is mandatory");
            }else if(!dto.getCatalogingDate().matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")){
                errors.add("catalogingDate format should be: yyyy-MM-dd");
            }

            if(dto.getTitle() == null || dto.getTitle().isEmpty())
                errors.add("title is mandatory");

            if(dto.getLibraryCode() == null || dto.getLibraryCode().isEmpty()){
                errors.add("libraryCode is mandatory");
            }else if(dynamoDBService.findByLibraryCode(dto.getLibraryCode()) != null){
                errors.add("book already exists");
            }

            if (!errors.isEmpty()) throw new DataValidationException(errors);

            var googleResponse = getGoogleResponse(dto.getTitle());

            book = dynamoDBService.save(construct(googleResponse, dto.getLibraryCode(),
                    dto.getCatalogingDate(), dto.getTitle()));

            LOG.info("[BOOK-SAVE] END");

            return book;

        }catch (Exception e){
            LOG.error("[BOOK-SAVE] Error: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Book update(String libraryCode, BookUpdateDTO dto)
            throws GoogleApiGenericException, DataValidationException, IOException, DynamoDBGeneralException, EntityNotFoundException {

        LOG.info("[BOOK-UPDATE] START");

        Book book;

        try{
            List<String> errors = new ArrayList<>();

            if(dto.getCatalogingDate() == null || dto.getTitle().isEmpty()){
                errors.add("catalogingDate is mandatory");
            }else if(!dto.getCatalogingDate().matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")){
                errors.add("catalogingDate format should be: yyyy-MM-dd");
            }

            if(dto.getTitle() == null || dto.getTitle().isEmpty())
                errors.add("title is mandatory");

            if (!errors.isEmpty()) throw new DataValidationException(errors);

            if (dynamoDBService.findByLibraryCode(libraryCode) == null)
                throw new EntityNotFoundException("Book not found");

            var googleResponse = getGoogleResponse(dto.getTitle());

            book = dynamoDBService.save(construct(googleResponse, libraryCode,
                    dto.getCatalogingDate(), dto.getTitle()));

            LOG.info("[BOOK-UPDATE] END");

            return book;

        }catch (Exception e){
            LOG.error("[BOOK-UPDATE] Error: " + e.getMessage(), e);
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
    public void delete(String libraryCode) throws DataValidationException, DynamoDBGeneralException, EntityNotFoundException {

        LOG.info("[BOOK-DELETE] START");

        if(libraryCode == null || libraryCode.trim().isEmpty())
            throw new DataValidationException(Arrays.asList("libraryCode is mandatory"));

        Book book = dynamoDBService.findByLibraryCode(libraryCode);
        if (book == null)
            throw new EntityNotFoundException("Book not found");

        dynamoDBService.delete(book);

        LOG.info("[BOOK-DELETE] END");
    }

    @Override
    public Book findByLibraryCode(String findByLibraryCode) throws DynamoDBGeneralException, EntityNotFoundException {

        LOG.info("[BOOK-FIND] START");

        var book = dynamoDBService.findByLibraryCode(findByLibraryCode);
        if(book == null)
            throw new EntityNotFoundException("Book not found");

        LOG.info("[BOOK-FIND] END");

        return book;
    }

}
