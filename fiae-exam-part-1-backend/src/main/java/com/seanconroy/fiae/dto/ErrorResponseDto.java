package com.seanconroy.fiae.dto;

public class ErrorResponseDto {

    public String message;
    public int status;
    public String timestamp;

    public ErrorResponseDto(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = java.time.Instant.now().toString();

    }

}
