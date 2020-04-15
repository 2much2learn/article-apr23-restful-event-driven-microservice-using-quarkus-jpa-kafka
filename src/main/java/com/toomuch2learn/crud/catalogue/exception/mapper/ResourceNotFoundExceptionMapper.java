package com.toomuch2learn.crud.catalogue.exception.mapper;

import com.toomuch2learn.crud.catalogue.error.Error;
import com.toomuch2learn.crud.catalogue.error.ErrorCodes;
import com.toomuch2learn.crud.catalogue.error.ErrorResponse;
import com.toomuch2learn.crud.catalogue.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    private Logger log = LoggerFactory.getLogger(ResourceNotFoundExceptionMapper.class);

    @Override
    public Response toResponse(ResourceNotFoundException e) {
        log.error(String.format("No resource found exception occurred: %s ", e.getMessage()));

        ErrorResponse error = new ErrorResponse();
        error.getErrors().add(
            new Error(
                ErrorCodes.ERR_RESOURCE_NOT_FOUND,
                "Resource not found",
                e.getMessage()
            )
        );

        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }
}
