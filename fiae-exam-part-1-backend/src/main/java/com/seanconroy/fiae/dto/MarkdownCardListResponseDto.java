package com.seanconroy.fiae.dto;

import java.util.List;

public class MarkdownCardListResponseDto {
    public List<MarkdownCardDto> data;

    public MarkdownCardListResponseDto(List<MarkdownCardDto> data) {
        this.data = data;
    }
}
