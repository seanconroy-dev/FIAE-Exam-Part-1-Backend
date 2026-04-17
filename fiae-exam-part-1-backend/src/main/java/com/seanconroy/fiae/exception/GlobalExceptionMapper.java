package com.seanconroy.fiae.exception;

import com.seanconroy.fiae.dto.ErrorResponseDto;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {

        LOG.error("Unexpected error occurred", exception);

        ErrorResponseDto error = new ErrorResponseDto(
                "An unexpected error occurred",
                500);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();

    }
}
