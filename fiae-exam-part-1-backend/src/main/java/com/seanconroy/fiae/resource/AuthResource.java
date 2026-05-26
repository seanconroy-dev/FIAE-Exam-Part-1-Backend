package com.seanconroy.fiae.resource;

import com.seanconroy.fiae.dto.WhitelistUserResponseDto;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.service.AuthContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthContext authContext;

    @GET
    @Path("/me")
    public Response me() {
        WhitelistUser currentUser = authContext.getCurrentUser();

        if (currentUser == null) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"Missing or invalid API key\",\"status\":403}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.ok(new WhitelistUserResponseDto(currentUser)).build();
    }
}