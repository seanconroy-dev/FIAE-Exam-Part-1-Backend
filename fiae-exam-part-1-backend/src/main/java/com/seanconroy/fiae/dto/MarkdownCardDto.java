package com.seanconroy.fiae.dto;

import java.util.List;

public class MarkdownCardDto {
    public String id;
    public String slug;
    public String title;
    public String module;
    public List<String> topics;
    public List<String> tags;
    public CardContentDto card;
    public String status;
    public String created;
    public String updated;
    public String body;
    public String category;
}