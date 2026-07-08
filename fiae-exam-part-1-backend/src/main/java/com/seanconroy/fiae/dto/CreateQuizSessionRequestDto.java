package com.seanconroy.fiae.dto;

/**
 * Request body for starting a new quiz session.
 */
public class CreateQuizSessionRequestDto {

    public String module;

    public CreateQuizSessionRequestDto() {
    }

    public CreateQuizSessionRequestDto(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}