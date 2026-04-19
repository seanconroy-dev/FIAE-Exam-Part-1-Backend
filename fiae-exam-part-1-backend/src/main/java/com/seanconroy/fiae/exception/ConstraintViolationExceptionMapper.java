package com.seanconroy.fiae.exception;

import com.seanconroy.fiae.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        String message = exception.getConstraintViolations()
            .stream()
            .findFirst()
            .map(violation -> violation.getMessage())
            .orElse("Validation failed");

        ErrorResponseDto error = new ErrorResponseDto(
            message,
            400
        );

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}