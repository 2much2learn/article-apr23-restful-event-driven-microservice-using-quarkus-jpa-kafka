package com.toomuch2learn.crud.catalogue.exception.mapper;

import com.toomuch2learn.crud.catalogue.error.Error;
import com.toomuch2learn.crud.catalogue.error.ErrorCodes;
import com.toomuch2learn.crud.catalogue.error.ErrorResponse;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException e) {

        ErrorResponse error = new ErrorResponse();
        error.getErrors().add(
            new Error(
                ErrorCodes.ERR_RESOURCE_NOT_FOUND,
                "Invalid Path",
                e.getMessage()
            )
        );
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }
}
