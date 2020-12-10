package com.vitor.cordeiro.teste.quarkus.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BookUpdateDTO {

    @NotNull(message = "title is mandatory")
    private String title;

    @NotNull(message = "catalogingDate is mandatory")
    @Pattern(regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))",
            message="catalogingDate format should be: yyyy-MM-dd")
    private String catalogingDate;

    public BookUpdateDTO(){}

    public BookUpdateDTO(String title, String catalogingDate) {
        setTitle(title);
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

    public String getCatalogingDate() {
        return catalogingDate;
    }

    public void setCatalogingDate(String catalogingDate) {
        this.catalogingDate = catalogingDate;
    }

}
