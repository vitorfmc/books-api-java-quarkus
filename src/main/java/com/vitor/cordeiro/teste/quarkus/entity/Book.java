package com.vitor.cordeiro.teste.quarkus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.vitor.cordeiro.teste.quarkus.converter.OffsetDateTimeConverter;
import com.vitor.cordeiro.teste.quarkus.enums.BookStatusEnum;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.OffsetDateTime;
import java.util.List;

@RegisterForReflection
@DynamoDBTable(tableName = "teste-quarkus-book")
public class Book {

    @DynamoDBHashKey
    private String libraryCode;

    //STATUS IS A RESERVED WORD FOR DYNAMO, SO THE NAME MUST CHANGE
    @DynamoDBAttribute(attributeName = "bookStatus")
    @DynamoDBTypeConvertedEnum
    private BookStatusEnum status;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime catalogingDate;

    @DynamoDBAttribute
    private String title;

    @DynamoDBAttribute
    private String googleTitle;

    @DynamoDBAttribute
    private List<String> authors;

    @DynamoDBAttribute
    private List<String> categories;

    @DynamoDBAttribute
    private String publisher;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime publishedDate;

    @DynamoDBAttribute
    private String description;

    @DynamoDBAttribute
    private Integer pageCount;

    @DynamoDBAttribute
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.M)
    private Image thumbnail;

    public Book(){}

    public Book(String libraryCode, BookStatusEnum status, OffsetDateTime catalogingDate, String title, String googleTitle, List<String> authors,
                List<String> categories, String publisher, OffsetDateTime publishedDate, String description, Integer pageCount, Image thumbnail) {
        this.libraryCode = libraryCode;
        this.status = status;
        this.catalogingDate = catalogingDate;
        this.title = title;
        this.googleTitle = googleTitle;
        this.authors = authors;
        this.categories = categories;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
        this.thumbnail = thumbnail;
    }

    public String getLibraryCode() {
        return libraryCode;
    }

    public void setLibraryCode(String libraryCode) {
        this.libraryCode = libraryCode;
    }

    public BookStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BookStatusEnum status) {
        this.status = status;
    }

    public OffsetDateTime getCatalogingDate() {
        return catalogingDate;
    }

    public void setCatalogingDate(OffsetDateTime catalogingDate) {
        this.catalogingDate = catalogingDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public OffsetDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(OffsetDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGoogleTitle() {
        return googleTitle;
    }

    public void setGoogleTitle(String googleTitle) {
        this.googleTitle = googleTitle;
    }
}
