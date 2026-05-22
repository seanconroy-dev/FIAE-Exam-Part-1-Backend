package com.seanconroy.fiae.resource;

import com.seanconroy.fiae.dto.WhitelistUserResponseDto;
import com.seanconroy.fiae.service.AuthContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)

public class AuthResource {

    @Inject
    AuthContext authContext;

    @GET
    @Path("/me")
    public WhitelistUserResponseDto me() {
        return new WhitelistUserResponseDto(authContext.getCurrentUser());
    }
}
