package com.seanconroy.fiae.dto;

import java.util.List;

public class ListResponseDto<T> {
    public List<T> data;

    public ListResponseDto(List<T> data) {
        this.data = data;
    }
}
