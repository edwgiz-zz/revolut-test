package ru.edwgiz.test.rest.framework.jersey.exceptions;

import javax.ws.rs.core.Response;

public class WebApplicationException extends RuntimeException {

    private final Response.Status status;

    public WebApplicationException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }

}
