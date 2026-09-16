package com.seanconroy.fiae.dto;

import com.seanconroy.fiae.entity.QuizState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuizStateResponseDto {

    public String moduleKey;
    public String moduleName;
    public List<String> queueSlugs;
    public int currentIndex;
    public Map<String, String> resultsBySlug;
    public boolean completed;
    public long revision;
    public Instant startedAt;
    public Instant updatedAt;

    public QuizStateResponseDto() {
    }

    public QuizStateResponseDto(QuizState state) {
        this.moduleKey = state.moduleKey;
        this.moduleName = state.moduleName;
        this.queueSlugs = new ArrayList<>(state.queueSlugs);
        this.currentIndex = state.currentIndex;
        this.resultsBySlug = new LinkedHashMap<>(state.resultsBySlug);
        this.completed = state.completed;
        this.revision = state.revision;
        this.startedAt = state.startedAt;
        this.updatedAt = state.updatedAt;
    }
}
