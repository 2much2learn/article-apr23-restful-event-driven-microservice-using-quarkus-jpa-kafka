package com.toomuch2learn.crud.catalogue.exception.mapper;

import com.toomuch2learn.crud.catalogue.error.Error;
import com.toomuch2learn.crud.catalogue.error.ErrorCodes;
import com.toomuch2learn.crud.catalogue.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private Logger log = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException e) {

        log.error("Runtime exception occurred while processing the request", e);

        ErrorResponse error = new ErrorResponse();
        error.getErrors().add(
            new Error(
                ErrorCodes.ERR_RUNTIME,
                "Internal Server Error",
                "Error occurred while processing your request. Please try again !!"
            )
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}
