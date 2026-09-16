package com.seanconroy.fiae.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class QuizStateUpsertRequestDto {

    public String moduleName;

    @NotNull
    public List<String> queueSlugs;

    @NotNull
    @Min(0)
    public Integer currentIndex;

    @NotNull
    public Map<String, String> resultsBySlug;

    @NotNull
    public Boolean completed;

    @NotNull
    @Min(0)
    public Long revision;

    @NotNull
    public Instant startedAt;
}
