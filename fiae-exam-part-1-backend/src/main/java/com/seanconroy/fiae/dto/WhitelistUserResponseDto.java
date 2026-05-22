package com.seanconroy.fiae.dto;

import com.seanconroy.fiae.entity.WhitelistUser;

import java.time.LocalDateTime;

public class WhitelistUserResponseDto {
    
    public Long id;
    public String email;
    public String githubUsername;
    public LocalDateTime createdAt;
    public boolean isActive;

    public WhitelistUserResponseDto() {
    }

    public WhitelistUserResponseDto(WhitelistUser user) {
        this.id = user.id;
        this.email = user.email;
        this.githubUsername = user.githubUsername;
        this.createdAt = user.createdAt;
        this.isActive = user.isActive;
    }
}
