package com.seanconroy.fiae.exception;

import com.seanconroy.fiae.dto.ErrorResponseDto;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalBadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException exception) {
        ErrorResponseDto error = new ErrorResponseDto(
            exception.getMessage(),
            400
        );

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}