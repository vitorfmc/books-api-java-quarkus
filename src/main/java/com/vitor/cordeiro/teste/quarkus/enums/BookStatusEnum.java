package com.vitor.cordeiro.teste.quarkus.enums;

public enum BookStatusEnum {

    NEW("NEW"),
    OLD("OLD");

    private String description;

    BookStatusEnum(String description){
        this.description = description;
    }

    public String getDescription() { return this.description; }
}
