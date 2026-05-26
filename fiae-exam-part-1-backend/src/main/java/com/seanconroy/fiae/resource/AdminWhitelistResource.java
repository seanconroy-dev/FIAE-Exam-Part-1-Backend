package com.seanconroy.fiae.resource;

import com.seanconroy.fiae.dto.CreateWhitelistUserRequestDto;
import com.seanconroy.fiae.dto.CreateWhitelistUserResponseDto;
import com.seanconroy.fiae.dto.WhitelistUserResponseDto;
import com.seanconroy.fiae.service.WhitelistService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/admin/whitelist")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminWhitelistResource {

    @Inject
    WhitelistService whitelistService;

    @ConfigProperty(name = "admin.token")
    String adminToken;

    @POST
    public Response createWhitelistService(
        @Valid CreateWhitelistUserRequestDto request, 
        @HeaderParam("X-Admin-Token") String providedAdminToken
    ) {
    if (providedAdminToken == null || !providedAdminToken.equals(adminToken)) {
        return Response.status(Response.Status.FORBIDDEN)
        .entity("{\"error\":\"Missing or invalid admin token\"}")
        .type(MediaType.APPLICATION_JSON)
        .build();
    }  
    try {
        WhitelistService.CreatedWhitelistUser createdUser = whitelistService.createUser(request.email,request.githubUsername);

        WhitelistUserResponseDto userDto = new WhitelistUserResponseDto(createdUser.user());

        return Response.status(Response.Status.CREATED)
        .entity(new CreateWhitelistUserResponseDto(userDto,createdUser.plaintextApiKey()))
        .build();
        
    }  catch (IllegalStateException e) {
        return Response.status(Response.Status.CONFLICT)
        .entity("{\"error\":\"Whitelist user already exists\"}")
        .type(MediaType.APPLICATION_JSON)
        .build();
    }
}
}