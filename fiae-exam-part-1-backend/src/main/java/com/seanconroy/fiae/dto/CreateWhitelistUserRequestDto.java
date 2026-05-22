package com.seanconroy.fiae.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateWhitelistUserRequestDto {
    
    @NotBlank
    @Email
    public String email;

    public String githubUsername;
}
