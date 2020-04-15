package com.toomuch2learn.crud.catalogue.exception.mapper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.toomuch2learn.crud.catalogue.error.Error;
import com.toomuch2learn.crud.catalogue.error.ErrorCodes;
import com.toomuch2learn.crud.catalogue.error.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException e) {
        ErrorResponse error = new ErrorResponse();

        error.getErrors().add(
            new Error(
                ErrorCodes.ERR_REQUEST_PARAMS_BODY_VALIDATION_FAILED,
                "Invalid request format. Please verify your request body and try again !!",
                e.getMessage()
            )
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}
