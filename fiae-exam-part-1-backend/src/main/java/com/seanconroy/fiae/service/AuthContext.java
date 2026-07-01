package com.seanconroy.fiae.service;

import com.seanconroy.fiae.entity.WhitelistUser;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AuthContext {

    private WhitelistUser currentUser;

    public WhitelistUser getCurrentUser(){
        return currentUser;
    }

    public void setCurrentUser(WhitelistUser currentUser){
        this.currentUser = currentUser;
    }
}