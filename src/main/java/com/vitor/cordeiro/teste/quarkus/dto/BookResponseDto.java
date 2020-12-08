package com.vitor.cordeiro.teste.quarkus.dto;

import com.vitor.cordeiro.teste.quarkus.entity.Book;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.UUID;

@RegisterForReflection
public class BookResponseDto {

    private String requestId;
    private List<String> errors;
    private Book data;

    public BookResponseDto(Book data, List<String> errors) {
        this.data = data;
        this.errors = errors;
        this.requestId = UUID.randomUUID().toString();
    }

    public Book getData() {
        return data;
    }

    public void setData(Book data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
