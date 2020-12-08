package com.vitor.cordeiro.teste.quarkus.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BookCreateDTO {

    @NotNull(message = "title is mandatory")
    private String title;

    @NotNull(message = "libraryCode is mandatory")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "libraryCode must be only letters and numbers")
    private String libraryCode;

    @NotNull(message = "catalogingDate is mandatory")
    @Pattern(regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))",
            message="catalogingDate format should be: yyyy-MM-dd")
    private String catalogingDate;

    public BookCreateDTO(){}

    public BookCreateDTO(String title, String libraryCode,  String catalogingDate) {
        setTitle(title);
        this.libraryCode = libraryCode;
        this.catalogingDate = catalogingDate;
    }

    public void setTitle(String title) {
        if(title != null)
            title = title.trim();

        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getLibraryCode() {
        return libraryCode;
    }

    public void setLibraryCode(String libraryCode) {
        this.libraryCode = libraryCode;
    }

    public String getCatalogingDate() {
        return catalogingDate;
    }

    public void setCatalogingDate(String catalogingDate) {
        this.catalogingDate = catalogingDate;
    }

}
