package com.seanconroy.fiae.dto;

public class CreateWhitelistUserResponseDto {

    public WhitelistUserResponseDto user;
    public String apiKey;
    public String message;
    
    public CreateWhitelistUserResponseDto(){
    }

    public CreateWhitelistUserResponseDto(WhitelistUserResponseDto user,String apiKey) {
        this.user = user;
        this.apiKey = apiKey;
        this.message = "Store this API key now. It will not be shown again.";
    }
}
