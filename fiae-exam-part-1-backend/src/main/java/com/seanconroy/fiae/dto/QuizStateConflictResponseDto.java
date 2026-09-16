package com.seanconroy.fiae.dto;

public class QuizStateConflictResponseDto {

    public String message;
    public int status;
    public String timestamp;
    public QuizStateResponseDto currentState;

    public QuizStateConflictResponseDto(String message, QuizStateResponseDto currentState) {
        this.message = message;
        this.status = 409;
        this.timestamp = java.time.Instant.now().toString();
        this.currentState = currentState;
    }
}
