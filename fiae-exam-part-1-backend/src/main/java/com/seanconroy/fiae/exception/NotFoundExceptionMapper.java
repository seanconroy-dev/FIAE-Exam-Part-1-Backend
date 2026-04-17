package com.seanconroy.fiae.exception;

import com.seanconroy.fiae.dto.ErrorResponseDto;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponseDto error = new ErrorResponseDto(
            exception.getMessage(),
            404
        );

        return Response.status(Response.Status.NOT_FOUND)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
