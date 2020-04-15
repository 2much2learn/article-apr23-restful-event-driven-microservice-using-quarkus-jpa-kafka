package com.toomuch2learn.crud.catalogue.exception.mapper;

import com.toomuch2learn.crud.catalogue.error.Error;
import com.toomuch2learn.crud.catalogue.error.ErrorCodes;
import com.toomuch2learn.crud.catalogue.error.ErrorResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        ErrorResponse error = new ErrorResponse();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getErrors().add(
                new Error(
                    ErrorCodes.ERR_CONSTRAINT_CHECK_FAILED,
                    violation.getPropertyPath().toString(),
                    violation.getMessage())
            );
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}
