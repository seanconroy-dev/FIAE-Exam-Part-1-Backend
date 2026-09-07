package com.seanconroy.fiae.dto;

import com.seanconroy.fiae.entity.LearningProgress;

import java.time.LocalDateTime;

public class LearningProgressResponseDto {

    public Long id;
    public String cardSlug;
    public int timesSeen;
    public int timesCorrect;
    public LocalDateTime lastSeenAt;

    public LearningProgressResponseDto() {
    }

    public LearningProgressResponseDto(LearningProgress progress) {
        this.id = progress.id;
        this.cardSlug = progress.cardSlug;
        this.timesSeen = progress.timesSeen;
        this.timesCorrect = progress.timesCorrect;
        this.lastSeenAt = progress.lastSeenAt;
    }
}
