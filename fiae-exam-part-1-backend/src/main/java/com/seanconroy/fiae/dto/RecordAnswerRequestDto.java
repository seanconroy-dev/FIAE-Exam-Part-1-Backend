package com.seanconroy.fiae.dto;

import jakarta.validation.constraints.NotNull;

public class RecordAnswerRequestDto {

    @NotNull
    public Boolean correct;
}
